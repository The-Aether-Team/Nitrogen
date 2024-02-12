package com.aetherteam.nitrogen.recipe;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
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

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * [CODE COPY] - {@link net.minecraft.world.item.crafting.Ingredient}.<br><br>
 * Modified to be based on a {@link Predicate}<{@link BlockState}>.
 */
public class BlockStateIngredient implements Predicate<BlockState> {
    @SuppressWarnings({"unused", "unchecked", "rawtypes"})
    public static final Codec<BlockStateIngredient> CODEC = Util.make(() -> {
        Codec<BlockStateIngredient> blockStateIngredientCodec = ExtraCodecs.withAlternative((Codec<BlockStateIngredient>) (Codec) StateValue.CODEC, (Codec<BlockStateIngredient>) (Codec) BlockValue.CODEC);
        Codec<BlockStateIngredient> codec = (Codec<BlockStateIngredient>) (Codec) TagValue.CODEC;
        return ExtraCodecs.withAlternative(codec, blockStateIngredientCodec);
    });

    public static final BlockStateIngredient EMPTY = new BlockStateIngredient(Stream.empty());
    private final BlockStateIngredient.Value[] values;
    @Nullable
    private BlockPropertyPair[] pairs;

    public BlockStateIngredient(Stream<? extends BlockStateIngredient.Value> values) {
        this.values = values.toArray(Value[]::new);
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

    /**
     * Warning for "ConstantConditions" is suppressed because the potential of {@link BlockStateIngredient#pairs} being null is avoided by {@link BlockStateIngredient#dissolve()}.
     */
    @SuppressWarnings("ConstantConditions")
    public final void toNetwork(FriendlyByteBuf buf) {
        this.dissolve();
        buf.writeCollection(Arrays.asList(this.pairs), BlockStateRecipeUtil::writePair);
    }

    public static BlockStateIngredient fromNetwork(FriendlyByteBuf buf) {
        var size = buf.readVarInt();
        return fromValues(Stream.generate(() -> {
            BlockPropertyPair pair = BlockStateRecipeUtil.readPair(buf);
            return new BlockStateIngredient.StateValue(pair.block(), pair.properties());
        }).limit(size));
    }

    public JsonElement toJson() {
        /*if (this.values.length == 1) {
            return this.values[0].serialize();
        } else {
            JsonArray jsonArray = new JsonArray();
            for (BlockStateIngredient.Value value : this.values) {
                jsonArray.add(value.serialize());
            }
            return jsonArray;
        }*/

        return BlockStateIngredient.CODEC.encodeStart(JsonOps.INSTANCE, this).result().get();
    }

    public static BlockStateIngredient fromValues(Stream<? extends BlockStateIngredient.Value> stream) {
        BlockStateIngredient ingredient = new BlockStateIngredient(stream);
        return ingredient.values.length == 0 ? EMPTY : ingredient;
    }

    public static class StateValue implements BlockStateIngredient.Value {
        public static final Codec<BlockStateIngredient.StateValue> CODEC = BlockPropertyPair.BLOCKSTATE_CODEC.xmap(StateValue::new, value -> new BlockPropertyPair(value.block, value.properties));

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

//        @Override
//        public JsonObject serialize() {
//            JsonObject jsonObject = new JsonObject();
//            ResourceLocation blockLocation = BuiltInRegistries.BLOCK.getKey(this.block);
//            if (blockLocation == null) {
//                throw new JsonParseException("Block for ingredient StateValue serialization shouldn't be null");
//            } else {
//                jsonObject.addProperty("block", blockLocation.toString());
//            }
//            JsonObject jsonObject1 = new JsonObject();
//            if (!this.properties.isEmpty()) {
//                for (Map.Entry<Property<?>, Comparable<?>> entry : this.properties.entrySet()) {
//                    Property<?> property = entry.getKey();
//                    jsonObject1.addProperty(property.getName(), BlockStateRecipeUtil.getName(property, entry.getValue()));
//                }
//            }
//            jsonObject.add("properties", jsonObject1);
//            return jsonObject;
//        }
    }

    public static class BlockValue implements BlockStateIngredient.Value {
        public static final Codec<BlockStateIngredient.BlockValue> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BlockPropertyPair.BLOCK_CODEC.fieldOf("block").forGetter(value -> value.block)
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

//        @Override
//        public JsonObject serialize() {
//            JsonObject jsonObject = new JsonObject();
//            ResourceLocation blockLocation = BuiltInRegistries.BLOCK.getKey(this.block);
//            if (blockLocation == null) {
//                throw new JsonParseException("Block for ingredient StateValue serialization shouldn't be null");
//            } else {
//                jsonObject.addProperty("block", blockLocation.toString());
//            }
//            return jsonObject;
//        }
    }

    public static class TagValue implements BlockStateIngredient.Value {
        public static final Codec<BlockStateIngredient.TagValue> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(value -> value.tag)
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

//        @Override
//        public JsonObject serialize() {
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("tag", this.tag.location().toString());
//            return jsonObject;
//        }
    }

    public interface Value {


        Collection<BlockPropertyPair> getPairs();
    }
}
