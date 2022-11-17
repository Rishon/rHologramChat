package dev.rishon.rhologramchat.data;

import dev.rishon.rhologramchat.data.player.PlayerData;
import dev.rishon.rhologramchat.handler.Handler;
import dev.rishon.rhologramchat.handler.MainHandler;
import dev.rishon.rhologramchat.types.DataTypes;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.UUID;

public class SQLData implements Handler {

    private final MainHandler handler;
    private boolean enabled;
    private String PLAYERS_TABLE;
    private Connection connection;

    // Credentials
    private String host;
    private String database;
    private String username;
    private String password;
    private int port;

    public SQLData(MainHandler handler) {
        this.handler = handler;
        this.handler.getPlugin().getLogger().info("Initializing SQLData...");
        register();
    }

    @Override
    public void register() {

        if (!this.handler.getDataType().equals(DataTypes.MYSQL)) return;

        this.enabled = true;
        String path = "storage.mysql-credentials";
        FileConfiguration config = handler.getConfig();
        this.host = config.getString(path + ".host");
        this.database = config.getString(path + ".database");
        this.username = config.getString(path + ".username");
        this.password = config.getString(path + ".password");
        this.port = config.getInt(path + ".port");
        this.PLAYERS_TABLE = config.getString(path + ".table_prefix") + "players";
        openConnection();
        createTables();
    }

    @Override
    public void unregister() {
        savePlayers();
        closeConnection();
        this.enabled = false;
    }

    // Load the user into the database
    public void loadUser(UUID uuid) {
        if (!enabled) return;
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM " + PLAYERS_TABLE + " WHERE UUID='" + uuid.toString() + "';").executeQuery();
            if (!resultSet.next()) {
                connection.createStatement().executeUpdate("INSERT INTO " + PLAYERS_TABLE + " (UUID, SELF_HOLOGRAM) VALUES ('" + uuid + "', FALSE)");
                this.handler.getCacheData().loadUser(uuid, new PlayerData(uuid));
            } else {
                PlayerData playerData = new PlayerData(uuid);
                playerData.setSelfHologram(resultSet.getBoolean("SELF_HOLOGRAM"));
                this.handler.getCacheData().loadUser(uuid, playerData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Save the user into the database
    public void saveUser(UUID uuid, PlayerData playerData) {
        if (!enabled) return;
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
        if (!enabled) return;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + PLAYERS_TABLE + "` (UUID VARCHAR(36), SELF_HOLOGRAM BOOLEAN, PRIMARY KEY (UUID)) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Testing
    public void dropTables() {
        if (!enabled) return;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE " + PLAYERS_TABLE + ";");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void savePlayers() {
        if (!enabled) return;
        this.handler.getPlugin().getLogger().info("Saving players...");
        this.handler.getPlugin().getHandler().getCacheData().getData().keySet().forEach(uuid -> this.handler.getCacheData().saveUser(uuid));
    }

    // Database Connection
    private Connection openConnection() {
        if (!enabled) return null;
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
            this.enabled = false;
            this.handler.getPlugin().getLogger().warning("Could not connect to MySQL server!\n" + e.getMessage());
        }
        return connection;
    }

    private void closeConnection() {
        if (!enabled) return;
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}
