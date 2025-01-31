package com.aetherteam.nitrogen.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface BasePacket extends ServerPacket, ClientPacket {
    void encode(FriendlyByteBuf buf);

    @Override
    default void executeServer(@Nullable Player player) {

    }

    @Override
    default void executeClient() {

    }
}