package me.darkmun.blockcitytycooneconomy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.text.DecimalFormat;
import java.util.Arrays;

public final class BlockCityTycoonEconomy extends JavaPlugin {
    private static BlockCityTycoonEconomy plugin;
    public static Economy eco = null;
    private static Config gemsEconomyConfig = new Config();
    private static Config playerEventsDataConfig = new Config();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        if (getConfig().getBoolean("enable")) {
            plugin = this;

            playerEventsDataConfig.setup(getServer().getPluginManager().getPlugin("BlockCityTycoonEvents").getDataFolder(), "playerEventsData");

            gemsEconomyConfig.setup(Bukkit.getPluginManager().getPlugin("GemsEconomy").getDataFolder(), "data");
            FileConfiguration gemsConfig = gemsEconomyConfig.getConfig();

            MainCommand mainCommand = new MainCommand(this);
            this.getCommand("business").setExecutor(mainCommand);
            this.getCommand("increaseincome").setExecutor(mainCommand);
            this.getCommand("testt").setExecutor(new Test());

            getServer().getPluginManager().registerEvents(new CreatingScoreboardOnJoin(), this);

            if (!this.setupEconomy()) {
                this.getLogger().info("Плагин выключен, потому что Vault не был найден!");
                this.getServer().getPluginManager().disablePlugin(this);
            }
            Bukkit.getScheduler().runTaskTimer(this, () -> {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    double multiplier = 1;
                    double income = this.getConfig().getDouble("DataBaseIncome." + player.getName() + ".total-income");

                    //Увеличение баланса игрока
                    if (this.getConfig().getDouble("DataBaseIncome." + player.getName() + ".total-income") > 0.0) {
                        playerEventsDataConfig.reloadConfig();
                        FileConfiguration config = playerEventsDataConfig.getConfig();
                        //Bukkit.getLogger().info(String.valueOf(config.getBoolean(player.getUniqueId() + ".rain-event.running")));
                        if (config.getBoolean(player.getUniqueId() + ".night-event.running")) {
                            multiplier = 0;
                            //Bukkit.getLogger().info("Multiplier (night): ");
                        } else {
                            if (config.getBoolean(player.getUniqueId() + ".economic-growth-event.running")) {
                                multiplier = 2;
                                //Bukkit.getLogger().info("Multiplier (economic growth): ");
                            }
                            if (config.getBoolean(player.getUniqueId() + ".rain-event.running")) {
                                multiplier = 0.5;
                                //Bukkit.getLogger().info("Multiplier (rain): ");
                            }
                        }
                        income *= multiplier;
                        eco.depositPlayer(player, income);
                    }
                    //Bukkit.getLogger().info("Income: " + income);
                    //Изменение скорборда игрока
                    Scoreboard scoreboard = player.getScoreboard();
                    //int teamNum = CreatingScoreboardOnJoin.getTeamNumber(player.getUniqueId());

                    String balanceNumberFont = getConfig().getString("scoreboard-balance-number-font-attributes");
                    String balanceUnitFont = getConfig().getString("scoreboard-balance-unit-font-attributes");
                    String populationNumberFont = getConfig().getString("scoreboard-population-number-font-attributes");
                    String populationUnitFont = getConfig().getString("scoreboard-population-unit-font-attributes");
                    String incomeNumberFont = getConfig().getString("scoreboard-income-number-font-attributes");
                    String incomeUnitFont = getConfig().getString("scoreboard-income-unit-font-attributes");
                    String wordsFont = BlockCityTycoonEconomy.getPlugin().getConfig().getString("scoreboard-words-font-attributes");

                    //Баланс
                    String newBalance = wordsFont + "Баланс: " + balanceNumberFont + formatNumber(eco.getBalance(player)) + balanceUnitFont + " $";

                    Bukkit.getLogger().info("balance");
                    //String oldBalance = scoreboard.getTeam("balance").getPrefix() + scoreboard.getTeam("balance").getSuffix();
                    scoreboard.getTeam("balance").setPrefix(newBalance.substring(0, 16));
                    scoreboard.getTeam("balance").setSuffix(balanceNumberFont + newBalance.substring(16));

                    //Численность
                    //String newPopulation = "Численность: " + numberFont + formatNumber(gemsConfig.getDouble("accounts." + player.getUniqueId().toString() + ".balances.e2d28c59-70e6-4fa3-ac58-2018569c08a8"));
                    //String newPopulationPrefix = newPopulation.substring(0, 16)
                    //String oldPopulation =
                    String newPopulationSuffix = populationNumberFont + formatNumber(gemsConfig.getDouble("accounts." + player.getUniqueId().toString() + ".balances.e2d28c59-70e6-4fa3-ac58-2018569c08a8"));
                    if (!scoreboard.getTeam("population").getSuffix().equals(newPopulationSuffix)) {
                        if (gemsConfig.contains("accounts." + player.getUniqueId().toString() + ".balances.e2d28c59-70e6-4fa3-ac58-2018569c08a8")) {
                            scoreboard.getTeam("population").setSuffix(newPopulationSuffix);
                        }
                        else {
                            scoreboard.getTeam("population").setSuffix(populationNumberFont + "0");
                        }
                    }

                    //Доход
                    String newIncome = wordsFont + "Доход: " + incomeNumberFont + formatNumber(income) + incomeUnitFont + " $/с";
                    String newIncomePrefix = newIncome.substring(0, 16);
                    String newIncomeSuffix = incomeNumberFont + newIncome.substring(16);
                    String oldIncome = scoreboard.getTeam("income").getPrefix() + scoreboard.getTeam("income").getSuffix();
                    if (!oldIncome.equals(newIncome)) {
                        if (getConfig().contains("DataBaseIncome." + player.getName() + ".total-income")) {
                            scoreboard.getTeam("income").setPrefix(newIncomePrefix);
                            scoreboard.getTeam("income").setSuffix(newIncomeSuffix);
                        }
                        else {
                            scoreboard.getTeam("income").setSuffix(incomeNumberFont + "0" + incomeUnitFont + " $/сек");
                        }
                    }

                    //player.setScoreboard(scoreboard);
                }

            }, 0L, 20L);
            getLogger().info("Plugin enabled.");
        }
        else {
            getLogger().info("Plugin not enabled.");
        }
    }

    /*private String formatNumber(double num) {
        String[] units = new String[] {"тыс.", "млн.", "млрд.", "трлн.", "квдрлн.", "квнтлн.", "скстлн."};
        DecimalFormat df = new DecimalFormat("#.000");
        String result = df.format(num);
        double curNum = num;

        int i = 0;
        while (curNum/1000d >= 1) {
            curNum = curNum/1000d;
            result = df.format(curNum) + units[i];
            i++;
        }
        return result;
    }*/

    private String formatNumber(double num) {
        String[] units = new String[] {"тыс.", "млн.", "млрд.", "трлн.", "квдрлн.", "квнтлн.", "скстлн."};
        /*String format = "#";
        if (numbersAfterComma > 0) {
            format += ".";
            for (int i = 0; i < numbersAfterComma; i++) {
                format += "0";
            }
        }*/
        String result;
        DecimalFormat df;
        df = new DecimalFormat("#.###");

        //String number = df.format(num);
        //String unit = "";
        result = df.format(num);
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
        //String format = "#";
        //if (numbersAfterComma > 0) {
        //    format += ".";
        //    for (int i = 0; i < numbersAfterComma; i++) {
        //        format += "0";
         //   }
        //}
        DecimalFormat df = new DecimalFormat("#.###");

        //String number = df.format(num);
        //String unit = "";
        result = df.format(num);
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
    }*/

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled.");
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            } else {
                eco = (Economy)rsp.getProvider();
                return eco != null;
            }
        }
    }
    public static BlockCityTycoonEconomy getPlugin() {
        return plugin;
    }
    public static Economy getEconomy() {
        return eco;
    }

    public static Config getGemsEconomyConfig() {
        return gemsEconomyConfig;
    }
}
