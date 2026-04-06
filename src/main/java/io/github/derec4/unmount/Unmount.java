package io.github.derec4.unmount;

import org.bukkit.plugin.java.JavaPlugin;

public final class Unmount extends JavaPlugin {

    private static Unmount instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        // Register collision listener
        getServer().getPluginManager().registerEvents(new MinecartCollisionListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
    }

    public static Unmount getInstance() {
        return instance;
    }
}
