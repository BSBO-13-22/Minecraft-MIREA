package fun.mirea.bukkit.scoreboard;

import fun.mirea.common.user.Institute;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.UniversityData;
import fun.mirea.common.user.UserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class UniversityScoreboard {

    private final UserManager userManager;
    private final Scoreboard scoreboard;

    public UniversityScoreboard(UserManager userManager) {
        this.userManager = userManager;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        for (Institute institute : Institute.values()) {
            Team team = scoreboard.registerNewTeam(!institute.getPrefix().isEmpty() ? institute.getPrefix() : "яunknown");
            Component prefix = Component.text(institute.getPrefix() + " ").color(TextColor.fromHexString(institute.getColorScheme())).decorate(TextDecoration.BOLD);
            if (!institute.getPrefix().isEmpty()) team.prefix(prefix);
            team.color(NamedTextColor.GRAY);
        }
    }

    public void addPlayer(Player player) {
        CompletableFuture.runAsync(() -> {
            try {
                MireaUser user = userManager.getUserCache().get(player.getName());
                Team team = scoreboard.getTeam("яunknown");
                if (user.hasUniversityData()) {
                    UniversityData universityData = user.getUniversityData();
                    Institute institute = Institute.of(universityData.getInstitute());
                    team = scoreboard.getTeam(institute.getPrefix());
                }
                if (team != null) team.addEntry(player.getName());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            player.setScoreboard(scoreboard);
        });
    }

    public void removePlayer(Player player) {
        CompletableFuture.runAsync(() -> scoreboard.getTeams().forEach(team -> team.removeEntry(player.getName())));
    }

    public void updatePlayer(Player player) {
        removePlayer(player);
        addPlayer(player);
    }
}
