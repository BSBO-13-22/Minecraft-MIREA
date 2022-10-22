package fun.mirea.purpur.utility;

import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.university.Institute;
import fun.mirea.common.user.university.UniversityData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class ComponentUtils {

    public static TextComponent createPrefix(UniversityData data, boolean hoverEvents) {
        ComponentBuilder<TextComponent, TextComponent.Builder> prefixBuilder = Component.text();
        if (data != null && Institute.of(data.getInstitute()) != Institute.UNKNOWN) {
            Institute institute = Institute.of(data.getInstitute());
            prefixBuilder.append(Component.text(institute.getPrefix(), TextColor.fromHexString(institute.getColorScheme())).decorate(TextDecoration.BOLD));
            if (hoverEvents) {
                TextComponent hoverPrefixComponent = Component.text(FormatUtils.colorize("&7Институт: &f" + data.getInstitute()
                        + "\n&7Группа: &f" + data.getGroupName() + " &8(" + data.getGroupSuffix() + ")"));
                prefixBuilder.hoverEvent(HoverEvent.showText(hoverPrefixComponent));
            }
        } else prefixBuilder.append(Component.empty().color(TextColor.fromHexString(Institute.UNKNOWN.getColorScheme())));
        return prefixBuilder.build();
    }

    public static TextComponent createDisplayName(MireaUser<Player> user, boolean hoverEvents) {
        ComponentBuilder<TextComponent, TextComponent.Builder> displayBuilder = Component.text();
        if (user.hasUniversityData() && Institute.of(user.getUniversityData().getInstitute()) != Institute.UNKNOWN) {
            UniversityData data = user.getUniversityData();
            Institute institute = Institute.of(data.getInstitute());
            displayBuilder.append(createPrefix(data, hoverEvents));
            displayBuilder.append(Component.space());
            ComponentBuilder<TextComponent, TextComponent.Builder> nicknameBuilder = Component.text().append(Component.text(user.getName(), TextColor.fromHexString(institute.getColorScheme())));
            if (hoverEvents && user.hasStudentName())
                nicknameBuilder.hoverEvent(HoverEvent.showText(Component.text(user.getStudentName().toString(), TextColor.fromHexString("#FFFF55"))));
            displayBuilder.append(nicknameBuilder);
        } else displayBuilder.append(Component.text(user.getName(), TextColor.fromHexString(Institute.UNKNOWN.getColorScheme())));
        return displayBuilder.build();
    }
}
