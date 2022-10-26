package fun.mirea.common.format;

import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;

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
