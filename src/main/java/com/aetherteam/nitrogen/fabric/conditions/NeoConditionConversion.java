package com.aetherteam.nitrogen.fabric.conditions;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.fabric.impl.resource.conditions.DefaultResourceConditionTypes;
import net.fabricmc.fabric.impl.resource.conditions.conditions.AllModsLoadedResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.RegistryContainsResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.TagsPopulatedResourceCondition;
import net.minecraft.Optionull;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class NeoConditionConversion {
    public static final String NEO_CONDITIONS_KEY = "neoforge:conditions";

    private static final Map<ResourceLocation, ConditionHandler<?>> CONVERSION_HANDLER = new HashMap<>();

    public static <T extends ResourceCondition> void registerConversion(ResourceLocation neoforgeId, Supplier<ResourceConditionType<T>> fabricType) {
        registerConversion(neoforgeId, fabricType, tMapCodec -> tMapCodec);
    }

    public static <T extends ResourceCondition> void registerConversion(ResourceLocation neoforgeId, Supplier<ResourceConditionType<T>> fabricType, Function<MapCodec<T>, MapCodec<? extends ResourceCondition>> conversionCodec) {
        if (CONVERSION_HANDLER.containsKey(neoforgeId)) {
            throw new IllegalArgumentException("Unable to register neo type [" + neoforgeId + "] as such has already been registered!");
        }

        CONVERSION_HANDLER.put(neoforgeId, new ConditionHandler<T>(Suppliers.memoize(fabricType::get), conversionCodec));
    }

    public record ConditionHandler<T extends ResourceCondition>(Supplier<ResourceConditionType<T>> type, Function<MapCodec<T>, MapCodec<? extends ResourceCondition>> conversionCodec) {
        public ResourceConditionType<ResourceCondition> createType() {
            return new ResourceConditionType<>() {
                @Override
                public ResourceLocation id() {
                    return type.get().id();
                }

                @Override
                public MapCodec<ResourceCondition> codec() {
                    return (MapCodec<ResourceCondition>) conversionCodec.apply(type().get().codec());
                }
            };
        }
    }

    private static final Codec<ResourceConditionType<?>> TYPE_CODEC_WITH_FALLBACK = ResourceLocation.CODEC.comapFlatMap(id -> {
            var conditionType = ResourceConditions.getConditionType(id);

            if (conditionType == null) {
                var handler = CONVERSION_HANDLER.get(id);

                if (handler != null) {
                    conditionType = handler.createType();
                }
            }

            return Optionull.mapOrElse(conditionType, DataResult::success, () -> DataResult.error(() -> "Unknown resource condition key: "+ id));
        },
        ResourceConditionType::id
    );

    private static final Codec<ResourceCondition> CODEC = TYPE_CODEC_WITH_FALLBACK.dispatch(
        ResourceCondition::getType,
        ResourceConditionType::codec);

    private static final Codec<List<ResourceCondition>> LIST_CODEC = CODEC.listOf();

    private static final Codec<ResourceCondition> CONDITION_CODEC = Codec.withAlternative(CODEC, LIST_CODEC, conditions -> ResourceConditions.and(conditions.toArray(new ResourceCondition[0])));

    public static Codec<ResourceCondition> wrapCodec(Codec<ResourceCondition> fabricCodec) {
        return Codec.withAlternative(CONDITION_CODEC, fabricCodec);
    }

    static {
        registerConversion(neo("true"), () -> DefaultResourceConditionTypes.TRUE);
        registerConversion(neo("not"), () -> DefaultResourceConditionTypes.NOT);
        registerConversion(neo("or"), () -> DefaultResourceConditionTypes.OR);
        registerConversion(neo("and"), () -> DefaultResourceConditionTypes.AND);
        registerConversion(neo("mod_loaded"), () -> DefaultResourceConditionTypes.ALL_MODS_LOADED, codec -> {
            return RecordCodecBuilder.<AllModsLoadedResourceCondition>mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("modid").forGetter(resourceCondition -> resourceCondition.modIds().getFirst())
            ).apply(instance, string -> new AllModsLoadedResourceCondition(List.of(string))));
        });
        registerConversion(neo("tag_empty"), () -> DefaultResourceConditionTypes.TAGS_POPULATED, codec -> {
            return RecordCodecBuilder.<TagsPopulatedResourceCondition>mapCodec(
                builder -> builder
                    .group(
                        ResourceLocation.CODEC.xmap(loc -> TagKey.create(Registries.ITEM, loc), TagKey::location).fieldOf("tag").forGetter(resourceCondition -> TagKey.create(Registries.ITEM, resourceCondition.tags().getFirst())))
                    .apply(builder, itemTagKey -> (TagsPopulatedResourceCondition) ResourceConditions.tagsPopulated(itemTagKey)));
        });
        registerConversion(neo("item_exists"), () -> DefaultResourceConditionTypes.REGISTRY_CONTAINS, codec -> {
            return RecordCodecBuilder.<RegistryContainsResourceCondition>mapCodec(
                builder -> builder
                    .group(
                        ResourceLocation.CODEC.fieldOf("item").forGetter(resourceCondition -> resourceCondition.entries().getFirst()))
                    .apply(builder, location -> new RegistryContainsResourceCondition(Registries.ITEM.location(), location)));
        });

        registerConversion(neo("false"), () -> DefaultResourceConditionTypes.TRUE, codec -> MapCodec.unit(ResourceConditions.not(ResourceConditions.alwaysTrue())));

    }

    private static ResourceLocation neo(String path) {
        return ResourceLocation.fromNamespaceAndPath("neoforge", path);
    }
}
