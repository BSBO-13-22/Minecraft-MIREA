package fun.mirea.purpur.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Syntax;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fun.mirea.common.server.MireaComponent;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.UserManager;
import fun.mirea.common.user.university.UniversityData;
import fun.mirea.purpur.MireaModulePlugin;
import fun.mirea.purpur.gui.ChestGui;
import fun.mirea.purpur.gui.ClickEvents;
import fun.mirea.purpur.gui.GuiManager;
import fun.mirea.purpur.gui.GuiSlot;
import fun.mirea.purpur.utility.FormatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GuiCommands extends BaseCommand {

    private final UserManager<Player> userManager;
    private final GuiManager guiManager;

    public GuiCommands(UserManager<Player> userManager, GuiManager guiManager) {
        this.userManager = userManager;
        this.guiManager = guiManager;
    }

    @CommandAlias("schedule|расписание")
    public void onScheduleCommand(MireaUser<Player> user) {
        Player player = user.getPlayer();
        if (user.hasUniversityData()) {
            player.sendActionBar(Component.text(FormatUtils.colorize("&7&oПожалуйста, подождите...")));
            UniversityData data = user.getUniversityData();
            getGroupSchedule(data.getGroupName()).thenCombineAsync(getCurrentWeek(), (jsonObject, currentWeek) -> {
                try {
                    ChestGui chestGui = new ChestGui(data.getGroupName() + " | Неделя " + currentWeek, 1);
                    chestGui.open(player);
                    player.sendActionBar(Component.empty());
                    JsonArray schedule = jsonObject.get("schedule").getAsJsonArray();
                    int i = 0;
                    for (JsonElement day : schedule) {
                        GuiSlot.GuiSlotBuilder slotBuilder = GuiSlot.builder()
                                .material(Material.LECTERN)
                                .amount(i + 1)
                                .displayName("&a&l" + FormatUtils.capitalize(day.getAsJsonObject().get("day").getAsString()));
                        List<String> lore = new ArrayList<>();
                        lore.add(" ");
                        JsonArray lessons = day.getAsJsonObject().getAsJsonArray(currentWeek % 2 == 0 ? "even" : "odd");
                        int k = 1;
                        for (JsonElement lesson : lessons) {
                            JsonArray lessonArray = lesson.getAsJsonArray();
                            if (!lessonArray.isEmpty()) {
                                JsonObject lessonObject = lessonArray.get(0).getAsJsonObject();
                                lore.add(" &8№" + k + ". &e" + lessonObject.get("name").getAsString() + " &7(" + lessonObject.get("type").getAsString() + ")");
                                JsonElement placeElement = lessonObject.get("place");
                                JsonElement tutorElement = lessonObject.get("tutor");
                                if (!placeElement.isJsonNull() || !tutorElement.isJsonNull())
                                    lore.add((tutorElement.isJsonNull() ? "      " : "      &6" + tutorElement.getAsString()) +
                                            (placeElement.isJsonNull() ? "" : " &7" + placeElement.getAsString() + ""));
                            }
                            k++;
                        }
                        lore.add(" ");
                        lore.add("&fИсточник: &3&nmirea.xyz");
                        slotBuilder.lore(lore);
                        chestGui.addSlot(i, slotBuilder.build());
                        i++;
                    }
                    guiManager.saveGui(player.getName(), "schedule", chestGui);
                    chestGui.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
        } else player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Для начала укажите свою группу! Используйте: §6/card group <группа> &8(Прим. БСБО-13-22)"));
    }

    private CompletableFuture<JsonObject> getGroupSchedule(String groupName) {
        return  CompletableFuture.supplyAsync(() -> {
            HttpClientBuilder clientBuilder = HttpClients.custom();
            clientBuilder.setDefaultHeaders(Arrays.asList(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())));
            CloseableHttpClient httpClient = clientBuilder.build();
            HttpGet httpGet = new HttpGet("https://mirea.xyz/api/v1.3/groups/certain?name=" + groupName);
            try {
                CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
                JsonObject schedule = JsonParser.parseReader(new InputStreamReader(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8))
                        .getAsJsonArray().get(0).getAsJsonObject();
                httpResponse.close();
                httpClient.close();
                return schedule;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    private CompletableFuture<Integer> getCurrentWeek() {
        return CompletableFuture.supplyAsync(() -> {
            HttpClientBuilder clientBuilder = HttpClients.custom();
            clientBuilder.setDefaultHeaders(Arrays.asList(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())));
            CloseableHttpClient httpClient = clientBuilder.build();
            HttpGet httpGet = new HttpGet("https://mirea.xyz/api/v1.3/time/week");
            try {
                CloseableHttpResponse response = httpClient.execute(httpGet);
                int currentWeek = Integer.parseInt(new BufferedReader(new InputStreamReader(response.getEntity().getContent())).readLine());
                response.close();
                httpClient.close();
                return currentWeek;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }
}
