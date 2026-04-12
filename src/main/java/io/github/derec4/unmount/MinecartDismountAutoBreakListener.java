package io.github.derec4.unmount;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.persistence.PersistentDataType;

public class MinecartDismountAutoBreakListener implements Listener {

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        // quick null-safe plugin reference for logging
        var plugin = Unmount.getInstance();
        if (plugin == null) {
            return;
        }

        // debug: event fired
        if (!ConfigManager.AUTO_BREAK_ENABLED || !Unmount.AUTO_BREAK_ENABLED) {
            plugin.getLogger().info("[unmount] auto-break disabled in config, ignoring exit");
            return;
        }

        Entity exited = event.getExited();
        if (!(exited instanceof Player player)) {
            return;
        }

        // pdc check for player toggle
        NamespacedKey toggleKey = new NamespacedKey(plugin, "autobreak_disabled");
        if (player.getPersistentDataContainer().has(toggleKey, PersistentDataType.BYTE)) {
            plugin.getLogger().info("[unmount] player has auto-break toggled off, ignoring");
            return;
        }

        if (!(event.getVehicle() instanceof Minecart cart)) {
            return;
        }

        if (cart.isDead() || !cart.isValid()) {
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
//            cart.setDamage(100); 4.11.2026 idk why this doesnt work sigh
            cart.getWorld().dropItemNaturally(cart.getLocation(), new org.bukkit.inventory.ItemStack(org.bukkit.Material.MINECART));
            cart.remove();
        });
    }
}
