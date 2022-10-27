package fun.mirea.common.format;

import lombok.Getter;

public class Placeholder {

    @Getter
    private final String tag;
    @Getter
    private final Object value;

    public Placeholder(String tag, Object value) {
        this.tag = tag;
        this.value = value;
    }
}
