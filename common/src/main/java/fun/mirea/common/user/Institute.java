package fun.mirea.common.user;

import lombok.Getter;

public enum Institute {

    IIT("Институт информационных технологий",  "ИИТ", "#424a52"),
    III("Институт искусственного интеллекта",  "ИИИ", "#036c4a"),
    IKB("Институт кибербезопасности и цифровых технологий",  "ИКБ", "#0c5e81"),
    IPTIP("Институт перспективных технологий и индустриального программирования",  "ИПТИП","#a7a647"),
    IRI("Институт радиоэлектроники и информатики",  "ИРИ","#683180"),
    ITU("Институт технологий управления",  "ИТУ","#cb5937"),
    ITXT("Институт тонких химических технологий им. М.В. Ломоносова",  "ИТХТ","#ac719f"),
    //РЭА в г. Фрязино"},{"groupNam
    FRYAZINO("Филиал Фрязино", "ФВГФ", "#008ee3"),
    STAVROPOL("Филиал Ставрополь", "ФВГС","#008ee3"),
    UNKNOWN("Нет данных", "", "#AAAAAA");


    public static Institute of(String name) {
        for (Institute institute : values()) {
            if (institute.getDisplayName().equals(name))
                return institute;
        }
        return UNKNOWN;
    }
    @Getter private final String displayName;

    @Getter private final String prefix;
    @Getter private final String colorScheme;

    Institute(String displayName, String prefix, String colorScheme) {
        this.colorScheme = colorScheme;
        this.prefix = prefix;
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "&" + colorScheme + displayName;
    }


}
