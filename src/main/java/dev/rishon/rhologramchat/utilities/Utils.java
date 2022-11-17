package dev.rishon.rhologramchat.utilities;

import dev.rishon.rhologramchat.types.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static double fixHeight(double state) {
        if (state == 1) {
            return 1;
        } else if (state == 2) {
            return 1.3;
        }
        return state;
    }

    public static Component fixText(Player player, Component text) {
        String serialize = LegacyComponentSerializer.legacySection().serialize(text);
        if (player.hasPermission("rhologramchat.color")) serialize = colored(serialize);
        if (serialize.length() > 50) return Component.text(serialize.substring(0, 50) + "...");
        return Component.text(serialize);
    }

    public static String colored(String message) {
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            ChatColor color = ChatColor.of(matcher.group().substring(1));
            String start = message.substring(0, matcher.start());
            String end = message.substring(matcher.end());
            message = start + color + end;
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendConfigMessage(CommandSender sender, Messages type) {
        String[] messages = type.getValue();
        messages = messages == null ? new String[0] : messages;
        if (messages.length == 0) return;
        for (String s : messages) {
            sender.sendMessage(Utils.colored(s));
        }
    }


}
