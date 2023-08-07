package com.aetherteam.nitrogen.api.users;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.time.format.DateTimeFormatter;

public final class User {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    @Nullable
    private Tier highestPastTier;
    @Nullable
    private Tier currentTier;
    private String renewalDate;
    @Nullable
    private Group highestGroup;

    User(@Nullable Tier highestPastTier, @Nullable Tier currentTier, String renewalDate, @Nullable Group highestGroup) {
        this.highestPastTier = highestPastTier;
        this.currentTier = currentTier;
        this.renewalDate = renewalDate;
        this.highestGroup = highestGroup;
    }

    /**
     * @return The highest Patreon {@link Tier} that this user has had in the past.
     */
    @Nullable
    public Tier getHighestPastTier() {
        return this.highestPastTier;
    }

    /**
     * Sets a new past highest Patreon {@link Tier} level.
     * @param highestPastTier The Patreon {@link Tier}.
     */
    private void updateHighestPastTier(@Nullable Tier highestPastTier) {
        this.highestPastTier = highestPastTier;
    }

    /**
     * @return The {@link Integer} for the highest Patreon {@link Tier} level that this user has had in the past.
     */
    public int getHighestPastTierLevel() {
        Tier tier = this.getHighestPastTier();
        return tier != null ? tier.getLevel() : 0;
    }

    /**
     * @return The current Patreon {@link Tier} for this user.
     */
    @Nullable
    public Tier getCurrentTier() {
        return this.currentTier;
    }

    /**
     * Sets a new current Patreon {@link Tier} for this user.
     * @param currentTier The Patreon {@link Tier}.
     */
    private void updateCurrentTier(@Nullable Tier currentTier) {
        this.currentTier = currentTier;
    }

    /**
     * @return The {@link Integer} for the current Patreon {@link Tier} level of this user.
     */
    public int getCurrentTierLevel() {
        Tier tier = this.getCurrentTier();
        return tier != null ? tier.getLevel() : 0;
    }

    /**
     * @return A {@link String} for the renewal date when this user's information has to be re-verified.
     */
    public String getRenewalDate() {
        return this.renewalDate;
    }

    /**
     * Sets a new renewal date for when this user's information has to be re-verified.
     * @param renewalDate The {@link String} for the date.
     */
    private void updateRenewalDate(String renewalDate) {
        this.renewalDate = renewalDate;
    }

    /**
     * @return The highest ranked {@link Group} that this user is in.
     */
    @Nullable
    public Group getHighestGroup() {
        return this.highestGroup;
    }

    /**
     * Sets a new highest {@link Group} value for the user.
     * @param highestGroup The {@link Group}.
     */
    private void updateHighestGroup(@Nullable Group highestGroup) {
        this.highestGroup = highestGroup;
    }

    /**
     * Reads a {@link User} from a {@link FriendlyByteBuf} network buffer.
     * @param buffer The {@link FriendlyByteBuf} buffer.
     * @return A {@link User}.
     */
    public static User read(FriendlyByteBuf buffer) {
        boolean canRead = buffer.readBoolean();
        if (canRead) {
            String highestPastTierName = buffer.readUtf();
            Tier highestPastTier = highestPastTierName.equals("null") ? null : Tier.valueOf(highestPastTierName);
            String currentTierName = buffer.readUtf();
            Tier currentTier = currentTierName.equals("null") ? null : Tier.valueOf(currentTierName);
            String renewalDate = buffer.readUtf();
            renewalDate = renewalDate.equals("null") ? null : renewalDate;
            String highestGroupName = buffer.readUtf();
            Group highestGroup = highestGroupName.equals("null") ? null : Group.valueOf(highestGroupName);
            return new User(highestPastTier, currentTier, renewalDate, highestGroup);
        } else {
            return null;
        }
    }

    /**
     * Writes a {@link User} to a {@link FriendlyByteBuf} network buffer.
     * @param buffer The {@link FriendlyByteBuf} buffer.
     * @param user A {@link User}.
     */
    public static void write(FriendlyByteBuf buffer, User user) {
        if (user == null) {
            buffer.writeBoolean(false);
        } else {
            buffer.writeBoolean(true);
            buffer.writeUtf(user.getHighestPastTier() == null ? "null" : user.getHighestPastTier().name());
            buffer.writeUtf(user.getCurrentTier() == null ? "null" : user.getCurrentTier().name());
            buffer.writeUtf(user.getRenewalDate() == null ? "null" : user.getRenewalDate());
            buffer.writeUtf(user.getHighestGroup() == null ? "null" : user.getHighestGroup().name());
        }
    }

    /**
     * The Patreon tiers that this {@link User} can have.
     */
    public enum Tier {
        HUMAN(0, 2429462, Component.translatable("nitrogen_internals.patreon.tier.human")),
        ASCENTAN(1, 616325, Component.translatable("nitrogen_internals.patreon.tier.ascentan")),
        VALKYRIE(2, 616326, Component.translatable("nitrogen_internals.patreon.tier.valkyrie")),
        ARKENZUS(3, 616327, Component.translatable("nitrogen_internals.patreon.tier.arkenzus"));

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

    /**
     * The groups that this {@link User} can be in.
     */
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
