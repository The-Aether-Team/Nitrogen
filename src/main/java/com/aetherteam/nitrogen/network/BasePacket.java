package com.aetherteam.nitrogen.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.NetworkEvent;

public interface BasePacket {
    void encode(FriendlyByteBuf buf);

    default boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> execute(context.getSender()));
        return true;
    }

    void execute(Player player);
}
