package fun.mirea.purpur.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import fun.mirea.common.server.MireaComponent;
import fun.mirea.common.user.MireaUser;
import fun.mirea.database.Database;
import fun.mirea.database.ExecutionResult;
import fun.mirea.database.ExecutionState;
import fun.mirea.database.SqlDatabase;
import fun.mirea.purpur.utility.FormatUtils;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

@CommandAlias("sql")
public class SqlCommands extends BaseCommand {

    private final SqlDatabase database;

    public SqlCommands(SqlDatabase database) {
        this.database = database;
    }

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
                    new MireaComponent.Placeholder("error", executionResult.error())));
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
                    StringBuilder responseBuilder = new StringBuilder();
                    ResultSet resultSet = executionResult.content();
                        while (resultSet.next()) {
                            StringBuilder lineBuilder = new StringBuilder();
                            int i = 1;
                            String line = null;
                            do {
                                try {
                                    line = resultSet.getString(i);
                                    lineBuilder.append(FormatUtils.colorize("&e")).append(line).append(" ");
                                    i++;
                                } catch (SQLException e) {
                                    line = null;
                                }
                            } while (line != null);
                            responseBuilder.append(lineBuilder.append(FormatUtils.colorize("\n&8&m")).append(" ".repeat(80)));
                        }
                        if (!responseBuilder.isEmpty())
                            player.sendMessage(responseBuilder.toString());
                        else player.sendMessage(FormatUtils.colorize("&eСервер вернул пустой ответ."));
                    } else player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Сервер вернул ошибку: {error}",
                        new MireaComponent.Placeholder("error", executionResult.error())));
            } catch (InterruptedException | ExecutionException | SQLException e) {
                player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Сервер вернул ошибку: {error}",
                        new MireaComponent.Placeholder("error", e.getMessage())));
            }
        } else player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Для выполнения этой команды нужно обладать правами оператора!"));
    }
}
