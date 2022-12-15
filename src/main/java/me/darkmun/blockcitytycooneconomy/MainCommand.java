package me.darkmun.blockcitytycooneconomy;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {
    static BlockCityTycoonEconomy plugin;
    DecimalFormat df = new DecimalFormat("##.##");

    public MainCommand(BlockCityTycoonEconomy main) {
        plugin = main;
    }

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("bcteconomy.commands")) {
            if (label.equalsIgnoreCase("business")) {
                if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("business.reload")) {
                        sender.sendMessage(color(plugin.getConfig().getString("Misc.Successfully-reloaded-config")));
                        plugin.reloadConfig();
                        return true;
                    }

                    sender.sendMessage(color(plugin.getConfig().getString("Misc.Havent-permission")));
                    return true;
                }

                if (args.length > 1 && args[0].equalsIgnoreCase("income")) {
                    Player pl = Bukkit.getPlayerExact(args[1]);
                    if (pl != null) {
                        if (0.0 < plugin.getConfig().getDouble("DataBaseIncome." + args[1] + ".total-income")) {
                            sender.sendMessage(color(plugin.getConfig().getString("Misc.Your-income").replace("%income%", String.valueOf(df.format(plugin.getConfig().getDouble("DataBaseIncome." + args[1] + ".total-income"))))));
                            return true;
                        }

                        sender.sendMessage(color(plugin.getConfig().getString("Misc.Income-is-null")));
                        return true;
                    }
                    else {
                        sender.sendMessage("Игрока с таким ником сейчас нет на сервере");
                    }
                }

                Iterator var5;
                String msg;
                if (args.length == 0) {
                    var5 = plugin.getConfig().getStringList("Misc.Help-message").iterator();

                    while(var5.hasNext()) {
                        msg = (String)var5.next();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("buy")) {
                    if (args.length > 2) {
                        if (plugin.getConfig().getConfigurationSection("companies").contains(args[2])) {
                            Player pl = Bukkit.getPlayerExact(args[1]);
                            if (pl != null) {
                                if (BlockCityTycoonEconomy.eco.getBalance(pl) >= plugin.getConfig().getDouble("companies." + args[2] + ".cost")) {
                                    if (!plugin.getConfig().getStringList("DataBase." + args[1]).contains(args[2])) {
                                        BlockCityTycoonEconomy.eco.withdrawPlayer(pl, plugin.getConfig().getDouble("companies." + args[2] + ".cost"));
                                        Double BuyPrice = plugin.getConfig().getDouble("companies." + args[2] + ".cost");
                                        List<String> data = plugin.getConfig().getStringList("DataBase." + args[1]);
                                        data.add(args[2]);
                                        plugin.getConfig().set("DataBase." + args[1], data);
                                        plugin.getConfig().set("DataBaseIncome." + args[1] + ".real-income",
                                                plugin.getConfig().getDouble("companies." + args[2] + ".income") + plugin.getConfig().getDouble("DataBaseIncome." + args[1] + ".real-income"));

                                        int increasePercent = plugin.getConfig().getInt("DataBaseIncome." + args[1] + ".increase-income-percent"); //если нету в конфиге такого путя, то getInt() возвращает 0
                                        double realIncome = plugin.getConfig().getDouble(String.format("DataBaseIncome.%s.real-income", args[1]));
                                        double extraIncome = realIncome / 100d * (double) increasePercent;

                                        plugin.getConfig().set("DataBaseIncome." + args[1] + ".total-income", realIncome + extraIncome);
                                        plugin.saveConfig();
                                        pl.sendMessage(color(plugin.getConfig().getString("Misc.Successfully-bought")).replace("%business%", plugin.getConfig().getString("companies." + args[2] + ".display-name")));
                                        return true;
                                    }

                                    pl.sendMessage(color(plugin.getConfig().getString("Misc.Already-have")));
                                    return true;
                                }

                                pl.sendMessage(color(plugin.getConfig().getString("Misc.Havent-money")));
                                return true;
                            }
                            else {
                                sender.sendMessage("Игрока с таким ником сейчас нет на сервере");
                            }
                        }

                        sender.sendMessage(color(plugin.getConfig().getString("Misc.Unknown-business")));
                        return true;
                    }

                    var5 = plugin.getConfig().getStringList("Misc.Help-message").iterator();

                    while(var5.hasNext()) {
                        msg = (String)var5.next();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    }

                    return true;
                }
            }

            if (label.equalsIgnoreCase("increaseincome")) {
                if (sender.hasPermission("bcteconomy.increaseincome")) {
                    if (args.length == 2) {
                        Player pl = Bukkit.getPlayerExact(args[0]);
                        if (pl == null) {
                            sender.sendMessage(ChatColor.RED + String.format("Игрока с ником %s сейчас нет на сервере!", args[0]));
                        }
                        else if (args[1].endsWith("%")) {
                            String[] numPercent = args[1].split("%");
                            try {
                                int increasePercent = Integer.parseInt(numPercent[0]);
                                if (increasePercent >= 0) {
                                    double realIncome = plugin.getConfig().getDouble(String.format("DataBaseIncome.%s.real-income", args[0]));
                                    double extraIncome = realIncome / 100d * (double) increasePercent;
                                    plugin.getConfig().set("DataBaseIncome." + args[0] + ".increase-income-percent", increasePercent);
                                    plugin.getConfig().set("DataBaseIncome." + args[0] + ".total-income", realIncome + extraIncome);
                                    plugin.saveConfig();
                                    //sender.sendMessage(ChatColor.GREEN + String.format("Для игрока %s был увеличен доход на %d процентов от изначального", args[0], increasePercent));
                                    pl.sendMessage(ChatColor.GREEN + String.format("Ваш доход был увеличен на %d процентов от изначального!", increasePercent));
                                    return true;
                                }
                                else {
                                    sender.sendMessage(ChatColor.RED + "Процент не может быть меньше нуля");
                                }
                            }
                            catch (NumberFormatException ex) {
                                sender.sendMessage(ChatColor.RED + "Второй аргумент команды введен не верно");
                                sender.sendMessage(ChatColor.RED + "/increaseincome <player> <percent-num>%");
                            }
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Второй аргумент команды введен не верно");
                            sender.sendMessage(ChatColor.RED + "/increaseincome <player> <percent-num>%");
                        }
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "Аргументов команды должно быть два");
                        sender.sendMessage(ChatColor.RED + "/increaseincome <player> <percent-num>%");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "У вас нет прав на использование этой команды");
                }
            }
        }
        else {
            sender.sendMessage(ChatColor.RED + "У вас недостаточно прав на использование этой команды");
        }
        return false;
    }
}