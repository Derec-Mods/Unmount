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

    public MinecartCollisionListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onTrackClearCollision(VehicleEntityCollisionEvent event) {
        // config checks
        if (!ConfigManager.COLLISION_ENABLED) {
            return;
        }
        if (!Unmount.COLLISION_ENABLED) {
            return;
        }

        // minecart vs minecart only
        if (!(event.getVehicle() instanceof Minecart movingCart)) {
            return;
        }
        if (!(event.getEntity() instanceof Minecart hitCart)) {
            return;
        }

        Vector velocity = movingCart.getVelocity();
        double speed = velocity.length();
        if (speed < Unmount.COLLISION_BULLET_SPEED_THRESHOLD) {
            return;
        }
        if (speed < ConfigManager.COLLISION_BULLET_SPEED_THRESHOLD) {
            return;
        }

        // only break empty, vanilla minecarts
        if (!hitCart.getPassengers().isEmpty()) {
            return;
        }
        if (hitCart.getType() != EntityType.MINECART) {
            return;
        }

        // stop bounce
        event.setCancelled(true);
        try {
            event.setCollisionCancelled(true);
        } catch (NoSuchMethodError ignored) {
            // older api
        }

        // drop cart, delete entity
        hitCart.getWorld().dropItemNaturally(hitCart.getLocation(), new ItemStack(Material.MINECART));
        hitCart.remove();

        // keep speed
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