package fun.mirea.bukkit;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import fun.mirea.bukkit.commands.GuiCommands;
import fun.mirea.bukkit.commands.HelpCommand;
import fun.mirea.bukkit.commands.ProfileCommands;
import fun.mirea.bukkit.gui.GuiManager;
import fun.mirea.bukkit.handlers.ChatHandler;
import fun.mirea.bukkit.handlers.ConnectionHandler;
import fun.mirea.bukkit.handlers.GuiHandler;
import fun.mirea.bukkit.scoreboard.UniversityScoreboard;
import fun.mirea.common.multithreading.ThreadManager;
import fun.mirea.common.user.UniversityData;
import fun.mirea.common.user.UserManager;
import fun.mirea.database.SqlDatabase;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;

public class MireaModulePlugin extends JavaPlugin {

    @Getter
    private static ThreadManager threadManager;

    @Getter
    private static PaperCommandManager commandManager;

    @Getter
    private static GuiManager guiManager;

    @Getter
    private static UserManager userManager;

    @Getter
    private static UniversityScoreboard universityScoreboard;

    @Override
    public void onEnable() {
        init();
    }

    private void init() {
        commandManager = new PaperCommandManager(this);
        threadManager = new ThreadManager(Executors.newFixedThreadPool(16));
        guiManager = new GuiManager();
        userManager = new UserManager(new SqlDatabase("jdbc:postgresql://localhost:5432/mirea", "root", "admin", false));
        universityScoreboard = new UniversityScoreboard(userManager);
        registerCommands(new HelpCommand(), new GuiCommands(), new ProfileCommands(userManager, universityScoreboard));
        registerHandlers(new ChatHandler(userManager), new ConnectionHandler(userManager, universityScoreboard), new GuiHandler(guiManager));
    }

    private void registerCommands(BaseCommand... commands) {
        for (BaseCommand command : commands) commandManager.registerCommand(command);
    }

    private void registerHandlers(Listener... listeners) {
        for (Listener listener : listeners) getServer().getPluginManager().registerEvents(listener, this);
    }

}
