package fun.mirea.common.user;

import com.google.common.cache.*;
import com.google.gson.Gson;
import fun.mirea.database.Database;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class UserManager {

    private final Database database;
    private final Gson gson;

    public UserManager(Database database) {
        this.database = database;
        this.gson = new Gson();
    }

    @Getter
    private final LoadingCache<String, MireaUser> userCache = CacheBuilder.newBuilder()
            .maximumSize(30)
            .expireAfterAccess(Duration.ofMinutes(30))
            .removalListener((RemovalListener<String, MireaUser>) notification -> {
                //todo Событие выгрузки из кэша
            }).build(new CacheLoader<>() {
                @Override
                public @NotNull MireaUser load(@NotNull String name) {
                    try {
                        Optional<ResultSet> optional = database.executeQuery(String.format("SELECT * FROM users WHERE name = '%s'", name)).get();
                        if (optional.isPresent()) {
                            ResultSet resultSet = optional.get();
                            if (resultSet.next()) {
                                MireaUser user = gson.fromJson(resultSet.getString("data"), MireaUser.class);
                                resultSet.close();
                                return user;
                            } else {
                                MireaUser mireaUser = new MireaUser(name);
                                createUser(mireaUser);
                                return mireaUser;
                            }
                        }
                    } catch (ExecutionException | InterruptedException | SQLException e) {
                        e.printStackTrace();
                    }
                    return new MireaUser(name);
                }
            });

    protected void createUser(MireaUser user) {
        database.execute(String.format("INSERT INTO users VALUES ('%s', '%s')", user.getName(), gson.toJson(user)));
    }

    protected void updateUser(MireaUser user) {
        database.execute(String.format("UPDATE users SET data = '%s' WHERE name = '%s'", gson.toJson(user), user.getName()));
    }
}
