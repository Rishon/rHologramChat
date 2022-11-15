package dev.rishon.rhologramchat.handler;

import dev.rishon.rhologramchat.nms.HologramEntity;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class NMSHandler implements Handler {

    private final MainHandler handler;

    // Map of all the holograms
    private Map<UUID, List<HologramEntity>> holograms;

    public NMSHandler(MainHandler handler) {
        this.handler = handler;
        this.getHandler().getPlugin().getLogger().info("Initializing NMSHandler...");
        this.register();
    }

    @Override
    public void register() {
        this.holograms = new ConcurrentHashMap<>();
        this.getHandler().getPlugin().getLogger().info("NMSHandler initialized!");
    }

    @Override
    public void unregister() {
        for (List<HologramEntity> hologram : this.holograms.values()) hologram.forEach(HologramEntity::destroy);
    }

}
