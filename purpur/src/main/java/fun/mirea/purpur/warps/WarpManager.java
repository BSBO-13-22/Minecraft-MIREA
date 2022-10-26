package fun.mirea.purpur.warps;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.gson.Gson;
import fun.mirea.common.server.ConsoleLogger;
import fun.mirea.common.user.MireaUser;
import fun.mirea.database.Database;
import fun.mirea.database.ExecutionResult;
import fun.mirea.database.ExecutionState;
import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class WarpManager {

    private final Database database;
    private final ConsoleLogger logger;
    private final Gson gson;

    @Getter
    private final LoadingCache<String, Optional<Warp>> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(Duration.ofMinutes(15))
            .build(new CacheLoader<>() {
                @Override
                public @NotNull Optional<Warp> load(@NotNull String name) {
                    try {
                        ExecutionResult<ResultSet> executionResult = database.executeQuery(String.format("SELECT * FROM warps WHERE name ILIKE '%s'", name)).get();
                        if (executionResult.state() == ExecutionState.SUCCESS) {
                            ResultSet resultSet = executionResult.content();
                            if (resultSet != null && resultSet.next()) {
                                Warp warp = gson.fromJson(resultSet.getString("data"), Warp.class);
                                return Optional.of(warp);
                            }
                        } else logger.error(executionResult.stackTrace());
                    } catch (ExecutionException | InterruptedException | SQLException e) {
                        e.printStackTrace();
                    }
                    return Optional.empty();
                }
            });

    public WarpManager(Database database, ConsoleLogger logger) {
        this.database = database;
        this.logger = logger;
        this.gson = new Gson();
        this.database.execute("CREATE TABLE IF NOT EXISTS warps (name VARCHAR(16) NOT NULL, data TEXT NOT NULL);").thenRun(loadWarps());
    }

    private Runnable loadWarps() {
        return () -> database.executeQuery("SELECT * FROM warps").thenAcceptAsync(result -> {
           if (result.state() == ExecutionState.SUCCESS) {
               ResultSet resultSet = result.content();
               int warpsCount = 0;
               try {
                   while (resultSet.next()) {
                       Warp warp = gson.fromJson(resultSet.getString("data"), Warp.class);
                       cache.put(warp.getName().toLowerCase(), Optional.of(warp));
                       warpsCount++;
                   }
               } catch (SQLException e) {
                   e.printStackTrace();
               }
               if (warpsCount > 0) logger.log(String.format("Successfully loaded %s warp(s) from database", warpsCount));
           } else logger.error(result.stackTrace());
        });
    }

    public Warp registerWarp(String name, String owner, Location location) {
        Warp warp = new Warp(name, owner, location);
        cache.put(name.toLowerCase(), Optional.of(warp));
        try {
            ExecutionResult<Void> result = database.execute(String.format("INSERT INTO warps VALUES ('%s', '%s')", name, gson.toJson(warp))).get();
            if (result.state() == ExecutionState.SUCCESS)
                logger.log("Successfully registered warp \"" + name + "\"");
            else logger.error(result.stackTrace());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return warp;
    }

    public boolean unregisterWarp(String name) {
        try {
            Optional<Warp> warp = cache.get(name.toLowerCase());
            if (warp.isPresent()) {
                cache.asMap().remove(name.toLowerCase());
                database.execute(String.format("DELETE FROM warps WHERE name ILIKE '%s'", name));
                return true;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
