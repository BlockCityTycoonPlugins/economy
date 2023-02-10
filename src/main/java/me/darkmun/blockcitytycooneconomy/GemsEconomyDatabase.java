package me.darkmun.blockcitytycooneconomy;

import java.sql.*;

public class GemsEconomyDatabase {
    private Connection connection = null;

    public Connection getConnection() throws SQLException {
        if (connection == null) {
            java.util.Properties conProperties = new java.util.Properties();
            conProperties.put("user", "u95570_sCAlcElV6l");
            conProperties.put("password", "ztc=9@A6gO+t248N@!we2e34");
            conProperties.put("autoReconnect", "true");
            String url = "jdbc:mysql://mysql2.joinserver.xyz:3306/s95570_GemsEconomyDatabase"; //"jdbc:mysql://mysql2.joinserver.xyz:3306/s95570_BlockCityTycoon";
            connection = DriverManager.getConnection(url, conProperties);
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
