package dev.rishon.rhologramchat.data;

import dev.rishon.rhologramchat.Main;
import dev.rishon.rhologramchat.data.player.PlayerData;
import dev.rishon.rhologramchat.handler.Handler;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.UUID;

public class SQLData implements Handler {

    private final Main plugin;
    private String PLAYERS_TABLE;
    private Connection connection;

    // Credientials
    private String host;
    private String database;
    private String username;
    private String password;
    private long port;

    public SQLData(Main plugin) {
        this.plugin = plugin;
        this.plugin.getLogger().info("Initializing SQLData...");
        register();
    }

    @Override
    public void register() {
        String path = "database.mysql-credentials";
        FileConfiguration config = plugin.getConfig();
        this.host = config.getString(path + ".host");
        this.database = config.getString(path + ".database");
        this.username = config.getString(path + ".username");
        this.password = config.getString(path + ".password");
        this.port = config.getLong(path + ".port");
        this.PLAYERS_TABLE = config.getString(path + "table_prefix") + "players";

        openConnection();
        createTables();
    }

    @Override
    public void unregister() {

    }

    // Load the user into the database
    public void loadUser(UUID uuid) {
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM " + PLAYERS_TABLE + " WHERE UUID='" + uuid.toString() + "';").executeQuery();
            if (!resultSet.next()) {
                connection.createStatement().executeUpdate("INSERT INTO " + PLAYERS_TABLE + " (UUID, SELF_HOLOGRAM) VALUES ('" + uuid + "', FALSE)");
                this.plugin.getLogger().info("Player " + uuid + " was not found in the database, adding him now.");
                this.plugin.getHandler().getCacheData().loadUser(uuid, new PlayerData(uuid));
            } else {
                PlayerData playerData = new PlayerData(uuid);
                playerData.setSelfHologram(resultSet.getBoolean("SELF_HOLOGRAM"));
                this.plugin.getHandler().getCacheData().loadUser(uuid, playerData);
                this.plugin.getLogger().info("Loading player " + uuid + " from the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Save the user into the database
    public void saveUser(UUID uuid, PlayerData playerData) {
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM " + PLAYERS_TABLE + " WHERE UUID='" + uuid.toString() + "';").executeQuery();
            if (resultSet.next())
                connection.createStatement().executeUpdate("UPDATE " + PLAYERS_TABLE + " SET SELF_HOLOGRAM=" + playerData.isSelfHologram() + " WHERE UUID='" + uuid + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create tables
    private void createTables() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + PLAYERS_TABLE + "` (UUID VARCHAR(36), SELF_HOLOGRAM BOOLEAN, PRIMARY KEY (UUID)) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Testing
    public void dropTables() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE " + PLAYERS_TABLE + ";");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Database Connection
    private Connection openConnection() {
        try {
            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return connection;
                } else {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useJDBCCompliantTimezoneShift=true&&serverTimezone=UTC&&useUnicode=true&autoReconnect=true", username, password);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

}
