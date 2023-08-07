package com.aetherteam.nitrogen.network;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketRelay {
    public static <MSG> void sendToPlayer(SimpleChannel handler, MSG message, ServerPlayer player) {
        handler.send(PacketDistributor.PLAYER.with(() -> player), message); // Clientbound
    }

    public static <MSG> void sendToNear(SimpleChannel handler, MSG message, double x, double y, double z, double radius, ResourceKey<Level> dimension) {
        handler.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, radius, dimension)), message); // Clientbound
    }

    public static <MSG> void sendToAll(SimpleChannel handler, MSG message) {
        handler.send(PacketDistributor.ALL.noArg(), message); // Clientbound
    }

    public static <MSG> void sendToServer(SimpleChannel handler, MSG message) {
        handler.sendToServer(message); // Serverbound
    }

    public static <MSG> void sendToDimension(SimpleChannel handler, MSG message, ResourceKey<Level> dimension) {
        handler.send(PacketDistributor.DIMENSION.with(() -> dimension), message); // Clientbound
    }
}
