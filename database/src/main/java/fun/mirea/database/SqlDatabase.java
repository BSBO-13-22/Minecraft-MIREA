package fun.mirea.database;

import java.sql.*;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class SqlDatabase implements Database {


    private final String url;
    private final Properties properties;
    private Connection connection;

    public SqlDatabase(String url, String user, String password, boolean ssl) {
        this.url = url;
        properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("ssl", String.valueOf(ssl));
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        execute("CREATE TABLE IF NOT EXISTS users (name VARCHAR(16) NOT NULL, data TEXT NOT NULL);");
    }

    private CompletableFuture<Connection> establishConnection() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (connection == null || connection.isClosed()) {
                    connection = DriverManager.getConnection(url, properties);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return connection;
        });
    }

    @Override
    public boolean isConnectionEstablished() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public CompletableFuture<ExecutionResult<ResultSet>> executeQuery(String query) {
        return establishConnection().thenApplyAsync(established -> {
            try {
                Statement statement = established.createStatement();
                return new ExecutionResult<>(ExecutionState.SUCCESS, null, null, statement.executeQuery(query));
            } catch (SQLException e) {
                return new ExecutionResult<>(ExecutionState.FAILED, e.getMessage(), e.getStackTrace(), null);
            }
        });
    }

    @Override
    public CompletableFuture<ExecutionResult<Integer>> executeUpdate(String update) {
        return establishConnection().thenApplyAsync(established -> {
            try {
                Statement statement = established.createStatement();
                int result = statement.executeUpdate(update);
                statement.close();
                return new ExecutionResult<>(ExecutionState.SUCCESS, null, null, result);
            } catch (SQLException e) {
                e.printStackTrace();
                return new ExecutionResult<>(ExecutionState.FAILED, e.getMessage(), e.getStackTrace(), 0);
            }
        });
    }

    @Override
    public CompletableFuture<ExecutionResult<Void>> execute(String execution) {
        return establishConnection().thenApplyAsync(established -> {
            try {
                Statement statement = established.createStatement();
                statement.execute(execution);
                statement.close();
                return new ExecutionResult<>(ExecutionState.SUCCESS, null, null, null);
            } catch (SQLException e) {
                return new ExecutionResult<>(ExecutionState.FAILED, e.getMessage(), e.getStackTrace(), null);
            }
        });
    }
}
