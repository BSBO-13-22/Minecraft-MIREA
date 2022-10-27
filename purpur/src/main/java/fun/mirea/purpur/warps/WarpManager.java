package fun.mirea.purpur.warps;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.gson.Gson;
import fun.mirea.common.server.ConsoleLogger;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.university.Institute;
import fun.mirea.database.Database;
import fun.mirea.database.ExecutionResult;
import fun.mirea.database.ExecutionState;
import fun.mirea.purpur.MireaModulePlugin;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
               try {
                   int warpsCount = 0;
                   while (resultSet.next()) {
                       Warp warp = gson.fromJson(resultSet.getString("data"), Warp.class);
                       cache.put(warp.getName().toLowerCase(), Optional.of(warp));
                       warpsCount++;
                   }
                   if (warpsCount > 0) logger.log("Successfully loaded " + warpsCount + " warp(s) form database!");
               } catch (SQLException e) {
                   e.printStackTrace();
               }
           } else logger.error(result.stackTrace());
        });
    }

    public CompletableFuture<Optional<Warp>> registerWarp(String name, String owner, Location location) {
        Warp warp = new Warp(name, owner, location);
        return database.execute(String.format("INSERT INTO warps VALUES ('%s', '%s')", name, gson.toJson(warp))).thenApply(result -> {
            if (result.state() == ExecutionState.SUCCESS) {
                cache.put(name.toLowerCase(), Optional.of(warp));
                logger.log("Successfully registered warp \"" + name + "\"");
                drawDynmapMarker(warp);
                return Optional.of(warp);
            } else logger.error(result.stackTrace());
            return Optional.empty();
        });
    }

    public CompletableFuture<Void> unregisterWarp(String name) {
       return database.execute(String.format("DELETE FROM warps WHERE name ILIKE '%s'", name)).thenAccept(result -> {
           cache.asMap().remove(name.toLowerCase());
           removeDynmapMarker(name);
       });
    }

    private void drawDynmapMarker(Warp warp) {
        if (MireaModulePlugin.getDynmapApi() != null) {
            Location location = warp.getLocation();
            MarkerAPI markerApi = MireaModulePlugin.getDynmapApi().getMarkerAPI();
            Marker marker = markerApi.getMarkerSet("mirea_warps")
                    .createMarker("warp_" + warp.getName().toLowerCase(),
                            "Варп «" + warp.getName() + "»",
                            true,
                            location.getWorld().getName(),
                            location.getX(),
                            location.getY(),
                            location.getZ(),
                            markerApi.getMarkerIcon("compass"),
                            false);
            if (marker != null) {
                StringBuilder description = new StringBuilder();
                description.append("<h3>Варп «").append(warp.getName()).append("»</h3>");
                warp.getCreator().ifPresent(creator -> {
                    String creatorField = "Создатель: ";
                    if (creator.hasUniversityData()) {
                        Institute institute = Institute.of(creator.getUniversityData().getInstitute());
                        creatorField += "<font color=\"" + institute.getColorScheme() + "\">" + institute.getPrefix() + " " + creator.getName() + "</font>";
                    } else creatorField += creator.getName();
                    description.append(creatorField).append("<br>");
                });
                description.append("Создан: ").append(new SimpleDateFormat("d MMMM HH:mm").format(warp.getCreationDate()));
                marker.setDescription(description.toString());
            }
        }
    }

    private void removeDynmapMarker(String name) {
        if (MireaModulePlugin.getDynmapApi() != null) {
            MarkerAPI markerApi = MireaModulePlugin.getDynmapApi().getMarkerAPI();
            Marker marker = markerApi.getMarkerSet("mirea_warps").findMarker("warp_" + name.toLowerCase());
            if (marker != null) marker.deleteMarker();
        }
    }
}
