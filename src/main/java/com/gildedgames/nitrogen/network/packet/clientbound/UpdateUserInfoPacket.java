package com.gildedgames.nitrogen.network.packet.clientbound;

import com.gildedgames.nitrogen.api.users.RoleData;
import com.gildedgames.nitrogen.api.users.User;
import com.gildedgames.nitrogen.network.BasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record UpdateUserInfoPacket(User user) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        User.write(buffer, this.user());
    }

    public static UpdateUserInfoPacket decode(FriendlyByteBuf buffer) {
        User user = User.read(buffer);
        return new UpdateUserInfoPacket(user);
    }

    @Override
    public void execute(Player player) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            RoleData.Client.setClientUser(this.user());
        }
    }
}
