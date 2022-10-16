package fun.mirea.bukkit.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GuiManager {

    private final Map<String, Map<String, InventoryGui>> guiMap;

    public GuiManager() {
        guiMap = new HashMap<>();
    }

    public InventoryGui getGui(String owner, String id) {
        if (!guiMap.containsKey(owner))
            return null;
        else return guiMap.get(owner).get(id);
    }

    public void saveGui(String owner, String id, InventoryGui gui) {
        if (!guiMap.containsKey(owner))
            guiMap.put(owner, new HashMap<>());
        guiMap.get(owner).put(id, gui);
    }

    public void removeGui(String owner, String id) {
        if (guiMap.containsKey(owner))
            guiMap.get(owner).remove(id);
    }

    public Collection<InventoryGui> getAllGui(String owner) {
        Collection<InventoryGui> collection = new ArrayList<>();
        if (guiMap.containsKey(owner))
            collection.addAll(guiMap.get(owner).values());
        return collection;
    }
}
