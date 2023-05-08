package com.aetherteam.nitrogen.api.users;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public class User {
    private Tier highestPastTier;
    private Tier currentTier;
    private String renewalDate;
    private Group highestGroup;

    protected User(Tier highestPastTier, Tier currentTier, String renewalDate, Group highestGroup) {
        this.highestPastTier = highestPastTier;
        this.currentTier = currentTier;
        this.renewalDate = renewalDate;
        this.highestGroup = highestGroup;
    }

    public Tier getHighestPastTier() {
        return this.highestPastTier;
    }

    protected void updateHighestPastTier(Tier highestPastTier) {
        this.highestPastTier = currentTier;
    }

    public int getHighestPastTierLevel() {
        Tier tier = this.getHighestPastTier();
        return tier != null ? tier.getLevel() : 0;
    }

    public Tier getCurrentTier() {
        return this.currentTier;
    }

    protected void updateCurrentTier(Tier currentTier) {
        this.currentTier = currentTier;
    }

    public int getCurrentTierLevel() {
        Tier tier = this.getCurrentTier();
        return tier != null ? tier.getLevel() : 0;
    }

    public String getRenewalDate() {
        return this.renewalDate;
    }

    protected void updateRenewalDate(String renewalDate) {
        this.renewalDate = renewalDate;
    }

    public Group getHighestGroup() {
        return this.highestGroup;
    }

    protected void updateHighestGroup(Group highestGroup) {
        this.highestGroup = highestGroup;
    }

    public static User read(FriendlyByteBuf buffer) {
        String highestPastTierName = buffer.readUtf();
        Tier highestPastTier = highestPastTierName.equals("null") ? null : Tier.valueOf(highestPastTierName);
        String currentTierName = buffer.readUtf();
        Tier currentTier = currentTierName.equals("null") ? null : Tier.valueOf(currentTierName);
        String renewalDate = buffer.readUtf();
        renewalDate = renewalDate.equals("null") ? null : renewalDate;
        String highestGroupName = buffer.readUtf();
        Group highestGroup = highestGroupName.equals("null") ? null : Group.valueOf(highestGroupName);
        return new User(highestPastTier, currentTier, renewalDate, highestGroup);
    }

    public static void write(FriendlyByteBuf buffer, User user) {
        buffer.writeUtf(user.getHighestPastTier() == null ? "null" : user.getHighestPastTier().name());
        buffer.writeUtf(user.getCurrentTier() == null ? "null" : user.getCurrentTier().name());
        buffer.writeUtf(user.getRenewalDate() == null ? "null" : user.getRenewalDate());
        buffer.writeUtf(user.getHighestGroup() == null ? "null" : user.getHighestGroup().name());
    }

    public enum Tier {
        HUMAN(0, 2429462, Component.translatable("nitrogen.patreon.tier.human")),
        ASCENTAN(1, 616325, Component.translatable("nitrogen.patreon.tier.ascentan")),
        VALKYRIE(2, 616326, Component.translatable("nitrogen.patreon.tier.valkyrie")),
        ARKENZUS(3, 616327, Component.translatable("nitrogen.patreon.tier.arkenzus"));

        private final int level;
        private final int id;
        private final Component displayName;

        Tier(int level, int id, Component displayName) {
            this.level = level;
            this.id = id;
            this.displayName = displayName;
        }

        public int getLevel() {
            return this.level;
        }

        public int getId() {
            return this.id;
        }

        public Component getDisplayName() {
            return this.displayName;
        }

        public static Tier byId(int id) {
            switch(id) {
                case 2429462 -> {
                    return HUMAN;
                }
                case 616325 -> {
                    return ASCENTAN;
                }
                case 616326 -> {
                    return VALKYRIE;
                }
                case 616327 -> {
                    return ARKENZUS;
                }
                default -> {
                    return null;
                }
            }
        }
    }

    public enum Group {
        AETHER_TEAM(7),
        MODDING_LEGACY(6),
        CONTRIBUTOR(5),
        LEGACY_CONTRIBUTOR(4),
        STAFF(3),
        CELEBRITY(2),
        TRANSLATOR(1);

        private final int level;

        Group(int level) {
            this.level = level;
        }

        public int getLevel() {
            return this.level;
        }
    }
}
