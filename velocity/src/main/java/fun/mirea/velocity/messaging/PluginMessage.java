package fun.mirea.velocity.messaging;

import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Builder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

@Builder
public class PluginMessage {

    private ChannelData channelData;
    private String player;
    private String service;
    private Collection<RegisteredServer> servers;
    private LinkedList<String> values;

    public void send() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(service);
            if (player != null) out.writeUTF(player);
            if (values != null) {
                for (String value : values)
                    out.writeUTF(value);
            }
            MinecraftChannelIdentifier identifier = MinecraftChannelIdentifier.create(channelData.channel(), channelData.subChannel());
            servers.forEach(connection -> connection.sendPluginMessage(identifier, b.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
