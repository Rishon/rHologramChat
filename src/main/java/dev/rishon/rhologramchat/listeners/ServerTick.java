package dev.rishon.rhologramchat.listeners;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.rishon.rhologramchat.handler.NMSHandler;
import dev.rishon.rhologramchat.nms.HologramEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.concurrent.CompletableFuture;

public class ServerTick implements Listener {

    private final NMSHandler handler;

    public ServerTick(NMSHandler handler) {
        this.handler = handler;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onServerTick(ServerTickStartEvent event) {
        if (this.handler.getHolograms().isEmpty()) return;
        CompletableFuture.runAsync(() -> this.handler.getHolograms().forEach((uuid, hologram) -> hologram.forEach(HologramEntity::update)));
    }

}
