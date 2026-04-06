package io.github.derec4.unmount;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Unmount extends JavaPlugin {

    // --- Config (loaded on startup) ---
    public static boolean COLLISION_ENABLED = false;
    public static double COLLISION_BULLET_SPEED_THRESHOLD = 0.5;

    private static Unmount instance;

    @Override
    public void onEnable() {


        loadConfigIntoStatics();

        if (COLLISION_ENABLED) {
            getServer().getPluginManager().registerEvents(new MinecartCollisionListener(this), this);
            getLogger().info("Minecart collision snowplow: enabled");
        } else {
            getLogger().info("Minecart collision snowplow: disabled (enable it in config.yml)");
        }
    }

    private void loadConfigIntoStatics() {
        // Creates plugins/Unmount/config.yml from jar resources if missing
        saveDefaultConfig();

        // Ensure defaults exist on disk if we add more options later
        getConfig().options().copyDefaults(true);
        saveConfig();

        reloadConfig();
        FileConfiguration cfg = getConfig();

        COLLISION_ENABLED = cfg.getBoolean("collision.enabled", false);
        COLLISION_BULLET_SPEED_THRESHOLD = cfg.getDouble("collision.bulletSpeedThreshold", 0.5);

        if (COLLISION_BULLET_SPEED_THRESHOLD < 0.0) {
            COLLISION_BULLET_SPEED_THRESHOLD = 0.0;
        }

        // Register listeners based on config
        if (ConfigManager.COLLISION_ENABLED) {
            getServer().getPluginManager().registerEvents(new MinecartCollisionListener(this), this);
        } else {
            getLogger().info("Minecart collision snowplow: disabled (enable it in config.yml)");
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static Unmount getInstance() {
        return instance;
    }
}
