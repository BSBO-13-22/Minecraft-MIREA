package fun.mirea.common.user;

import lombok.Getter;
import lombok.Setter;

public class MireaUser {

    @Getter
    private final String name;

    @Getter
    @Setter
    private UniversityData universityData;

    public MireaUser(String name) {
        this.name = name;
    }

    public boolean hasUniversityData() {
        return universityData != null;
    }

    public void create(UserManager userManager) {
        userManager.createUser(this);
    }

    public void save(UserManager userManager) {
        userManager.updateUser(this);
    }

}
