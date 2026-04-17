package com.aetherteam.nitrogen.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.Holder;
import net.minecraft.core.TypedInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Used to store a block alongside a block's properties.
 */
public record BlockPropertyPair(Block block, Optional<HashSet<Property.Value<?>>> properties) implements TypedInstance<Block> {
    public static final  MapCodec<BlockPropertyPair> CODEC = RawPair.CODEC.xmap(
        (rawPair) -> {
            Block rawBlock = rawPair.block();
            Optional<Map<String, String>> rawPropertiesOptional = rawPair.properties();
            Optional<HashSet<Property.Value<?>>> propertiesOptional = Optional.empty();
            if (rawPropertiesOptional.isPresent()) {
                Map<String, String> rawPropertiesMap = rawPropertiesOptional.get();
                StateDefinition<Block, BlockState> rawStateDefinition = rawBlock.getStateDefinition();
                Collection<Property<?>> availableProperties = rawStateDefinition.getProperties();
                Map<String, Property<?>> nameToPropertyMap = availableProperties.stream().collect(Collectors.toMap(Property::getName, (value) -> value));
                HashSet<Property.Value<?>> properties = new HashSet<>();
                for (Map.Entry<String, String> rawPropertiesEntry : rawPropertiesMap.entrySet()) {
                    String rawPropertyName = rawPropertiesEntry.getKey();
                    if (nameToPropertyMap.containsKey(rawPropertyName)) {
                        Property<?> property = nameToPropertyMap.get(rawPropertyName);
                        if (property != null) {
                            Optional<Comparable<?>> comparableOptional = (Optional<Comparable<?>>) property.getValue(rawPropertiesEntry.getValue());
                            comparableOptional.ifPresent(value -> properties.add(new Property.Value(property, value)));
                        }
                    }
                }
                propertiesOptional = Optional.of(properties);
            }
            return new BlockPropertyPair(rawBlock, propertiesOptional);
        },
        (blockPropertyPair) -> {
            Block block = blockPropertyPair.block();
            Optional<HashSet<Property.Value<?>>> propertiesOptional = blockPropertyPair.properties();
            Optional<Map<String, String>> rawPropertiesOptional = Optional.empty();
            if (propertiesOptional.isPresent()) {
                HashSet<Property.Value<?>> properties = propertiesOptional.get();
                Map<String, String> rawProperties = properties.stream().collect(Collectors.toMap((entry) -> entry.property().getName(), (entry) -> entry.value().toString()));
                rawPropertiesOptional = Optional.of(rawProperties);
            }
            return new BlockPropertyPair.RawPair(block, rawPropertiesOptional);
        }
    );

    public static BlockPropertyPair of(Block block, Optional<HashSet<Property.Value<?>>> properties) {
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
    public static boolean matches(BlockState state, Block block, Optional<HashSet<Property.Value<?>>> properties) {
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
    public static boolean propertiesMatch(BlockState state, Optional<HashSet<Property.Value<?>>> properties) {
        if (properties.isPresent() && !properties.get().isEmpty()) {
//            HashSet<Property.Value<?>> stateProperties = new HashSet<>(state.getValues().entrySet());

            HashSet<Property.Value<?>> stateProperties = state.getValues().collect(Collectors.toCollection(HashSet::new));

            return stateProperties.containsAll(properties.get());
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

    @Override
    public Holder<Block> typeHolder() {
        return this.block().builtInRegistryHolder();
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
