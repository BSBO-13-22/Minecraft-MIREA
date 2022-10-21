package fun.mirea.common.server;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Configuration {

    public static CompletableFuture<Configuration> fromFile(File file) {
        return CompletableFuture.supplyAsync(() -> new Toml().read(file).to(Configuration.class));
    }

    @Getter
    private final String dbHost;
    @Getter
    private final int dbPort;
    @Getter
    private final String dbName;
    @Getter
    private final String dbUser;
    @Getter
    private final String dbUserPassword;

    public Configuration(String dbHost, int dbPort, String dbName, String dbUser, String dbUserPassword) {
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbUserPassword = dbUserPassword;
    }

    public CompletableFuture<Void> toFile(String path) {
        return CompletableFuture.runAsync(() -> {
            try {
                File file = new File(path);
                if (!file.exists())
                    file.createNewFile();
                new TomlWriter().write(this, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
