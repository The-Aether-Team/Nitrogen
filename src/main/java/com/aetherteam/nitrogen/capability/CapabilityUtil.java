package com.aetherteam.nitrogen.capability;

import com.aetherteam.nitrogen.network.packet.SyncLevelPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CapabilityUtil {
    /**
     * Used to sync data for level capabilities to the client. This is in its own class to avoid classloading {@link Minecraft} from {@link SyncLevelPacket} on servers.
     * @param syncLevelPacket The {@link SyncLevelPacket}.
     * @param key The {@link String} key for the field to sync.
     * @param value The {@link Object} value to sync to the field.
     * @see SyncLevelPacket
     */
    public static void syncLevelCapabilityToClient(SyncLevelPacket<?> syncLevelPacket, String key, Object value) {
        syncLevelPacket.getCapability(Minecraft.getInstance().level).ifPresent((synchable) -> synchable.getSynchableFunctions().get(key).getMiddle().accept(value));
    }

    /**
     * Used to sync data for level capabilities to the server. This is in its own class to avoid classloading {@link Minecraft} from {@link SyncLevelPacket} on servers.
     * @param syncLevelPacket The {@link SyncLevelPacket}.
     * @param playerEntity The {@link Player} that the level belongs to.
     * @param key The {@link String} key for the field to sync.
     * @param value The {@link Object} value to sync to the field.
     * @see SyncLevelPacket
     */
    public static void syncLevelCapabilityToServer(SyncLevelPacket<?> syncLevelPacket, Player playerEntity, String key, Object value) {
        syncLevelPacket.getCapability(playerEntity.level()).ifPresent((synchable) -> synchable.getSynchableFunctions().get(key).getMiddle().accept(value));
    }
}