package com.aetherteam.nitrogen.network;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface ClientPacket extends S2CPacket {
    @Override
    default void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
        client.execute(() -> ClientPacket.this.execute(client.player));
    }

    void execute(@Nullable Player player);
}
