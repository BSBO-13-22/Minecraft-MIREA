package fun.mirea.purpur.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import fun.mirea.common.format.FormatUtils;
import fun.mirea.common.format.MireaComponent;
import fun.mirea.common.network.MireaApiClient;
import fun.mirea.common.user.UserManager;
import fun.mirea.database.Database;
import fun.mirea.purpur.MireaModulePlugin;
import fun.mirea.purpur.gui.GuiManager;
import fun.mirea.purpur.scoreboard.UniversityScoreboard;
import fun.mirea.purpur.teleport.TeleportManager;
import fun.mirea.purpur.warps.WarpManager;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;

public class BukkitMireaCommand extends BaseCommand {

    protected static final UserManager<Player> userManager = MireaModulePlugin.getUserManager();
    protected static final GuiManager guiManager = MireaModulePlugin.getGuiManager();
    protected static final WarpManager warpManager = MireaModulePlugin.getWarpManager();
    protected static final TeleportManager teleportManager = MireaModulePlugin.getTeleportManager();
    protected static final Database database = MireaModulePlugin.getDatabase();
    protected static final MireaApiClient mireaApi = MireaModulePlugin.getMireaApi();
    protected static final UniversityScoreboard universityScoreboard = MireaModulePlugin.getUniversityScoreboard();

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
