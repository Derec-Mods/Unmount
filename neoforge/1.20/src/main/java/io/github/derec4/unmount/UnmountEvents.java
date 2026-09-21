package io.github.derec4.unmount;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

final class UnmountEvents {
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("togglecart").executes(this::toggleAutoBreak));
        event.getDispatcher().register(Commands.literal("autobreak").executes(this::toggleAutoBreak));
    }

    @SubscribeEvent
    public void onDismount(EntityMountEvent event) {
        if (event.isMounting() || event.getLevel().isClientSide() || !UnmountConfig.AUTO_BREAK_ENABLED.get()) {
            return;
        }
        if (!(event.getEntityMounting() instanceof Player player)) {
            return;
        }
        if (!(event.getEntityBeingMounted() instanceof AbstractMinecart cart)) {
            return;
        }
        if (player.getPersistentData().getBoolean(Unmount.TOGGLE_KEY)) {
            return;
        }
        if (!cart.isAlive()) {
            return;
        }

        if (!(cart.level() instanceof ServerLevel level) || level.getServer() == null) {
            return;
        }

        level.getServer().tell(new TickTask(level.getServer().getTickCount() + 1, () -> {
            if (!cart.isAlive() || !cart.getPassengers().isEmpty()) {
                return;
            }
            MinecartDropUtil.dropMinecartLikeVanilla(cart);
            cart.discard();
        }));
    }

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient() || !UnmountConfig.COLLISION_ENABLED.get()) {
            return;
        }
        if (!(event.level instanceof ServerLevel level)) {
            return;
        }

        double threshold = UnmountConfig.COLLISION_BULLET_SPEED_THRESHOLD.get();
        double thresholdSq = threshold * threshold;

        for (Entity entity : level.getEntities().getAll()) {
            if (!(entity instanceof AbstractMinecart moving) || !moving.isAlive()) {
                continue;
            }

            Vec3 velocity = moving.getDeltaMovement();
            if (velocity.lengthSqr() < thresholdSq) {
                continue;
            }

            for (AbstractMinecart hit : level.getEntitiesOfClass(AbstractMinecart.class, moving.getBoundingBox().inflate(0.2),
                    other -> other != moving
                            && other.isAlive()
                            && other.getPassengers().isEmpty()
                            && other.getType() == EntityType.MINECART)) {
                MinecartDropUtil.dropMinecartLikeVanilla(hit);
                hit.discard();
                moving.setDeltaMovement(velocity);
            }
        }
    }

    private int toggleAutoBreak(CommandContext<CommandSourceStack> context) {
        ServerPlayer player;
        try {
            player = context.getSource().getPlayerOrException();
        } catch (Exception ignored) {
            context.getSource().sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }

        CompoundTag data = player.getPersistentData();
        if (data.getBoolean(Unmount.TOGGLE_KEY)) {
            data.remove(Unmount.TOGGLE_KEY);
            player.sendSystemMessage(Component.literal("Minecart auto-break is now ENABLED.").withStyle(ChatFormatting.GREEN));
        } else {
            data.putBoolean(Unmount.TOGGLE_KEY, true);
            player.sendSystemMessage(Component.literal("Minecart auto-break is now DISABLED.").withStyle(ChatFormatting.RED));
        }
        return Command.SINGLE_SUCCESS;
    }
}
