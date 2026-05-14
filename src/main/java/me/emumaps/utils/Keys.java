package me.emumaps.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class Keys {

    public static NamespacedKey KIT_KEY;
    public static NamespacedKey TEAM_COLOR_KEY;
    public static NamespacedKey BEACON_KEY;

    private Keys() {}

    public static void init(Plugin plugin) {
        KIT_KEY = new NamespacedKey(plugin, "kit_key");
        TEAM_COLOR_KEY = new NamespacedKey(plugin, "team_color_key");
        BEACON_KEY = new NamespacedKey(plugin, "beacon_key");
    }
}