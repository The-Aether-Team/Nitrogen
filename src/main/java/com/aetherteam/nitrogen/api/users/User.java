package com.aetherteam.nitrogen.api.users;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public final class User { //TODO VERIFY


    public static final Codec<User> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Tier.CODEC.optionalFieldOf("highest_past_tier").forGetter(User::getHighestPastTier),
        Tier.CODEC.optionalFieldOf("current_tier").forGetter(User::getCurrentTier),
        Codec.STRING.fieldOf("renewal_data").forGetter(User::getRenewalDate),
        Group.CODEC.optionalFieldOf("highest_group").forGetter(User::getHighestGroup)
    ).apply(instance, User::new));
    public static final StreamCodec<FriendlyByteBuf, User> STREAM_CODEC = StreamCodec.composite(
        Tier.STREAM_CODEC.apply(ByteBufCodecs::optional), User::getHighestPastTier,
        Tier.STREAM_CODEC.apply(ByteBufCodecs::optional), User::getCurrentTier,
        ByteBufCodecs.STRING_UTF8, User::getRenewalDate,
        Group.STREAM_CODEC.apply(ByteBufCodecs::optional), User::getHighestGroup,
        User::new);


    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    @Nullable
    private Optional<Tier> highestPastTier;
    @Nullable
    private Optional<Tier> currentTier;
    private String renewalDate;
    @Nullable
    private Optional<Group> highestGroup;

    User(Optional<Tier> highestPastTier, Optional<Tier> currentTier, String renewalDate, Optional<Group> highestGroup) {
        this.highestPastTier = highestPastTier;
        this.currentTier = currentTier;
        this.renewalDate = renewalDate;
        this.highestGroup = highestGroup;
    }

    /**
     * @return The highest Patreon {@link Tier} that this user has had in the past.
     */
    @Nullable
    public Optional<Tier> getHighestPastTier() {
        return this.highestPastTier;
    }

    /**
     * Sets a new past highest Patreon {@link Tier} level.
     *
     * @param highestPastTier The Patreon {@link Tier}.
     */
    private void updateHighestPastTier(Optional<Tier> highestPastTier) {
        this.highestPastTier = highestPastTier;
    }

    /**
     * @return The {@link Integer} for the highest Patreon {@link Tier} level that this user has had in the past.
     */
    public int getHighestPastTierLevel() {
        Optional<Tier> tier = this.getHighestPastTier();
        return tier.isPresent() ? tier.get().getLevel() : 0;
    }

    /**
     * @return The current Patreon {@link Tier} for this user.
     */
    @Nullable
    public Optional<Tier> getCurrentTier() {
        return this.currentTier;
    }

    /**
     * Sets a new current Patreon {@link Tier} for this user.
     *
     * @param currentTier The Patreon {@link Tier}.
     */
    private void updateCurrentTier(Optional<Tier> currentTier) {
        this.currentTier = currentTier;
    }

    /**
     * @return The {@link Integer} for the current Patreon {@link Tier} level of this user.
     */
    public int getCurrentTierLevel() {
        Optional<Tier> tier = this.getCurrentTier();
        return tier.isPresent() ? tier.get().getLevel() : 0;
    }

    /**
     * @return A {@link String} for the renewal date when this user's information has to be re-verified.
     */
    public String getRenewalDate() {
        return this.renewalDate;
    }

    /**
     * Sets a new renewal date for when this user's information has to be re-verified.
     *
     * @param renewalDate The {@link String} for the date.
     */
    private void updateRenewalDate(String renewalDate) {
        this.renewalDate = renewalDate;
    }

    /**
     * @return The highest ranked {@link Group} that this user is in.
     */
    @Nullable
    public Optional<Group> getHighestGroup() {
        return this.highestGroup;
    }

    /**
     * Sets a new highest {@link Group} value for the user.
     *
     * @param highestGroup The {@link Group}.
     */
    private void updateHighestGroup(Optional<Group> highestGroup) {
        this.highestGroup = highestGroup;
    }


    /**
     * The Patreon tiers that this {@link User} can have.
     */
    public enum Tier implements StringRepresentable {
        HUMAN(0, 2429462, Component.translatable("nitrogen_internals.patreon.tier.human")),
        ASCENTAN(1, 616325, Component.translatable("nitrogen_internals.patreon.tier.ascentan")),
        VALKYRIE(2, 616326, Component.translatable("nitrogen_internals.patreon.tier.valkyrie")),
        ARKENZUS(3, 616327, Component.translatable("nitrogen_internals.patreon.tier.arkenzus"));

        public static final Codec<Tier> CODEC = StringRepresentable.fromEnum(Tier::values);
        public static final StreamCodec<FriendlyByteBuf, Tier> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, Tier::name, Tier::valueOf);

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
            switch (id) {
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

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }

    /**
     * The groups that this {@link User} can be in.
     */
    public enum Group implements StringRepresentable {
        AETHER_TEAM(7),
        MODDING_LEGACY(6),
        CONTRIBUTOR(5),
        LEGACY_CONTRIBUTOR(4),
        STAFF(3),
        CELEBRITY(2),
        TRANSLATOR(1);

        public static final Codec<Group> CODEC = StringRepresentable.fromEnum(Group::values);
        public static final StreamCodec<FriendlyByteBuf, Group> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, Group::name, Group::valueOf);

        private final int level;

        Group(int level) {
            this.level = level;
        }

        public int getLevel() {
            return this.level;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}
