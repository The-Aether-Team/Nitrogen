package com.aetherteam.nitrogen.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.Nullable;

public interface BasePacket extends CustomPacketPayload {

    default void handle(PlayPayloadContext context) {
        context.workHandler().execute(() -> this.execute(context.player().orElse(null)));
    }

    void execute(@Nullable Player player);
}
