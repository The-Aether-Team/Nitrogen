package com.aetherteam.nitrogen.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface BossMob<T extends Mob & BossMob<T>> {
    TargetingConditions NON_COMBAT = TargetingConditions.forNonCombat();

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    Component getBossName();

    void setBossName(Component component);

    boolean isBossFight();

    void setBossFight(boolean isFighting);

    @Nullable
    BossRoomTracker<T> getDungeon();

    void setDungeon(@Nullable BossRoomTracker<T> dungeon);

    int getDeathScore();

    /**
     * Should be called from {@link Mob#customServerAiStep()}.<br>
     * This is used to track the contents of the dungeon room.
     * If any of the tracked players leave or if the boss leaves, then the boss fight resets.
     */
    default void trackDungeon() {
        if (this.getDungeon() != null) {
            this.getDungeon().trackPlayers();
            if (this.isBossFight() && (this.getDungeon().dungeonPlayers().isEmpty() || !this.getDungeon().isBossWithinRoom())) {
                this.reset();
            }
        }
    }

    /**
     * Displays a message when the player tries to start a boss fight but is outside of the room.
     *
     * @param player The {@link Player}.
     */
    default void displayTooFarMessage(Player player) {
        player.sendSystemMessage(Component.translatable("gui.nitrogen_internals.boss.message.far"));
    }

    default void onDungeonPlayerAdded(@Nullable Player player) {
    }

    default void onDungeonPlayerRemoved(@Nullable Player player) {
    }

    void reset();

    /**
     * Called when the boss is defeated to change all blocks to unlocked blocks.
     */
    default void tearDownRoom() {
        if (this.getDungeon() != null) {
            this.getDungeon().modifyRoom(this::convertBlock);
        }
    }

    void closeRoom();

    void openRoom();

    @Nullable
    BlockState convertBlock(BlockState state);

    default void addBossSaveData(CompoundTag tag) {
        tag.putString("BossName", Component.Serializer.toJson(this.getBossName()));
        tag.putBoolean("BossFight", this.isBossFight());
        if (this.getDungeon() != null) {
            tag.put("Dungeon", this.getDungeon().addAdditionalSaveData());
        }
    }

    default void readBossSaveData(CompoundTag tag) {
        if (tag.contains("BossName")) {
            Component name = Component.Serializer.fromJson(tag.getString("BossName"));
            if (name != null) {
                this.setBossName(name);
            }
        }
        if (tag.contains("BossFight")) {
            this.setBossFight(tag.getBoolean("BossFight"));
        }
        if (tag.contains("Dungeon") && tag.get("Dungeon") instanceof CompoundTag dungeonTag) {
            this.setDungeon(BossRoomTracker.readAdditionalSaveData(dungeonTag, self()));
        }
    }
}

