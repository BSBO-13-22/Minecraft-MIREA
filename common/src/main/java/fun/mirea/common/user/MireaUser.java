package fun.mirea.common.user;

import com.velocitypowered.api.proxy.ProxyServer;
import fun.mirea.common.user.university.UniversityData;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class MireaUser<T> {

    @Getter
    private final String name;

    @Getter
    @Setter
    private UniversityData universityData;

    @Getter
    @Setter
    private StudentName studentName;

    @Setter
    private transient PlayerProvider<T> provider;

    public MireaUser(String name) {
        this.name = name;
    }

    public T getPlayer() {
        return provider.providePlayer(name);
    }

    public boolean hasUniversityData() {
        return universityData != null;
    }

    protected void create(UserManager<T> userManager) {
        userManager.createUser(this);
    }

    public void save(UserManager<T> userManager) {
        userManager.updateUser(this);
    }

}
