package com.aetherteam.nitrogen.fabric.mixin.client;

import com.aetherteam.nitrogen.fabric.events.RecipeBookCategoriesHelper;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientRecipeBook.class)
public abstract class ClientRecipeBookMixin {
    @WrapMethod(method = "getCategory")
    private static RecipeBookCategories nitrogen_fabric$lookupAlternative(RecipeHolder<?> recipe, Operation<RecipeBookCategories> original) {
        var recipeType = recipe.value().getType();

        var lookup = RecipeBookCategoriesHelper.INSTANCE.recipeCategoryLookups.get(recipeType);

        return lookup != null ? lookup.apply(recipe) : original.call(recipe);
    }
}
