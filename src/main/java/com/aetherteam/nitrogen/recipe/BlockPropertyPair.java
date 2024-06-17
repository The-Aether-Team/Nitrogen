package com.aetherteam.nitrogen.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Used to store a block alongside a block's properties.
 */
public record BlockPropertyPair(Block block, Optional<Map<Property<?>, Comparable<?>>> properties) {
    public static final MapCodec<BlockPropertyPair> CODEC = RawPair.CODEC.xmap(
        (rawPair) -> {
            Block rawBlock = rawPair.block();
            Optional<Map<String, String>> rawPropertiesOptional = rawPair.properties();
            Optional<Map<Property<?>, Comparable<?>>> propertiesOptional = Optional.empty();
            if (rawPropertiesOptional.isPresent()) {
                Map<String, String> rawPropertiesMap = rawPropertiesOptional.get();
                StateDefinition<Block, BlockState> rawStateDefinition = rawBlock.getStateDefinition();
                Collection<Property<?>> availableProperties = rawStateDefinition.getProperties();
                Map<String, Property<?>> nameToPropertyMap = availableProperties.stream().collect(Collectors.toMap(Property::getName, (value) -> value));
                Map<Property<?>, Comparable<?>> properties = new HashMap<>();
                for (Map.Entry<String, String> rawPropertiesEntry : rawPropertiesMap.entrySet()) {
                    String rawPropertyName = rawPropertiesEntry.getKey();
                    if (nameToPropertyMap.containsKey(rawPropertyName)) {
                        Property<?> property = nameToPropertyMap.get(rawPropertyName);
                        if (property != null) {
                            Optional<Comparable<?>> comparableOptional = (Optional<Comparable<?>>) property.getValue(rawPropertiesEntry.getValue());
                            comparableOptional.ifPresent(value -> properties.put(property, value));
                        }
                    }
                }
                propertiesOptional = Optional.of(properties);
            }
            return new BlockPropertyPair(rawBlock, propertiesOptional);
        },
        (blockPropertyPair) -> {
            Block block = blockPropertyPair.block();
            Optional<Map<Property<?>, Comparable<?>>> propertiesOptional = blockPropertyPair.properties();
            Optional<Map<String, String>> rawPropertiesOptional = Optional.empty();
            if (propertiesOptional.isPresent()) {
                Map<Property<?>, Comparable<?>> properties = propertiesOptional.get();
                Map<String, String> rawProperties = properties.entrySet().stream().collect(Collectors.toMap((entry) -> entry.getKey().getName(), (entry) -> entry.getValue().toString()));
                rawPropertiesOptional = Optional.of(rawProperties);
            }
            return new BlockPropertyPair.RawPair(block, rawPropertiesOptional);
        }
    );

    public static BlockPropertyPair of(Block block, Optional<Map<Property<?>, Comparable<?>>> properties) {
        return new BlockPropertyPair(block, properties);
    }

    /**
     * Checks if the {@link BlockState} matches the block, before calling {@link BlockPropertyPair#propertiesMatch(BlockState, Optional)}.
     *
     * @param state      The {@link BlockState}.
     * @param block      The {@link Block}.
     * @param properties The {@link Optional} {@link Map} of {@link Property} keys and {@link Comparable} values.
     * @return Whether the block and properties match the {@link BlockState}.
     */
    public static boolean matches(BlockState state, Block block, Optional<Map<Property<?>, Comparable<?>>> properties) {
        if (state.is(block)) {
            return propertiesMatch(state, properties);
        }
        return false;
    }

    /**
     * Checks if the set of given properties all exist within the set of properties of the given {@link BlockState}.
     *
     * @param state      The {@link BlockState}.
     * @param properties The {@link Optional} {@link Map} of {@link Property} keys and {@link Comparable} values.
     * @return Whether all the properties are found within the {@link BlockState}.
     */
    public static boolean propertiesMatch(BlockState state, Optional<Map<Property<?>, Comparable<?>>> properties) {
        if (properties.isPresent() && !properties.get().isEmpty()) {
            HashSet<Map.Entry<Property<?>, Comparable<?>>> stateProperties = new HashSet<>(state.getValues().entrySet());
            return stateProperties.containsAll(properties.get().entrySet());
        }
        return true;
    }

    /**
     * Calls {@link BlockPropertyPair#matches(BlockState, Block, Optional)} with the provided values from this {@link BlockPropertyPair}.
     *
     * @param state A {@link BlockState} from the world.
     * @return Whether the block and properties match the {@link BlockState}.
     */
    public boolean matches(BlockState state) {
        return BlockPropertyPair.matches(state, this.block(), this.properties());
    }

    public record RawPair(Block block, Optional<Map<String, String>> properties) {
        public static final MapCodec<RawPair> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(RawPair::block),
                ExtraCodecs.strictUnboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("properties").forGetter(RawPair::properties)
            ).apply(instance, RawPair::new)
        );
    }
}
