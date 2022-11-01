package me.darkmun.blockcitytycooneconomy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.logging.Logger;

public final class BlockCityTycoonEconomy extends JavaPlugin {
    private Logger log;
    public Economy eco = null;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        if (getConfig().getBoolean("enable")) {
            MainCommand mainCommand = new MainCommand(this);
            this.getCommand("business").setExecutor(mainCommand);
            this.getCommand("increaseincome").setExecutor(mainCommand);

            this.log = this.getLogger();
            if (!this.setupEconomy()) {
                this.log.info("Плагин выключен, потому что Vault не был найден!");
                this.getServer().getPluginManager().disablePlugin(this);
            }
            Bukkit.getScheduler().runTaskTimer(this, () -> {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (this.getConfig().getDouble("DataBaseIncome." + player.getName() + ".total-income") > 0.0) {
                        this.eco.depositPlayer(player, this.getConfig().getDouble("DataBaseIncome." + player.getName() + ".total-income"));
                    }
                }

            }, 0L, 20L);
            log.info("Plugin enabled.");
        }
        else {
            log.info("Plugin not enabled.");
        }
    }
    @Override
    public void onDisable() {
        log.info("Plugin disabled.");
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            } else {
                this.eco = (Economy)rsp.getProvider();
                return this.eco != null;
            }
        }
    }
}
