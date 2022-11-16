package dev.rishon.rhologramchat.listeners;

import dev.rishon.rhologramchat.handler.MainHandler;
import dev.rishon.rhologramchat.nms.HologramEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Connections implements Listener {

    private final MainHandler handler;

    public Connections(MainHandler handler) {
        this.handler = handler;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onConnection(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        CompletableFuture.runAsync(() -> this.handler.getSqlData().loadUser(uuid));
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
       this.handler.getCacheData().saveUser(uuid);
    }
}
