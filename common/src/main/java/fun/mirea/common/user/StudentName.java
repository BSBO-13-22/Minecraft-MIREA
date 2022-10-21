package fun.mirea.common.user;

import lombok.Getter;
import lombok.Setter;

public final class StudentName {

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String middleName;

    @Getter
    @Setter
    private String lastName;

    public StudentName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public StudentName(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

}
