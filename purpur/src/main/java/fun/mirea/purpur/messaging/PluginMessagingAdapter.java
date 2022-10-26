package fun.mirea.purpur.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fun.mirea.common.user.UserManager;
import fun.mirea.common.user.skin.SkinData;
import fun.mirea.purpur.MireaModulePlugin;
import fun.mirea.purpur.utility.SkinApplier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class PluginMessagingAdapter implements PluginMessageListener {

    private final MireaModulePlugin plugin;
    private final UserManager<Player> userManager;

    public PluginMessagingAdapter(MireaModulePlugin plugin, UserManager<Player> userManager) {
        this.plugin = plugin;
        this.userManager = userManager;
    }

    public void registerChannel(String channel) {
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel, this);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player plyer, byte[] message) {
        if (channel.equals("mirea:user")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subChannel = in.readUTF();
            switch (subChannel) {
                case "updateUser" -> {
                    String name = in.readUTF();
                    userManager.getCache().asMap().remove(name);
                    userManager.getCache().refresh(name);
                }
                case "refreshSkin" -> {
                    String name = in.readUTF();
                    String value = in.readUTF();
                    String signature = in.readUTF();
                    SkinApplier skinApplier = new SkinApplier(Bukkit.getPlayerExact(name));
                    skinApplier.process(new SkinData(value, signature));
                }
            }
        }
    }
}
