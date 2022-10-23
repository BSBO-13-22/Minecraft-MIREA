package fun.mirea.purpur.scoreboard;

import fun.mirea.common.user.university.Institute;
import fun.mirea.common.user.MireaUser;
import fun.mirea.common.user.university.UniversityData;
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

    private final UserManager<Player> userManager;
    private final Scoreboard scoreboard;

    public UniversityScoreboard(UserManager<Player> userManager) {
        this.userManager = userManager;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        for (Institute institute : Institute.values()) {
            Team team = scoreboard.registerNewTeam(institute != Institute.UNKNOWN ? institute.getPrefix() : "яunknown");
            Component prefix = Component.text(institute.getPrefix() + " ").color(TextColor.fromHexString(institute.getColorScheme())).decorate(TextDecoration.BOLD);
            if (!institute.getPrefix().isEmpty()) team.prefix(prefix);
            team.color(NamedTextColor.GRAY);
        }
    }

    public void addUser(MireaUser<Player> user) {
        Player player = user.getPlayer();
        player.setScoreboard(scoreboard);
        Team team = scoreboard.getTeam("яunknown");
        if (user.hasUniversityData()) {
            UniversityData universityData = user.getUniversityData();
            Institute institute = Institute.of(universityData.getInstitute());
            if (institute != Institute.UNKNOWN)
                team = scoreboard.getTeam(institute.getPrefix());
        }
        if (team != null) team.addEntry(player.getName());
    }

    public void removePlayer(Player player) {
        scoreboard.getTeams().forEach(team -> {
            if (team.getEntries().contains(player.getName())) team.removeEntry(player.getName());
        });
    }

    public void updatePlayer(MireaUser<Player> user) {
        removePlayer(user.getPlayer());
        addUser(user);
    }
}
