package fun.mirea.purpur.utility;

import fun.mirea.purpur.MireaModulePlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ResourcePackInstaller {

    private final String url;
    private byte[] hash;

    public ResourcePackInstaller(String url) {
        this.url = url;
        try {
            File downloadedFile = downloadFile().get();
            this.hash = calculateHash(downloadedFile);
            downloadedFile.delete();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            this.hash = new byte[8192];
        }
    }

    public void install(Player player, Component message) {
        player.setResourcePack(url, hash, message, false);
    }

    private CompletableFuture<File> downloadFile() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL packUrl = new URL(url);
                ReadableByteChannel rbc = Channels.newChannel(packUrl.openStream());
                File packFile = new File(MireaModulePlugin.getTempDirectory() + File.separator + UUID.randomUUID().toString().replace("-", "") + ".zip");
                FileOutputStream fos = new FileOutputStream(packFile);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();
                rbc.close();
                return packFile;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static byte[] calculateHash(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            InputStream fis = Files.newInputStream(file.toPath());
            int n = 0;
            byte[] buffer = new byte[8192];
            while (n != -1) {
                n = fis.read(buffer);
                if (n > 0) {
                    digest.update(buffer, 0, n);
                }
            }
            fis.close();
            return digest.digest();
        } catch (Exception exception) {
            exception.printStackTrace();
            return new byte[]{};
        }
    }
}
