package dev.rishon.rhologramchat.commands;

import dev.rishon.rhologramchat.data.CacheData;
import dev.rishon.rhologramchat.data.player.PlayerData;
import dev.rishon.rhologramchat.types.Messages;
import dev.rishon.rhologramchat.utilities.Utils;
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
            Utils.sendConfigMessage(sender, Messages.NO_CONSOLE);
            return true;
        }

        if (!player.hasPermission("rhologramchat.cmd.selfhologram")) {
            Utils.sendConfigMessage(player, Messages.NO_PERMISSION);
            return true;
        }

        UUID uuid = player.getUniqueId();
        PlayerData data = this.cacheData.getData().get(uuid);

        if (data == null) {
            Utils.sendConfigMessage(player, Messages.DATA_ERROR);
            return true;
        }

        if (data.isSelfHologram()) {
            data.setSelfHologram(false);
            String[] selfHologramDisabled = Messages.SELF_DISABLED.getValue();
            for (String s : selfHologramDisabled) {
                player.sendMessage(Utils.colored(s));
            }
        } else {
            data.setSelfHologram(true);
            String[] selfHologramEnabled = Messages.SELF_ENABLED.getValue();
            for (String s : selfHologramEnabled) {
                player.sendMessage(Utils.colored(s));
            }
        }

        return false;
    }
}
