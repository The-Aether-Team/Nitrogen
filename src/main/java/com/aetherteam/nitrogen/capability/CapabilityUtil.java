package com.aetherteam.nitrogen.capability;

import com.aetherteam.nitrogen.network.packet.SyncLevelPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CapabilityUtil {
    /**
     * Used to sync data for level capabilities. This is in its own class to avoid classloading {@link Minecraft} from {@link SyncLevelPacket} on servers.
     * @param syncLevelPacket The {@link SyncLevelPacket}.
     * @param playerEntity The {@link Player} that the level belongs to.
     * @param key The {@link String} key for the field to sync.
     * @param value The {@link Object} value to sync to the field.
     * @param isClientSide Whether the level is clientside, as a {@link Boolean}.
     * @see SyncLevelPacket
     */
    public static void syncLevelCapability(SyncLevelPacket<?> syncLevelPacket, Player playerEntity, String key, Object value, boolean isClientSide) {
        Level level = isClientSide ? Minecraft.getInstance().level : playerEntity.level();
        syncLevelPacket.getCapability(level).ifPresent((synchable) -> synchable.getSynchableFunctions().get(key).getMiddle().accept(value));
    }
}
