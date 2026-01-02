/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.loot;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.fabric.pond.LootContextExtension;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class LootTableModificationAPI {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceKey<Registry<MapCodec<? extends IGlobalLootModifier>>> GLOBAL_LOOT_MODIFIER_CODEC_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("neoforge", "global_loot_modifier_serializers"));
    public static final Registry<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_CODEC = FabricRegistryBuilder.createSimple(GLOBAL_LOOT_MODIFIER_CODEC_KEY).buildAndRegister();

    public static final ResourceLocation LISTENER_ID = Nitrogen.id("loot_table_modifications");

    private static final Map<ResourceLocation, IGlobalLootModifier> CACHED_LOOT_MODIFIERS = new LinkedHashMap<>();

    private static final Map<ResourceLocation, Function<HolderLookup.Provider, IGlobalLootModifier>> LOOT_MODIFIERS = new HashMap<>();

    public static final ResourceLocation UNKNOWN_TABLE_ID = ResourceLocation.fromNamespaceAndPath(Nitrogen.MODID, "unknown");

    static {
        LootTableEvents.MODIFY_DROPS.register((entry, context, drops) -> {
            var lootTableId = entry.unwrapKey().map(ResourceKey::location);

            var ext = ((LootContextExtension) context);

            // Handles case where the given loot table is Unknown at all meaning an unknown location is pushed to the top.
            // Typically, occurs when the table is a nested one or injected in manor where its it not registered
            ext.nitrogen_fabric$pushTableId(lootTableId.orElse(UNKNOWN_TABLE_ID));

            var stacks = new ObjectArrayList<>(drops);
            drops.clear();

            drops.addAll(LootTableModificationAPI.apply(stacks, context));

            ext.nitrogen_fabric$popTableId();
        });
    }

    //--

    public static void register(ResourceLocation location, Supplier<IGlobalLootModifier> modifier) {
        register(location, provider -> modifier.get());
    }

    public static void register(ResourceLocation location, Function<HolderLookup.Provider, IGlobalLootModifier> modifier) {
        if (LOOT_MODIFIERS.containsKey(location)) {
            throw new IllegalStateException("Unable to register custom loot modifier as one with the given id already exists: " + location);
        }

        LOOT_MODIFIERS.put(location, modifier);
    }

    //--

    public static void init() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(LISTENER_ID, ReloadListener::new);

        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, ResourceLocation.fromNamespaceAndPath("neoforge", "loot_table_id"), LootTableCondition.TYPE);
    }

    public static ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (var value : CACHED_LOOT_MODIFIERS.values()) {
            generatedLoot = value.apply(generatedLoot, context);
        }

        return generatedLoot;
    }

    private static void gatherAllLootModifiers(Map<ResourceLocation, JsonElement> data, HolderLookup.Provider provider) {
        CACHED_LOOT_MODIFIERS.clear();

        LOOT_MODIFIERS.forEach((location, supplier) -> CACHED_LOOT_MODIFIERS.put(location, supplier.apply(provider)));

        for (var entry : data.entrySet()) {
            var location = entry.getKey();
            var json = entry.getValue();

            IGlobalLootModifier.DIRECT_CODEC.parse(provider.createSerializationContext(JsonOps.INSTANCE), json)
                .resultOrPartial(errorMsg -> LOGGER.warn("Could not decode GlobalLootModifier with json id {} - error: {}", location, errorMsg))
                .ifPresent(modifier -> CACHED_LOOT_MODIFIERS.put(location, modifier));
        }
    }

    public static class ReloadListener extends SimpleJsonResourceReloadListener<JsonElement> implements IdentifiableResourceReloadListener {
        private static final ResourceLocation LOOT_MODIFIER_CONFIG = ResourceLocation.fromNamespaceAndPath("neoforge", "loot_modifiers/global_loot_modifiers.json");
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        private final HolderLookup.Provider provider;

        private ReloadListener(HolderLookup.Provider provider) {
            super(ExtraCodecs.JSON, FileToIdConverter.json("loot_modifiers"));

            this.provider = provider;
        }

        private record LootModificationConfig(boolean replace, List<ResourceLocation> allowedModificationEntries) {
            public static final Codec<LootModificationConfig> CODEC = RecordCodecBuilder.<LootModificationConfig>mapCodec(instance -> {
                return instance.group(
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(LootModificationConfig::replace),
                    ResourceLocation.CODEC.listOf().fieldOf("entries").forGetter(LootModificationConfig::allowedModificationEntries)
                ).apply(instance, LootModificationConfig::new);
            }).codec();
        }

        @Override
        protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
            var data = super.prepare(resourceManager, profiler);

            var validLocations = new ArrayList<ResourceLocation>();

            for (var resource : resourceManager.getResourceStack(LOOT_MODIFIER_CONFIG)) {
                try (var reader = resource.openAsReader()) {
                    var obj = GsonHelper.fromJson(GSON, reader, JsonObject.class);

                    var config = LootModificationConfig.CODEC.parse(JsonOps.INSTANCE, obj).getOrThrow();

                    if (config.replace()) validLocations.clear();

                    for (var location : config.allowedModificationEntries()) {
                        validLocations.remove(location);
                        validLocations.add(location);
                    }
                } catch (RuntimeException | IOException ioexception) {
                    LOGGER.error("Couldn't read global loot modifier list {} in data pack {}", LOOT_MODIFIER_CONFIG, resource.sourcePackId(), ioexception);
                }
            }

            var validData = new LinkedHashMap<ResourceLocation, JsonElement>();

            for (var location : validLocations) validData.put(location, data.get(location));

            return validData;
        }



        @Override
        protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
            LootTableModificationAPI.gatherAllLootModifiers(object, provider);
        }

        @Override
        public ResourceLocation getFabricId() {
            return LISTENER_ID;
        }
    }
}
