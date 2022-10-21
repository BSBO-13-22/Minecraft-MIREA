package fun.mirea.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import fun.mirea.common.server.Configuration;
import fun.mirea.common.user.PlayerProvider;
import fun.mirea.common.user.UserManager;
import fun.mirea.database.SqlDatabase;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Plugin(id = "mireamodule", name = "MireaModule", version = "1.0-SNAPSHOT",
        url = "https://www.mirea.fun", description = "MireaModule for Velocity", authors = {"DrKapdor"})

public class MireaModulePlugin {

    @Getter
    private static MireaModulePlugin instance;
    @Getter
    private static Configuration configuration;
    @Getter
    private static UserManager<Player> userManager;
    @Getter
    private final ProxyServer proxyServer;
    @Getter
    private final Logger logger;
    @Getter
    private final Path dataDirectory;

    private static int registeredUsers = 0;

    @Inject
    public MireaModulePlugin(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    private void createFiles() {
        File dataFolder = dataDirectory.toFile();
        if (!dataFolder.exists())
            dataFolder.mkdir();
        File configFile = new File(dataFolder + File.separator + "config.toml");
        if (!configFile.exists()) {
            try {
                configuration = new Configuration("localhost", 5432, "mirea", "postgres", "admin");
                if (configFile.createNewFile()) configuration.toFile(configFile.getPath()).get();
            } catch (IOException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                configuration = Configuration.fromFile(configFile).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void runRegisteredUsersUpdater() {
        proxyServer.getScheduler().buildTask(this, () -> {
            try {
                registeredUsers = userManager.getTotalUsersCount().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).repeat(10, TimeUnit.SECONDS).schedule();
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        createFiles();
        instance = this;
        userManager = new UserManager<>(name -> {
            Optional<Player> optional = proxyServer.getPlayer(name);
            return optional.orElse(null);
        }, new SqlDatabase("jdbc:postgresql://" + configuration.getDbHost() + ":" + configuration.getDbPort() + "/" + configuration.getDbName(),
                configuration.getDbUser(), configuration.getDbUserPassword(), false));
        runRegisteredUsersUpdater();
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onServerPing(ProxyPingEvent event) {
        ServerPing defaultPing = event.getPing();
        TextComponent motd = Component.text()
                .append(Component.text("МИРЭА").color(TextColor.fromHexString("#faa61a")))
                .append(Component.text(" — Российский кубический сервер").color(TextColor.fromHexString("#2688c8")))
                .append(Component.newline())
                .append(Component.text("§fПроект посетили уже §e" + registeredUsers + " §fстудентов!")).build();
        ServerPing serverPing = ServerPing.builder()
                .favicon(defaultPing.getFavicon().get())
                .description(motd)
                .version(new ServerPing.Version(760, "МИРЭА"))
                .maximumPlayers(proxyServer.getPlayerCount() + 1)
                .onlinePlayers(proxyServer.getPlayerCount())
                .build();
        event.setPing(serverPing);
    }

}
