package io.github.derec4.unmount;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MinecartCollisionListener implements Listener {

    private final JavaPlugin plugin;
    // Speed threshold for "bullet" carts. Adjustable.
    private final double bulletSpeedThreshold = 0.5;

    public MinecartCollisionListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTrackClearCollision(VehicleEntityCollisionEvent event) {
        // Ensure both the vehicle and the collided entity are Minecarts
        if (!(event.getVehicle() instanceof Minecart movingCart)) return;
        if (!(event.getEntity() instanceof Minecart hitCart)) return;

        Vector velocity = movingCart.getVelocity();
        double speed = velocity.length();

        if (speed < bulletSpeedThreshold) return;

        // Only break empty, vanilla passenger minecarts
        if (!hitCart.getPassengers().isEmpty()) return;
        if (hitCart.getType() != EntityType.MINECART) return;

        // Cancel vanilla collision physics (prevents bounce-back)
        event.setCancelled(true);
        try {
            // newer Spigot/Paper expose this method to specifically cancel collision physics
            event.setCollisionCancelled(true);
        } catch (NoSuchMethodError ignored) {
            // Older API: ignore if method doesn't exist
        }

        // Drop the minecart item so it isn't lost
        hitCart.getWorld().dropItemNaturally(hitCart.getLocation(), new ItemStack(Material.MINECART));

        // Remove the abandoned cart from world
        hitCart.remove();

        // Re-apply the velocity to the moving cart immediately and on the next tick
        movingCart.setVelocity(velocity);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!movingCart.isDead()) {
                    movingCart.setVelocity(velocity);
                }
            }
        }.runTaskLater(plugin, 1L);
    }
}

