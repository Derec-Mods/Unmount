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
        this.plugin = plugin;
    }

    @EventHandler
    public void onTrackClearCollision(VehicleEntityCollisionEvent event) {
        // Extra guard: even if listener is registered, allow runtime toggling via /reload etc.
        if (!ConfigManager.COLLISION_ENABLED) return;
        // Extra guard: even if listener is registered, allow runtime toggling via /reload etc.
        if (!Unmount.COLLISION_ENABLED) return;


        // Ensure both the vehicle and the collided entity are Minecarts
        if (!(event.getVehicle() instanceof Minecart movingCart)) return;
        if (!(event.getEntity() instanceof Minecart hitCart)) return;

        Vector velocity = movingCart.getVelocity();
        double speed = velocity.length();
        if (speed < Unmount.COLLISION_BULLET_SPEED_THRESHOLD) return;
        if (speed < ConfigManager.COLLISION_BULLET_SPEED_THRESHOLD) return;

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
