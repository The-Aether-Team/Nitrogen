package com.aetherteam.nitrogen.recipe;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class BlockStateRecipeUtil {
    public static Codec<ResourceKey<Biome>> RESOURCE_KEY_CODEC = Codec.STRING.comapFlatMap((to) -> !to.startsWith("#")
            ? DataResult.success(ResourceKey.create(Registries.BIOME, new ResourceLocation(to)))
            : DataResult.error(() -> "Value is not a resource key"), (from) -> from.location().toString());
    public static Codec<TagKey<Biome>> TAG_KEY_CODEC = Codec.STRING.comapFlatMap((to) -> to.startsWith("#") 
            ? DataResult.success(TagKey.create(Registries.BIOME, new ResourceLocation(to.replace("#", ""))))
            : DataResult.error(() -> "Value is not a tag key"), (from) -> "#" + from.location());
    public static Codec<Either<ResourceKey<Biome>, TagKey<Biome>>> KEY_CODEC = ExtraCodecs.xor(RESOURCE_KEY_CODEC, TAG_KEY_CODEC);

    /**
     * Executes an {@link net.minecraft.commands.CommandFunction.CacheableFunction}.
     * @param level The {@link Level} to execute in.
     * @param pos The {@link BlockPos} to execute at.
     * @param function The {@link Optional} {@link net.minecraft.commands.CommandFunction.CacheableFunction} to execute.
     */
    public static void executeFunction(Level level, BlockPos pos, Optional<CommandFunction.CacheableFunction> function) {
        if (level instanceof ServerLevel serverLevel) {
            function.ifPresent((cacheableFunction) -> {
                MinecraftServer minecraftServer = serverLevel.getServer();
                cacheableFunction.get(minecraftServer.getFunctions()).ifPresent(command -> {
                    CommandSourceStack context = minecraftServer.getFunctions().getGameLoopSender()
                            .withPosition(Vec3.atBottomCenterOf(pos))
                            .withLevel(serverLevel);
                    minecraftServer.getFunctions().execute(command, context);
                });
            });
        }
    }

    /**
     * Builds an {@link Optional} {@link net.minecraft.commands.CommandFunction.CacheableFunction} from an {@link Optional} {@link ResourceLocation} ID.
     * @param functionLocation The {@link Optional} {@link ResourceLocation} ID.
     * @return The {@link Optional} {@link net.minecraft.commands.CommandFunction.CacheableFunction}.
     */
    public static Optional<CommandFunction.CacheableFunction> buildFunction(Optional<ResourceLocation> functionLocation) {
        return functionLocation.map(CommandFunction.CacheableFunction::new);
    }

    // Buffer write methods.
    /**
     * Writes a {@link BlockPropertyPair} to the networking buffer.
     * @param buffer The networking {@link FriendlyByteBuf}.
     * @param pair The {@link BlockPropertyPair}.
     */
    public static void writePair(FriendlyByteBuf buffer, BlockPropertyPair pair) {
        ResourceLocation blockLocation = BuiltInRegistries.BLOCK.getKey(pair.block());
        if (pair.block().defaultBlockState().isAir() && pair.properties().isEmpty()) {
            buffer.writeBoolean(false);
        } else {
            buffer.writeBoolean(true);
            buffer.writeUtf(blockLocation.toString());
            buffer.writeOptional(pair.properties(), ((friendlyByteBuf, propertyComparableMap) -> {
                CompoundTag tag = new CompoundTag();
                for (Map.Entry<Property<?>, Comparable<?>> entry : propertyComparableMap.entrySet()) {
                    Property<?> property = entry.getKey();
                    tag.putString(property.getName(), getName(property, entry.getValue()));
                }
                friendlyByteBuf.writeNbt(tag);
            }));
        }
    }

    // Buffer read methods.
    /**
     * Reads a {@link BlockPropertyPair} from the networking buffer.<br><br>
     * Warning for "unchecked" is suppressed because casting within this method works fine.
     * @param buffer The networking {@link FriendlyByteBuf}.
     * @return The {@link BlockPropertyPair}.
     */
    @SuppressWarnings("unchecked")
    public static BlockPropertyPair readPair(FriendlyByteBuf buffer) {
        if (!buffer.readBoolean()) {
            return BlockPropertyPair.of(Blocks.AIR, Optional.empty());
        } else {
            String blockString = buffer.readUtf();
            ResourceLocation blockLocation = new ResourceLocation(blockString);
            Block block = BuiltInRegistries.BLOCK.get(blockLocation);

            Optional<Map<Property<?>, Comparable<?>>> propertiesOptional = buffer.readOptional((friendlyByteBuf -> {
                Map<Property<?>, Comparable<?>> properties = new HashMap<>();
                CompoundTag tag = friendlyByteBuf.readNbt();
                if (tag != null) {
                    for (String propertyName : tag.getAllKeys()) {
                        Property<?> property = block.getStateDefinition().getProperty(propertyName);
                        if (property != null) {
                            Optional<Comparable<?>> comparable = (Optional<Comparable<?>>) property.getValue(propertyName);
                            comparable.ifPresent(value -> properties.put(property, value));
                        }
                    }
                }
                return properties;
            }));

            return BlockPropertyPair.of(block, propertiesOptional);
        }
    }

    // JSON write methods.
    /**
     * Adds an {@link Optional} {@link Biome} {@link ResourceKey} to a {@link JsonObject}.
     * @param json The {@link JsonObject}.
     * @param biomeKey The {@link Biome} {@link ResourceKey}.
     */
    public static void biomeKeyToJson(JsonObject json, Optional<ResourceKey<Biome>> biomeKey) {
        biomeKey.ifPresent((key) -> json.addProperty("biome", key.location().toString()));
    }

    /**
     * Adds an {@link Optional} {@link Biome} {@link TagKey} to a {@link JsonObject}.
     * @param json The {@link JsonObject}.
     * @param biomeTag The {@link Biome} {@link TagKey}.
     */
    public static void biomeTagToJson(JsonObject json, Optional<TagKey<Biome>> biomeTag) {
        biomeTag.ifPresent((tag) -> json.addProperty("biome", "#" + tag.location()));
    }

    // Extra methods.
    /**
     * Sets a property to a {@link BlockState} from a property map entry.<br><br>
     * Warning for "unchecked" is suppressed because casting within this method works fine.
     * @param properties The property map entry, as a {@link Map.Entry} of a {@link Property} and {@link Comparable}.
     * @param state The {@link BlockState}.
     * @return The {@link BlockState} with the applied property.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>, V extends T> BlockState setHelper(Map.Entry<Property<?>, Comparable<?>> properties, BlockState state) {
        return state.setValue((Property<T>) properties.getKey(), (V) properties.getValue());
    }

    /**
     * Gets the name of a block property.<br><br>
     * Warning for "unchecked" is suppressed because casting within this method works fine.
     * @param property The block {@link Property}.
     * @param value The property value, as a {@link Comparable}.
     * @return The name as a {@link String}.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> String getName(Property<T> property, Comparable<?> value) {
        return property.getName((T) value);
    }
}
