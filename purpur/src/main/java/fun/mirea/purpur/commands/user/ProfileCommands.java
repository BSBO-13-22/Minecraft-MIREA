package fun.mirea.purpur.commands.user;

import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.HelpCommand;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fun.mirea.common.format.MireaComponent;
import fun.mirea.common.format.Placeholder;
import fun.mirea.common.user.StudentName;
import fun.mirea.purpur.commands.BukkitMireaCommand;
import fun.mirea.common.format.FormatUtils;
import fun.mirea.common.user.university.Institute;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.university.UniversityData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
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
public class ProfileCommands extends BukkitMireaCommand {

    @Subcommand("profile")
    @Syntax("[никнейм]")
    @CommandCompletion("@users")
    public void onProfileSubcommand(MireaUser<Player> user, String nickname) throws ExecutionException {
        if (nickname == null)
            nickname = user.getName();
        userManager.getCache().get(nickname).ifPresent(target -> {
            ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text().append(Component.newline())
                    .append(FormatUtils.colorize("&a&lКарточка &2&l" + target.getName()));
            if (target.hasUniversityData()) {
                UniversityData data = target.getUniversityData();
                Institute institute = Institute.of(data.getInstitute());
                builder.append(Component.newline()).append(Component.newline()).append(Component.space())
                        .append(FormatUtils.colorize("&8| &7Головной институт:"))
                        .append(Component.space())
                        .append(Component.text(institute.getPrefix(), TextColor.fromHexString(institute.getColorScheme())))
                        .append(Component.newline()).append(Component.space())
                        .append(FormatUtils.colorize("&8| &7Группа: &f" + data.getGroupName() + " &8(" + data.getGroupSuffix() + ")"));
            }
            if (target.hasStudentName()) {
                builder.append(Component.newline()).append(Component.space())
                        .append(FormatUtils.colorize("&8| &7Имя: &f" + target.getStudentName()));
            }
            builder.append(Component.newline());
            user.getPlayer().sendMessage(builder.build());
        });
    }

    @Subcommand("group")
    @Syntax("<группа>")
    @CommandCompletion("@mireaGroups")
    public void onGroupSubcommand(MireaUser<Player> user, String group) throws ExecutionException, InterruptedException {
        Player player = user.getPlayer();
        if (!user.hasUniversityData() || !user.getUniversityData().getGroupName().equals(group.toUpperCase())) {
            CompletableFuture.supplyAsync(() -> {
                try {
                    player.sendMessage(new MireaComponent(MireaComponent.Type.INFO,"Обрабатываем запрос..."));
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
                    return null;
                }
            }).thenAcceptAsync(universityData -> {
                if (universityData != null) {
                    user.setUniversityData(universityData);
                    user.save(userManager);
                    player.sendMessage(FormatUtils.colorize(
                            "&r\n&aИнформация о студенте: " +
                                    "\n&r\n &8| &7Институт: " + Institute.of(universityData.getInstitute()) +
                                    "\n &8| &7Группа: &f" + universityData.getGroupName() +
                                    "\n &8| &7Аббревиатура: &f" + universityData.getGroupSuffix() + "\n&r"));
                    universityScoreboard.updatePlayer(user);
                } else player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Не удалось найти указанную группу!"));
            }).get();
        } else  player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Вы уже состоите в группе {group}&с!",
                new Placeholder("group", group.toUpperCase())));
    }

    @Subcommand("name")
    @Syntax("<имя> <фамилия> [отчество]")
    public void onNameCommand(MireaUser<Player> user, String firstName, String lastName, @Optional String middleName) {
        Player player = user.getPlayer();
        StudentName studentName = null;
        if (firstName != null && middleName != null && lastName != null ) {
            studentName = new StudentName(firstName, middleName, lastName);
        } else if (firstName != null && lastName != null) {
            studentName = new StudentName(firstName, lastName);
        }
        if (studentName != null) {
            player.sendMessage(new MireaComponent(MireaComponent.Type.SUCCESS, "Имя успешно установлено: &e{name}",
                    new Placeholder("name", studentName)));
            user.setStudentName(studentName);
            user.save(userManager);
        } else player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Нужно обязательно указать фамилию и имя!"));
    }

    @HelpCommand
    public static void onHelp(CommandSender sender) {
        TextComponent component = Component.text()
                .append(Component.newline()).append(Component.space())
                .append(FormatUtils.colorize("&8| &7Установить группу: &f/card group <группа>\n&8  прим. /card group БСБО-13-22"))
                .append(Component.newline()).append(Component.newline())
                .append(Component.space()).append(FormatUtils.colorize("&8| &7Установить имя: &f/card name <Ф> <И> [О]\n&8  прим. /card name Иван Иванов Иванович"))
                .append(Component.newline()).append(Component.newline())
                .append(Component.space()).append(FormatUtils.colorize("&8| &7Посмотреть карточу: &f/card profile [никйнем]"))
                .append(Component.newline()).append(Component.space())
                .build();
        sender.sendMessage(component);
    }
}
