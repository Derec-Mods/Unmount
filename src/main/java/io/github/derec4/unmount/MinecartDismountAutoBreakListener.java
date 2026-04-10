package io.github.derec4.unmount;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

public class MinecartDismountAutoBreakListener implements Listener {

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (!ConfigManager.AUTO_BREAK_ENABLED || !Unmount.AUTO_BREAK_ENABLED) {
            return;
        }

        Entity exited = event.getExited();
        if (!(exited instanceof Player)) {
            return;
        }

        if (!(event.getVehicle() instanceof Minecart cart)) {
            return;
        }

        if (cart.isDead() || !cart.isValid()) {
            return;
        }

        // wait a tick so the dismount finishes cleanly
        Unmount.getInstance().getServer().getScheduler().runTask(Unmount.getInstance(), () -> {
            if (cart.isDead() || !cart.isValid()) {
                return;
            }

            if (!cart.getPassengers().isEmpty()) {
                return;
            }

            cart.setDamage(100);
        });
    }
}

