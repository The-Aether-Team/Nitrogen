package com.aetherteam.nitrogen.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

// Used by BlockPropertyPair, whose Map keys depend on deserializing the Block for its information + passing the block itself.
// Not entirely different from a Dispatch Codec, except the Returnable does hold the object key.
public record DependentMapCodec<Dependent, Keyable, Value, Returnable>(
		// The json name-key for the object in which this map depends on
		String dependentKey,
		// The json name-key for the map, dependent on object referred above
		String mapKey,
		// Serializer/Deserializer for the block in which the map is dependent for keys
		Codec<Dependent> dependentCodec,
		// Function for obtaining map keys from the object dependency (eg BlockState Property)
		Function<Dependent, Collection<Keyable>> keyGetter,
		// Function for obtaining the value codec for a specific key (eg BlockState Property Value Codec)
		Function<Keyable, Codec<Value>> valueCodecGetter,
		// Function for obtaining the String name from a key (eg BlockState Property name)
		Function<Keyable, String> mapKeyNameGetter,
		// The actual type of this codec is determined by the return type of this Bifunction
		BiFunction<Dependent, Map<Keyable, Value>, Returnable> constructor,
		// Conversion function for getting the dependent the returnable
		Function<Returnable, Dependent> mainGetter,
		// Conversion function for getting the map from the returnable
		Function<Returnable, Map<Keyable, Value>> mapGetter
) implements Codec<Returnable> {

	@Override
	public <Data> DataResult<Pair<Returnable, Data>> decode(DynamicOps<Data> ops, Data input) {
		return ops.getMap(input).flatMap(object -> this.dependentCodec
				.parse(ops, object.get(this.dependentKey))
				.flatMap(block -> ops
						.getMapEntries(object.get(this.mapKey))
						.flatMap(map -> this.decodeMap(ops, block, map))
				)
		);
	}

	private <Data> DataResult<Pair<Returnable, Data>> decodeMap(DynamicOps<Data> ops, Dependent block, Consumer<BiConsumer<Data, Data>> propertyMap) {
		// Derived from CompoundListCodec.decode

		final ImmutableMap.Builder<Keyable, Value> successful = ImmutableMap.builder();
		final ImmutableMap.Builder<Data, Data> failed = ImmutableMap.builder();

		final MutableObject<DataResult<Unit>> resultAccumulator = new MutableObject<>(DataResult.success(Unit.INSTANCE, Lifecycle.experimental()));

		final Map<String, Keyable> properties = this.keyGetter.apply(block).stream().collect(Collectors.toMap(this.mapKeyNameGetter, Function.identity()));

		propertyMap.accept((key, value) -> {
			DataResult<Keyable> propertyInfo = ops.getStringValue(key).map(properties::get);
			DataResult<Value> decodedValue = propertyInfo
					.map(this.valueCodecGetter)
					.map(ops::withDecoder)
					.flatMap(a -> a.apply(value))
					.map(Pair::getFirst);

			DataResult<Pair<Keyable, Value>> readEntry = propertyInfo.apply2stable(Pair::new, decodedValue);

			readEntry.error().ifPresent(e -> failed.put(key, value));

			resultAccumulator.setValue(resultAccumulator.getValue().apply2stable((u, e) -> {
				successful.put(e.getFirst(), e.getSecond());
				return u;
			}, readEntry));
		});

		final Data errors = ops.createMap(failed.build());

		final Pair<Returnable, Data> pair = Pair.of(this.constructor.apply(block, successful.build()), errors);

		return resultAccumulator.getValue().map(unit -> pair).setPartial(pair);
	}

	@Override
	public <Data> DataResult<Data> encode(Returnable input, DynamicOps<Data> ops, Data prefix) {
		final RecordBuilder<Data> properties = ops.mapBuilder();

		for (Map.Entry<Keyable, Value> pair : this.mapGetter.apply(input).entrySet()) {
			Keyable property = pair.getKey();
			properties.add(this.mapKeyNameGetter.apply(property), this.valueCodecGetter.apply(property).encode(pair.getValue(), ops, prefix));
		}

		final RecordBuilder<Data> main = ops.mapBuilder();

		main.add(this.dependentKey, this.dependentCodec.encode(this.mainGetter.apply(input), ops, prefix));
		main.add(this.mapKey, properties.build(ops.emptyMap()));

		return main.build(prefix);
	}
}
