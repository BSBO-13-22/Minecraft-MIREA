package fun.mirea.database;

import lombok.NonNull;

public record ExecutionResult<T>(@NonNull ExecutionState state, String error, StackTraceElement[] stackTrace, T content) {

}

