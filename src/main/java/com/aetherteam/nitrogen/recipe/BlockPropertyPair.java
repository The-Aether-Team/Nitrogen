package com.aetherteam.nitrogen.recipe;

import com.aetherteam.nitrogen.util.DependentMapCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

/**
 * Used to store a block alongside a block's properties.
 */
public record BlockPropertyPair(Block block, Optional<Map<Property<?>, Comparable<?>>> properties) {
//    @SuppressWarnings("unchecked")
//    public static final Codec<BlockPropertyPair> BLOCKSTATE_CODEC = new DependentMapCodec<>(
//            "block",
//            "properties",
//            BuiltInRegistries.BLOCK.byNameCodec(),
//            block -> block.defaultBlockState().getProperties(),
//            property -> (Codec<Comparable<?>>) property.codec(),
//            Property::getName,
//            BlockPropertyPair::new,
//            BlockPropertyPair::block,
//            BlockPropertyPair::properties
//    );
    public static final Codec<BlockPropertyPair> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BlockPropertyPair.BLOCK_CODEC.fieldOf("block").forGetter(value -> value.block),
                    BlockPropertyPair.PROPERTIES_CODEC.optionalFieldOf("properties").forGetter(value -> value.properties)
            ).apply(instance, BlockPropertyPair::new)
    );
    public static final Codec<Block> BLOCK_CODEC = ExtraCodecs.validate(
            BuiltInRegistries.BLOCK.byNameCodec(),
            block -> block == Blocks.AIR ? DataResult.error(() -> "Crafting result must not be minecraft:air") : DataResult.success(block)
    );
    public static final Codec<Map<Property<?>, Comparable<?>>> PROPERTIES_CODEC = null; //TODO;
    /*
    Example 1:
                {
                  "type": "aether:placement_conversion",
                  "biome": "#aether:ultracold",
                  "ingredient": {
                    "block": "minecraft:lava"
                  },
                  "result": {
                    "block": "aether:aerogel"
                  }
                }
     */
    /*
    Example 2:
                {
                  "type": "aether:placement_conversion",
                  "biome": "#aether:ultracold",
                  "ingredient": {
                    "block": "minecraft:candle_cake",
                    "properties": {
                      "lit": "true"
                    }
                  },
                  "result": {
                    "block": "minecraft:candle_cake",
                    "properties": {
                      "lit": "false"
                    }
                  }
                }
     */

    public static BlockPropertyPair of(Block block,  Optional<Map<Property<?>, Comparable<?>>> properties) {
        return new BlockPropertyPair(block, properties);
    }

    /**
     * Checks if the {@link BlockState} matches the block, before calling {@link BlockPropertyPair#propertiesMatch(BlockState, Optional)}.
     * @param state The {@link BlockState}.
     * @param block The {@link Block}.
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
     * @param state The {@link BlockState}.
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
     * @param state A {@link BlockState} from the world.
     * @return Whether the block and properties match the {@link BlockState}.
     */
    public boolean matches(BlockState state) {
        return BlockPropertyPair.matches(state, this.block(), this.properties());
    }
}
