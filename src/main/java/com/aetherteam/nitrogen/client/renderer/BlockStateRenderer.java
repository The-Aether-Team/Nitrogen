package com.aetherteam.nitrogen.client.renderer;

import com.aetherteam.nitrogen.client.renderer.state.BlockStateRenderState;
import com.aetherteam.nitrogen.integration.jei.FakeLevel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;

public class BlockStateRenderer extends PictureInPictureRenderer<BlockStateRenderState> {
    public BlockStateRenderer(MultiBufferSource.BufferSource source) {
        super(source);
    }

    @Override
    public Class<BlockStateRenderState> getRenderStateClass() {
        return BlockStateRenderState.class;
    }

    @Override
    protected void renderToTexture(BlockStateRenderState state, PoseStack stack) {
        stack.translate(0.425F, -0.25F, 5.0F);
        stack.mulPose(Axis.XP.rotationDegrees(-30.0F));
        stack.mulPose(Axis.YP.rotationDegrees(45.0F));
        stack.scale(-0.6F, -0.6F, -0.6F);

        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        Options options = Minecraft.getInstance().options;
        ModelBlockRenderer blockRenderer = new ModelBlockRenderer(options.ambientOcclusion().get(), true, Minecraft.getInstance().getBlockColors());
        BlockAndTintGetter level = FakeLevel.of(state.blockState());

        BlockQuadOutput output = (x, y, z, quad, instance) -> putBakedQuad(stack, bufferSource, x, y, z, quad, instance, quad.materialInfo().layer());
        BlockQuadOutput solidOutput = (x, y, z, quad, instance) -> putBakedQuad(stack, bufferSource, x, y, z, quad, instance, ChunkSectionLayer.SOLID);
        blockRenderer.tesselateBlock(
            ModelBlockRenderer.forceOpaque(options.cutoutLeaves().get(), state.blockState()) ? solidOutput : output,
            0.0F,
            0.0F,
            0.0F,
            level,
            BlockPos.ZERO,
            state.blockState(),
            modelManager.getBlockStateModelSet().get(state.blockState()),
            0L);
    }

    private static void putBakedQuad(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, float x, float y, float z, BakedQuad quad, QuadInstance instance, ChunkSectionLayer layer) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        VertexConsumer buffer = bufferSource.getBuffer(switch (layer) {
            case SOLID -> RenderTypes.solidMovingBlock();
            case CUTOUT -> RenderTypes.cutoutMovingBlock();
            case TRANSLUCENT -> RenderTypes.translucentMovingBlock();
        });
        buffer.putBakedQuad(poseStack.last(), quad, instance);
        poseStack.popPose();
    }

    @Override
    protected String getTextureLabel() {
        return "BlockState";
    }
}
