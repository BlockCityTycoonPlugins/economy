package me.darkmun.blockcitytycooneconomy;

import jdk.internal.net.http.common.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

import java.text.DecimalFormat;

public class CreatingScoreboardOnJoin implements Listener {

    //Map<UUID, Scoreboard> playerScoreboards = new HashMap<>();
    FileConfiguration gemsConfig = BlockCityTycoonEconomy.getGemsEconomyConfig().getConfig();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player pl = event.getPlayer();

        if (!pl.getScoreboard().getObjectives().isEmpty()) {
            for (Objective obj : pl.getScoreboard().getObjectives()) {
                Bukkit.getLogger().info("Objective name: " + obj.getName());
            }
        }

        Bukkit.getLogger().info("Player: " + pl.getName());
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        String objName = BlockCityTycoonEconomy.getPlugin().getConfig().getString("scoreboard-name-font-attributes") + "BlockCity";
        Objective objective = scoreboard.getObjective(objName);
        if (objective == null) {
            Bukkit.getLogger().info("Null scoreboard objective");
            objective = scoreboard.registerNewObjective(objName, "dummy");
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

        //Численность
        Team population = scoreboard.registerNewTeam("population");
        population.addEntry(ChatColor.AQUA.toString());
        if (gemsConfig.contains("accounts." + pl.getUniqueId().toString() + ".balances.e2d28c59-70e6-4fa3-ac58-2018569c08a8")) {
            //String str = "Численность: " + numberFont + formatNumber(gemsConfig.getDouble("accounts." + pl.getUniqueId().toString() + ".balances.e2d28c59-70e6-4fa3-ac58-2018569c08a8"), 0) + unitFont + " чел.";
            population.setPrefix(wordsFont + "Численность: ");
            population.setSuffix(populationNumberFont + formatNumber(gemsConfig.getDouble("accounts." + pl.getUniqueId().toString() + ".balances.e2d28c59-70e6-4fa3-ac58-2018569c08a8")));
        }
        else {
            population.setPrefix(wordsFont + "Численность: ");
            population.setSuffix(populationNumberFont + "0"/* + unitFont + " чел."*/);
        }
        objective.getScore(ChatColor.AQUA.toString()).setScore(3);

        //Баланс
        Team balance = scoreboard.registerNewTeam("balance");
        balance.addEntry(ChatColor.BLACK.toString());
        str = wordsFont + "Баланс: " + balanceNumberFont + formatNumber(BlockCityTycoonEconomy.getEconomy().getBalance(pl)) + balanceUnitFont + " $";
        balance.setPrefix(str.substring(0, 16));
        balance.setSuffix(balanceNumberFont + str.substring(16));
        objective.getScore(ChatColor.BLACK.toString()).setScore(2);

        //Доход
        Team income = scoreboard.registerNewTeam("income");
        income.addEntry(ChatColor.BLUE.toString());
        if (BlockCityTycoonEconomy.getPlugin().getConfig().contains("DataBaseIncome." + pl.getName() + ".total-income")) {
            str = wordsFont + "Доход: " + incomeNumberFont + formatNumber(BlockCityTycoonEconomy.getPlugin().getConfig().getDouble("DataBaseIncome." + pl.getName() + ".total-income")) + incomeUnitFont + " $/с";
            income.setPrefix(str.substring(0, 16));
            income.setSuffix(incomeNumberFont + str.substring(16));
        }
        else {
            income.setPrefix(wordsFont + "Доход: ");
            income.setSuffix(incomeNumberFont + "0" + incomeUnitFont + " $/сек");
        }
        objective.getScore(ChatColor.BLUE.toString()).setScore(1);


        pl.setScoreboard(scoreboard);

        if (!pl.getScoreboard().getObjectives().isEmpty()) {
            for (Objective obj : pl.getScoreboard().getObjectives()) {
                Bukkit.getLogger().info("Objective name: " + obj.getName());
            }
        }

        for (Objective obj : pl.getScoreboard().getObjectives()) {
            Bukkit.getLogger().info(String.format("Name: %s Display name: %s Display slot: %s Criteria: %s", obj.getName(), obj.getDisplayName(), obj.getDisplaySlot().toString(), obj.getCriteria()));
        }


    }

    private String formatNumber(double num) {
        String[] units = new String[] {"тыс.", "млн.", "млрд.", "трлн.", "квдрлн.", "квнтлн.", "скстлн."};
        /*String format = "#";
        if (numbersAfterComma > 0) {
            format += ".";
            for (int i = 0; i < numbersAfterComma; i++) {
                format += "0";
            }
        }*/
        DecimalFormat df = new DecimalFormat("#.###");

        //String number = df.format(num);
        //String unit = "";
        String result = df.format(num);
        double curNum = num;

        int i = 0;
        while (curNum/1000d >= 1) {
            curNum = curNum/1000d;
            //number = df.format(curNum);
            //unit = units[i];
            result = df.format(curNum) + units[i];
            i++;
        }
        return result;
    }

    /*private String formatNumber(int num) {
        String[] units = new String[] {"тыс.", "млн.", "млрд.", "трлн.", "квдрлн.", "квнтлн.", "скстлн."};
        String result;
        if (num < 1000) {
            result = String.valueOf(num);
        }
        else {
            //String format = "#";
            //if (numbersAfterComma > 0) {
            //    format += ".";
            //    for (int i = 0; i < numbersAfterComma; i++) {
            //        format += "0";
            //    }
            //}
            DecimalFormat df = new DecimalFormat("#.000");

            //String number = df.format(num);
            //String unit = "";
            result = String.valueOf(num);
            double curNum = num;

            int i = 0;
            while (curNum/1000d >= 1) {
                curNum = curNum/1000d;
                //number = df.format(curNum);
                //unit = units[i];
                result = df.format(curNum) + units[i];
                i++;
            }
        }
        Bukkit.getLogger().info(result);
        return result;
    }*/
}
