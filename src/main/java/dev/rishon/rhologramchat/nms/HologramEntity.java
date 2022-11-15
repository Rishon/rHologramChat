package dev.rishon.rhologramchat.nms;

import dev.rishon.rhologramchat.handler.NMSHandler;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
public class HologramEntity {

    private final NMSHandler handler;
    private final Player player;
    private final Component text;
    private EntityArmorStand entity;
    private double state = 1;
    private int task;

    public HologramEntity(NMSHandler handler, Player player, Component text) {
        this.handler = handler;
        this.player = player;
        this.text = text;
    }

    private void create() {
        CraftWorld world = (CraftWorld) player.getWorld();
        Location location = player.getLocation();
        setEntity(new EntityArmorStand(world.getHandle(), location.getX(), location.getY() + 0.5, location.getZ()));
        ArmorStand armorStand = (ArmorStand) entity.getBukkitEntity();
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.customName(text);
        armorStand.setCustomNameVisible(true);
        armorStand.setSmall(true);
        armorStand.setMarker(false);
        armorStand.setInvulnerable(true);
    }

    public void spawn() {
        create();
        UUID uuid = player.getUniqueId();
        DataWatcher watcher = entity.ai();
        watcher.b(DataWatcherRegistry.a.a(0), (byte) 32);
        CompletableFuture.runAsync(() -> {
            ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutSpawnEntity(entity, 78));
            ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityMetadata(entity.ae(), watcher, true));
        });
        List<HologramEntity> holograms = handler.getHolograms().get(uuid);
        if (holograms != null) {
            Bukkit.broadcastMessage("holograms size: " + holograms.size());
            holograms.add(this);
            handler.getHolograms().put(uuid, holograms);
        } else {
            holograms = new LinkedList<>();
            holograms.add(this);
            handler.getHolograms().put(uuid, holograms);
        }
    }

    public void update() {
        Location location = player.getLocation();
        CompletableFuture.runAsync(() -> {
            entity.a(location.getX(), location.getY() + state, location.getZ());
            PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(entity);
            DataWatcher watcher = entity.ai();
            watcher.b(DataWatcherRegistry.a.a(0), (byte) 32);
            ((CraftPlayer) player).getHandle().b.a(packet);
            ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityMetadata(entity.ae(), watcher, true));
        });
    }

    public void remove() {
        UUID uuid = player.getUniqueId();
        CompletableFuture.runAsync(() -> ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityDestroy(entity.ae())));
        List<HologramEntity> holograms = handler.getHolograms().get(uuid);
        holograms.remove(this);
        handler.getHolograms().put(uuid, holograms);
    }

    public void destroy() {
        UUID uuid = player.getUniqueId();
        ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityDestroy(entity.ae()));
        handler.getHolograms().remove(uuid);
    }

}
