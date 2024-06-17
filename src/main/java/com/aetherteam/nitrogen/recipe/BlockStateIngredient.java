package com.aetherteam.nitrogen.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * [CODE COPY] - {@link net.minecraft.world.item.crafting.Ingredient}.<br><br>
 * Modified to be based on a {@link Predicate}<{@link BlockState}>.
 */
public class BlockStateIngredient implements Predicate<BlockState> {
    public static final BlockStateIngredient EMPTY = new BlockStateIngredient(Stream.empty());

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockStateIngredient> CONTENTS_STREAM_CODEC = StreamCodec.of(BlockStateIngredient::toNetwork, BlockStateIngredient::fromNetwork);

    public static final Codec<BlockStateIngredient> CODEC = codec(true);
    public static final Codec<BlockStateIngredient> CODEC_NONEMPTY = codec(false);

    private final BlockStateIngredient.Value[] values;
    @Nullable
    private BlockPropertyPair[] pairs;

    public BlockStateIngredient(Stream<? extends BlockStateIngredient.Value> values) {
        this.values = values.toArray(BlockStateIngredient.Value[]::new);
    }

    public BlockStateIngredient(BlockStateIngredient.Value[] values) {
        this.values = values;
    }

    private void dissolve() {
        if (this.pairs == null) {
            this.pairs = Arrays.stream(this.values).flatMap(value -> value.getPairs().stream()).distinct().toArray(BlockPropertyPair[]::new);
        }
    }

    /**
     * Warning for "ConstantConditions" is suppressed because the potential of {@link BlockStateIngredient#pairs} being null is avoided by {@link BlockStateIngredient#dissolve()}.
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean test(BlockState state) {
        this.dissolve();
        for (BlockPropertyPair pair : this.pairs) {
            if (pair.matches(state)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return this.values.length == 0 && (this.pairs == null || this.pairs.length == 0);
    }

    @Nullable
    public BlockPropertyPair[] getPairs() {
        this.dissolve();
        return this.pairs;
    }

    public static BlockStateIngredient of() {
        return EMPTY;
    }

    public static BlockStateIngredient of(BlockPropertyPair... blockPropertyPairs) {
        return ofBlockPropertyPair(Arrays.stream(blockPropertyPairs));
    }

    public static BlockStateIngredient ofBlockPropertyPair(Stream<BlockPropertyPair> blockPropertyPairs) {
        return fromValues(blockPropertyPairs.filter((pair) -> !pair.block().defaultBlockState().isAir()).map(BlockStateIngredient.BlockStateValue::new));
    }

    public static BlockStateIngredient of(Block... blocks) {
        return ofBlock(Arrays.stream(blocks));
    }

    public static BlockStateIngredient ofBlock(Stream<Block> blocks) {
        return fromValues(blocks.filter((block) -> !block.defaultBlockState().isAir()).map(BlockStateIngredient.BlockStateValue::new));
    }

    public static BlockStateIngredient of(TagKey<Block> tag) {
        return fromValues(Stream.of(new BlockStateIngredient.TagValue(tag)));
    }

    public static BlockStateIngredient fromValues(Stream<? extends BlockStateIngredient.Value> stream) {
        BlockStateIngredient ingredient = new BlockStateIngredient(stream.toArray(Value[]::new));
        return ingredient.values.length == 0 ? EMPTY : ingredient;
    }

    /**
     * Warning for "ConstantConditions" is suppressed because the potential of {@link BlockStateIngredient#pairs} being null is avoided by {@link BlockStateIngredient#dissolve()}.
     */
    @SuppressWarnings("ConstantConditions")
    private static void toNetwork(FriendlyByteBuf buf, BlockStateIngredient ingredient) {
        ingredient.dissolve();
        buf.writeCollection(Arrays.asList(ingredient.pairs), BlockStateRecipeUtil::writePair);
    }

    private static BlockStateIngredient fromNetwork(FriendlyByteBuf buf) {
        var size = buf.readVarInt();
        return fromValues(Stream.generate(() -> {
            BlockPropertyPair pair = BlockStateRecipeUtil.readPair(buf);
            return new BlockStateIngredient.BlockStateValue(pair.block(), pair.properties());
        }).limit(size));
    }

    private static Codec<BlockStateIngredient> codec(boolean allowEmpty) {
        Codec<BlockStateIngredient.Value[]> codec = Codec.list(BlockStateIngredient.Value.CODEC)
            .comapFlatMap(
                list -> !allowEmpty && list.isEmpty()
                    ? DataResult.error(() -> "Array cannot be empty, at least one item must be defined")
                    : DataResult.success(list.toArray(new BlockStateIngredient.Value[0])),
                List::of
            );
        return Codec.either(codec, BlockStateIngredient.Value.CODEC)
            .flatComapMap(
                either -> either.map(BlockStateIngredient::new, value -> new BlockStateIngredient(new BlockStateIngredient.Value[]{value})),
                ingredient -> {
                    if (ingredient.values.length == 1) {
                        return DataResult.success(Either.right(ingredient.values[0]));
                    } else {
                        return ingredient.values.length == 0 && !allowEmpty
                            ? DataResult.error(() -> "Array cannot be empty, at least one item must be defined")
                            : DataResult.success(Either.left(ingredient.values));
                    }
                }
            );
    }

    public record BlockStateValue(Block block, Optional<Map<Property<?>, Comparable<?>>> properties) implements BlockStateIngredient.Value {
        public static final MapCodec<BlockStateValue> MAP_CODEC = BlockPropertyPair.CODEC.xmap(BlockStateValue::new, BlockStateValue::cast);
        public static final Codec<BlockStateValue> CODEC = MAP_CODEC.codec();

        public BlockStateValue(Block block) {
            this(block, Optional.empty());
        }

        public BlockStateValue(BlockPropertyPair pair) {
            this(pair.block(), pair.properties());
        }

        @Override
        public Collection<BlockPropertyPair> getPairs() {
            return Collections.singleton(BlockPropertyPair.of(this.block, this.properties));
        }

        private static BlockPropertyPair cast(Value value) {
            BlockStateValue cast = (BlockStateValue) value;
            return new BlockPropertyPair(cast.block(), cast.properties());
        }
    }

    public record TagValue(TagKey<Block> tag) implements BlockStateIngredient.Value {
        public static final MapCodec<TagValue> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(value -> value.tag)
            ).apply(instance, BlockStateIngredient.TagValue::new)
        );
        public static final Codec<TagValue> CODEC = MAP_CODEC.codec();

        @Override
        public Collection<BlockPropertyPair> getPairs() {
            List<BlockPropertyPair> list = new ArrayList<>();

            Optional<HolderSet.Named<Block>> tags = BuiltInRegistries.BLOCK.getTag(this.tag);
            tags.ifPresent(holders -> holders.stream().forEach((block) -> list.add(BlockPropertyPair.of(block.value(), Optional.empty()))));

            return list;
        }
    }

    public interface Value {
        MapCodec<BlockStateIngredient.Value> MAP_CODEC = NeoForgeExtraCodecs.xor(BlockStateIngredient.BlockStateValue.MAP_CODEC, BlockStateIngredient.TagValue.MAP_CODEC)
            .xmap(either -> either.map(blockState -> blockState, tag -> tag), value -> {
                if (value instanceof BlockStateIngredient.TagValue tagValue) {
                    return Either.right(tagValue);
                } else if (value instanceof BlockStateIngredient.BlockStateValue blockStateValue) {
                    return Either.left(blockStateValue);
                } else {
                    throw new UnsupportedOperationException("This is neither a blockstate value nor a tag value.");
                }
            });
        Codec<BlockStateIngredient.Value> CODEC = MAP_CODEC.codec();

        Collection<BlockPropertyPair> getPairs();
    }
}
