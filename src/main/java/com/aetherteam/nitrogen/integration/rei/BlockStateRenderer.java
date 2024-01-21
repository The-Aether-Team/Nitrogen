package com.aetherteam.nitrogen.integration.rei;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.integration.jei.FakeLevel;
import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record BlockStateRenderer(BlockPropertyPair... pairs) implements EntryRenderer<ItemStack> {
    @Override
    public void render(EntryStack<ItemStack> entry, PoseStack poseStack, Rectangle bounds, int mouseX, int mouseY, float delta) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderDispatcher = minecraft.getBlockRenderer();

        BlockPropertyPair pair = this.getMatchingPair(entry.getValue());

        poseStack.pushPose();
        poseStack.translate(bounds.x, bounds.y, 0);

        if (pair.block() != null && pair.properties() != null && minecraft.level != null) {
            BlockState blockState = pair.block().defaultBlockState();
            for (Map.Entry<Property<?>, Comparable<?>> propertyEntry : pair.properties().entrySet()) {
                blockState = BlockStateRecipeUtil.setHelper(propertyEntry, blockState);
            }

            poseStack.pushPose();

            poseStack.translate(15.0F, 12.33F, 5.0F);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-30.0F));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(45.0F));
            poseStack.scale(-9.9F, -9.9F, -9.9F);

            RenderSystem.setupGui3DDiffuseLighting(Util.make(new Vector3f(0.4F, 0.0F, 1.0F), Vector3f::normalize), Util.make(new Vector3f(-0.4F, 1.0F, -0.2F), Vector3f::normalize));

            ModelBlockRenderer modelBlockRenderer = blockRenderDispatcher.getModelRenderer();
            MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
            BakedModel model = blockRenderDispatcher.getBlockModel(blockState);
            modelBlockRenderer.tesselateBlock(FakeLevel.of(blockState), model, blockState, BlockPos.ZERO, poseStack, bufferSource.getBuffer(Sheets.translucentCullBlockSheet()), false, minecraft.level.getRandom(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            bufferSource.endBatch();

            Lighting.setupFor3DItems();

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public Tooltip getTooltip(EntryStack<ItemStack> ingredient, TooltipContext context) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        try {
            Tooltip tooltip = Tooltip.create();

            BlockPropertyPair pair = this.getMatchingPair(ingredient.getValue());
            Block block = pair.block();
            Map<Property<?>, Comparable<?>> properties = pair.properties();

            if (block != null && properties != null) {
                // Display block name.
                MutableComponent mutablecomponent = Component.empty().append(block.getName()).withStyle(ingredient.getValue().getRarity().color);
                tooltip.add(mutablecomponent);
                if (context.getFlag().isAdvanced()) {
                    ResourceLocation blockKey = ForgeRegistries.BLOCKS.getKey(block);
                    if (blockKey != null && block.defaultBlockState().isAir()) {
                        tooltip.add(Component.literal(blockKey.toString()).withStyle(ChatFormatting.DARK_GRAY));
                    }
                }
                // Display block properties.
                if (!properties.isEmpty()) {
                    tooltip.add(Component.translatable("gui.aether.jei.properties.tooltip").withStyle(ChatFormatting.GRAY));
                    for (Map.Entry<Property<?>, Comparable<?>> entry : properties.entrySet()) {
                        tooltip.add(Component.literal(entry.getKey().getName() + ": " + entry.getValue().toString()).withStyle(ChatFormatting.DARK_GRAY));
                    }
                }
            }
            return tooltip;
        } catch (RuntimeException | LinkageError e) {
            Nitrogen.LOGGER.error("Failed to get tooltip: {}", ingredient, e);
            return Tooltip.create(Component.translatable("jei.tooltip.error.crash").withStyle(ChatFormatting.RED));
        }
    }

    /**
     * Warning for "deprecation" is suppressed because the non-sensitive version of {@link net.minecraft.world.level.block.Block#getCloneItemStack(BlockGetter, BlockPos, BlockState)} is needed in this context.
     */
    private BlockPropertyPair getMatchingPair(ItemStack ingredient) {
        Map<Block, Map<Property<?>, Comparable<?>>> pairsMap = Stream.of(this.pairs).collect(Collectors.toMap(BlockPropertyPair::block, BlockPropertyPair::properties));
        Block block = null;
        Map<Property<?>, Comparable<?>> propertiesMap = null;
        if (Minecraft.getInstance().level != null) {
            for (Map.Entry<Block, Map<Property<?>, Comparable<?>>> entry : pairsMap.entrySet()) {
                ItemStack stack = entry.getKey().getCloneItemStack(Minecraft.getInstance().level, BlockPos.ZERO, entry.getKey().defaultBlockState());
                stack = stack.isEmpty() ? new ItemStack(Blocks.STONE) : stack;
                if (stack.getItem() == ingredient.getItem()) {
                    block = entry.getKey();
                    propertiesMap = entry.getValue();
                }
            }
        }
        return BlockPropertyPair.of(block, propertiesMap);
    }
}
