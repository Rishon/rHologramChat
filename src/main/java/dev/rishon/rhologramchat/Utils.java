package dev.rishon.rhologramchat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
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

    private static String colored(String message) {
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            final ChatColor hexColor = ChatColor.of(matcher.group().substring(1));
            final String before = message.substring(0, matcher.start());
            final String after = message.substring(matcher.end());
            message = before + hexColor + after;
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }


}
