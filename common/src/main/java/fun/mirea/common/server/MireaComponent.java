package fun.mirea.common.server;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;;
import java.util.List;
import java.util.stream.Stream;

public class MireaComponent implements TextComponent {

    private String content;
    private final TextComponent component;

    public MireaComponent(Type type, String content, Placeholder... placeholders) {
        for (Placeholder placeholder : placeholders)
            content = content.replace("{" + placeholder.getTag() + "}", String.valueOf(placeholder.getValue()));
        content = content.replace('&', '§');
        Component prefix = Component.text().append(Component.text("MF", TextColor.fromHexString("#196bb1")).decorate(TextDecoration.BOLD))
                .append(Component.space()).append(Component.text("::", TextColor.fromHexString("#555555"))).build();
        Component message = Component.text(content, type.getColor());
        if (type == Type.INFO) message = message.decorate(TextDecoration.ITALIC);
        this.component = Component.text().append(prefix).append(Component.space()).append(message).build();
        this.content = component.content();
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return TextComponent.super.examinableProperties();
    }

    @Override
    public @NotNull String content() {
        return content;
    }

    @Override
    public @NotNull TextComponent content(@NotNull String content) {
        this.content = content;
        return this;
    }

    @Override
    public @NotNull Builder toBuilder() {
        return component.toBuilder();
    }

    @Override
    public @Unmodifiable @NotNull List<Component> children() {
        return component.children();
    }

    @Override
    public @NotNull TextComponent children(@NotNull List<? extends ComponentLike> children) {
        return component.children(children);
    }



    @Override
    public @NotNull Style style() {
        return component.style();
    }

    @Override
    public @NotNull TextComponent style(@NotNull Style style) {
        return component.style(style);
    }

    public static class Placeholder {

        @Getter
        private final String tag;
        @Getter
        private final Object value;

        public Placeholder(String tag, Object value) {
            this.tag = tag;
            this.value = value;
        }
    }

    public enum Type {

        INFO("#AAAAAA"),
        SUCCESS("#FFFFFF"),
        ERROR("#FF5555");

        @Getter
        private final TextColor color;

        Type(String colorScheme) {
            this.color = TextColor.fromHexString(colorScheme);
        }
    }
}
