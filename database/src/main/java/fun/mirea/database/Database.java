package fun.mirea.database;

import java.sql.ResultSet;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Database {

    boolean isConnectionEstablished();

    CompletableFuture<ExecutionResult<ResultSet>> executeQuery(String query);

    CompletableFuture<ExecutionResult<Integer>> executeUpdate(String update);

    CompletableFuture<ExecutionResult<Void>> execute(String execution);
}
