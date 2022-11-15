package dev.rishon.rhologramchat.listeners;

import dev.rishon.rhologramchat.handler.MainHandler;
import dev.rishon.rhologramchat.handler.NMSHandler;
import dev.rishon.rhologramchat.nms.HologramEntity;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class PlayerChat implements Listener {

    public MainHandler handler;
    @Getter
    public NMSHandler nms;

    public PlayerChat(MainHandler handler) {
        this.handler = handler;
        this.nms = handler.getNmsHandler();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerChat(AsyncChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Component message = event.message();

        HologramEntity hologram = new HologramEntity(this.getNms(), player, message);

        List<HologramEntity> holograms = this.nms.getHolograms().get(uuid);


        HologramEntity oldHologram = holograms.get(0);
        Bukkit.broadcastMessage("test " + holograms.size() + " | state " + oldHologram.getState());
        if (oldHologram.getState() >= 2.0) {
            oldHologram.remove();
            return;
        }

        oldHologram.setState(oldHologram.getState() + 0.5);
        oldHologram.update();
        holograms.set(holograms.indexOf(oldHologram), oldHologram);
        this.nms.getHolograms().put(uuid, holograms);
    }

}
