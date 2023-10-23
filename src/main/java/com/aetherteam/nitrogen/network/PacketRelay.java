package com.aetherteam.nitrogen.network;

import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PacketRelay {
    public static <MSG extends BasePacket> void sendToPlayer(SimpleChannel handler, MSG message, ServerPlayer player) {
        handler.sendToClient(message, player); // Clientbound
    }

    public static <MSG extends BasePacket> void sendToNear(SimpleChannel handler, MSG message, double x, double y, double z, double radius, ResourceKey<Level> dimension) {
        handler.sendToClientsAround(message, ServerLifecycleHooks.getCurrentServer().getLevel(dimension), new Vec3(x, y, z), radius); // Clientbound
    }

    public static <MSG extends BasePacket> void sendToAll(SimpleChannel handler, MSG message) {
        handler.sendToClientsInCurrentServer(message); // Clientbound
    }

    public static <MSG extends BasePacket> void sendToServer(SimpleChannel handler, MSG message) {
        handler.sendToServer(message); // Serverbound
    }

    public static <MSG extends BasePacket> void sendToDimension(SimpleChannel handler, MSG message, ResourceKey<Level> dimension) {
        handler.sendToClientsInWorld(message, ServerLifecycleHooks.getCurrentServer().getLevel(dimension)); // Clientbound
    }
}
