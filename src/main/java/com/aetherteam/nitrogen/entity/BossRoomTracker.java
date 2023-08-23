package com.aetherteam.nitrogen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public record BossRoomTracker<T extends Mob & BossMob<T>>(@Nullable T boss, Vec3 originCoordinates, AABB roomBounds, List<UUID> dungeonPlayers) {
    /**
     * @return Whether the dungeon boss is within the room bounds, as a {@link Boolean}.
     */
    public boolean isBossWithinRoom() {
        if (this.boss() != null) {
            return this.roomBounds().contains(this.boss().position());
        }
        return false;
    }

    /**
     * Checks whether a player is within the room bounds.
     * @param entity The player {@link Entity}.
     * @return The {@link Boolean} result.
     */
    public boolean isPlayerWithinRoom(Entity entity) {
        if (this.boss() != null) {
            return this.roomBounds().contains(entity.position());
        }
        return false;
    }

    /**
     * Checks whether a player is within the interior of the room bounds, not including the wall, floor, or ceiling positions.
     * @param entity The player {@link Entity}.
     * @return The {@link Boolean} result.
     */
    public boolean isPlayerWithinRoomInterior(Entity entity) {
        if (this.boss() != null) {
            return this.roomBounds().deflate(1.0, 1.0, 1.0).contains(entity.position());
        }
        return false;
    }

    /**
     * Checks whether this player is tracked within the list of players inside the boss room.
     * @param player The {@link Player}.
     * @return The {@link Boolean} result.
     */
    public boolean isPlayerTracked(Player player) {
        if (this.boss() != null) {
            return this.dungeonPlayers().contains(player.getUUID());
        }
        return false;
    }

    /**
     * Tracks whether players enter or leave the boss room.
     * If there are living players in the boss room, then they are tracked to {@link BossRoomTracker#dungeonPlayers()}.
     * If any players die, leave the room, or no longer exist, then they are removed from tracking.
     */
    public void trackPlayers() {
        if (this.boss() != null) {
            this.boss().level().getEntities(EntityType.PLAYER, this.roomBounds(), Entity::isAlive).forEach(player -> {
                if (!isPlayerTracked(player)) {
                    this.boss().onDungeonPlayerAdded(player);
                    this.dungeonPlayers().add(player.getUUID());
                }
            });
            this.dungeonPlayers().removeIf(uuid -> {
                Player player = this.boss().level().getPlayerByUUID(uuid);
                boolean shouldRemove = player != null && (!this.isPlayerWithinRoom(player) || !player.isAlive());
                if (shouldRemove) {
                    this.boss().onDungeonPlayerRemoved(player);
                }
                return shouldRemove;
            });
        }
    }

    /**
     * Marks every player in the boss room as having killed the boss, so they all get achievements.
     * @param damageSource The {@link DamageSource} used to kill the boss.
     */
    public void grantAdvancements(DamageSource damageSource) {
        if (this.boss() != null) {
            for (UUID uuid : this.dungeonPlayers()) {
                Player player = this.boss().level().getPlayerByUUID(uuid);
                if (player != null) {
                    player.awardKillScore(this.boss(), this.boss().getDeathScore(), damageSource);
                }
            }
        }
    }

    /**
     * Iterates on every block within the bounds of the dungeon
     * @param function A {@link Function} of two {@link BlockState}s, used to modify blocks within the room.
     */
    public void modifyRoom(Function<BlockState, BlockState> function) {
        if (this.boss() != null) {
            AABB bounds = this.roomBounds();
            Level level = this.boss().level();
            for (BlockPos pos : BlockPos.betweenClosed((int) bounds.minX, (int) bounds.minY, (int) bounds.minZ, (int) bounds.maxX, (int) bounds.maxY, (int) bounds.maxZ)) {
                BlockState state = level.getBlockState(pos);
                BlockState newState = function.apply(state);
                if (newState != null) {
                    level.setBlock(pos, newState, 1 | 2);
                }
            }
        }
    }

    public CompoundTag addAdditionalSaveData() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("OriginX", this.originCoordinates().x());
        tag.putDouble("OriginY", this.originCoordinates().y());
        tag.putDouble("OriginZ", this.originCoordinates().z());

        tag.putDouble("RoomBoundsMinX", this.roomBounds().minX);
        tag.putDouble("RoomBoundsMinY", this.roomBounds().minY);
        tag.putDouble("RoomBoundsMinZ", this.roomBounds().minZ);
        tag.putDouble("RoomBoundsMaxX", this.roomBounds().maxX);
        tag.putDouble("RoomBoundsMaxY", this.roomBounds().maxY);
        tag.putDouble("RoomBoundsMaxZ", this.roomBounds().maxZ);

        tag.putInt("DungeonPlayersSize", this.dungeonPlayers().size());
        for (int i = 0; i < this.dungeonPlayers().size(); i++) {
            tag.putUUID("Player" + i, this.dungeonPlayers().get(i));
        }
        return tag;
    }

    public static <T extends Mob & BossMob<T>> BossRoomTracker<T> readAdditionalSaveData(CompoundTag tag, T boss) {
        double originX = tag.getDouble("OriginX");
        double originY = tag.getDouble("OriginY");
        double originZ = tag.getDouble("OriginZ");
        Vec3 originCoordinates = new Vec3(originX, originY, originZ);

        double minX = tag.getDouble("RoomBoundsMinX");
        double minY = tag.getDouble("RoomBoundsMinY");
        double minZ = tag.getDouble("RoomBoundsMinZ");
        double maxX = tag.getDouble("RoomBoundsMaxX");
        double maxY = tag.getDouble("RoomBoundsMaxY");
        double maxZ = tag.getDouble("RoomBoundsMaxZ");
        AABB roomBounds = new AABB(minX, minY, minZ, maxX, maxY, maxZ);

        List<UUID> dungeonPlayers = new ArrayList<>();
        int size = tag.getInt("DungeonPlayersSize");
        for (int i = 0; i < size; i++) {
            UUID uuid = tag.getUUID("Player" + i);
            dungeonPlayers.add(uuid);
        }

        return new BossRoomTracker<>(boss, originCoordinates, roomBounds, dungeonPlayers);
    }
}

