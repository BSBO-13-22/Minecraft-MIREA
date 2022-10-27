package fun.mirea.common.user;

import fun.mirea.common.server.SerializableLocation;
import fun.mirea.common.user.skin.SkinData;
import fun.mirea.common.user.university.UniversityData;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class MireaUser<T> {

    @Getter
    private final String name;

    @Getter
    @Setter
    private UniversityData universityData;

    @Getter
    @Setter
    private StudentName studentName;

    @Getter
    @Setter
    private SkinData skinData;

    @Setter
    private transient PlayerProvider<T> provider;

    @Getter @Setter
    private SerializableLocation homeLocation;

    public MireaUser(String name) {
        this.name = name;
        this.universityData = UniversityData.NULL;
        this.studentName = StudentName.NULL;
    }

    public T getPlayer() {
        return provider.providePlayer(name);
    }

    public boolean hasUniversityData() {
        return universityData != null;
    }

    public boolean hasStudentName() {
        return studentName != null;
    }

    public boolean hasSkinData() {
        return skinData != null;
    }

    public void create(UserManager<T> userManager) {
        userManager.createUser(this);
    }

    public void save(UserManager<T> userManager) {
        userManager.updateUser(this);
    }
}
