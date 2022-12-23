package com.gildedgames.nitrogen.api.users;

import net.minecraft.network.FriendlyByteBuf;

interface Role {
    default void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.getType().name());
    }

    Type getType();

    enum Type {
        BOOSTER,
        DONOR,
        PATRON,
        RANKED
    }
}
