package fun.mirea.common.user;

import com.google.common.cache.*;
import com.google.gson.Gson;
import fun.mirea.database.Database;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class UserManager<T> {

    private final PlayerProvider<T> provider;
    private final Database database;
    private final Gson gson;

    public UserManager(PlayerProvider<T> provider, Database database) {
        this.provider = provider;
        this.database = database;
        this.gson = new Gson();
    }

    @Getter
    private final LoadingCache<String, MireaUser<T>> userCache = CacheBuilder.newBuilder()
            .maximumSize(30)
            .expireAfterAccess(Duration.ofMinutes(30))
            .removalListener((RemovalListener<String, MireaUser<T>>) notification -> {
                //todo Событие выгрузки из кэша
            }).build(new CacheLoader<>() {
                @Override
                public @NotNull MireaUser<T> load(@NotNull String name) {
                    try {
                        ResultSet resultSet = database.executeQuery(String.format("SELECT * FROM users WHERE name = '%s'", name)).get();
                        if (resultSet != null &&
                                resultSet.next()) {
                            MireaUser<T> user = gson.fromJson(resultSet.getString("data"), MireaUser.class);
                            user.setProvider(provider);
                            resultSet.close();
                            return user;
                        } else {
                            MireaUser<T> user = new MireaUser<>(name);
                            user.setProvider(provider);
                            createUser(user);
                            return user;
                        }
                    } catch (ExecutionException | InterruptedException | SQLException e) {
                        e.printStackTrace();
                    }
                    MireaUser<T> user = new MireaUser<>(name);
                    user.setProvider(provider);
                    return user;
                }
            });

    public CompletableFuture<Integer> getTotalUsersCount() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ResultSet resultSet = database.executeQuery("SELECT COUNT(*) AS recordCount FROM users").get();
                if (resultSet != null && resultSet.next()) return resultSet.getInt("recordCount");
            } catch (SQLException| InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    void createUser(MireaUser<T> user) {
        database.execute(String.format("INSERT INTO users VALUES ('%s', '%s')", user.getName(), gson.toJson(user)));
    }

    void updateUser(MireaUser<T> user) {
        database.execute(String.format("UPDATE users SET data = '%s' WHERE name = '%s'", gson.toJson(user), user.getName()));
    }
}
