package com.aetherteam.nitrogen.network.packet.serverbound;

import com.aetherteam.nitrogen.api.users.UserData;
import com.aetherteam.nitrogen.network.BasePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record TriggerUpdateInfoPacket(int playerID) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.playerID());
    }

    public static TriggerUpdateInfoPacket decode(FriendlyByteBuf buffer) {
        int playerID = buffer.readInt();
        return new TriggerUpdateInfoPacket(playerID);
    }

    @Override
    public void execute(Player player) {
        if (player != null && player.getServer() != null && player.level.getEntity(this.playerID) instanceof ServerPlayer serverPlayer) {
            UserData.Server.sendUserRequest(serverPlayer.getServer(), serverPlayer, serverPlayer.getGameProfile().getId());
        }
    }
}
