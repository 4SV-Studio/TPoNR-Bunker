package org.studio4sv.bunker.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.studio4sv.bunker.BunkerMain;

public class EnteranceBlock extends Block {

    public EnteranceBlock(Properties properties) {
        super(properties);
    }

    public static final ResourceKey<Level> BUNKER_SAFE_DIMENSION = ResourceKey.create(Registries.DIMENSION, BunkerMain.id("safe"));

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            ServerLevel overworld = level.getServer().getLevel(BUNKER_SAFE_DIMENSION);
            if (overworld != null) {
                player.teleportTo(overworld, 70 + 0.5, 121, 16 + 0.5, player.getYRot(), player.getXRot());
            }
        }
        super.entityInside(state, level, pos, entity);
    }
}
