package com.aetherteam.nitrogen.fabric.conditions;

import com.aetherteam.nitrogen.fabric.mixin.accessor.HolderLookupAdapterAccessor;
import com.aetherteam.nitrogen.fabric.mixin.accessor.RegistryOpsAccessor;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.RegistryOps;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ConditionalOps {

    /**
     * Key used for the conditions inside an object.
     */
    public static final String DEFAULT_CONDITIONS_KEY = ResourceConditions.CONDITIONS_KEY;
    /**
     * Key used to store the value associated with conditions,
     * when the value is not represented as a map.
     * For example, if we wanted to store the value 2 with some conditions, we could do:
     *
     * <pre>
     * {
     *     "fabric:conditions": [ ... ],
     *     "fabric:value": 2
     * }
     * </pre>
     */
    public static final String CONDITIONAL_VALUE_KEY = "fabric:value";

    /**
     * @see #createConditionalCodec(Codec, String)
     */
    public static <T> Codec<Optional<T>> createConditionalCodec(final Codec<T> ownerCodec) {
        return createConditionalCodec(ownerCodec, DEFAULT_CONDITIONS_KEY);
    }

    /**
     * Creates a conditional codec.
     *
     * <p>The conditional codec is generally not suitable for use as a dispatch target because it is never a {@link MapCodec.MapCodecCodec}.
     */
    public static <T> Codec<Optional<T>> createConditionalCodec(final Codec<T> ownerCodec, String conditionalsKey) {
        return createConditionalCodecWithConditions(ownerCodec, conditionalsKey).xmap(r -> r.map(WithConditions::carrier), r -> r.map(i -> new WithConditions<>(List.of(), i)));
    }

    /**
     * @see #createConditionalCodecWithConditions(Codec, String)
     */
    public static <T> Codec<Optional<WithConditions<T>>> createConditionalCodecWithConditions(final Codec<T> ownerCodec) {
        return createConditionalCodecWithConditions(ownerCodec, DEFAULT_CONDITIONS_KEY);
    }

    /**
     * Creates a conditional codec.
     *
     * <p>The conditional codec is generally not suitable for use as a dispatch target because it is never a {@link MapCodec.MapCodecCodec}.
     */
    public static <T> Codec<Optional<WithConditions<T>>> createConditionalCodecWithConditions(final Codec<T> ownerCodec, String conditionalsKey) {
        return Codec.of(
            new ConditionalEncoder<>(conditionalsKey, ResourceCondition.LIST_CODEC, ownerCodec),
            new ConditionalDecoder<>(conditionalsKey, ResourceCondition.CONDITION_CODEC, ownerCodec));
    }

    private record ConditionalEncoder<A>(String conditionalsPropertyKey, Codec<List<ResourceCondition>> conditionsCodec, Encoder<A> innerCodec) implements Encoder<Optional<WithConditions<A>>> {
        @Override
        public <T> DataResult<T> encode(Optional<WithConditions<A>> input, DynamicOps<T> ops, T prefix) {
            if (ops.compressMaps()) {
                // Compressing ops are not supported at the moment because they require special handling.
                return DataResult.error(() -> "Cannot use ConditionalCodec with compressing DynamicOps");
            }

            if (input.isEmpty()) {
                // Optional must be present for encoding.
                return DataResult.error(() -> "Cannot encode empty Optional with a ConditionalEncoder. We don't know what to encode to!");
            }

            final WithConditions<A> withConditions = input.get();

            if (withConditions.conditions().isEmpty()) {
                // If there are no conditions, forward to the inner codec directly.
                return innerCodec.encode(withConditions.carrier(), ops, prefix);
            }

            // By now we know we will produce a map-like object, so let's start building one.
            var recordBuilder = ops.mapBuilder();
            // Add conditions
            recordBuilder.add(conditionalsPropertyKey, conditionsCodec.encodeStart(ops, withConditions.conditions()));

            // Serialize the object
            var encodedInner = innerCodec.encodeStart(ops, withConditions.carrier());

            return encodedInner.flatMap(inner -> {
                return ops.getMap(inner).map(innerMap -> {
                    // If the inner is a map...
                    if (innerMap.get(conditionalsPropertyKey) != null || innerMap.get(CONDITIONAL_VALUE_KEY) != null) {
                        // Conditional or value key cannot be used in the inner codec!
                        return DataResult.<T>error(() -> "Cannot wrap a value that already uses the condition or value key with a ConditionalCodec.");
                    }
                    // Copy all fields to the record builder
                    innerMap.entries().forEach(pair -> {
                        recordBuilder.add(pair.getFirst(), pair.getSecond());
                    });
                    return recordBuilder.build(prefix);
                }).result().orElseGet(() -> {
                    // If the inner is not a map, write it to a value field
                    recordBuilder.add(CONDITIONAL_VALUE_KEY, inner);
                    return recordBuilder.build(prefix);
                });
            });
        }

        @Override
        public String toString() {
            return "Conditional[" + innerCodec + "]";
        }
    }

    private record ConditionalDecoder<A>(String conditionalsPropertyKey, Codec<ResourceCondition> conditionsCodec, Decoder<A> innerCodec) implements Decoder<Optional<WithConditions<A>>> {
        // Note: I am not too sure of what to return in the second element of the pair.
        // If this turns out to be a problem, please change it but also document it and write some test cases.
        @Override
        public <T> DataResult<Pair<Optional<WithConditions<A>>, T>> decode(DynamicOps<T> ops, T input) {
            @Nullable var ctx = (ops instanceof RegistryOps<T> registryOps)
                ? ((RegistryOpsAccessor) registryOps).nitrogen_fabric$lookup()
                : null;

            if (ops.compressMaps()) {
                // Compressing ops are not supported at the moment because they require special handling.
                return DataResult.error(() -> "Cannot use ConditionalCodec with compressing DynamicOps");
            }

            return ops.getMap(input).map(inputMap -> {
                final T conditionsDataCarrier = inputMap.get(conditionalsPropertyKey);
                if (conditionsDataCarrier == null) {
                    // No conditions, forward to inner codec
                    return innerCodec.decode(ops, input).map(result -> result.mapFirst(carrier -> Optional.of(new WithConditions<>(carrier))));
                }

                return conditionsCodec.decode(ops, conditionsDataCarrier).flatMap(conditionsCarrier -> {
                    final ResourceCondition condition = conditionsCarrier.getFirst();

                    final boolean conditionsMatch = condition.test(ctx);
                    if (!conditionsMatch)
                        return DataResult.success(Pair.of(Optional.empty(), input));

                    DataResult<Pair<A, T>> innerDecodeResult;

                    T valueDataCarrier = inputMap.get(CONDITIONAL_VALUE_KEY);
                    if (valueDataCarrier != null) {
                        // If there is a value field use its contents to deserialize.
                        innerDecodeResult = innerCodec.decode(ops, valueDataCarrier);
                    } else {
                        // Else copy the input into a new map without our custom key and decode from that.
                        T conditionalsKey = ops.createString(conditionalsPropertyKey);
                        var mapForDecoding = ops.createMap(inputMap
                            .entries()
                            .filter(pair -> !pair.getFirst().equals(conditionalsKey)));
                        innerDecodeResult = innerCodec.decode(ops, mapForDecoding);
                    }

                    // Variable is required because type inference can't handle this
                    DataResult<Pair<Optional<WithConditions<A>>, T>> ret = innerDecodeResult.map(
                        result -> result.mapFirst(
                            carrier -> Optional.of(new WithConditions<>(List.of(condition), carrier))));
                    return ret;
                });
            }).result().orElseGet(() -> {
                // Not a map, forward to inner codec
                return innerCodec.decode(ops, input).map(result -> result.mapFirst(carrier -> Optional.of(new WithConditions<>(carrier))));
            });
        }
    }
}
