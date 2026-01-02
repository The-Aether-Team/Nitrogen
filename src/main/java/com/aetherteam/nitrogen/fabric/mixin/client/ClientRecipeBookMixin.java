package com.aetherteam.nitrogen.fabric.mixin.client;

import com.aetherteam.nitrogen.fabric.events.RecipeBookCategoriesHelper;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ClientRecipeBook.class)
public abstract class ClientRecipeBookMixin {
    @Inject(method = "rebuildCollections", at = @At(value = "INVOKE", target = "Ljava/util/Map;copyOf(Ljava/util/Map;)Ljava/util/Map;"))
    private static void nitrogen_fabric$addToLookup(CallbackInfo ci, @Local(ordinal = 1) Map<ExtendedRecipeBookCategory, List<RecipeCollection>> map) {
        RecipeBookCategoriesHelper.INSTANCE.getSearchCategories().forEach((extendedCategory, categories) -> {
            map.put(extendedCategory, categories.stream().flatMap(category -> map.getOrDefault(category, List.of()).stream()).toList());
        });
    }
}
