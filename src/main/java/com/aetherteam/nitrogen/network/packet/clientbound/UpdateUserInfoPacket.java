package com.aetherteam.nitrogen.network.packet.clientbound;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.UserData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Updates the {@link User} on the client.
 */
public record UpdateUserInfoPacket(User user) implements CustomPacketPayload {
    public static final Type<UpdateUserInfoPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Nitrogen.MODID, "update_user_info"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateUserInfoPacket> STREAM_CODEC = CustomPacketPayload.codec(
        UpdateUserInfoPacket::write,
        UpdateUserInfoPacket::decode);

    public void write(RegistryFriendlyByteBuf buffer) {
        User.write(buffer, this.user());
    }

    public static UpdateUserInfoPacket decode(RegistryFriendlyByteBuf buffer) {
        User user = User.read(buffer);
        return new UpdateUserInfoPacket(user);
    }

    @Override
    public Type<UpdateUserInfoPacket> type() {
        return TYPE;
    }

    public static void execute(UpdateUserInfoPacket payload, IPayloadContext context) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            UserData.Client.setClientUser(payload.user());
        }
    }
}
