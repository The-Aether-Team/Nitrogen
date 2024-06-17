package com.aetherteam.nitrogen.integration.rei;

//import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
//import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
//import me.shedaniel.rei.api.common.entry.EntryIngredient;
//import me.shedaniel.rei.api.common.entry.EntryStack;
//import me.shedaniel.rei.api.common.util.EntryStacks;
//import net.minecraft.client.Minecraft;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.LiquidBlock;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.properties.Property;
//import net.neoforged.neoforge.fluids.FluidType;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class REIUtils {
//    public static List<EntryIngredient> toIngredientList(BlockPropertyPair... pairs) {
//        if (pairs == null) return List.of();
//        // Sets up input slots.
//        List<EntryStack<?>> inputIngredients = new ArrayList<>();
//        for (BlockPropertyPair pair : pairs) {
//            if (pair.block() instanceof LiquidBlock liquidBlock) {
//                inputIngredients.add(EntryStacks.of(liquidBlock.getFluidState(liquidBlock.defaultBlockState()).getType(), FluidType.BUCKET_VOLUME));
//            } else {
//                inputIngredients.add(EntryStacks.of(setupIngredient(pair)));
//            }
//        }
//        return List.of(EntryIngredient.of(inputIngredients));
//    }
//
//    /**
//     * Warning for "deprecation" is suppressed because the non-sensitive version of {@link net.minecraft.world.level.block.Block#getCloneItemStack(net.minecraft.world.level.LevelReader, BlockPos, BlockState)} is needed in this context.
//     */
//    private static ItemStack setupIngredient(BlockPropertyPair recipeResult) {
//        ItemStack stack = ItemStack.EMPTY;
//        if (Minecraft.getInstance().level != null) {
//            BlockState resultState = recipeResult.block().defaultBlockState();
//            if (recipeResult.properties().isPresent()) {
//                for (Map.Entry<Property<?>, Comparable<?>> propertyEntry : recipeResult.properties().get().entrySet()) {
//                    resultState = BlockStateRecipeUtil.setHelper(propertyEntry, resultState);
//                }
//            }
//            stack = recipeResult.block().getCloneItemStack(Minecraft.getInstance().level, BlockPos.ZERO, resultState);
//        }
//        return stack.isEmpty() ? new ItemStack(Blocks.STONE) : stack;
//    }
//}
