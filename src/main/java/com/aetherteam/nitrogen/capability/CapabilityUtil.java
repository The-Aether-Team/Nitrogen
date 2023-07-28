package com.aetherteam.nitrogen.capability;

import com.aetherteam.nitrogen.network.packet.SyncLevelPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CapabilityUtil {
    public static void loadLevelCapability(SyncLevelPacket<?> syncLevelPacket, Player playerEntity, String key, Object value, boolean isClientSide) {
        Level level = isClientSide ? Minecraft.getInstance().level : playerEntity.getLevel();
        syncLevelPacket.getCapability(level).ifPresent((synchable) -> synchable.getSynchableFunctions().get(key).getMiddle().accept(value));
    }
}
