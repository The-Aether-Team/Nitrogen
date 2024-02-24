package com.aetherteam.nitrogen.recipe;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
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
        if (this.pairs.length != 0) {
            for (BlockPropertyPair pair : this.pairs) {
                if (pair.matches(state)) {
                    return true;
                }
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
        return fromValues(blockPropertyPairs.filter((pair) -> !pair.block().defaultBlockState().isAir()).map(BlockStateIngredient.StateValue::new));
    }

    public static BlockStateIngredient of(Block... blocks) {
        return ofBlock(Arrays.stream(blocks));
    }

    public static BlockStateIngredient ofBlock(Stream<Block> blocks) {
        return fromValues(blocks.filter((block) -> !block.defaultBlockState().isAir()).map(BlockStateIngredient.BlockValue::new));
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
    public final void toNetwork(FriendlyByteBuf buf) {
        this.dissolve();
        buf.writeCollection(Arrays.asList(this.pairs), BlockStateRecipeUtil::writePair);
    }

    public JsonElement toJson(boolean allowEmpty) {
        Codec<BlockStateIngredient> codec = allowEmpty ? CODEC : CODEC_NONEMPTY;
        return Util.getOrThrow(codec.encodeStart(JsonOps.INSTANCE, this), IllegalStateException::new);
    }

    public static BlockStateIngredient fromNetwork(FriendlyByteBuf buf) {
        var size = buf.readVarInt();
        return fromValues(Stream.generate(() -> {
            BlockPropertyPair pair = BlockStateRecipeUtil.readPair(buf);
            return new BlockStateIngredient.StateValue(pair.block(), pair.properties());
        }).limit(size));
    }

    public static BlockStateIngredient fromJson(JsonElement element, boolean nonEmpty) {
        Codec<BlockStateIngredient> codec = nonEmpty ? CODEC : CODEC_NONEMPTY;
        return Util.getOrThrow(codec.parse(JsonOps.INSTANCE, element), IllegalStateException::new);
    }

    private static Codec<BlockStateIngredient> codec(boolean allowEmpty) {
        Codec<BlockStateIngredient.Value[]> codec = Codec.list(BlockStateIngredient.Value.CODEC)
                .comapFlatMap(
                        list -> !allowEmpty && list.size() < 1
                                ? DataResult.error(() -> "Array cannot be empty, at least one item must be defined")
                                : DataResult.success(list.toArray(new BlockStateIngredient.Value[0])),
                        List::of
                );
        return ExtraCodecs.either(codec, BlockStateIngredient.Value.CODEC)
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

    public static class StateValue implements BlockStateIngredient.Value {
        public static final Codec<BlockStateIngredient.Value> CODEC = BlockPropertyPair.BLOCKSTATE_CODEC.flatComapMap(StateValue::new, StateValue::cast);

        private final Block block;
        private final Map<Property<?>, Comparable<?>> properties;

        public StateValue(Block block, Map<Property<?>, Comparable<?>> properties) {
            this.block = block;
            this.properties = properties;
        }

        public StateValue(BlockPropertyPair blockPropertyPair) {
            this.block = blockPropertyPair.block();
            this.properties = blockPropertyPair.properties();
        }

        @Override
        public Collection<BlockPropertyPair> getPairs() {
            return Collections.singleton(BlockPropertyPair.of(this.block, this.properties));
        }

        private static DataResult<? extends BlockPropertyPair> cast(Value value) {
            StateValue cast = (StateValue) value;
            return DataResult.success(new BlockPropertyPair(cast.block, cast.properties));
        }
    }

    public static class BlockValue implements BlockStateIngredient.Value {
        public static final Codec<BlockStateIngredient.Value> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BlockPropertyPair.BLOCK_CODEC.fieldOf("block").forGetter(value -> ((BlockValue) value).block)
                ).apply(instance, BlockStateIngredient.BlockValue::new)
        );

        private final Block block;

        public BlockValue(Block block) {
            this.block = block;
        }

        @Override
        public Collection<BlockPropertyPair> getPairs() {
            return Collections.singleton(BlockPropertyPair.of(this.block, Map.of()));
        }
    }

    public static class TagValue implements BlockStateIngredient.Value {
        public static final Codec<BlockStateIngredient.Value> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(value -> ((TagValue) value).tag)
                ).apply(instance, BlockStateIngredient.TagValue::new)
        );

        private final TagKey<Block> tag;

        public TagValue(TagKey<Block> tag) {
            this.tag = tag;
        }

        @Override
        public Collection<BlockPropertyPair> getPairs() {
            List<BlockPropertyPair> list = new ArrayList<>();

            Optional<HolderSet.Named<Block>> tags = BuiltInRegistries.BLOCK.getTag(this.tag);
            tags.ifPresent(holders -> holders.stream().forEach((block) -> list.add(BlockPropertyPair.of(block.value(), Map.of()))));

            return list;
        }
    }

    public interface Value {
        Codec<BlockStateIngredient.Value> CODEC = Util.make(() -> NeoForgeExtraCodecs.withAlternative(BlockStateIngredient.TagValue.CODEC, NeoForgeExtraCodecs.withAlternative(BlockStateIngredient.StateValue.CODEC, BlockStateIngredient.BlockValue.CODEC)));

        Collection<BlockPropertyPair> getPairs();
    }
}
