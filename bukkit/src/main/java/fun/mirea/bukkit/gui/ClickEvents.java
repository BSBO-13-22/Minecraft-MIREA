package fun.mirea.bukkit.gui;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class ClickEvents {

    @Getter
    private Runnable leftClickHandler;
    @Getter
    private Runnable rightClickHandler;
    @Getter
    private Runnable leftShiftClickHandler;
    @Getter
    private Runnable rightShiftClickHandler;
    @Getter
    private Runnable middleClickHandler;

}
