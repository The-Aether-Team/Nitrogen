package com.aetherteam.nitrogen.integration.jei;

import com.aetherteam.nitrogen.Nitrogen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

public record FluidStateRenderer<T>(IPlatformFluidHelperInternal<T> fluidHelper) implements IIngredientRenderer<T> {
    @Override
    public void render(PoseStack poseStack, T ingredient) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderDispatcher = minecraft.getBlockRenderer();

        poseStack.pushPose();

        poseStack.translate(15.0F, 12.33F, 0.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-30.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.scale(-9.9F, -9.9F, -9.9F);

        IIngredientTypeWithSubtypes<Fluid, T> type = this.fluidHelper.getFluidIngredientType();
        Fluid fluidType = type.getBase(ingredient);
        FluidState fluidState = fluidType.defaultFluidState();
        RenderType renderType = ItemBlockRenderTypes.getRenderLayer(fluidState);
        PoseStack worldStack = RenderSystem.getModelViewStack();

        renderType.setupRenderState();
        worldStack.pushPose();
        worldStack.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(renderType.mode(), renderType.format());
        blockRenderDispatcher.renderLiquid(BlockPos.ZERO, new FakeFluidLevel(fluidState), builder, fluidState.createLegacyBlock(), fluidState);
        if (builder.building()) {
            tesselator.end();
        }

        renderType.clearRenderState();
        worldStack.popPose();
        RenderSystem.applyModelViewMatrix();

        poseStack.popPose();
    }

    @Override
    public List<Component> getTooltip(T ingredient, TooltipFlag tooltipFlag) {
        try {
            return this.fluidHelper.getTooltip(ingredient, tooltipFlag);
        } catch (RuntimeException | LinkageError e) {
            Component displayName = this.fluidHelper.getDisplayName(ingredient);
            Nitrogen.LOGGER.error("Failed to get tooltip for fluid: " + displayName, e);
            return new ArrayList<>();
        }
    }

    /**
     * A fake level used for rendering.
     */
    private static class FakeFluidLevel extends FakeLevel {
        private final FluidState fluidState;

        public FakeFluidLevel(FluidState fluidState) {
            this.fluidState = fluidState;
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            if (pos.equals(BlockPos.ZERO)) {
                return this.fluidState.createLegacyBlock();
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            if (pos.equals(BlockPos.ZERO)) {
                return this.fluidState;
            } else {
                return Fluids.EMPTY.defaultFluidState();
            }
        }
    }
}

