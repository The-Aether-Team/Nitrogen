package com.aetherteam.nitrogen.world.biomemodifier;

import com.aetherteam.nitrogen.fabric.world.biome.BiomeModificationData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Predicate;

/**
 * Biome modifier to add a spawn charge to a biome.
 *
 * @param biomes         {@link Biome Biomes} to add spawn costs to.
 * @param entityType     {@link EntityType} to add spawn costs for.
 * @param charge         Charge for this entity type's spawning.
 * @param energyBudget   Energy budget for this entity type's spawning.
 */
public record AddMobChargeBiomeModifier(HolderSet<Biome> biomes, EntityType<?> entityType, double charge, double energyBudget) implements BiomeModificationData {
    public static final MapCodec<AddMobChargeBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddMobChargeBiomeModifier::biomes),
        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(AddMobChargeBiomeModifier::entityType),
        Codec.DOUBLE.fieldOf("charge").forGetter(AddMobChargeBiomeModifier::charge),
        Codec.DOUBLE.fieldOf("energy_budget").forGetter(AddMobChargeBiomeModifier::energyBudget)
    ).apply(builder, AddMobChargeBiomeModifier::new));

    @Override
    public void modify(BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) {
        modificationContext.getSpawnSettings().setSpawnCost(this.entityType(), this.charge(), this.energyBudget());
    }

    @Override
    public ModificationPhase phase() {
        return ModificationPhase.ADDITIONS;
    }

    @Override
    public Predicate<BiomeSelectionContext> selector() {
        return ctx -> this.biomes.contains(ctx.getBiomeRegistryEntry());
    }

    @Override
    public MapCodec<? extends BiomeModificationData> codec() {
        return AddMobChargeBiomeModifier.CODEC;
    }
}
