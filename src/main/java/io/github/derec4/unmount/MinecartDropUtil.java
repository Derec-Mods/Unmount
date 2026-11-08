package io.github.derec4.unmount;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

class MinecartDropUtil {

    private MinecartDropUtil() {
    }

    static void dropMinecartLikeVanilla(Minecart cart) {
        cart.getWorld().dropItemNaturally(cart.getLocation(), createMinecartDrop(cart));
    }

    private static ItemStack createMinecartDrop(Minecart cart) {
        ItemStack drop = new ItemStack(Material.MINECART);

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
}
