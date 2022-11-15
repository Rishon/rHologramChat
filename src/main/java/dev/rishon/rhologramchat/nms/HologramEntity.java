package dev.rishon.rhologramchat.nms;

import dev.rishon.rhologramchat.Utils;
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
        setEntity(new EntityArmorStand(world.getHandle(), location.getX(), location.getY() + Utils.fixHeight(state), location.getZ()));
        ArmorStand armorStand = (ArmorStand) entity.getBukkitEntity();
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.customName(Utils.fixText(text));
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
        setTask(player.getServer().getScheduler().runTaskLaterAsynchronously(this.handler.getHandler().getPlugin(), this::remove, 20L * 5).getTaskId());
        List<HologramEntity> holograms = handler.getHolograms().get(uuid);
        if (holograms != null) {
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
            entity.a(location.getX(), location.getY() + Utils.fixHeight(state), location.getZ());
            DataWatcher watcher = entity.ai();
            watcher.b(DataWatcherRegistry.a.a(0), (byte) 32);
            ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityTeleport(entity));
            ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityMetadata(entity.ae(), watcher, true));
        });
    }

    public void remove() {
        UUID uuid = player.getUniqueId();
        CompletableFuture.runAsync(() -> ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityDestroy(entity.ae())));
        player.getServer().getScheduler().cancelTask(getTask());
        List<HologramEntity> holograms = handler.getHolograms().get(uuid);
        holograms.remove(0);
        handler.getHolograms().put(uuid, holograms);
    }

    public void destroy() {
        UUID uuid = player.getUniqueId();
        ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityDestroy(entity.ae()));
        handler.getHolograms().remove(uuid);
    }

}
