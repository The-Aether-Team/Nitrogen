package com.aetherteam.nitrogen.fabric.pond;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

public interface BlockEntityExtension {
    default boolean nitrogen_fabric$handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        return false;
    }

    default boolean nitrogen_fabric$onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookupProvider) {
        return false;
    }
}
