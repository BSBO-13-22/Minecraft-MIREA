package fun.mirea.purpur.commands.gui;

import co.aikar.commands.annotation.CommandAlias;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fun.mirea.common.format.MireaComponent;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.university.UniversityData;
import fun.mirea.purpur.commands.BukkitMireaCommand;
import fun.mirea.purpur.gui.ChestGui;
import fun.mirea.purpur.gui.GuiSlot;
import fun.mirea.common.format.FormatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScheduleCommand extends BukkitMireaCommand {

    @CommandAlias("schedule|расписание")
    public void onScheduleCommand(MireaUser<Player> user) {
        Player player = user.getPlayer();
        if (user.hasUniversityData()) {
            player.sendActionBar(FormatUtils.colorize("&7&oПожалуйста, подождите..."));
            UniversityData data = user.getUniversityData();
            mireaApi.getGroupSchedule(data.getGroupName()).thenCombineAsync(mireaApi.getCurrentWeek(), (jsonObject, currentWeek) -> {
                try {
                    ChestGui chestGui = new ChestGui(data.getGroupName() + " | Неделя " + currentWeek, 1);
                    chestGui.open(player);
                    player.sendActionBar(Component.empty());
                    JsonArray schedule = jsonObject.get("schedule").getAsJsonArray();
                    int i = 0;
                    for (JsonElement day : schedule) {
                        GuiSlot.Builder slotBuilder = GuiSlot.builder(Material.LECTERN)
                                .setDisplayName(Component.text("§a§l" + FormatUtils.capitalize(day.getAsJsonObject().get("day").getAsString())))
                                .setAmount(i + 1);
                        List<String> lore = new ArrayList<>();
                        lore.add(" ");
                        JsonArray lessons = day.getAsJsonObject().getAsJsonArray(currentWeek % 2 == 0 ? "even" : "odd");
                        int k = 1;
                        for (JsonElement lesson : lessons) {
                            JsonArray lessonArray = lesson.getAsJsonArray();
                            if (!lessonArray.isEmpty()) {
                                JsonObject lessonObject = lessonArray.get(0).getAsJsonObject();
                                lore.add(" &8№" + k + ". &e" + lessonObject.get("name").getAsString() + " &7(" + lessonObject.get("type").getAsString() + ")");
                                JsonElement placeElement = lessonObject.get("place");
                                JsonElement tutorElement = lessonObject.get("tutor");
                                if (!placeElement.isJsonNull() || !tutorElement.isJsonNull())
                                    lore.add((tutorElement.isJsonNull() ? "      " : "      &6" + tutorElement.getAsString()) +
                                            (placeElement.isJsonNull() ? "" : " &7" + placeElement.getAsString() + ""));
                            }
                            k++;
                        }
                        lore.add(" ");
                        lore.add("&fИсточник: &3&nmirea.xyz");
                        slotBuilder.appendLore(lore);
                        chestGui.addSlot(i, slotBuilder.build());
                        i++;
                    }
                    guiManager.saveGui(player.getName(), "schedule", chestGui);
                    chestGui.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
        } else player.sendMessage(new MireaComponent(MireaComponent.Type.ERROR, "Для начала укажите свою группу! Используйте: §6/card group <группа> &8(Прим. БСБО-13-22)"));
    }
}
