package fun.mirea.purpur;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import fun.mirea.common.server.Configuration;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.server.ConsoleLogger;
import fun.mirea.purpur.commands.*;
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
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MireaModulePlugin extends JavaPlugin {

    @Getter
    private static MireaModulePlugin instance;

    @Getter
    private static Configuration configuration;

    @Getter
    private static SqlDatabase database;

    @Getter
    private static File tempDirectory;

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
        registerCommands(
                new HelpCommand(),
                new GuiCommands(userManager, guiManager),
                new ProfileCommands(userManager, universityScoreboard),
                new ResourcePackCommand(),
                new SqlCommands(database));
        registerHandlers(new ChatHandler(userManager), new ConnectionHandler(userManager, universityScoreboard), new GuiHandler(guiManager), new PlayerHandler());
    }

    private void init() {
        instance = this;
        commandManager = new PaperCommandManager(this);
        database = new SqlDatabase("jdbc:postgresql://" + configuration.getDbHost() + ":" + configuration.getDbPort() + "/" + configuration.getDbName(),
                configuration.getDbUser(), configuration.getDbUserPassword(), false);
        threadManager = new ThreadManager(Executors.newFixedThreadPool(16));
        guiManager = new GuiManager();
        userManager = new UserManager<>(Bukkit::getPlayerExact, database, new ConsoleLogger() {
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
        universityScoreboard = new UniversityScoreboard(userManager);
    }

    private void createFiles() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists())
            dataFolder.mkdir();
        tempDirectory = new File(getDataFolder() + File.separator + "temp");
        if (!tempDirectory.exists())
            tempDirectory.mkdir();
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
        commandManager.getCommandContexts().registerIssuerAwareContext(MireaUser.class, context -> {
            try {
                Optional<MireaUser<Player>> optional = userManager.getUserCache().get(context.getSender().getName());
                if (optional.isPresent())
                    return optional.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        });
        commandManager.getCommandCompletions().registerAsyncCompletion("users", completionContext -> {
            Collection<String> users = new ArrayList<>();
            try {
                userManager.getAllUsers().get().forEach(user -> users.add(user.getName()));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return users;
        });
        commandManager.getCommandCompletions().registerAsyncCompletion("mireaGroups", commandManager -> {
            try {
                return CompletableFuture.supplyAsync(() -> {
                    Collection<String> groups = new ArrayList<>();
                    HttpClientBuilder clientBuilder = HttpClients.custom();
                    clientBuilder.setDefaultHeaders(List.of(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())));
                    CloseableHttpClient httpClient = clientBuilder.build();
                    HttpGet httpGet = new HttpGet("https://mirea.xyz/api/v1.3/groups/all");
                    try {
                        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
                        JsonArray groupsArray = JsonParser.parseReader(new InputStreamReader(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8)).getAsJsonArray();
                        for (JsonElement groupElement : groupsArray) groups.add(groupElement.getAsJsonObject().get("groupName").getAsString());
                        httpResponse.close();
                        httpClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return groups;
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return ImmutableList.of();
            }
        });
        for (BaseCommand command : commands) commandManager.registerCommand(command);
    }

    private void registerHandlers(Listener... listeners) {
        for (Listener listener : listeners) getServer().getPluginManager().registerEvents(listener, this);
    }

}
