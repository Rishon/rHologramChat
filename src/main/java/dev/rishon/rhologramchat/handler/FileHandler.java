package dev.rishon.rhologramchat.handler;

import dev.rishon.rhologramchat.components.DataTypes;

import java.io.*;
import java.util.UUID;

public class FileHandler {

    private final MainHandler handler;

    public FileHandler(MainHandler handler) {
        this.handler = handler;
        init();
    }

    void init() {
        loadFiles();
        checkDataType();
        createPlayerDataDirectory();
    }

    public void loadFiles() {
        handler.getConfig().options().copyDefaults(true);
        handler.getPlugin().saveDefaultConfig();
        handler.getPlugin().reloadConfig();
    }

    private void checkDataType() {
        String type = handler.getConfig().getString("storage.type");
        if (type == null) {
            this.handler.setDataType(DataTypes.YAML);
            return;
        }
        type = type.toUpperCase();
        switch (type) {
            case "MYSQL" -> this.handler.setDataType(DataTypes.MYSQL);
            case "SQLITE" -> this.handler.setDataType(DataTypes.SQLITE);
            default -> this.handler.setDataType(DataTypes.YAML);
        }
    }

    // Check if playerdata dir exists
    public void createPlayerDataDirectory() {
        if (!this.handler.getDataType().equals(DataTypes.YAML)) return;
        File playerDataDir = new File(handler.getPlugin().getDataFolder() + File.separator + "playerdata");
        if (playerDataDir.mkdir()) handler.getPlugin().getLogger().info("Created playerdata directory!");
    }

    // Create Player Data File
    public void createPlayerData(UUID uuid) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(handler.getPlugin().getDataFolder() + "/playerdata/" + uuid + ".yml"))) {
            writer.write("UUID: " + uuid);
            writer.newLine();
            writer.write("SELF_HOLOGRAM: false");
            writer.close();
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get String from Player Data File
    public String getStringValue(UUID uuid, String value) {
        try (BufferedReader reader = new BufferedReader(new FileReader(handler.getPlugin().getDataFolder() + "/playerdata/" + uuid + ".yml"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(value)) {
                    return line.split(": ")[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    // Get existing key and value from Player Data File and change it
    public void setNewValue(UUID uuid, String key, String value) {
        try (BufferedReader reader = new BufferedReader(new FileReader(handler.getPlugin().getDataFolder() + "/playerdata/" + uuid + ".yml"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(key)) {
                    line = key + ": " + value;
                    saveValue(uuid, key, line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void saveValue(UUID uuid, String value, String newValue) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(handler.getPlugin().getDataFolder() + "/playerdata/" + uuid + ".yml"))) {
            writer.write(value + ": " + newValue);
            writer.close();
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
