package com.aetherteam.nitrogen.network.packet;

import com.aetherteam.nitrogen.attachment.AttachmentUtil;
import com.aetherteam.nitrogen.attachment.INBTSynchable;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.Supplier;

/**
 * An abstract packet used by level capabilities for data syncing.
 */
public abstract class SyncLevelPacket<T extends INBTSynchable> extends SyncPacket {
    public SyncLevelPacket(Triple<String, INBTSynchable.Type, Object> values) {
        super(values);
    }

    public SyncLevelPacket(String key, INBTSynchable.Type type, Object value) {
        super(key, type, value);
    }

    public static <T extends INBTSynchable> void execute(SyncLevelPacket<T> payload, Player playerEntity) {
        if (playerEntity != null && playerEntity.getServer() != null && payload.value != null) {
            AttachmentUtil.syncLevelCapability(payload, playerEntity, payload.key, payload.value, false);
        } else {
            if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null && payload.value != null) {
                AttachmentUtil.syncLevelCapability(payload, playerEntity, payload.key, payload.value, true);
            }
        }
    }

    public abstract Supplier<AttachmentType<T>> getAttachment();
}
