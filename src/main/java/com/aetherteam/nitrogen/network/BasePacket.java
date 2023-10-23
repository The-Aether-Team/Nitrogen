package com.aetherteam.nitrogen.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public interface BasePacket extends ServerPacket, ClientPacket {
    void encode(FriendlyByteBuf buf);

    void execute(Player player);
}