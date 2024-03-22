package com.aetherteam.nitrogen.network.packet.serverbound;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.api.users.UserData;
import com.aetherteam.nitrogen.network.BasePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Triggers the server to query the Patreon database.
 *
 * @see UserData.Server#sendUserRequest(MinecraftServer, ServerPlayer, UUID)
 */
public record TriggerUpdateInfoPacket(int playerID) implements BasePacket {

    public static final ResourceLocation ID = new ResourceLocation(Nitrogen.MODID, "trigger_patreon_info_update");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.playerID());
    }

    public static TriggerUpdateInfoPacket decode(FriendlyByteBuf buffer) {
        int playerID = buffer.readInt();
        return new TriggerUpdateInfoPacket(playerID);
    }

    @Override
    public void execute(@Nullable Player player) {
        if (player != null && player.getServer() != null && player.level().getEntity(this.playerID) instanceof ServerPlayer serverPlayer) {
            UserData.Server.sendUserRequest(serverPlayer.getServer(), serverPlayer, serverPlayer.getGameProfile().getId());
        }
    }
}
