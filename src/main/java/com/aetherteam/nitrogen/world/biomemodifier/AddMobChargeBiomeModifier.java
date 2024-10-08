package com.aetherteam.nitrogen.world.biomemodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;

/**
 * Biome modifier to add a spawn charge to a biome.
 *
 * @param biomes         {@link Biome Biomes} to add spawn costs to.
 * @param entityType     {@link EntityType} to add spawn costs for.
 * @param charge         Charge for this entity type's spawning.
 * @param energyBudget   Energy budget for this entity type's spawning.
 */
public record AddMobChargeBiomeModifier(HolderSet<Biome> biomes, EntityType<?> entityType, double charge, double energyBudget){
    public static final MapCodec<AddMobChargeBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddMobChargeBiomeModifier::biomes),
        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(AddMobChargeBiomeModifier::entityType),
        Codec.DOUBLE.fieldOf("charge").forGetter(AddMobChargeBiomeModifier::charge),
        Codec.DOUBLE.fieldOf("energy_budget").forGetter(AddMobChargeBiomeModifier::energyBudget)
    ).apply(builder, AddMobChargeBiomeModifier::new));

    public void addModification(ResourceLocation location) {
        BiomeModifications.create(location)
            .add(ModificationPhase.ADDITIONS,
                ctx -> this.biomes.contains(ctx.getBiomeRegistryEntry()),
                (selectionCtx, modificationCtx) -> {
                    modificationCtx.getSpawnSettings().setSpawnCost(this.entityType(), this.charge(), this.energyBudget());
                });
    }
}
