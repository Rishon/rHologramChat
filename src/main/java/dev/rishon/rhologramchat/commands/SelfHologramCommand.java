package dev.rishon.rhologramchat.commands;

import dev.rishon.rhologramchat.data.CacheData;
import dev.rishon.rhologramchat.data.player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SelfHologramCommand implements CommandExecutor {

    private final CacheData cacheData;

    public SelfHologramCommand(CacheData cacheData) {
        this.cacheData = cacheData;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to execute this command!");
            return true;
        }

        UUID uuid = player.getUniqueId();
        PlayerData data = this.cacheData.getData().get(uuid);

        if (data == null) {
            player.sendMessage("An error occurred while trying to get your data!");
            return true;
        }

        if (data.isSelfHologram()) {
            data.setSelfHologram(false);
            player.sendMessage("Self hologram disabled!");
        } else {
            data.setSelfHologram(true);
            player.sendMessage("Self hologram enabled!");
        }

        return false;
    }
}
