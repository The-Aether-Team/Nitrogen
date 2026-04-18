package com.aetherteam.nitrogen.integration.jei;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.client.renderer.state.BlockStateRenderState;
import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record BlockStateIngredientRenderer(BlockPropertyPair... pairs) implements IIngredientRenderer<ItemStack> {
    @Override
    public void render(GuiGraphicsExtractor graphics, @Nullable ItemStack ingredient) {
        BlockPropertyPair pair = this.getMatchingPair(ingredient);

        if (pair.block() != null && Minecraft.getInstance().level != null) {
            BlockState blockState = pair.block().defaultBlockState();
            if (pair.properties().isPresent()) {
                for (Property.Value<?> propertyEntry : pair.properties().get()) {
                    blockState = BlockStateRecipeUtil.setHelper(propertyEntry, blockState);
                }
            }

            //grab the x and y translation from the matrix for proper positioning
            int x = (int) graphics.pose().m20();
            int y = (int) graphics.pose().m21();

            graphics.submitPictureInPictureRenderState(new BlockStateRenderState(blockState, x, y, x + 16, y + 16, graphics.peekScissorStack()));
        }
    }

    @Override
    public List<Component> getTooltip(ItemStack ingredient, TooltipFlag tooltipFlag) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        try {
            List<Component> list = Lists.newArrayList();

            BlockPropertyPair pair = this.getMatchingPair(ingredient);
            Block block = pair.block();
            Optional<HashSet<Property.Value<?>>> properties = pair.properties();

            if (block != null) {
                // Display block name.
                MutableComponent mutablecomponent = Component.empty().append(block.getName()).withStyle(ingredient.getRarity().getStyleModifier());
                list.add(mutablecomponent);
                if (tooltipFlag.isAdvanced()) {
                    Identifier blockKey = BuiltInRegistries.BLOCK.getKey(block);
                    list.add(Component.literal(blockKey.toString()).withStyle(ChatFormatting.DARK_GRAY));
                }
                // Display whether this blockstate is enabled.
                if (player != null && !ingredient.getItem().isEnabled(player.level().enabledFeatures())) {
                    list.add(Component.translatable("item.disabled").withStyle(ChatFormatting.RED));
                }
                // Display block properties.
                if (properties.isPresent() && !properties.get().isEmpty()) {
                    list.add(Component.translatable("gui.aether.jei.properties.tooltip").withStyle(ChatFormatting.GRAY));
                    for (Property.Value<?> entry : properties.get()) {
                        list.add(Component.literal(entry.property().getName() + ": " + entry.valueName()).withStyle(ChatFormatting.DARK_GRAY));
                    }
                }
            }
            return list;
        } catch (RuntimeException | LinkageError e) {
            String itemStackInfo = ErrorUtil.getItemStackInfo(ingredient);
            Nitrogen.LOGGER.error("Failed to get tooltip: {}", itemStackInfo, e);
            List<Component> list = new ArrayList<>();
            MutableComponent crash = Component.translatable("jei.tooltip.error.crash");
            list.add(crash.withStyle(ChatFormatting.RED));
            return list;
        }
    }

    @Override
    public Font getFontRenderer(Minecraft minecraft, ItemStack ingredient) {
        IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
        return renderHelper.getFontRenderer(minecraft, ingredient);
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    private BlockPropertyPair getMatchingPair(ItemStack ingredient) {
        Map<Block, HashSet<Property.Value<?>>> pairsMap = Stream.of(this.pairs).collect(Collectors.toMap(BlockPropertyPair::block, blockPropertyPair -> blockPropertyPair.properties().orElse(new HashSet<>())));
        Block block = null;
        HashSet<Property.Value<?>> propertiesMap = null;
        if (Minecraft.getInstance().level != null) {
            for (Map.Entry<Block, HashSet<Property.Value<?>>> entry : pairsMap.entrySet()) {
                ItemStack stack = entry.getKey().getCloneItemStack(Minecraft.getInstance().level, BlockPos.ZERO, entry.getKey().defaultBlockState(), true, Minecraft.getInstance().player);
                stack = stack.isEmpty() ? new ItemStack(Blocks.STONE) : stack;
                if (stack.getItem() == ingredient.getItem()) {
                    block = entry.getKey();
                    propertiesMap = entry.getValue();
                }
            }
        }
        return BlockPropertyPair.of(block, Optional.ofNullable(propertiesMap));
    }
}

