package fun.mirea.purpur;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandContexts;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.PaperCommandManager;
import fun.mirea.common.server.Configuration;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.PlayerProvider;
import fun.mirea.purpur.commands.GuiCommands;
import fun.mirea.purpur.commands.HelpCommand;
import fun.mirea.purpur.commands.ProfileCommands;
import fun.mirea.purpur.gui.GuiManager;
import fun.mirea.purpur.handlers.ChatHandler;
import fun.mirea.purpur.handlers.ConnectionHandler;
import fun.mirea.purpur.handlers.GuiHandler;
import fun.mirea.purpur.handlers.PlayerHandler;
import fun.mirea.purpur.scoreboard.UniversityScoreboard;
import fun.mirea.common.multithreading.ThreadManager;
import fun.mirea.common.user.UserManager;
import fun.mirea.database.SqlDatabase;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MireaModulePlugin extends JavaPlugin {

    @Getter
    private static MireaModulePlugin instance;

    @Getter
    private static Configuration configuration;

    @Getter
    private static ThreadManager threadManager;

    @Getter
    private static PaperCommandManager commandManager;

    @Getter
    private static GuiManager guiManager;

    @Getter
    private static UserManager<Player> userManager;

    @Getter
    private static UniversityScoreboard universityScoreboard;

    @Override
    public void onEnable() {
        createFiles();
        init();
        registerCommands(new HelpCommand(), new GuiCommands(userManager, guiManager), new ProfileCommands(userManager, universityScoreboard));
        registerHandlers(new ChatHandler(userManager), new ConnectionHandler(userManager, universityScoreboard), new GuiHandler(guiManager), new PlayerHandler());
    }

    private void init() {
        instance = this;
        commandManager = new PaperCommandManager(this);
        threadManager = new ThreadManager(Executors.newFixedThreadPool(16));
        guiManager = new GuiManager();
        userManager = new UserManager<>(Bukkit::getPlayerExact, new SqlDatabase("jdbc:postgresql://" + configuration.getDbHost() + ":" + configuration.getDbPort() + "/" + configuration.getDbName(),
                        configuration.getDbUser(), configuration.getDbUserPassword(), false));
        universityScoreboard = new UniversityScoreboard(userManager);
    }

    private void createFiles() {
        File dataFolder = getDataFolder();
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

    private void registerCommands(BaseCommand... commands) {
        commandManager.getCommandContexts().registerIssuerAwareContext(MireaUser.class, context -> userManager.getUserCache().getUnchecked(context.getPlayer().getName()));
        commandManager.getCommandContexts().registerOptionalContext(MireaUser.class, context -> userManager.getUserCache().getUnchecked(context.getPlayer().getName()));
        for (BaseCommand command : commands) commandManager.registerCommand(command);
    }

    private void registerHandlers(Listener... listeners) {
        for (Listener listener : listeners) getServer().getPluginManager().registerEvents(listener, this);
    }

}
