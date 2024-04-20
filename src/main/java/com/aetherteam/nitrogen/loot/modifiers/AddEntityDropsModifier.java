package com.aetherteam.nitrogen.loot.modifiers;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;

/**
 * Adds an item to an entity's drop loot.
 */
public class AddEntityDropsModifier extends LootModifier {
    private static final Codec<LootItemFunction[]> LOOT_FUNCTIONS_CODEC = LootItemFunctions.CODEC.listOf().xmap(list -> list.toArray(LootItemFunction[]::new), List::of);
    public static final Codec<AddEntityDropsModifier> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(modifier -> modifier.itemStack),
            AddEntityDropsModifier.LOOT_FUNCTIONS_CODEC.fieldOf("functions").forGetter(modifier -> modifier.functions),
            LootModifier.LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(modifier -> modifier.conditions)
    ).apply(instance, AddEntityDropsModifier::new));

    private final LootItemFunction[] functions;
    private final ItemStack itemStack;

    public AddEntityDropsModifier(ItemStack itemStack, LootItemFunction[] functions, LootItemCondition[] conditions) {
        super(conditions);
        this.functions = functions;
        this.itemStack = itemStack;
    }

    @Override
    public ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> lootStacks, LootContext context) {
        for (LootItemFunction function : this.functions) {
            function.apply(this.itemStack, context);
        }
        lootStacks.add(this.itemStack);
        return lootStacks;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return AddEntityDropsModifier.CODEC;
    }

    @SuppressWarnings("unchecked")
    static <U> JsonElement getJson(Dynamic<?> dynamic) {
        Dynamic<U> typed = (Dynamic<U>) dynamic;
        return typed.getValue() instanceof JsonElement ? (JsonElement) typed.getValue() : typed.getOps().convertTo(JsonOps.INSTANCE, typed.getValue());
    }
}

