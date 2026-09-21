package io.github.derec4.unmount;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;

final class UnmountEvents {
    private static final Map<UUID, AbstractMinecart> LAST_CART = new HashMap<>();

    private UnmountEvents() {
    }

    static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("togglecart").executes(UnmountEvents::toggleAutoBreak));
            dispatcher.register(Commands.literal("autobreak").executes(UnmountEvents::toggleAutoBreak));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> LAST_CART.remove(handler.player.getUUID()));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!UnmountConfig.AUTO_BREAK_ENABLED) {
                LAST_CART.clear();
                return;
            }

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                Entity vehicle = player.getVehicle();
                AbstractMinecart previous = LAST_CART.get(player.getUUID());

                if (vehicle instanceof AbstractMinecart cart) {
                    if (previous != null && previous != cart) {
                        scheduleBreak(previous, player);
                    }
                    LAST_CART.put(player.getUUID(), cart);
                } else if (previous != null) {
                    LAST_CART.remove(player.getUUID());
                    scheduleBreak(previous, player);
                }
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.isClientSide() || !UnmountConfig.COLLISION_ENABLED) {
                return;
            }

            double thresholdSq = UnmountConfig.COLLISION_BULLET_SPEED_THRESHOLD * UnmountConfig.COLLISION_BULLET_SPEED_THRESHOLD;
            for (AbstractMinecart moving : world.getEntities(EntityTypeTest.forClass(AbstractMinecart.class), AbstractMinecart::isAlive)) {
                Vec3 velocity = moving.getDeltaMovement();
                if (velocity.lengthSqr() < thresholdSq) {
                    continue;
                }

                for (AbstractMinecart hit : world.getEntitiesOfClass(AbstractMinecart.class, moving.getBoundingBox().inflate(0.2),
                        other -> other != moving
                                && other.isAlive()
                                && other.getPassengers().isEmpty()
                                && other.getType() == EntityType.MINECART)) {
                    MinecartDropUtil.dropMinecartLikeVanilla(hit);
                    hit.discard();
                    moving.setDeltaMovement(velocity);
                }
            }
        });
    }

    private static void scheduleBreak(AbstractMinecart cart, ServerPlayer player) {
        if (player.getTags().contains(Unmount.TOGGLE_KEY) || !cart.isAlive()) {
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

    private static int toggleAutoBreak(CommandContext<CommandSourceStack> context) {
        ServerPlayer player;
        try {
            player = context.getSource().getPlayerOrException();
        } catch (Exception ignored) {
            context.getSource().sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }

        if (player.getTags().contains(Unmount.TOGGLE_KEY)) {
            player.removeTag(Unmount.TOGGLE_KEY);
            player.sendSystemMessage(Component.literal("Minecart auto-break is now ENABLED.").withStyle(ChatFormatting.GREEN));
        } else {
            player.addTag(Unmount.TOGGLE_KEY);
            player.sendSystemMessage(Component.literal("Minecart auto-break is now DISABLED.").withStyle(ChatFormatting.RED));
        }
        return Command.SINGLE_SUCCESS;
    }
}
