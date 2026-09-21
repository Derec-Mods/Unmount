package io.github.derec4.unmount;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

final class MinecartDropUtil {
    private MinecartDropUtil() {
    }

    static void dropMinecartLikeVanilla(AbstractMinecart cart) {
        ItemStack drop = new ItemStack(mapTypeToItem(cart.getType()));
        if (cart.hasCustomName()) {
            drop.set(DataComponents.CUSTOM_NAME, cart.getCustomName());
        }

        Level level = cart.level();
        level.addFreshEntity(new ItemEntity(level, cart.getX(), cart.getY(), cart.getZ(), drop));
    }

    private static Item mapTypeToItem(EntityType<?> type) {
        if (type == EntityType.CHEST_MINECART) {
            return Items.CHEST_MINECART;
        }
        if (type == EntityType.FURNACE_MINECART) {
            return Items.FURNACE_MINECART;
        }
        if (type == EntityType.TNT_MINECART) {
            return Items.TNT_MINECART;
        }
        if (type == EntityType.HOPPER_MINECART) {
            return Items.HOPPER_MINECART;
        }
        if (type == EntityType.COMMAND_BLOCK_MINECART) {
            return Items.COMMAND_BLOCK_MINECART;
        }
        return Items.MINECART;
    }
}
