package me.hsgamer.varblocks.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;

public final class PlaceholderAPIHook {
    private PlaceholderAPIHook() {

    }

    public static String replace(String text) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(null, text);
        }
        return text;
    }
}
