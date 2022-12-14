package fun.mirea.velocity.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.GameProfile;
import fun.mirea.common.format.FormatUtils;
import fun.mirea.common.format.MireaComponent;
import fun.mirea.common.format.Patterns;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.UserManager;
import fun.mirea.common.user.skin.SkinData;
import fun.mirea.velocity.MireaModulePlugin;
import fun.mirea.common.network.MineSkinApiClient;
import fun.mirea.common.network.MojangApiClient;
import fun.mirea.velocity.messaging.ChannelData;
import fun.mirea.velocity.messaging.PluginMessage;
import net.kyori.adventure.text.event.ClickEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
public class SkinCommand extends ProxyMireaCommand {

    @CommandAlias("skin")
    @Syntax("<никнейм> | url <ссылка>")
    public void onSkinCommand(MireaUser<Player> sender, String[] args) {
        if (args.length == 1 && !args[0].equalsIgnoreCase("url"))
            skinFromLicenseOption(sender, args[0]);
        else if (args.length == 1 && args[0].equalsIgnoreCase("url"))
            sender.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Используйте: &6/skin url <ссылка>")
                    .hoverEvent(FormatUtils.colorize("&e▶ Использовать"))
                    .clickEvent(ClickEvent.suggestCommand("/skin url ")));
        else if (args.length == 2 && args[0].equalsIgnoreCase("url"))
            skinFromImageOption(sender, args[1]);
        else sender.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Используйте: &6/skin <никнейм | url>")
                    .hoverEvent(FormatUtils.colorize("&e▶ Использовать"))
                    .clickEvent(ClickEvent.suggestCommand("/skin ")));
    }

    public void skinFromLicenseOption(MireaUser<Player> sender, String nickname) {
        sender.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.INFO, "Обрабатываем запрос..."));
        mojangApi.getLicenseId(nickname).thenAcceptAsync(optionalId -> {
            optionalId.ifPresentOrElse(uuid -> {
                mojangApi.getLicenseSkin(uuid).thenAcceptAsync(optionalSkin -> {
                    optionalSkin.ifPresentOrElse(skinData -> {
                        setUserSkin(sender, skinData);
                    }, () -> sender.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Не удалось обновить скин.")));
                });
            }, () -> sender.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Лицензионный аккаунт с таким именем не найден!")));
        });
    }

    public void skinFromImageOption(MireaUser<Player> sender, String url) {
        sender.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.INFO, "Обрабатываем запрос..."));
        if (Patterns.URL.matcher(url).matches()) {
            CompletableFuture.supplyAsync(() -> {
                try {
                    BufferedImage skinImage = ImageIO.read(new URL(url));
                    File skinFile = new File(UUID.randomUUID().toString().replace("-", "") + ".png");
                    ImageIO.write(skinImage, "png", skinFile);
                    return skinFile;
                } catch (IOException ignored) {
                    return null;
                }
            }).thenAcceptAsync(skinFile -> {
                if (skinFile != null) {
                    try {
                        SkinData skinData = mineSkinApi.uploadSkin(skinFile).get();
                        skinFile.delete();
                        setUserSkin(sender, skinData);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                } else sender.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Не удалось прочитать файл по указанной ссылке!"));
            });
        } else sender.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Ссылка на файл введена некорректно!"));
    }

    private void setUserSkin(MireaUser<Player> user, SkinData skinData) {
        GameProfile.Property skinProperty = new GameProfile.Property("textures", skinData.getValue(), skinData.getSignature());
        user.getPlayer().setGameProfileProperties(Collections.singletonList(skinProperty));
        user.setSkinData(skinData);
        user.save(userManager);
        ChannelData channelData = new ChannelData("mirea", "user");
        Collection<RegisteredServer> servers = MireaModulePlugin.getInstance().getProxyServer().getAllServers();
        PluginMessage updateUserMessage = PluginMessage.builder()
                .channelData(channelData)
                .service("updateUser")
                .player(user.getName())
                .servers(servers)
                .build();
        updateUserMessage.send();
        PluginMessage refreshSkinMessage = PluginMessage.builder()
                .channelData(channelData)
                .service("refreshSkin")
                .player(user.getName())
                .servers(servers)
                .values(new LinkedList<>(Arrays.asList(skinData.getValue(), skinData.getSignature())))
                .build();
        refreshSkinMessage.send();
        user.getPlayer().sendMessage(new MireaComponent(MireaComponent.Type.SUCCESS, "Ваш скин был успешно обновлён! Для его корректного отображения потребуется переподключиться к серверу."));
    }
}
