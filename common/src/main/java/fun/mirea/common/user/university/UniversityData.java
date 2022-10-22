package fun.mirea.common.user.university;

import lombok.Getter;
import lombok.Setter;

public final class UniversityData {

    public static UniversityData NULL = new UniversityData();

    @Getter
    @Setter
    private String institute;
    @Getter
    @Setter
    private String groupName;
    @Getter
    @Setter
    private String groupSuffix;

    public UniversityData(String institute, String groupName, String groupSuffix) {
        this.institute = institute;
        this.groupName = groupName;
        this.groupSuffix = groupSuffix;
    }

    private UniversityData() {
        this.institute = "UNKNOWN";
        this.groupName = "Нет данных";
        this.groupSuffix = "";
    }
}
