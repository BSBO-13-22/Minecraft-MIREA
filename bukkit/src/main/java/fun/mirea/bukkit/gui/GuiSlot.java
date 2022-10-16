package fun.mirea.bukkit.gui;

import fun.mirea.common.utility.FormatUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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
        itemMeta.setDisplayName(FormatUtils.colorize(displayName));
        List<String> lines = new ArrayList<>();
        for (String line : lore) lines.add(FormatUtils.colorize(line));
        itemMeta.setLore(lines);
        for (ItemFlag itemFlag : ItemFlag.values())
            itemMeta.addItemFlags(itemFlag);
        itemMeta.setUnbreakable(true);
        if (enchanted) itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}