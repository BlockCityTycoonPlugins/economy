package me.darkmun.blockcitytycooneconomy;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.sql.*;
import java.util.logging.Level;

public class GemsEconomyDatabase {
    private Connection connection = null;
    private int keepAlive = 0;

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Bukkit.getScheduler().cancelTask(keepAlive);
            java.util.Properties conProperties = new java.util.Properties();
            conProperties.put("user", "u95570_sCAlcElV6l");
            conProperties.put("password", "ztc=9@A6gO+t248N@!we2e34");
            conProperties.put("autoReconnect", "true");
            String url = "jdbc:mysql://mysql2.joinserver.xyz:3306/s95570_GemsEconomyDatabase"; //"jdbc:mysql://mysql2.joinserver.xyz:3306/s95570_BlockCityTycoon";
            connection = DriverManager.getConnection(url, conProperties);
            keepAlive = Bukkit.getScheduler().runTaskTimerAsynchronously(BlockCityTycoonEconomy.getPlugin(), () -> {
                try (PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM gemseconomy_accounts")) {
                    ps.executeQuery();
                } catch (SQLException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "Keep alive failed", ex);
                    ex.printStackTrace();
                }
            }, 0, 72000).getTaskId();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
