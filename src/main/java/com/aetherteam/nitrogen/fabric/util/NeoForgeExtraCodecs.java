package com.aetherteam.nitrogen.fabric.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.util.ExtraCodecs;

public class NeoForgeExtraCodecs {

    public static <A> Codec<A> decodeOnly(Decoder<A> decoder) {
        return Codec.of(Codec.unit(() -> {
            throw new UnsupportedOperationException("Cannot encode with decode-only codec! Decoder:" + decoder);
        }), decoder, "DecodeOnly[" + decoder + "]");
    }

    /**
     * Codec with two alternatives.
     * <p>
     * The vanilla {@link ExtraCodecs#withAlternative(Codec, Codec)} will try
     * the first codec and then the second codec for decoding, <b>but only the first for encoding</b>.
     * <p>
     * Unlike vanilla, this alternative codec also tries to encode with the second codec if the first encode fails.
     *
     * @see #withAlternative(MapCodec, MapCodec) for keeping {@link com.mojang.serialization.MapCodec MapCodecs} as MapCodecs.
     */
    public static <T> Codec<T> withAlternative(final Codec<T> codec, final Codec<T> alternative) {
        return new AlternativeCodec<>(codec, alternative);
    }

    private record AlternativeCodec<T>(Codec<T> codec, Codec<T> alternative) implements Codec<T> {
        @Override
        public <T1> DataResult<Pair<T, T1>> decode(final DynamicOps<T1> ops, final T1 input) {
            final var result = codec.decode(ops, input);

            return (result.error().isEmpty()) ? result : alternative.decode(ops, input);
        }

        @Override
        public <T1> DataResult<T1> encode(final T input, final DynamicOps<T1> ops, final T1 prefix) {
            final var result = codec.encode(input, ops, prefix);

            return (result.error().isEmpty()) ? result : alternative.encode(input, ops, prefix);
        }

        @Override
        public String toString() {
            return "Alternative[" + codec + ", " + alternative + "]";
        }
    }
}
