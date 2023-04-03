package com.aetherteam.nitrogen.api.users;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ranked implements Role {
    private List<Rank> ranks = new ArrayList<>();

    protected Ranked(List<Rank> ranks) {
        this.ranks.addAll(ranks);
    }

    protected Ranked(Rank... ranks) {
        this(List.of(ranks));
    }

    public List<Rank> getRanks() {
        return this.ranks;
    }

    protected void addRanks(Rank... ranks) {
        this.getRanks().addAll(List.of(ranks));
    }

    protected void addRank(Rank rank) {
        this.getRanks().add(rank);
    }

    protected void finalizeRanks() {
        this.ranks = Collections.unmodifiableList(this.ranks);
    }

    public static Role read(FriendlyByteBuf buffer) {
        List<Rank> ranks = buffer.readCollection(ArrayList::new, Rank::read);
        return new Ranked(ranks);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        Role.super.write(buffer);
        buffer.writeCollection(this.getRanks(), Rank::write);
    }

    @Override
    public Type getType() {
        return Type.RANKED;
    }

    public enum Rank {
        GILDED_GAMES,
        MODDING_LEGACY,
        CONTRIBUTOR,
        LEGACY_CONTRIBUTOR,
        STAFF,
        CELEBRITY,
        TRANSLATOR;

        private static Rank read(FriendlyByteBuf buffer) {
            return Rank.valueOf(buffer.readUtf());
        }

        private static void write(FriendlyByteBuf buffer, Rank rank) {
            buffer.writeUtf(rank.name());
        }
    }
}
