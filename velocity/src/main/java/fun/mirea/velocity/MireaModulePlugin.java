package fun.mirea.velocity;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.VelocityCommandManager;
import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.GameProfile;
import fun.mirea.common.network.MojangClient;
import fun.mirea.common.server.Configuration;
import fun.mirea.common.server.ConsoleLogger;
import fun.mirea.common.server.MireaComponent;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.PlayerProvider;
import fun.mirea.common.user.UserManager;
import fun.mirea.common.user.skin.SkinData;
import fun.mirea.database.Database;
import fun.mirea.database.SqlDatabase;
import fun.mirea.velocity.command.SkinCommand;
import fun.mirea.velocity.messaging.ChannelData;
import fun.mirea.velocity.messaging.PluginMessage;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Plugin(id = "mirea-velocity", name = "mirea-velocity", version = "1.0-SNAPSHOT",
        url = "https://www.mirea.fun", description = "MireaModule for Velocity", authors = {"DrKapdor"})

public class MireaModulePlugin {

    @Getter
    private static MireaModulePlugin instance;
    @Getter
    private static Configuration configuration;
    @Getter
    private static Database database;
    @Getter
    private static UserManager<Player> userManager;

    @Getter
    private static VelocityCommandManager commandManager;
    @Getter
    private final ProxyServer proxyServer;
    @Getter
    private final Logger logger;
    @Getter
    private final Path dataDirectory;

    private MojangClient mojangClient;

    private static int registeredUsers = 0;

    @Inject
    public MireaModulePlugin(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        createFiles();
        instance = this;
        database = new SqlDatabase("jdbc:postgresql://" + configuration.getDbHost() + ":" + configuration.getDbPort() + "/" + configuration.getDbName(),
                configuration.getDbUser(), configuration.getDbUserPassword(), false);
        userManager = new UserManager<>(name -> {
            Optional<Player> optional = proxyServer.getPlayer(name);
            return optional.orElse(null);
        }, database, new ConsoleLogger() {
                @Override
                public void log(String info) {
                    getLogger().info(info);
                }
                @Override
                public void error(StackTraceElement[] error) {
                    for (StackTraceElement element : error)
                        getLogger().severe(element.toString());
                }
                @Override
                public void error(String error) {
                    getLogger().severe(error);
                }
        });
        commandManager = new VelocityCommandManager(proxyServer, this);
        mojangClient = new MojangClient();
        registerCommands(new SkinCommand(userManager, configuration.getMineSkinToken(), mojangClient));
        runRegisteredUsersUpdater();
    }

    private void createFiles() {
        File dataFolder = dataDirectory.toFile();
        if (!dataFolder.exists())
            dataFolder.mkdir();
        File configFile = new File(dataFolder + File.separator + "config.toml");
        if (!configFile.exists()) {
            try {
                configuration = new Configuration("localhost", 5432, "mirea", "postgres", "admin",  "<insert_token_here>");
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

    private void registerCommands(BaseCommand... commands) {
        commandManager.getCommandContexts().registerIssuerAwareContext(MireaUser.class, context -> {
            try {
                Optional<MireaUser<Player>> optional = userManager.getUserCache().get(context.getIssuer().getPlayer().getUsername());
                if (optional.isPresent())
                    return optional.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        });
        for (BaseCommand command : commands) commandManager.registerCommand(command);
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

    @Subscribe
    public void onLogin(LoginEvent event) throws ExecutionException {
        Player player = event.getPlayer();
        userManager.getUserCache().get(player.getUsername()).ifPresent(user -> {
           if (user.hasSkinData()) {
               SkinData skinData = user.getSkinData();
               if (!skinData.getSignature().isEmpty() && !skinData.getValue().isEmpty()) {
                   GameProfile.Property skinProperty = new GameProfile.Property("textures", skinData.getValue(), skinData.getSignature());
                   event.getPlayer().setGameProfileProperties(Collections.singletonList(skinProperty));
               }
           } else {
               mojangClient.getLicenseId(player.getUsername()).thenAcceptAsync(optionalId -> {
                   optionalId.ifPresentOrElse(uuid -> {
                       mojangClient.getLicenseSkin(uuid).thenAcceptAsync(optionalSkin -> {
                           optionalSkin.ifPresent(skinData -> {
                               GameProfile.Property skinProperty = new GameProfile.Property("textures", skinData.getValue(), skinData.getSignature());
                               event.getPlayer().setGameProfileProperties(Collections.singletonList(skinProperty));
                               user.setSkinData(skinData);
                               user.save(userManager);
                               sendSkinMessages(user, user.getSkinData());
                           });
                       });
                   }, () -> {
                       user.setSkinData(new SkinData());
                       sendSkinMessages(user, user.getSkinData());
                   });
               });
           }
        });
    }

    private void sendSkinMessages(MireaUser<Player> user, SkinData skinData) {
        ChannelData channelData = new ChannelData("mirea", "user");
        Collection<RegisteredServer> servers = MireaModulePlugin.getInstance().getProxyServer().getAllServers();
        PluginMessage updateUserMessage = PluginMessage.builder()
                .channelData(channelData)
                .service("updateUser")
                .player(user.getName())
                .servers(servers)
                .build();
        updateUserMessage.send();
        PluginMessage refreshSkinMessage = PluginMessage.builder()
                .channelData(channelData)
                .service("refreshSkin")
                .player(user.getName())
                .servers(servers)
                .values(new LinkedList<>(Arrays.asList(skinData.getValue(), skinData.getSignature())))
                .build();
        refreshSkinMessage.send();
    }
}
