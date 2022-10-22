package fun.mirea.common.user;

import lombok.Getter;
import lombok.Setter;

public final class StudentName {

    public static final StudentName NULL = new StudentName();

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String middleName;

    @Getter
    @Setter
    private String lastName;

    private StudentName() {
        this.firstName = "Не";
        this.lastName = "указано";
    }

    public StudentName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public StudentName(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        if (firstName != null && middleName != null && lastName != null)
            return firstName + " " + middleName + " " + lastName;
        else if (firstName != null && lastName != null)
            return firstName + " " + lastName;
        else return "Не указано";
    }
}
