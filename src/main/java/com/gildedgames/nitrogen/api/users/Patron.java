package com.gildedgames.nitrogen.api.users;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public class Patron extends Donor {
    private boolean pledging;
    private Tier tier;

    protected Patron(boolean pledging, Tier tier, List<String> lifetimeSkins) {
        super(lifetimeSkins);
        this.pledging = pledging;
        this.tier = tier;
    }

    protected Patron(boolean pledging, Tier tier, String... lifetimeSkins) {
        this(pledging, tier, List.of(lifetimeSkins));
    }

    public boolean isPledging() {
        return this.pledging;
    }

    protected void updatePledging(boolean pledging) {
        this.pledging = pledging;
    }

    public Tier getTier() {
        return this.tier;
    }

    protected void updateTier(Tier tier) {
        this.tier = tier;
    }

    public static Role read(FriendlyByteBuf buffer) {
        List<String> lifetimeSkins = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readUtf);
        boolean pledging = buffer.readBoolean();
        Tier tier = Tier.valueOf(buffer.readUtf());
        return new Patron(pledging, tier, lifetimeSkins);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        super.write(buffer);
        buffer.writeBoolean(this.isPledging());
        buffer.writeUtf(this.getTier().name());
    }

    @Override
    public Type getType() {
        return Type.PATRON;
    }

    public enum Tier {
        HUMAN(0),
        ANGEL(1),
        VALKYRIE(2),
        ARKENZUS(3);

        private final int level;

        Tier(int level) {
            this.level = level;
        }

        public int getLevel() {
            return this.level;
        }
    }
}
