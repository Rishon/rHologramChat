package dev.rishon.rhologramchat.data.player;

import lombok.Data;

import java.util.UUID;

@Data
public class PlayerData {

    private UUID uuid;
    private boolean selfHologram;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }


}
