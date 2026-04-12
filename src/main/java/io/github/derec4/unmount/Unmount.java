package io.github.derec4.unmount;

import io.github.derec4.unmount.commands.ToggleCartCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        // Commands
        var toggleCmd = getCommand("togglecart");
        if (toggleCmd != null) {
            toggleCmd.setExecutor(new ToggleCartCommand(this));
        } else {
            getLogger().warning("Command 'togglecart' is missing from plugin.yml; command won't be available.");
        }

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

        Bukkit.getLogger().info("");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "  |_______|                             " +
                "  ");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "  | Derex |     Unmount v" + getDescription().getVersion());
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "  |_______|     Running on " + Bukkit.getName() + " - " + Bukkit.getVersion());
        Bukkit.getLogger().info("");
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static Unmount getInstance() {
        return instance;
    }
}
