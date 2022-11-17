package dev.rishon.rhologramchat.commands;

import dev.rishon.rhologramchat.handler.MainHandler;
import dev.rishon.rhologramchat.types.Messages;
import dev.rishon.rhologramchat.utilities.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record MainCommand(MainHandler handler) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("rhologramchat.cmd.use")) {
            sender.sendMessage("You do not have permission to execute this command!");
            return true;
        }

        if (args.length == 0) {
            Utils.sendConfigMessage(sender, Messages.HELP_COMMAND);
            if (sender.hasPermission("rhologramchat.cmd.admin"))
                Utils.sendConfigMessage(sender, Messages.ADMIN_COMMAND);
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("rhologramchat.cmd.reload")) {
                    Utils.sendConfigMessage(sender, Messages.NO_PERMISSION);
                    return true;
                }
                this.handler.getFileHandler().reloadFiles();
                Utils.sendConfigMessage(sender, Messages.RELOAD_COMMAND);
                return true;
            } else if (args[0].equalsIgnoreCase("toggle")) {
                // TO-DO
                if (!(sender instanceof Player player)) {
                    Utils.sendConfigMessage(sender, Messages.NO_CONSOLE);
                    return true;
                }

                return true;
            } else if (args[0].equalsIgnoreCase("selfhologram")) {
                if (!(sender instanceof Player player)) {
                    Utils.sendConfigMessage(sender, Messages.NO_CONSOLE);
                    return true;
                }

                player.performCommand("selfhologram");
                return true;
            }
        }

        return false;
    }
}
