package fun.mirea.common.user.skin;

import lombok.Getter;
import lombok.Setter;

public class SkinData {

    @Getter
    @Setter
    private String value;
    @Getter
    @Setter
    private String signature;

    public SkinData() {
        this.value = "";
        this.signature = "";
    }
    public SkinData(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public boolean signed() {
        return signature != null;
    }
}
