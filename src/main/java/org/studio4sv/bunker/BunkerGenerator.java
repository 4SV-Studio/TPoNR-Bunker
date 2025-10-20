package org.studio4sv.bunker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.studio4sv.bunker.utils.BunkerPlayerData;
import org.studio4sv.bunker.utils.StructurePlacedData;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = BunkerMain.MODID)
public class BunkerGenerator {
    // CONFIG
    private static final BlockPos spawnPos = new BlockPos(46, 115, 16);
    private static final float spawnYaw = 135;
    private static final float spawnPitch = 0;

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        if (!serverLevel.dimension().location().equals(BunkerMain.id("safe")))
            return;

        if (StructurePlacedData.checkAndSet(serverLevel)) return;

        ResourceLocation structureLoc = BunkerMain.id("main");
        StructureTemplateManager manager = serverLevel.getStructureManager();
        Optional<StructureTemplate> opt = manager.get(structureLoc);
        if (opt.isEmpty()) {
            BunkerMain.LOGGER.error("Structure {} not found!", structureLoc);
            return;
        }

        StructureTemplate template = opt.get();
        BlockPos pos = new BlockPos(0, 100, 0);

        template.placeInWorld(
                serverLevel,
                pos,
                pos,
                new StructurePlaceSettings(),
                serverLevel.random,
                2
        );

        BunkerMain.LOGGER.info("Main bunker part placed at {} in {}", pos, serverLevel.dimension().location());
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<net.minecraft.world.entity.Entity> event) {
        if (event.getObject() instanceof ServerPlayer) {
            BunkerPlayerData.Provider provider = new BunkerPlayerData.Provider();
            event.addCapability(BunkerMain.id("player_data"), provider);
            event.addListener(provider::invalidate);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        MinecraftServer server = player.server;
        ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, BunkerMain.id("safe"));
        ServerLevel targetWorld = server.getLevel(dimKey);
        if (targetWorld == null) return;

        player.getCapability(BunkerPlayerData.CAPABILITY).ifPresent(data -> {
            if (data.isNotFirstJoin()) return;
            data.setNotFirstJoin(true);

            if (!StructurePlacedData.checkAndSet(targetWorld)) {
                ResourceLocation structureLoc = BunkerMain.id("main");
                StructureTemplateManager manager = targetWorld.getStructureManager();
                manager.get(structureLoc).ifPresent(template -> {
                    BlockPos pos = new BlockPos(0, 100, 0);
                    template.placeInWorld(targetWorld, pos, pos, new StructurePlaceSettings(), targetWorld.random, 2);
                });
            }

            player.teleportTo(
                    targetWorld,
                    spawnPos.getX() + 0.5,
                    spawnPos.getY() + 1,
                    spawnPos.getZ() + 0.5,
                    spawnYaw,
                    spawnPitch
            );
            player.setRespawnPosition(targetWorld.dimension(), spawnPos, player.getYRot(), true, false);
        });
    }
}