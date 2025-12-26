package com.aetherteam.nitrogen.fabric.pond;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface EntityExtension {

    default boolean nitrogen_fabric$shouldRiderSit() {
        return true;
    }

    default boolean nitrogen_fabric$canRiderInteract() {
        return false;
    }

    default boolean nitrogen_fabric$isInFluidType() {
        return throwUnimplementedException();
    }

    default void nitrogen_fabric$sendPairingData(ServerPlayer serverPlayer, Consumer<CustomPacketPayload> bundleBuilder) {
        throwUnimplementedException();
    }

    static <T> T throwUnimplementedException() {
        throw new IllegalStateException("Injected Interface method not implement!");
    }
}
