package com.aetherteam.nitrogen.integration.jei;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record BlockStateRenderer(BlockPropertyPair... pairs) implements IIngredientRenderer<ItemStack> {
    @Override
    public void render(GuiGraphics guiGraphics, @Nullable ItemStack ingredient) {
        PoseStack poseStack = guiGraphics.pose();
        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderDispatcher = minecraft.getBlockRenderer();

        BlockPropertyPair pair = this.getMatchingPair(ingredient);

        if (pair.block() != null && minecraft.level != null) {
            BlockState blockState = pair.block().defaultBlockState();
            if (pair.properties().isPresent()) {
                for (Map.Entry<Property<?>, Comparable<?>> propertyEntry : pair.properties().get().entrySet()) {
                    blockState = BlockStateRecipeUtil.setHelper(propertyEntry, blockState);
                }
            }

            poseStack.pushPose();

            poseStack.translate(15.0F, 12.33F, 5.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-30.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
            poseStack.scale(-9.9F, -9.9F, -9.9F);

            RenderSystem.setupGui3DDiffuseLighting((new Vector3f(0.4F, 0.0F, 1.0F)).normalize(), (new Vector3f(-0.4F, 1.0F, -0.2F)).normalize());

            ModelBlockRenderer modelBlockRenderer = blockRenderDispatcher.getModelRenderer();
            MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
            BakedModel model = blockRenderDispatcher.getBlockModel(blockState);
            RenderType renderType = model.getRenderTypes(blockState, minecraft.level.getRandom(), ModelData.EMPTY).asList().get(0);
            modelBlockRenderer.tesselateBlock(new FakeBlockLevel(blockState), model, blockState, BlockPos.ZERO, poseStack, bufferSource.getBuffer(Sheets.translucentCullBlockSheet()), false, minecraft.level.getRandom(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
            bufferSource.endBatch();

            Lighting.setupFor3DItems();

            poseStack.popPose();
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
            Optional<Map<Property<?>, Comparable<?>>> properties = pair.properties();

            if (block != null) {
                // Display block name.
                MutableComponent mutablecomponent = Component.empty().append(block.getName()).withStyle(ingredient.getRarity().getStyleModifier());
                list.add(mutablecomponent);
                if (tooltipFlag.isAdvanced()) {
                    ResourceLocation blockKey = BuiltInRegistries.BLOCK.getKey(block);
                    list.add(Component.literal(blockKey.toString()).withStyle(ChatFormatting.DARK_GRAY));
                }
                // Display whether this blockstate is enabled.
                if (player != null && !ingredient.getItem().isEnabled(player.level().enabledFeatures())) {
                    list.add(Component.translatable("item.disabled").withStyle(ChatFormatting.RED));
                }
                // Display block properties.
                if (properties.isPresent() && !properties.get().isEmpty()) {
                    list.add(Component.translatable("gui.aether.jei.properties.tooltip").withStyle(ChatFormatting.GRAY));
                    for (Map.Entry<Property<?>, Comparable<?>> entry : properties.get().entrySet()) {
                        list.add(Component.literal(entry.getKey().getName() + ": " + entry.getValue().toString()).withStyle(ChatFormatting.DARK_GRAY));
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

    /**
     * Warning for "deprecation" is suppressed because the non-sensitive version of {@link net.minecraft.world.level.block.Block#getCloneItemStack(net.minecraft.world.level.LevelReader, BlockPos, BlockState)} is needed in this context.
     */
    @SuppressWarnings("deprecation")
    private BlockPropertyPair getMatchingPair(ItemStack ingredient) {
        Map<Block, Map<Property<?>, Comparable<?>>> pairsMap = Stream.of(this.pairs).collect(Collectors.toMap(BlockPropertyPair::block, blockPropertyPair -> blockPropertyPair.properties().orElse(Map.of())));
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
        return BlockPropertyPair.of(block, Optional.ofNullable(propertiesMap));
    }

    /**
     * A fake level used for rendering.
     */
    private static class FakeBlockLevel extends FakeLevel {
        private final BlockState blockState;

        public FakeBlockLevel(BlockState blockState) {
            this.blockState = blockState;
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            if (pos.equals(BlockPos.ZERO)) {
                return this.blockState;
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return Fluids.EMPTY.defaultFluidState();
        }
    }
}

