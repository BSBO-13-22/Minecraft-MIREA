package fun.mirea.common.server;

public interface ConsoleLogger {

    void log(String info);

    void error(StackTraceElement[] error);

    void error(String error);

}
