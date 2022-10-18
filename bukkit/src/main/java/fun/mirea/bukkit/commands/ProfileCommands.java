package fun.mirea.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Syntax;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fun.mirea.bukkit.MireaModulePlugin;
import fun.mirea.bukkit.scoreboard.UniversityScoreboard;
import fun.mirea.bukkit.utility.FormatUtils;
import fun.mirea.common.user.Institute;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.UniversityData;
import fun.mirea.common.user.UserManager;
import net.kyori.adventure.text.Component;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ProfileCommands extends BaseCommand {

    private final UserManager userManager;
    private final UniversityScoreboard scoreboard;

    public ProfileCommands(UserManager userManager, UniversityScoreboard scoreboard) {
        this.userManager = userManager;
        this.scoreboard = scoreboard;
    }

    @CommandAlias("setgroup")
    @Syntax("<group>")
    public void setGroupCommand(Player player, String[] args) throws ExecutionException, InterruptedException {
        if (args.length == 1) {
            CompletableFuture.supplyAsync(() -> {
                try {
                    player.sendMessage(FormatUtils.colorize("&7&oОбрабатываем запрос..."));
                    HttpClientBuilder clientBuilder = HttpClients.custom();
                    clientBuilder.setDefaultHeaders(Arrays.asList(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())));
                    CloseableHttpClient httpClient = clientBuilder.build();
                    HttpGet httpGet = new HttpGet("https://mirea.xyz/api/v1.3/groups/certain?name=" + args[0].toUpperCase());
                    CloseableHttpResponse response = httpClient.execute(httpGet);
                    String jsonResponse = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8)).readLine();
                    response.close();
                    httpClient.close();
                    if (!jsonResponse.equals("[]")) {
                        jsonResponse = jsonResponse.substring(1, jsonResponse.length() - 1);
                        JsonObject course = JsonParser.parseString(jsonResponse).getAsJsonObject();
                        UniversityData universityData = new UniversityData(
                                course.get("unitName").getAsString(),
                                course.get("groupName").getAsString(),
                                course.get("groupSuffix").getAsString()
                        );
                        return universityData;
                    }
                    return null;
                } catch (IOException | IllegalArgumentException e) {
                    return null;
                }
            }).thenAccept(universityData -> {
                if (universityData != null) {
                    try {
                        MireaUser user = userManager.getUserCache().get(player.getName());
                        user.setUniversityData(universityData);
                        user.save(userManager);
                        player.sendMessage(FormatUtils.colorize(
                                "&r\n&aВаша карточка студента: " +
                                        "\n&r\n &8| &7Институт: &e" + Institute.of(universityData.getInstitute()) +
                                        "\n &8| &7Группа: &f" + universityData.getGroupName() +
                                        "\n &8| &7Аббревиатура: &f" + universityData.getGroupSuffix() + "\n&r"));
                        scoreboard.updatePlayer(player);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        player.sendMessage(FormatUtils.colorize("&cНе удалось найти Ваш аккаунт в базе данных! Пожалуйста, сообщите об этом разработчику."));
                    }
                } else player.sendMessage(FormatUtils.colorize("&cУказанная Вами группа не найдена!"));
            }).get();
        } else player.sendMessage(FormatUtils.colorize("&cИспользуйте: &6/setgroup <группа>"));
    }

}
