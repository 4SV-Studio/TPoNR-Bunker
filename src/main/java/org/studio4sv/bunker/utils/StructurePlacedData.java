package org.studio4sv.bunker.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class StructurePlacedData extends SavedData {
    private static final String NAME = "placed_bunker";
    private boolean placed = false;

    public static StructurePlacedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            tag -> {
                StructurePlacedData data = new StructurePlacedData();
                data.placed = tag.getBoolean("Placed");
                return data;
            },
            StructurePlacedData::new,
            NAME
        );
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        tag.putBoolean("Placed", placed);
        return tag;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
        setDirty();
    }

    public static boolean checkAndSet(ServerLevel level) {
        StructurePlacedData data = get(level);
        if (data.isPlaced()) {
            return true;
        }
        data.setPlaced(true);
        return false;
    }
}
