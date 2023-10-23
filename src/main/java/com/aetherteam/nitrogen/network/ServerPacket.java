package com.aetherteam.nitrogen.network;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface ServerPacket extends C2SPacket {
    @Override
    default void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, PacketSender responseSender, SimpleChannel channel) {
        server.execute(() -> ServerPacket.this.execute(player));
    }

    void execute(@Nullable Player player);
}
