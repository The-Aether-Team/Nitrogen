package com.aetherteam.nitrogen.world.biomemodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.MobSpawnSettingsBuilder;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

/**
 * Biome modifier to add a spawn charge to a biome.
 *
 * @param biomes         {@link Biome Biomes} to add spawn costs to.
 * @param entityType     {@link EntityType} to add spawn costs for.
 * @param charge         Charge for this entity type's spawning.
 * @param energyBudget   Energy budget for this entity type's spawning.
 */
public record AddMobChargeBiomeModifier(HolderSet<Biome> biomes, EntityType<?> entityType, double charge, double energyBudget) implements BiomeModifier {
    public static final Codec<AddMobChargeBiomeModifier> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddMobChargeBiomeModifier::biomes),
        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(AddMobChargeBiomeModifier::entityType),
        Codec.DOUBLE.fieldOf("charge").forGetter(AddMobChargeBiomeModifier::charge),
        Codec.DOUBLE.fieldOf("energy_budget").forGetter(AddMobChargeBiomeModifier::energyBudget)
    ).apply(builder, AddMobChargeBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == BiomeModifier.Phase.ADD && this.biomes.contains(biome)) {
            MobSpawnSettingsBuilder spawns = builder.getMobSpawnSettings();
            spawns.addMobCharge(this.entityType(), this.charge(), this.energyBudget());
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return AddMobChargeBiomeModifier.CODEC;
    }
}
