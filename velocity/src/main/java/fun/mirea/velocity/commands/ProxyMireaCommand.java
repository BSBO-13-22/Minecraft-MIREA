package fun.mirea.velocity.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import com.velocitypowered.api.proxy.Player;
import fun.mirea.common.format.FormatUtils;
import fun.mirea.common.format.MireaComponent;
import fun.mirea.common.network.MineSkinApiClient;
import fun.mirea.common.network.MojangApiClient;
import fun.mirea.common.user.UserManager;
import fun.mirea.database.Database;
import fun.mirea.velocity.MireaModulePlugin;
import net.kyori.adventure.text.event.ClickEvent;

public class ProxyMireaCommand extends BaseCommand {

    protected static final UserManager<Player> userManager = MireaModulePlugin.getUserManager();
    protected static final Database database = MireaModulePlugin.getDatabase();
    protected static final MineSkinApiClient mineSkinApi = MireaModulePlugin.getMineSkinApi();
    protected static final MojangApiClient mojangApi = MireaModulePlugin.getMojangApi();

    @Override
    public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
        if (issuer.isPlayer()) {
            Player player = issuer.getIssuer();
            String command = cmd.getCommand() + (!cmd.getSyntaxText(issuer).isEmpty() ? " " + cmd.getSyntaxText(issuer) : "");
            player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Используйте: ").append(
                    FormatUtils.colorize("&6/" + command)
                            .hoverEvent(FormatUtils.colorize("&e▶ Использовать"))
                            .clickEvent(ClickEvent.suggestCommand("/" + command.split(" ")[0] + " "))));
        } else issuer.sendMessage("§cИспользуйте: §6/" + cmd.getCommand() + (!cmd.getSyntaxText(issuer).isEmpty() ? " " + cmd.getSyntaxText(issuer) : ""));
    }
}
