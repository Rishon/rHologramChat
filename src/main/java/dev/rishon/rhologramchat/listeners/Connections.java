package dev.rishon.rhologramchat.listeners;

import dev.rishon.rhologramchat.handler.MainHandler;
import dev.rishon.rhologramchat.nms.HologramEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;

public class Connections implements Listener {

    private final MainHandler handler;

    public Connections(MainHandler handler) {
        this.handler = handler;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        List<HologramEntity> holograms = this.handler.getNmsHandler().getHolograms().get(uuid);
        if (holograms != null && !holograms.isEmpty()) {
            holograms.forEach(HologramEntity::remove);
            this.handler.getNmsHandler().getHolograms().remove(uuid);
        }
    }
}
