package fun.mirea.common.user;

public interface PlayerProvider<T> {
    T providePlayer(String name);

}
