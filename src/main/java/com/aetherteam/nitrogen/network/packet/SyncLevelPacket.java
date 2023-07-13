package com.aetherteam.nitrogen.network.packet;

import com.aetherteam.nitrogen.capability.INBTSynchable;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Triple;

public abstract class SyncLevelPacket<T extends INBTSynchable<CompoundTag>> extends SyncPacket {
    public SyncLevelPacket(Triple<String, INBTSynchable.Type, Object> values) {
        super(values);
    }

    public SyncLevelPacket(String key, INBTSynchable.Type type, Object value) {
        super(key, type, value);
    }

    @Override
    public void execute(Player playerEntity) {
        if (playerEntity != null && playerEntity.getServer() != null && this.value != null) {
            this.getCapability(playerEntity.getLevel()).ifPresent((synchable) -> synchable.getSynchableFunctions().get(this.key).getMiddle().accept(this.value));
        } else {
            if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null && this.value != null) {
                this.getCapability(Minecraft.getInstance().level).ifPresent((synchable) -> synchable.getSynchableFunctions().get(this.key).getMiddle().accept(this.value));
            }
        }
    }

    protected abstract LazyOptional<T> getCapability(Level level);
}
