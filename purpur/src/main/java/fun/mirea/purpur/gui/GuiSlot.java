package fun.mirea.purpur.gui;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import fun.mirea.common.format.FormatUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class GuiSlot {

    public static Builder builder(Material material) {
        return new Builder(material);
    }

    @Getter
    private ItemStack itemStack;
    @Getter
    private ClickEvents clickEvents;

    private GuiSlot(ItemStack itemStack, ClickEvents clickEvents) {
        this.itemStack = itemStack;
        this.clickEvents = clickEvents;
    }

    public static class Builder {

        @Getter
        private ItemStack itemStack;
        @Getter
        private Component displayName;
        @Getter
        private List<Component> lore;
        @Getter
        private String textureValue;
        @Getter
        private int amount = 1;
        @Getter
        private boolean isEnchanted;
        @Getter
        private ClickEvents clickEvents;

        public Builder(Material material) {
            itemStack = new ItemStack(material);
        }

        public Builder setDisplayName(String content) {
            this.displayName = Component.text().resetStyle().append(FormatUtils.colorize(content)).build();
            return this;
        }

        public Builder setDisplayName(Component displayName) {
            this.displayName = Component.text().resetStyle().append(displayName).build();
            return this;
        }

        public Builder setTextureValue(String textureValue) {
            this.textureValue = textureValue;
            return this;
        }

        public Builder appendLore(String content) {
            return appendLore(FormatUtils.colorize(content));
        }

        public Builder appendLore(List<String> contents) {
            if (lore == null) lore = new ArrayList<>();
            for (String content : contents) lore.add(FormatUtils.colorize(content));
            return this;
        }

        public Builder appendLore(Component component) {
            if (lore == null) lore = new ArrayList<>();
            lore.add(component);
            return this;
        }

        public Builder appendLore(Component... components) {
            if (lore == null) lore = new ArrayList<>();
            Collections.addAll(lore, components);
            return this;
        }

        public Builder appendLoreSpace() {
            if (lore == null) lore = new ArrayList<>();
            lore.add(Component.space());
            return this;
        }

        public Builder setEnchanted(boolean isEnchanted) {
            this.isEnchanted = isEnchanted;
            return this;
        }

        public Builder setAmount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder setClickEvent(ClickEvents clickEvents) {
            this.clickEvents = clickEvents;
            return this;
        }

        public GuiSlot build() {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (displayName != null) itemMeta.displayName(displayName);
            if (lore != null && !lore.isEmpty()) itemMeta.lore(lore);
            if (isEnchanted) itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            for (ItemFlag itemFlag : ItemFlag.values())
                itemMeta.addItemFlags(itemFlag);
            itemStack.setItemMeta(itemMeta);
            if (textureValue != null) {
                if (itemStack.getType() != Material.PLAYER_HEAD) {
                    itemStack = new ItemStack(Material.PLAYER_HEAD);
                    itemStack.setItemMeta(itemMeta);
                }
                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), getRandomName());
                profile.setProperty(new ProfileProperty("textures", textureValue));
                skullMeta.setPlayerProfile(profile);
                itemStack.setItemMeta(skullMeta);
            }
            itemStack.setAmount(amount);
            return new GuiSlot(itemStack, clickEvents);
        }

        private String getRandomName() {
            StringBuilder name = new StringBuilder("CH");
            Random random = new Random();
            for (int i = 0; i < 14; i++) name.append(random.nextInt(0, 9));
            return name.toString();
        }
    }
}
