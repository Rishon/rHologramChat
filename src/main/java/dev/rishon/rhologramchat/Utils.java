package dev.rishon.rhologramchat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Utils {

    public static double fixHeight(double state) {
        if (state == 1) {
            return 1;
        } else if (state == 2) {
            return 1.3;
        }
        return state;
    }

    public static Component fixText(Component text) {
        String serialize = LegacyComponentSerializer.legacySection().serialize(text);
        if (serialize.length() > 50) return Component.text(serialize.substring(0, 50) + "...");
        return text;
    }

}
