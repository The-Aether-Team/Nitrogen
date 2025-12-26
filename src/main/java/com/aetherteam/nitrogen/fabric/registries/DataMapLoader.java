package com.aetherteam.nitrogen.fabric.registries;

import com.aetherteam.nitrogen.fabric.pond.FullDataMapAccess;
import com.aetherteam.nitrogen.fabric.registries.datamaps.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.*;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DataMapLoader implements PreparableReloadListener, IdentifiableResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String PATH = "data_maps";
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("aether_fabric", PATH);
    private static Map<ResourceKey<? extends Registry<?>>, LoadResult<?>> results = null;
    private final HolderLookup.Provider registryAccess;

    public DataMapLoader(HolderLookup.Provider registryAccess) {
        this.registryAccess = registryAccess;
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return this.load(resourceManager, backgroundExecutor, preparationsProfiler)
            .thenCompose(preparationBarrier::wait)
            .thenAcceptAsync(values -> this.results = values, gameExecutor);
    }

    public static void apply(RegistryAccess registryAccess) {
        if (results == null) return;

        results.forEach((key, result) -> apply((MappedRegistry) registryAccess.registryOrThrow(key), result, registryAccess));

        // Clear the intermediary maps and objects
        results = null;
    }

    private static <T> void apply(MappedRegistry<T> registry, LoadResult<T> result, RegistryAccess registryAccess) {
        ((FullDataMapAccess<T>)registry).nitrogen_fabric$setDataMaps(dataMaps -> {
            result.results().forEach((key, entries) -> dataMaps.put(key, buildDataMap(registry, key, (List) entries)));
        });

        DataMapsUpdatedEvent.EVENT.invoker().onUpdate(new DataMapsUpdatedEvent(registryAccess, registry, DataMapsUpdatedEvent.UpdateCause.SERVER_RELOAD));
    }

    private static <T, R> Map<ResourceKey<R>, T> buildDataMap(Registry<R> registry, DataMapType<R, T> attachment, List<DataMapFile<T, R>> entries) {
        record WithSource<T, R>(T attachment, Either<TagKey<R>, ResourceKey<R>> source) {}
        final Map<ResourceKey<R>, WithSource<T, R>> result = new IdentityHashMap<>();
        final BiConsumer<Either<TagKey<R>, ResourceKey<R>>, Consumer<Holder<R>>> valueResolver = (key, cons) -> key.ifLeft(tag -> registry.getTagOrEmpty(tag).forEach(cons)).ifRight(k -> cons.accept(registry.getHolderOrThrow(k)));
        final DataMapValueMerger<R, T> merger = attachment instanceof AdvancedDataMapType<R, T, ?> adv ? adv.merger() : DataMapValueMerger.defaultMerger();
        entries.forEach(entry -> {
            if (entry.replace()) {
                result.clear();
            }

            entry.values().forEach((tKey, value) -> {
                if (value.isEmpty()) return;

                valueResolver.accept(tKey, holder -> {
                    final var newValue = value.get().carrier();
                    final var key = holder.unwrapKey().orElseThrow();
                    final var oldValue = result.get(key);
                    if (oldValue == null || newValue.replace()) {
                        result.put(key, new WithSource<>(newValue.value(), tKey));
                    } else {
                        result.put(key, new WithSource<>(merger.merge(registry, oldValue.source(), oldValue.attachment(), tKey, newValue.value()), tKey));
                    }
                });
            });

            entry.removals().forEach(trRemoval -> valueResolver.accept(trRemoval.key(), holder -> {
                if (trRemoval.remover().isPresent()) {
                    final var key = holder.unwrapKey().orElseThrow();
                    final var oldValue = result.get(key);
                    if (oldValue != null) {
                        final var newValue = trRemoval.remover().get().remove(oldValue.attachment(), registry, oldValue.source(), holder.value());
                        if (newValue.isEmpty()) {
                            result.remove(key);
                        } else {
                            result.put(key, new WithSource<>(newValue.get(), oldValue.source()));
                        }
                    }
                } else {
                    result.remove(holder.unwrapKey().orElseThrow());
                }
            }));
        });
        final Map<ResourceKey<R>, T> newMap = new IdentityHashMap<>();
        result.forEach((key, val) -> newMap.put(key, val.attachment()));

        return newMap;
    }

    private CompletableFuture<Map<ResourceKey<? extends Registry<?>>, LoadResult<?>>> load(ResourceManager manager, Executor executor, ProfilerFiller profiler) {
        return CompletableFuture.supplyAsync(() -> load(manager, profiler, registryAccess), executor);
    }

    private static Map<ResourceKey<? extends Registry<?>>, LoadResult<?>> load(ResourceManager manager, ProfilerFiller profiler, HolderLookup.Provider access) {
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, access);

        final Map<ResourceKey<? extends Registry<?>>, LoadResult<?>> values = new HashMap<>();
        access.listRegistries().forEach(registryKey -> {
            profiler.push("registry_data_maps/" + registryKey.location() + "/locating");
            final var fileToId = FileToIdConverter.json(PATH + "/" + getFolderLocation(registryKey.location()));
            for (Map.Entry<ResourceLocation, List<Resource>> entry : fileToId.listMatchingResourceStacks(manager).entrySet()) {
                ResourceLocation key = entry.getKey();
                final ResourceLocation attachmentId = fileToId.fileToId(key);
                final var attachment = RegistryManager.getDataMap((ResourceKey) registryKey, attachmentId);
                if (attachment == null) {
                    // TODO: POSSIBLY ADD TOGGLE BUT THIS MAKES SENSE WHEN NOT USING NITROGEN AND SOME OTHER IMPLEMENTATION OF NEO API
                    //LOGGER.warn("Found data map file for non-existent data map type '{}' on registry '{}'.", attachmentId, registryKey.location());
                    continue;
                }
                profiler.popPush("registry_data_maps/" + registryKey.location() + "/" + attachmentId + "/loading");
                values.computeIfAbsent(registryKey, k -> new LoadResult<>(new HashMap<>())).results.put(attachment, readData(
                    ops, attachment, (ResourceKey) registryKey, entry.getValue()));
            }
            profiler.pop();
        });

        return values;
    }

    public static String getFolderLocation(ResourceLocation registryId) {
        return (registryId.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE) ? "" : registryId.getNamespace() + "/") + registryId.getPath();
    }

    private static <A, T> List<DataMapFile<A, T>> readData(RegistryOps<JsonElement> ops, DataMapType<T, A> attachmentType, ResourceKey<Registry<T>> registryKey, List<Resource> resources) {
        final var codec = DataMapFile.codec(registryKey, attachmentType);
        final List<DataMapFile<A, T>> entries = new LinkedList<>();
        for (final Resource resource : resources) {
            try (Reader reader = resource.openAsReader()) {
                JsonElement jsonelement = JsonParser.parseReader(reader);
                entries.add(codec.decode(ops, jsonelement).getOrThrow().getFirst());
            } catch (Exception exception) {
                LOGGER.error("Could not read data map of type {} for registry {}", attachmentType.id(), registryKey, exception);
            }
        }
        return entries;
    }

    private record LoadResult<T>(Map<DataMapType<T, ?>, List<DataMapFile<?, T>>> results) {}
}
