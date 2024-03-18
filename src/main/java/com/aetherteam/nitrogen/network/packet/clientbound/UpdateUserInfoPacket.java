package com.aetherteam.nitrogen.network.packet.clientbound;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.UserData;
import com.aetherteam.nitrogen.network.BasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Updates the {@link User} on the client.
 */
public record UpdateUserInfoPacket(User user) implements BasePacket {

    public static final ResourceLocation ID = new ResourceLocation(Nitrogen.MODID, "update_user_info");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        User.write(buffer, this.user());
    }

    public static UpdateUserInfoPacket decode(FriendlyByteBuf buffer) {
        User user = User.read(buffer);
        return new UpdateUserInfoPacket(user);
    }

    @Override
    public void execute(Player player) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            UserData.Client.setClientUser(this.user());
        }
    }
}
