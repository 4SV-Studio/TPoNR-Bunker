package org.studio4sv.bunker.utils;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BunkerPlayerData implements INBTSerializable<CompoundTag> {
    public static final Capability<BunkerPlayerData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private boolean notFirstJoin = false;

    public boolean isNotFirstJoin() {
        return notFirstJoin;
    }

    public void setNotFirstJoin(boolean notFirstJoin) {
        this.notFirstJoin = notFirstJoin;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("notFirstJoin", notFirstJoin);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        notFirstJoin = nbt.getBoolean("notFirstJoin");
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final BunkerPlayerData data = new BunkerPlayerData();
        private final LazyOptional<BunkerPlayerData> optional = LazyOptional.of(() -> data);

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == CAPABILITY ? optional.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return data.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            data.deserializeNBT(nbt);
        }

        public void invalidate() {
            optional.invalidate();
        }
    }
}
