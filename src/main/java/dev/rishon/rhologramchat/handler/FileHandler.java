package dev.rishon.rhologramchat.handler;

import dev.rishon.rhologramchat.components.DataTypes;
import dev.rishon.rhologramchat.data.player.PlayerData;

import java.io.*;
import java.util.UUID;

public class FileHandler implements Handler {

    private final MainHandler handler;

    public FileHandler(MainHandler handler) {
        this.handler = handler;
        register();
    }

    @Override
    public void register() {
        loadFiles();
        checkDataType();
        createPlayerDataDirectory();
        loadPlayers();
    }

    @Override
    public void unregister() {
        handler.getPlugin().reloadConfig();
        handler.getPlugin().saveConfig();
        savePlayers();
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
    private void createPlayerDataDirectory() {
        if (!this.handler.getDataType().equals(DataTypes.YAML)) return;
        File playerDataDir = new File(handler.getPlugin().getDataFolder() + File.separator + "playerdata");
        if (playerDataDir.mkdir()) handler.getPlugin().getLogger().info("Created playerdata directory!");
    }

    // Create Player Data File
    private void createPlayerData(UUID uuid) {
        if (!this.handler.getDataType().equals(DataTypes.YAML)) return;
        if (checkIfPlayerDataExists(uuid)) return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(handler.getPlugin().getDataFolder() + "/playerdata/" + uuid + ".yml"))) {
            writer.write("UUID: " + uuid);
            writer.newLine();
            writer.write("SELF_HOLOGRAM: false");
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateExistingPlayerData(UUID uuid) {
        if (!this.handler.getDataType().equals(DataTypes.YAML)) return;
        if (!checkIfPlayerDataExists(uuid)) return;
        PlayerData data = handler.getCacheData().getData().get(uuid);
        setNewValue(uuid, "SELF_HOLOGRAM", String.valueOf(data.isSelfHologram()).toLowerCase());
    }

    // Get String from Player Data File
    private String getStringValue(UUID uuid, String value) {
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

    // Update value from key in file
    private void setNewValue(UUID uuid, String key, String value) {
        if (!this.handler.getDataType().equals(DataTypes.YAML)) return;
        if (!checkIfPlayerDataExists(uuid)) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(handler.getPlugin().getDataFolder() + "/playerdata/" + uuid + ".yml"))) {
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(key)) {
                    builder.append(key).append(": ").append(value).append("\n");
                } else {
                    builder.append(line).append("\n");
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(handler.getPlugin().getDataFolder() + "/playerdata/" + uuid + ".yml"))) {
                writer.write(builder.toString());
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfPlayerDataExists(UUID uuid) {
        File file = new File(handler.getPlugin().getDataFolder() + "/playerdata/" + uuid + ".yml");
        return file.exists();
    }

    private void loadPlayers() {
        if (!this.handler.getDataType().equals(DataTypes.YAML)) return;
        this.handler.getPlugin().getServer().getOnlinePlayers().forEach(player -> {
            UUID uuid = player.getUniqueId();
            createPlayerData(uuid);
            PlayerData data = new PlayerData(uuid);
            data.setSelfHologram(Boolean.parseBoolean(getStringValue(uuid, "SELF_HOLOGRAM").toLowerCase()));
            this.handler.getCacheData().loadUser(uuid, data);
        });
    }

    private void savePlayers() {
        if (!this.handler.getDataType().equals(DataTypes.YAML)) return;
        handler.getCacheData().getData().keySet().forEach(uuid -> {
            String selfHologram = handler.getCacheData().getData().get(uuid).isSelfHologram() ? "true" : "false";
            setNewValue(uuid, "SELF_HOLOGRAM", selfHologram);
        });
    }
}
