package fun.mirea.common.user;

import lombok.Getter;

public enum Institute {

    IIT("Институт информационных технолгоий", "#424a52"),
    III("Институт искусственного интеллекта", "#036c4a"),
    IKB("Институт кибербезопасности и цифровых технологий", "#0c5e81"),
    IPTIP("Институт перспективных технологий и индустриального программирования", "#a7a647"),
    IRI("Институт радиоэлектроники и информатики", "#683180"),
    ITU("Институт технологий управления", "#cb5937"),
    ITXT("Институт тонких химических технологий", "#ac719f");

    @Getter private final String displayName;
    @Getter private final String colorScheme;

    Institute(String displayName, String colorScheme) {
        this.displayName = displayName;
        this.colorScheme = colorScheme;
    }


}
