package fun.mirea.purpur.gui;

import fun.mirea.common.format.FormatUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
public class GuiSlot {

    private ItemStack itemStack;
    private Material material;
    private String displayName;
    private List<String > lore;
    private int amount;
    private boolean enchanted;
    @Getter
    private ClickEvents clickEvents;

    public ItemStack toItemStack() {
        if (itemStack == null)
            itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(FormatUtils.colorize(displayName));
        List<Component> lines = new ArrayList<>();
        for (String line : lore) lines.add(FormatUtils.colorize(line));
        itemMeta.lore(lines);
        for (ItemFlag itemFlag : ItemFlag.values())
            itemMeta.addItemFlags(itemFlag);
        itemMeta.setUnbreakable(true);
        if (enchanted) itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        itemStack.setItemMeta(itemMeta);
        itemStack.setAmount(amount);
        return itemStack;
    }

}
