package fun.mirea.purpur.commands.user;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import fun.mirea.common.user.MireaUser;
import fun.mirea.purpur.utility.ResourcePackInstaller;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

@CommandAlias("resourcepack")
public class ResourcePackCommand extends BaseCommand {

    @Subcommand("download")
    @Syntax("<ссылка>")
    public void onResourcePackCommand(MireaUser<Player> user, String url) {
        if (url != null) {
            ResourcePackInstaller installer = new ResourcePackInstaller(url);
            installer.install(user.getPlayer(), Component.text(url, TextColor.fromHexString("#55FF55")).decorate(TextDecoration.UNDERLINED));
        }
    }
}
