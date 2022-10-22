package fun.mirea.common.user;

import com.google.common.cache.*;
import com.google.gson.Gson;
import fun.mirea.common.server.ConsoleLogger;
import fun.mirea.database.Database;
import fun.mirea.database.ExecutionResult;
import fun.mirea.database.ExecutionState;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class UserManager<T> {

    @Getter
    private final PlayerProvider<T> provider;
    private final Database database;
    private final ConsoleLogger logger;
    private final Gson gson;

    public UserManager(PlayerProvider<T> provider, Database database, ConsoleLogger logger) {
        this.provider = provider;
        this.database = database;
        this.logger = logger;
        this.gson = new Gson();
    }

    @Getter
    private final LoadingCache<String, Optional<MireaUser<T>>> userCache = CacheBuilder.newBuilder()
            .maximumSize(30)
            .expireAfterAccess(Duration.ofMinutes(30))
            .removalListener((RemovalListener<String, Optional<MireaUser<T>>>) notification -> {
                //todo Событие выгрузки из кэша
            }).build(new CacheLoader<>() {
                @Override
                public @NotNull Optional<MireaUser<T>> load(@NotNull String name) {
                    try {
                        ExecutionResult<ResultSet> executionResult = database.executeQuery(String.format("SELECT * FROM users WHERE name ILIKE '%s'", name)).get();
                        if (executionResult.state() == ExecutionState.SUCCESS) {
                            ResultSet resultSet = executionResult.content();
                            if (resultSet != null && resultSet.next()) {
                                MireaUser<T> user = gson.fromJson(resultSet.getString("data"), MireaUser.class);
                                user.setProvider(provider);
                                resultSet.close();
                                return Optional.of(user);
                            }
                        } else logger.error(executionResult.stackTrace());
                    } catch (ExecutionException | InterruptedException | SQLException e) {
                        e.printStackTrace();
                    }
                    return Optional.empty();
                }
            });

    public CompletableFuture<Integer> getTotalUsersCount() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ExecutionResult<ResultSet> executionResult = database.executeQuery("SELECT COUNT(*) AS recordCount FROM users").get();
                if (executionResult.state() == ExecutionState.SUCCESS) {
                    ResultSet resultSet = executionResult.content();
                    if (resultSet.next()) {
                        int count = resultSet.getInt("recordCount");
                        resultSet.close();
                        return count;
                    }
                } else {
                    StackTraceElement[] elements = executionResult.stackTrace();
                    if (elements != null) logger.error(elements);
                    else logger.error(executionResult.error());
                }
            } catch (SQLException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    public CompletableFuture<Collection<MireaUser<T>>> getAllUsers() {
        return CompletableFuture.supplyAsync(() -> {
            Collection<MireaUser<T>> users = new ArrayList<>();
            try {
                ExecutionResult<ResultSet> executionResult = database.executeQuery("SELECT * FROM users").get();
                if (executionResult.state() == ExecutionState.SUCCESS) {
                    ResultSet resultSet = executionResult.content();
                    while (resultSet.next()) {
                        MireaUser<T> user = gson.fromJson(resultSet.getString("data"), MireaUser.class);
                        user.setProvider(provider);
                        users.add(user);
                    }
                    resultSet.close();
                } else logger.error(executionResult.stackTrace());
            } catch (SQLException| InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return users;
        });
    }

    void createUser(MireaUser<T> user) {
        userCache.put(user.getName(), Optional.of(user));
        try {
            ExecutionResult<Void> result = database.execute(String.format("INSERT INTO users VALUES ('%s', '%s')", user.getName(), gson.toJson(user))).get();
            System.out.println(result.state());
            if (result.state() == ExecutionState.SUCCESS)
                logger.log("Successfully created account for " + user.getName());
            else logger.error(result.stackTrace());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        };
    }

    void updateUser(MireaUser<T> user) {
        try {
            ExecutionResult<Void> result =  database.execute(String.format("UPDATE users SET data = '%s' WHERE name = '%s'", gson.toJson(user), user.getName())).get();
            if (result.state() == ExecutionState.SUCCESS)
                logger.log("Successfully updated data for " + user.getName());
            else logger.error(result.stackTrace());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
