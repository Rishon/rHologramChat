package dev.rishon.rhologramchat.data;

import dev.rishon.rhologramchat.Main;
import dev.rishon.rhologramchat.data.player.PlayerData;
import lombok.Data;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class CacheData {

    private final Main plugin;

    private final Map<UUID, PlayerData> data;

    public CacheData(Main plugin) {
        this.plugin = plugin;
        this.data = new ConcurrentHashMap<>();
    }

    public void loadUser(UUID uuid, PlayerData playerData) {
        data.put(uuid, playerData);
    }

    public void saveUser(UUID uuid) {
        if (data.containsKey(uuid)) {
            plugin.getHandler().getSqlData().saveUser(uuid, data.get(uuid));
            data.remove(uuid);
        }
    }
}
