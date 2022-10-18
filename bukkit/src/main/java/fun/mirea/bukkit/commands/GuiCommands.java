package fun.mirea.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Syntax;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fun.mirea.bukkit.MireaModulePlugin;
import fun.mirea.bukkit.gui.ChestGui;
import fun.mirea.bukkit.gui.ClickEvents;
import fun.mirea.bukkit.gui.GuiManager;
import fun.mirea.bukkit.gui.GuiSlot;
import fun.mirea.bukkit.utility.FormatUtils;
import net.kyori.adventure.text.Component;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GuiCommands extends BaseCommand {

    private GuiManager guiManager = MireaModulePlugin.getGuiManager();

    @CommandAlias("menu|меню")
    public void onMenuCommand(Player player) {
        ChestGui chestGui = new ChestGui("Главное меню", 6);
        chestGui.addSlot(3, 3,
                GuiSlot.builder().material(Material.EMERALD).displayName("&aПривет!").lore(Arrays.asList("&fКак твои дела?", "&fКак настроение?", "&fПошёл нахуй)"))
                        .amount(1).enchanted(false).clickEvents(ClickEvents.builder().leftClickHandler(() -> {
                            player.sendMessage("Пшёл нахуй");
                            player.closeInventory();
                        }).build()).build());
        guiManager.saveGui(player.getName(), "main", chestGui);
        chestGui.open(player);
    }

    @CommandAlias("schedule|расписание")
    @Syntax("<группа>")
    public void onScheduleCommand(Player player, String[] args) throws ExecutionException, InterruptedException {
        if (args.length == 1) {
            CompletableFuture.supplyAsync(() -> {
                try {
                    player.sendActionBar(Component.text(FormatUtils.colorize("&eОбрабатываем запрос...")));
                    HttpClientBuilder clientBuilder = HttpClients.custom();
                    clientBuilder.setDefaultHeaders(Arrays.asList(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())));CloseableHttpClient httpClient = clientBuilder.build();
                    HttpGet httpGet = new HttpGet("https://mirea.xyz/api/v1.3/groups/certain?name=" + args[0]);
                    CloseableHttpResponse response = httpClient.execute(httpGet);
                    JsonArray jsonArray = JsonParser.parseReader(new InputStreamReader(response.getEntity().getContent())).getAsJsonArray();
                    response.close();
                    httpClient.close();
                    return jsonArray;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).thenAccept(response -> {
                if (response != null)
                    player.sendMessage(response.toString());
                else player.sendMessage(FormatUtils.colorize("&cУказанная Вами группа не найдена!"));
            }).get();
        }
    }
}
