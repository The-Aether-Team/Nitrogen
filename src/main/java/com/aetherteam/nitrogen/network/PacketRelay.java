package com.aetherteam.nitrogen.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class PacketRelay {
    public static <MSG extends CustomPacketPayload> void sendToPlayer(MSG message, ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(message); // Clientbound
    }

    public static <MSG extends CustomPacketPayload> void sendToNear(MSG message, double x, double y, double z, double radius, ResourceKey<Level> dimension) {
        PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, radius, dimension).get()).send(message); // Clientbound
    }

    public static <MSG extends CustomPacketPayload> void sendToAll(MSG message) {
        PacketDistributor.ALL.noArg().send(message); // Clientbound
    }

    public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.SERVER.noArg().send(message); // Serverbound
    }

    public static <MSG extends CustomPacketPayload> void sendToDimension(MSG message, ResourceKey<Level> dimension) {
        PacketDistributor.DIMENSION.with(dimension).send(message); // Clientbound
    }
}
