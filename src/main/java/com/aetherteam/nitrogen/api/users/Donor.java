package com.aetherteam.nitrogen.api.users;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Donor implements Role {
    private List<String> lifetimeSkins;

    protected Donor(List<String> lifetimeSkins) {
        this.lifetimeSkins = lifetimeSkins;
    }

    protected Donor(String... lifetimeSkins) {
        this(List.of(lifetimeSkins));
    }

    public List<String> getLifetimeSkins() {
        return this.lifetimeSkins;
    }

    protected void addLifetimeSkins(String... skins) {
        this.getLifetimeSkins().addAll(List.of(skins));
    }

    protected void addLifetimeSkin(String skin) {
        this.getLifetimeSkins().add(skin);
    }

    protected void finalizeLifetimeSkins() {
        this.lifetimeSkins = Collections.unmodifiableList(this.lifetimeSkins);
    }

    public static Role read(FriendlyByteBuf buffer) {
        List<String> lifetimeSkins = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readUtf);
        return new Donor(lifetimeSkins);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        Role.super.write(buffer);
        buffer.writeCollection(this.getLifetimeSkins(), FriendlyByteBuf::writeUtf);
    }

    @Override
    public Type getType() {
        return Type.DONOR;
    }
}
