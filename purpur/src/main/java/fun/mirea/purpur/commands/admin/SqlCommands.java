package fun.mirea.purpur.commands.admin;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import fun.mirea.common.format.MireaComponent;
import fun.mirea.common.format.Placeholder;
import fun.mirea.common.user.MireaUser;
import fun.mirea.database.ExecutionResult;
import fun.mirea.database.ExecutionState;
import fun.mirea.common.format.FormatUtils;
import fun.mirea.purpur.commands.BukkitMireaCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

@CommandAlias("sql")
public class SqlCommands extends BukkitMireaCommand {

    @Subcommand("execute")
    @Syntax("<запрос> <парметры...>")
    public void onExecuteSubcommand(MireaUser<Player> executor, String... params) throws ExecutionException, InterruptedException {
        Player player = executor.getPlayer();
        if (player.isOp()) {
            StringBuilder builder = new StringBuilder();
            for (String param : params)
                builder.append(" ").append(param);
            ExecutionResult<Void> executionResult = database.execute(builder.substring(1)).get();
            if (executionResult.state() == ExecutionState.SUCCESS)
                player.sendMessage(new MireaComponent(MireaComponent.Type.SUCCESS, "Запрос был успешно выполнен!"));
            else player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Сервер вернул ошибку: {error}",
                    new Placeholder("error", executionResult.error())));
        } else player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Для выполнения этой команды нужно обладать правами оператора!"));
    }

    @Subcommand("query")
    @Syntax("<запрос> <парметры...>")
    public void onQuerySubcommand(MireaUser<Player> executor, String... params) {
        Player player = executor.getPlayer();
        if (player.isOp()) {
            try {
                StringBuilder builder = new StringBuilder();
                for (String param : params)
                    builder.append(" ").append(param);
                ExecutionResult<ResultSet> executionResult = database.executeQuery(builder.substring(1)).get();
                if (executionResult.state() == ExecutionState.SUCCESS) {
                    ComponentBuilder<TextComponent, TextComponent.Builder> responseBuilder = Component.text();
                    ResultSet resultSet = executionResult.content();
                        while (resultSet.next()) {
                            ComponentBuilder<TextComponent, TextComponent.Builder> lineBuilder = Component.text();
                            int i = 1;
                            String line;
                            do {
                                try {
                                    line = resultSet.getString(i);
                                    lineBuilder.append(FormatUtils.colorize("&e"))
                                            .append(Component.text(line))
                                            .append(Component.space());
                                    i++;
                                } catch (SQLException e) {
                                    line = null;
                                }
                            } while (line != null);
                            responseBuilder.append(lineBuilder.append(Component.newline())
                                    .append(FormatUtils.colorize("&8&m"))
                                    .append(Component.text(" ".repeat(80))));
                        }
                        player.sendMessage(responseBuilder.build());
                    } else player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Сервер вернул ошибку: {error}",
                        new Placeholder("error", executionResult.error())));
            } catch (InterruptedException | ExecutionException | SQLException e) {
                player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Сервер вернул ошибку: {error}",
                        new Placeholder("error", e.getMessage())));
            }
        } else player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Для выполнения этой команды нужно обладать правами оператора!"));
    }
}
