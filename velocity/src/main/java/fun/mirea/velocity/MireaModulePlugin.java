package fun.mirea.velocity;

import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.UserManager;
import fun.mirea.database.SqlDatabase;
import fun.mirea.velocity.command.RegisterCommand;
import lombok.Getter;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboFactory;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.chunk.Dimension;
import net.elytrium.limboapi.api.chunk.VirtualWorld;
import net.elytrium.limboapi.api.command.LimboCommandMeta;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import net.elytrium.limboapi.api.player.LimboPlayer;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
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
    private final ProxyServer proxyServer;
    @Getter
    private final Logger logger;
    @Getter
    private final Path dataDirectory;

    private final UserManager userManager;

    private LimboFactory factory;
    private Limbo authLimbo;
    private RegisteredServer mainServer;

    @Inject
    public MireaModulePlugin(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.userManager = new UserManager(new SqlDatabase("jdbc:postgresql://localhost:5432/mirea", "root", "admin", false));
        logger.info("Hello there! I made my first plugin with Velocity.");
        instance = this;
    }

//    @Subscribe
//    public void onInitialize(ProxyInitializeEvent event) {
//        registerCommands();
//        Optional<RegisteredServer> server = this.getProxyServer().getServer("main");
//        server.ifPresent(registeredServer -> mainServer = registeredServer);
//
//        factory = (LimboFactory) this.proxyServer.getPluginManager().getPlugin("limboapi").flatMap(PluginContainer::getInstance).orElseThrow();
//        VirtualWorld world = this.factory.createVirtualWorld(Dimension.THE_END, 0, 0, 0, (float) 90, (float) 0.0);
//        authLimbo = factory.createLimbo(world).setName("AuthLimbo").setWorldTime(6000).registerCommand(new LimboCommandMeta(List.of("register")));
//    }
//
//    @Subscribe
//    public void onLoginLimboRegister(LoginLimboRegisterEvent event) {
//        event.addOnJoinCallback(() -> {
//            Player player = event.getPlayer();
//            ProxyServer proxyServer = this.proxyServer;
//            authLimbo.spawnPlayer(player, new LimboSessionHandler() {
//                @Override
//                public void onSpawn(Limbo server, LimboPlayer limboPlayer) {
//                    limboPlayer.disableFalling();
//                    Player player = limboPlayer.getProxyPlayer();
//                    try {
//                        MireaUser user = userManager.getUserCache().get(player.getUsername());
//                        //todo зареган
//                    } catch (CacheLoader.InvalidCacheLoadException e) {
//                        //todo Не зареган
//                        System.out.println("Not registened!");
//                        proxyServer.getScheduler().buildTask(instance, () -> {
//                            limboPlayer.disconnect(mainServer);
//                        }).delay(1, TimeUnit.SECONDS).schedule();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        });
//    }
//
//    private void registerCommands() {
//        CommandManager commandManager = proxyServer.getCommandManager();
//        CommandMeta commandMeta = commandManager.metaBuilder("register")
//                .aliases("reg")
//                .plugin(this)
//                .build();
//        commandManager.register(commandMeta, new RegisterCommand());
//    }
}
