package fun.mirea.database;

import java.sql.ResultSet;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Database {

    CompletableFuture<Optional<ResultSet>> executeQuery(String query);

    CompletableFuture<Integer> executeUpdate(String update);

    CompletableFuture<Boolean> execute(String execution);
}
