package com.aetherteam.nitrogen.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public interface BasePacket extends CustomPacketPayload {

    default void handle(PlayPayloadContext context) {
        context.workHandler().execute(() -> execute(context.player().orElseThrow()));
    }

    void execute(Player player);
}
