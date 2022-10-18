package fun.mirea.common.user;

import lombok.Getter;

public class UniversityData {

    @Getter
    private String institute;
    @Getter
    private String groupName;
    @Getter
    private String groupSuffix;

    public UniversityData(String institute, String groupName, String groupSuffix) {
        this.institute = institute;
        this.groupName = groupName;
        this.groupSuffix = groupSuffix;
    }
}
