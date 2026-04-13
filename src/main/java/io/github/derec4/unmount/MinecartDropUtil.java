package io.github.derec4.unmount;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

final class MinecartDropUtil {

    private MinecartDropUtil() {
    }

    static void dropMinecartLikeVanilla(Minecart cart) {
        cart.getWorld().dropItemNaturally(cart.getLocation(), createMinecartDrop(cart));
    }

    private static ItemStack createMinecartDrop(Minecart cart) {
        ItemStack drop = new ItemStack(mapTypeToItem(cart.getType()));

        String customName = cart.getCustomName();
        if (customName != null && !customName.isBlank()) {
            ItemMeta meta = drop.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(customName);
                drop.setItemMeta(meta);
            }
        }

        return drop;
    }

    private static Material mapTypeToItem(EntityType type) {
        return switch (type) {
            case MINECART -> Material.MINECART;
            case CHEST_MINECART -> Material.CHEST_MINECART;
            case FURNACE_MINECART -> Material.FURNACE_MINECART;
            case TNT_MINECART -> Material.TNT_MINECART;
            case HOPPER_MINECART -> Material.HOPPER_MINECART;
            case COMMAND_BLOCK_MINECART -> Material.COMMAND_BLOCK_MINECART;
            default -> Material.MINECART;
        };
    }
}
