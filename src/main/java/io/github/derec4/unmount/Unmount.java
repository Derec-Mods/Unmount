package io.github.derec4.unmount;

import io.github.derec4.unmount.commands.ToggleCartCommand;
import io.github.derec4.unmount.pdc.PDCManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Unmount extends JavaPlugin {

    private static Unmount instance;

    @Override
    public void onEnable() {

        instance = this;

        PDCManager.init(this);
        ConfigManager.load(this);

        // Commands
        var toggleCmd = getCommand("togglecart");
        if (toggleCmd != null) {
            ToggleCartCommand toggleCartCommand = new ToggleCartCommand();
            toggleCmd.setExecutor(toggleCartCommand);
            toggleCmd.setTabCompleter(toggleCartCommand);
        } else {
            getLogger().warning("Command 'togglecart' is missing from plugin.yml; command won't be available.");
        }

        if (ConfigManager.AUTO_BREAK_ENABLED) {
            getServer().getPluginManager().registerEvents(new MinecartDismountAutoBreakListener(), this);
            getLogger().info("Auto-break on dismount: enabled");
        } else {
            getLogger().info("Auto-break on dismount: disabled (enable it in config.yml)");
        }

        if (ConfigManager.COLLISION_ENABLED) {
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
        PDCManager.save();
        instance = null;
    }

    public static Unmount getInstance() {
        return instance;
    }
}
