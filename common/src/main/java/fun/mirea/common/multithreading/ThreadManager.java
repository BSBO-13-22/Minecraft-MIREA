package fun.mirea.common.multithreading;

import lombok.Getter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public class ThreadManager {

    @Getter
    private final ExecutorService executorService;

    public ThreadManager(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public CompletableFuture<Void> runTaskAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executorService);
    }

}
