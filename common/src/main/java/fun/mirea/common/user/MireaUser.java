package fun.mirea.common.user;

import lombok.Getter;
import lombok.Setter;

public class MireaUser {

    @Getter
    private final String name;

    @Getter
    @Setter
    private Institute institute;

    public MireaUser(String name) {
        this.name = name;
    }

}
