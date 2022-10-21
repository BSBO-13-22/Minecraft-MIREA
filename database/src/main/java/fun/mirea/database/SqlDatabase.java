package fun.mirea.database;

import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class SqlDatabase implements Database {

    private Connection connection;
    @Getter
    private boolean isConnected;

    //jdbc:postgresql://localhost/test
    public SqlDatabase(String url, String user, String password, boolean ssl) {
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("ssl", String.valueOf(ssl));
        establishConnection(url, properties);
    }

    private CompletableFuture<Void> establishConnection(String url, Properties properties) {
        return CompletableFuture.runAsync(() -> {
            try {
                Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();
                if (connection == null || connection.isClosed())
                    connection = DriverManager.getConnection(url, properties);
                isConnected = true;
                execute("CREATE TABLE IF NOT EXISTS users (name VARCHAR(16) NOT NULL, data TEXT NOT NULL);");
            } catch (SQLException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<ResultSet> executeQuery(String query) {
        return CompletableFuture.supplyAsync(() -> {
            if (isConnected) {
                try {
                    Statement statement = connection.createStatement();
                    return statement.executeQuery(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    public CompletableFuture<Integer> executeUpdate(String update) {
        return CompletableFuture.supplyAsync(() -> {
            if (isConnected) {
                try {
                    Statement statement = connection.createStatement();
                    int result = statement.executeUpdate(update);
                    statement.close();
                    return result;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        });
    }

    public CompletableFuture<Boolean> execute(String execution) {
        return CompletableFuture.supplyAsync(() -> {
            if (isConnected) {
                try {
                    Statement statement = connection.createStatement();
                    boolean result = statement.execute(execution);
                    statement.close();
                    return result;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        });
    }
}
