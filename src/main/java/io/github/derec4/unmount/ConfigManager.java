package io.github.derec4.unmount;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Simple config manager that ensures the config exists and reads values into static fields.
 * Values are loaded with reasonable defaults and can be reloaded by calling load(plugin).
 */
public final class ConfigManager {

    // Config-backed static values (read on startup)
    public static boolean COLLISION_ENABLED = false;
    public static double COLLISION_BULLET_SPEED_THRESHOLD = 0.5;

    private static JavaPlugin plugin;

    private ConfigManager() { }

    /**
     * Load (or reload) the config from disk and populate static fields. Creates default config if missing.
     */
    public static void load(JavaPlugin owningPlugin) {
        plugin = owningPlugin;

        // Ensure config file exists in plugin data folder
        plugin.saveDefaultConfig();

        // Ensure defaults are applied on disk
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();

        // Read config values
        plugin.reloadConfig();

        COLLISION_ENABLED = plugin.getConfig().getBoolean("collision.enabled", false);
        COLLISION_BULLET_SPEED_THRESHOLD = plugin.getConfig().getDouble("collision.bulletSpeedThreshold", 0.5);

        if (COLLISION_BULLET_SPEED_THRESHOLD < 0.0) {
            COLLISION_BULLET_SPEED_THRESHOLD = 0.0;
        }
    }

    public static void reload() {
        if (plugin != null) {
            load(plugin);
        }
    }
}

