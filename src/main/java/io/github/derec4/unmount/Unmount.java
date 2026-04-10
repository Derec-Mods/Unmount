package io.github.derec4.unmount;

import org.bukkit.plugin.java.JavaPlugin;

public final class Unmount extends JavaPlugin {

    public static boolean AUTO_BREAK_ENABLED = true;
    public static boolean COLLISION_ENABLED = false;
    public static double COLLISION_BULLET_SPEED_THRESHOLD = 0.5;

    private static Unmount instance;

    @Override
    public void onEnable() {

        instance = this;

        ConfigManager.load(this);

        AUTO_BREAK_ENABLED = ConfigManager.AUTO_BREAK_ENABLED;
        COLLISION_ENABLED = ConfigManager.COLLISION_ENABLED;
        COLLISION_BULLET_SPEED_THRESHOLD = ConfigManager.COLLISION_BULLET_SPEED_THRESHOLD;

        if (AUTO_BREAK_ENABLED) {
            getServer().getPluginManager().registerEvents(new MinecartDismountAutoBreakListener(), this);
            getLogger().info("Auto-break on dismount: enabled");
        } else {
            getLogger().info("Auto-break on dismount: disabled (enable it in config.yml)");
        }

        if (COLLISION_ENABLED) {
            getServer().getPluginManager().registerEvents(new MinecartCollisionListener(this), this);
            getLogger().info("Minecart collision snowplow: enabled");
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
