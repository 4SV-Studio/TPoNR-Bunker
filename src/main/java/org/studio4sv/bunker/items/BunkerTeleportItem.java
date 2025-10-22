package org.studio4sv.bunker.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.studio4sv.bunker.blocks.EnteranceBlock;

public class BunkerTeleportItem extends Item {
    public BunkerTeleportItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            ServerLevel bunkerLevel = level.getServer().getLevel(EnteranceBlock.BUNKER_SAFE_DIMENSION);
            if (bunkerLevel != null) {
                serverPlayer.teleportTo(bunkerLevel, 70 + 0.5, 121, 16 + 0.5, serverPlayer.getYRot(), serverPlayer.getXRot());
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
