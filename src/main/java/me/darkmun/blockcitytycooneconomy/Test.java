package me.darkmun.blockcitytycooneconomy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class Test implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player pl = (Player) sender;

        Bukkit.getLogger().info("Player: " + pl.getName());
        Scoreboard scoreboard1 = pl.getScoreboard();
        for (Objective obj : scoreboard1.getObjectives()) {
            Bukkit.getLogger().info("Objective name: " + obj.getName());
            Bukkit.getLogger().info("Objective display name: " + obj.getDisplayName());
            Bukkit.getLogger().info("Display slot: " + obj.getDisplaySlot());
        }
        for (Team team : scoreboard1.getTeams()) {
            Bukkit.getLogger().info("Team name: " + team.getName());
            Bukkit.getLogger().info("Team display name: " + team.getDisplayName());
            Bukkit.getLogger().info("Team suffix: " + team.getSuffix());
            Bukkit.getLogger().info("Team prefix: " + team.getPrefix());
            Bukkit.getLogger().info("Team entries: " + team.getEntries());
        }
        Bukkit.getLogger().info("Scoreboard entries: " + scoreboard1.getEntries());

        UUID plUID = pl.getUniqueId();
        Bukkit.getLogger().info("Player display name: " + pl.getDisplayName());
        Bukkit.getLogger().info("Player name: " + pl.getName());

        if (!pl.getScoreboard().getObjectives().isEmpty()) {
            for (Objective obj : pl.getScoreboard().getObjectives()) {
                Bukkit.getLogger().info("Objective name: " + obj.getName());
            }
        }

        Bukkit.getLogger().info("Player: " + pl.getName());
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            Team newTeam = scoreboard.registerNewTeam(team.getName());
            newTeam.setPrefix(team.getPrefix());
            newTeam.setSuffix(team.getSuffix());
            newTeam.setDisplayName(team.getDisplayName());
            for (String entry : team.getEntries()) {
                newTeam.addEntry(entry);
            }
            newTeam.setColor(team.getColor());
        }

        Bukkit.getLogger().info("Player: " + pl.getName());
        for (Objective obj : scoreboard.getObjectives()) {
            Bukkit.getLogger().info("Objective name: " + obj.getName());
            Bukkit.getLogger().info("Objective display name: " + obj.getDisplayName());
            Bukkit.getLogger().info("Display slot: " + obj.getDisplaySlot());
        }
        for (Team team : scoreboard.getTeams()) {
            Bukkit.getLogger().info("Team name: " + team.getName());
            Bukkit.getLogger().info("Team display name: " + team.getDisplayName());
            Bukkit.getLogger().info("Team suffix: " + team.getSuffix());
            Bukkit.getLogger().info("Team prefix: " + team.getPrefix());
            Bukkit.getLogger().info("Team entries: " + team.getEntries());
        }
        Bukkit.getLogger().info("Scoreboard entries: " + scoreboard.getEntries());

        String objName = "BlockCity";
        String objDisplayName = BlockCityTycoonEconomy.getPlugin().getConfig().getString("scoreboard-name-font-attributes") + "BlockCity";
        Objective objective = scoreboard.getObjective(objName);
        if (objective == null) {
            Bukkit.getLogger().info("Null scoreboard objective");
            objective = scoreboard.registerNewObjective(objName, "dummy");
            objective.setDisplayName(objDisplayName);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        String wordsFont = BlockCityTycoonEconomy.getPlugin().getConfig().getString("scoreboard-words-font-attributes");
        String balanceNumberFont = BlockCityTycoonEconomy.getPlugin().getConfig().getString("scoreboard-balance-number-font-attributes");
        String balanceUnitFont = BlockCityTycoonEconomy.getPlugin().getConfig().getString("scoreboard-balance-unit-font-attributes");
        String populationNumberFont = BlockCityTycoonEconomy.getPlugin().getConfig().getString("scoreboard-population-number-font-attributes");
        String populationUnitFont = BlockCityTycoonEconomy.getPlugin().getConfig().getString("scoreboard-population-unit-font-attributes");
        String incomeNumberFont = BlockCityTycoonEconomy.getPlugin().getConfig().getString("scoreboard-income-number-font-attributes");
        String incomeUnitFont = BlockCityTycoonEconomy.getPlugin().getConfig().getString("scoreboard-income-unit-font-attributes");
        String str;

        /*for (Player player : pl.getWorld().getEntitiesByClass(Player.class)) {
            Bukkit.getLogger().info("Player: " + player.getName());
            Scoreboard scoreboard1 = player.getScoreboard();
            for (Objective obj : scoreboard1.getObjectives()) {
                Bukkit.getLogger().info("Objective name: " + obj.getName());
                Bukkit.getLogger().info("Objective display name: " + obj.getDisplayName());
                Bukkit.getLogger().info("Display slot: " + obj.getDisplaySlot());
            }
            for (Team team : scoreboard1.getTeams()) {
                Bukkit.getLogger().info("Team name: " + team.getName());
                Bukkit.getLogger().info("Team display name: " + team.getDisplayName());
                Bukkit.getLogger().info("Team suffix: " + team.getSuffix());
                Bukkit.getLogger().info("Team prefix: " + team.getPrefix());
                Bukkit.getLogger().info("Team entries: " + team.getEntries());
            }
            Bukkit.getLogger().info("Scoreboard entries: " + scoreboard1.getEntries());

        }*/
        pl.setScoreboard(scoreboard);
        /*for (Player player : pl.getWorld().getEntitiesByClass(Player.class)) {
            Bukkit.getLogger().info("Player: " + player.getName());
            Scoreboard scoreboard1 = player.getScoreboard();
            for (Objective obj : scoreboard1.getObjectives()) {
                Bukkit.getLogger().info("Objective name: " + obj.getName());
                Bukkit.getLogger().info("Objective display name: " + obj.getDisplayName());
                Bukkit.getLogger().info("Display slot: " + obj.getDisplaySlot());
            }
            for (Team team : scoreboard1.getTeams()) {
                Bukkit.getLogger().info("Team name: " + team.getName());
                Bukkit.getLogger().info("Team display name: " + team.getDisplayName());
                Bukkit.getLogger().info("Team suffix: " + team.getSuffix());
                Bukkit.getLogger().info("Team prefix: " + team.getPrefix());
                Bukkit.getLogger().info("Team entries: " + team.getEntries());
            }
            Bukkit.getLogger().info("Scoreboard entries: " + scoreboard1.getEntries());

        }*/

        if (!pl.getScoreboard().getObjectives().isEmpty()) {
            for (Objective obj : pl.getScoreboard().getObjectives()) {
                Bukkit.getLogger().info("Objective name: " + obj.getName());
            }
        }

        for (Objective obj : pl.getScoreboard().getObjectives()) {
            Bukkit.getLogger().info(String.format("Name: %s Display name: %s Display slot: %s Criteria: %s", obj.getName(), obj.getDisplayName(), obj.getDisplaySlot().toString(), obj.getCriteria()));
        }

        return true;
    }
}
