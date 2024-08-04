// TeleportMod.java
package com.phantomz3;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

public class BeaconWarp implements ModInitializer {

    @Override
    public void onInitialize() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (!world.isClient && stack.getItem() == Items.COMPASS && player.isSneaking()) {
                BlockPos hitPos = hitResult.getBlockPos();
                if (world.getBlockState(hitPos).getBlock() == Blocks.BEACON) {
                    GlobalPos lodestonePos = getLodestonePos(stack, world);
                    if (lodestonePos != null) {
                        teleportPlayer((ServerPlayerEntity) player, lodestonePos.pos());
                        stack.decrement(1);
                        return ActionResult.SUCCESS;
                    } else {
                        player.sendMessage(Text.literal("No lodestone position found on this compass."), false);
                    }
                }
            }
            return ActionResult.PASS;
        });
    }

    private GlobalPos getLodestonePos(ItemStack compass, World world) {
        LodestoneTrackerComponent lodestoneTracker = compass.get(DataComponentTypes.LODESTONE_TRACKER);
        return lodestoneTracker != null ? lodestoneTracker.target().get() : null;
    }

    private void teleportPlayer(ServerPlayerEntity player, BlockPos pos) {
        player.teleport(player.getServerWorld(), pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0);
    }
}
