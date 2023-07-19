package com.aetherteam.nitrogen.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public record CapabilityProvider(Capability<?> registeredCapability, INBTSerializable<CompoundTag> capabilityInterface) implements ICapabilitySerializable<CompoundTag> {
    @Override
    public CompoundTag serializeNBT() {
        return this.capabilityInterface().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.capabilityInterface().deserializeNBT(tag);
    }

    /**
     * Warning for "unchecked" is suppressed because the generic cast is fine for capabilities.
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction side) {
        if (capability == this.registeredCapability()) {
            return LazyOptional.of(() -> (T) this.capabilityInterface());
        }
        return LazyOptional.empty();
    }
}
