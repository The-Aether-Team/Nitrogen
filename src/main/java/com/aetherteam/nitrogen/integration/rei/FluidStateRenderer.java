package com.aetherteam.nitrogen.integration.rei;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.integration.jei.FakeLevel;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import dev.architectury.fluid.FluidStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class FluidStateRenderer implements EntryRenderer<FluidStack> {
    @Override
    public void render(EntryStack<FluidStack> entry, GuiGraphics guiGraphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
        PoseStack poseStack = guiGraphics.pose();
        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderDispatcher = minecraft.getBlockRenderer();

        poseStack.pushPose();

        poseStack.translate(bounds.x, bounds.y, 0);

        poseStack.translate(15.0F, 12.33F, 0.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-30.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.scale(-9.9F, -9.9F, -9.9F);

        Fluid fluidType = entry.getValue().getFluid();
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
        blockRenderDispatcher.renderLiquid(BlockPos.ZERO, FakeLevel.of(fluidState), builder, fluidState.createLegacyBlock(), fluidState);
        if (builder.building()) {
            tesselator.end();
        }

        renderType.clearRenderState();
        worldStack.popPose();
        RenderSystem.applyModelViewMatrix();

        poseStack.popPose();
    }

    @Override
    public Tooltip getTooltip(EntryStack<FluidStack> entry, TooltipContext context) {
        var fluidStack = entry.getValue();

        try {
            return entry.getDefinition().getRenderer().getTooltip(entry, context);
        } catch (RuntimeException | LinkageError e) {
            Component displayName = fluidStack.getName();
            Nitrogen.LOGGER.error("Failed to get tooltip for fluid: " + displayName, e);
            return Tooltip.create();
        }
    }
}
