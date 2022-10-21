package fun.mirea.purpur.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fun.mirea.purpur.scoreboard.UniversityScoreboard;
import fun.mirea.purpur.utility.FormatUtils;
import fun.mirea.common.user.university.Institute;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.university.UniversityData;
import fun.mirea.common.user.UserManager;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@CommandAlias("card")
public class ProfileCommands extends BaseCommand {

    private final UserManager<Player> userManager;
    private final UniversityScoreboard scoreboard;

    public ProfileCommands(UserManager<Player> userManager, UniversityScoreboard scoreboard) {
        this.userManager = userManager;
        this.scoreboard = scoreboard;
    }

    @Subcommand("setgroup")
    @Syntax("<группа>")
    public void onGroupSubcommand(MireaUser<Player> user, String group) throws ExecutionException, InterruptedException {
        Player player = user.getPlayer();
        CompletableFuture.supplyAsync(() -> {
            try {
                player.sendMessage(FormatUtils.colorize("&7&oОбрабатываем запрос..."));
                HttpClientBuilder clientBuilder = HttpClients.custom();
                clientBuilder.setDefaultHeaders(Arrays.asList(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())));
                CloseableHttpClient httpClient = clientBuilder.build();
                HttpGet httpGet = new HttpGet("https://mirea.xyz/api/v1.3/groups/certain?name=" + group.toUpperCase());
                CloseableHttpResponse response = httpClient.execute(httpGet);
                String jsonResponse = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8)).readLine();
                response.close();
                httpClient.close();
                if (!jsonResponse.equals("[]")) {
                    jsonResponse = jsonResponse.substring(1, jsonResponse.length() - 1);
                    JsonObject course = JsonParser.parseString(jsonResponse).getAsJsonObject();
                    return new UniversityData(
                            course.get("unitName").getAsString(),
                            course.get("groupName").getAsString(),
                            course.get("groupSuffix").getAsString()
                    );
                }
                return null;
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
                return null;
            }
        }).thenAcceptAsync(universityData -> {
            if (universityData != null) {
                user.setUniversityData(universityData);
                user.save(userManager);
                player.sendMessage(FormatUtils.colorize(
                        "&r\n&aВаша карточка студента: " +
                                "\n&r\n &8| &7Институт: &e" + Institute.of(universityData.getInstitute()) +
                                "\n &8| &7Группа: &f" + universityData.getGroupName() +
                                "\n &8| &7Аббревиатура: &f" + universityData.getGroupSuffix() + "\n&r"));
                scoreboard.updatePlayer(player);
            } else player.sendMessage(FormatUtils.colorize("&cУказанная Вами группа не найдена!"));
        }).get();
    }

    @HelpCommand
    public static void onHelp(CommandSender sender) {
        sender.sendMessage(FormatUtils.colorize("&cИспользуйте: &6/card <группа/имя>"));
    }
}
