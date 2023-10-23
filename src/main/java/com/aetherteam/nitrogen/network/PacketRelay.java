package com.aetherteam.nitrogen.network;

import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class PacketRelay {
    public static <MSG extends ClientPacket> void sendToPlayer(SimpleChannel handler, MSG message, ServerPlayer player) {
        handler.sendToClient(message, player); // Clientbound
    }

    public static <MSG extends ClientPacket> void sendToNear(SimpleChannel handler, MSG message, double x, double y, double z, double radius, ServerLevel dimension) {
        handler.sendToClientsAround(message, dimension, new Vec3(x, y, z), radius); // Clientbound
    }

    public static <MSG extends ClientPacket> void sendToAll(SimpleChannel handler, MSG message, MinecraftServer server) {
        handler.sendToClientsInServer(message, server); // Clientbound
    }

    public static <MSG extends ServerPacket> void sendToServer(SimpleChannel handler, MSG message) {
        handler.sendToServer(message); // Serverbound
    }

    public static <MSG extends ClientPacket> void sendToDimension(SimpleChannel handler, MSG message, ServerLevel dimension) {
        handler.sendToClientsInWorld(message, dimension); // Clientbound
    }
}
