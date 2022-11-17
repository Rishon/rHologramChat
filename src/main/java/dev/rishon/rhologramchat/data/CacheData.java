package dev.rishon.rhologramchat.data;

import dev.rishon.rhologramchat.data.player.PlayerData;
import dev.rishon.rhologramchat.handler.MainHandler;
import lombok.Data;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class CacheData {

    private final MainHandler handler;

    private final Map<UUID, PlayerData> data;

    public CacheData(MainHandler handler) {
        this.handler = handler;
        this.data = new ConcurrentHashMap<>();
    }

    public void loadUser(UUID uuid, PlayerData playerData) {
        data.put(uuid, playerData);
    }

    public void saveUser(UUID uuid) {
        if (data.containsKey(uuid)) {

            switch (this.handler.getDataType()) {
                case MYSQL -> this.handler.getSqlData().saveUser(uuid, data.get(uuid));
                case YAML -> this.handler.getFileHandler().updateExistingPlayerData(uuid);
            }

            data.remove(uuid);
        }
    }
}
