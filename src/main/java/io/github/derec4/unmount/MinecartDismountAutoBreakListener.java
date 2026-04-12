package io.github.derec4.unmount;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class MinecartDismountAutoBreakListener implements Listener {

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        // quick null-safe plugin reference for logging
        var plugin = Unmount.getInstance();
        if (plugin == null) {
            return;
        }

        // debug: event fired
        plugin.getLogger().info("[unmount] vehicle exit event fired: exited=" + event.getExited() + ", vehicle=" + event.getVehicle());

        if (!ConfigManager.AUTO_BREAK_ENABLED || !Unmount.AUTO_BREAK_ENABLED) {
            plugin.getLogger().info("[unmount] auto-break disabled in config, ignoring exit");
            return;
        }

        Entity exited = event.getExited();
        if (!(exited instanceof Player)) {
            plugin.getLogger().info("[unmount] exited entity is not a player, ignoring: " + exited.getType());
            return;
        }

        if (!(event.getVehicle() instanceof Minecart cart)) {
            plugin.getLogger().info("[unmount] vehicle is not a minecart, ignoring: " + event.getVehicle().getType());
            return;
        }

        if (cart.isDead() || !cart.isValid()) {
            plugin.getLogger().info("[unmount] cart is dead or invalid, ignoring");
            return;
        }

        // wait a tick so the dismount finishes cleanly
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getLogger().info("[unmount] scheduled check running for cart at " + cart.getLocation());

            if (cart.isDead() || !cart.isValid()) {
                plugin.getLogger().info("[unmount] cart became dead/invalid before break");
                return;
            }

            if (!cart.getPassengers().isEmpty()) {
                plugin.getLogger().info("[unmount] cart has passengers after dismount, not breaking");
                return;
            }

            plugin.getLogger().info("[unmount] breaking empty minecart");
            cart.setDamage(100);
        });
    }
}
