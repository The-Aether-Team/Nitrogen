package com.aetherteam.nitrogen.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface BasePacket {
    void encode(FriendlyByteBuf buf);

    default boolean handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> execute(context.get().getSender()));
        return true;
    }

    void execute(Player player);
}
