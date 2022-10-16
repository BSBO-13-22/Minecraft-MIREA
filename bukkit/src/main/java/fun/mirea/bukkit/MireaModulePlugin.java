package fun.mirea.bukkit;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import fun.mirea.bukkit.commands.GuiCommands;
import fun.mirea.bukkit.commands.HelpCommand;
import fun.mirea.bukkit.gui.GuiManager;
import fun.mirea.bukkit.handlers.ChatHandler;
import fun.mirea.bukkit.handlers.ConnectionHandler;
import fun.mirea.bukkit.handlers.GuiHandler;
import fun.mirea.common.multithreading.ThreadManager;
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

    @Override
    public void onEnable() {
        init();
    }

    private void init() {
        commandManager = new PaperCommandManager(this);
        threadManager = new ThreadManager(Executors.newFixedThreadPool(16));
        guiManager = new GuiManager();
        registerCommands(new HelpCommand(), new GuiCommands());
        registerHandlers(new ChatHandler(), new ConnectionHandler(), new GuiHandler());
    }

    private void registerCommands(BaseCommand... commands) {
        for (BaseCommand command : commands) commandManager.registerCommand(command);
    }

    private void registerHandlers(Listener... listeners) {
        for (Listener listener : listeners) getServer().getPluginManager().registerEvents(listener, this);
    }

}
