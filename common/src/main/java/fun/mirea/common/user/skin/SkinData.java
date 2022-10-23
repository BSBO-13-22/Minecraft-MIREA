package fun.mirea.common.user.skin;

public class SkinData {

    private final String value;
    private final String signature;
    public SkinData(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public String getValue() {
        return value;
    }

    public String getSignature() {
        return signature;
    }

    public boolean signed() {
        return signature != null;
    }
}
