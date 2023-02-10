package me.darkmun.blockcitytycooneconomy;

import me.darkmun.blockcitytycooneconomy.commands.MainCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public final class BlockCityTycoonEconomy extends JavaPlugin {
    private static BlockCityTycoonEconomy plugin;
    public static Economy eco = null;
    private static final GemsEconomyDatabase database = new GemsEconomyDatabase();
    //private static final Config gemsEconomyConfig = new Config();
    private static final Config playerEventsDataConfig = new Config();

    @Override @SuppressWarnings("unused")
    public void onEnable() {
        this.saveDefaultConfig();

        if (getConfig().getBoolean("enable")) {
            plugin = this;

            playerEventsDataConfig.setup(getServer().getPluginManager().getPlugin("BlockCityTycoonEvents").getDataFolder(), "playerEventsData");

            //gemsEconomyConfig.setup(Bukkit.getPluginManager().getPlugin("GemsEconomy").getDataFolder(), "data");
            //FileConfiguration gemsConfig = gemsEconomyConfig.getConfig();

            MainCommand mainCommand = new MainCommand(this);
            getCommand("business").setExecutor(mainCommand);
            getCommand("bcteconomy").setExecutor(mainCommand);
            getCommand("increaseincome").setExecutor(mainCommand);

            getServer().getPluginManager().registerEvents(new CreatingScoreboardOnJoin(), this);

            if (!this.setupEconomy()) {
                this.getLogger().info("Плагин выключен, потому что Vault не был найден!");
                this.getServer().getPluginManager().disablePlugin(this);
            }
            Bukkit.getScheduler().runTaskTimer(this, () -> {
                //gemsEconomyConfig.reloadConfig();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    double multiplier = 1;
                    double income = this.getConfig().getDouble("DataBaseIncome." + player.getName() + ".total-income");

                    //Увеличение баланса игрока
                    if (this.getConfig().getDouble("DataBaseIncome." + player.getName() + ".total-income") > 0.0) {
                        playerEventsDataConfig.reloadConfig();
                        FileConfiguration config = playerEventsDataConfig.getConfig();
                        if (config.getBoolean(player.getUniqueId() + ".night-event.running")) {
                            multiplier = 0;
                        } else {
                            if (config.getBoolean(player.getUniqueId() + ".economic-growth-event.running")) {
                                multiplier *= 2;
                            }
                            if (config.getBoolean(player.getUniqueId() + ".rain-event.running")) {
                                multiplier *= 0.5;
                            }
                        }
                        income *= multiplier;
                        eco.depositPlayer(player, income);
                    }

                    //Изменение скорборда игрока
                    Scoreboard scoreboard = player.getScoreboard();

                    String balanceNumberFont = getConfig().getString("scoreboard-balance-number-font-attributes");
                    String balanceUnitFont = getConfig().getString("scoreboard-balance-unit-font-attributes");
                    String populationNumberFont = getConfig().getString("scoreboard-population-number-font-attributes");
                    String populationUnitFont = getConfig().getString("scoreboard-population-unit-font-attributes");
                    String incomeNumberFont = getConfig().getString("scoreboard-income-number-font-attributes");
                    String incomeUnitFont = getConfig().getString("scoreboard-income-unit-font-attributes");
                    String wordsFont = BlockCityTycoonEconomy.getPlugin().getConfig().getString("scoreboard-words-font-attributes");

                    //Баланс
                    String newBalance = wordsFont + "Баланс: " + balanceNumberFont + formatNumber(eco.getBalance(player)) + balanceUnitFont + " $";
                    scoreboard.getTeam("balance").setPrefix(newBalance.substring(0, 16));
                    scoreboard.getTeam("balance").setSuffix(balanceNumberFont + newBalance.substring(16));

                    //Численность
                    try {
                        Connection con = database.getConnection();
                        PreparedStatement statement = con.prepareStatement("SELECT * FROM gemseconomy_accounts WHERE uuid=?");
                        statement.setString(1, player.getUniqueId().toString());
                        ResultSet rs = statement.executeQuery();
                        if (rs.next() && !rs.getString("balance_data").equals("{}")) {
                            String balanceData = rs.getString("balance_data");
                            String[] balanceDataStrings = balanceData.split(":");
                            double populationNumber = Double.parseDouble(balanceDataStrings[1].substring(0, balanceDataStrings[1].length() - 1));
                            String newPopulationSuffix = populationNumberFont + formatNumber(populationNumber);
                            if (!scoreboard.getTeam("population").getSuffix().equals(newPopulationSuffix)) {
                                scoreboard.getTeam("population").setSuffix(newPopulationSuffix);
                            }
                        }
                        else {
                            scoreboard.getTeam("population").setSuffix(populationNumberFont + "0");
                        }
                        rs.close();
                        statement.close();
                    } catch (SQLException e) {
                        //Bukkit.getLogger().log(Level.WARNING, ChatColor.RED + "Игроку " + player.getName() + " не удалось обновить численность в скорборде", e);
                        e.printStackTrace();
                    }

                    /*String newPopulationSuffix = populationNumberFont + formatNumber(gemsEconomyConfig.getConfig().getDouble("accounts." + player.getUniqueId().toString() + ".balances.e2d28c59-70e6-4fa3-ac58-2018569c08a8"));
                    if (!scoreboard.getTeam("population").getSuffix().equals(newPopulationSuffix)) {
                        if (gemsEconomyConfig.getConfig().contains("accounts." + player.getUniqueId().toString() + ".balances.e2d28c59-70e6-4fa3-ac58-2018569c08a8")) {
                            scoreboard.getTeam("population").setSuffix(newPopulationSuffix);
                        }
                        else {
                            scoreboard.getTeam("population").setSuffix(populationNumberFont + "0");
                        }
                    }*/

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

    private String formatNumber(double num) {
        String[] units = new String[] {"тыс.", "млн.", "млрд.", "трлн.", "квдр.", "квнт.", "скст."};
        String result;
        DecimalFormat df;
        df = new DecimalFormat("#.##");

        result = df.format(num);
        double curNum = num;

        for (int i = 0; curNum/1000d >= 1; i++) {
            curNum = curNum/1000d;
            result = df.format(curNum) + units[i];
        }
        return result;
    }

    @Override
    public void onDisable() {
        database.closeConnection();
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
                eco = rsp.getProvider();
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

    public static GemsEconomyDatabase getPopulationDatabase() {
        return database;
    }

    /*public static Config getGemsEconomyConfig() {
        return gemsEconomyConfig;
    }*/
}
