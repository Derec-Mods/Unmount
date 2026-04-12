package io.github.derec4.unmount.commands;

import io.github.derec4.unmount.Unmount;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class ToggleCartCommand implements CommandExecutor {

    private final NamespacedKey toggleKey;

    public ToggleCartCommand(Unmount plugin) {
        this.toggleKey = new NamespacedKey(plugin, "autobreak_disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        // Get the player's data container
        var pdc = player.getPersistentDataContainer();

        if (pdc.has(toggleKey, PersistentDataType.BYTE)) {
            pdc.remove(toggleKey);
            player.sendMessage("§aMinecart auto-break is now ENABLED.");
        } else {
            pdc.set(toggleKey, PersistentDataType.BYTE, (byte) 1);
            player.sendMessage("§cMinecart auto-break is now DISABLED.");
        }

        return true;
    }
}