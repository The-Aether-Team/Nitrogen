package com.aetherteam.nitrogen.network.packet.serverbound;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.api.users.UserData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

/**
 * Triggers the server to query the Patreon database.
 *
 * @see UserData.Server#sendUserRequest(MinecraftServer, ServerPlayer, UUID)
 */
public record TriggerUpdateInfoPacket(int playerID) implements CustomPacketPayload {
    public static final Type<TriggerUpdateInfoPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Nitrogen.MODID, "trigger_patreon_info_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TriggerUpdateInfoPacket> STREAM_CODEC = CustomPacketPayload.codec(
        TriggerUpdateInfoPacket::write,
        TriggerUpdateInfoPacket::decode);

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.playerID());
    }

    public static TriggerUpdateInfoPacket decode(RegistryFriendlyByteBuf buffer) {
        int playerID = buffer.readInt();
        return new TriggerUpdateInfoPacket(playerID);
    }

    @Override
    public Type<TriggerUpdateInfoPacket> type() {
        return TYPE;
    }

    public static void execute(TriggerUpdateInfoPacket payload, IPayloadContext context) {
        Player player = context.player();
        if (player.getServer() != null && player.level().getEntity(payload.playerID()) instanceof ServerPlayer serverPlayer) {
            UserData.Server.sendUserRequest(serverPlayer.getServer(), serverPlayer, serverPlayer.getGameProfile().getId());
        }
    }
}
