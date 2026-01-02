package com.aetherteam.nitrogen.fabric.pond;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.storage.ValueInput;

public interface BlockEntityExtension {
    default boolean nitrogen_fabric$handleUpdateTag(ValueInput tag) {
        return false;
    }

    default boolean nitrogen_fabric$onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookupProvider) {
        return false;
    }
}
