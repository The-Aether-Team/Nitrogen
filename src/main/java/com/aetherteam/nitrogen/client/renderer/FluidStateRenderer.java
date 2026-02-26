package com.aetherteam.nitrogen.client.renderer;

import com.aetherteam.nitrogen.client.renderer.state.FluidStateRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;

public class FluidStateRenderer extends PictureInPictureRenderer<FluidStateRenderState> {

    public FluidStateRenderer(MultiBufferSource.BufferSource source) {
        super(source);
    }

    @Override
    public Class<FluidStateRenderState> getRenderStateClass() {
        return FluidStateRenderState.class;
    }

    //TODO
    //currently does not render anything (that I can see) and causes FPS to DRAMATICALLY drop when viewing a category that uses a fluid (such as icestone freezing)
    @Override
    protected void renderToTexture(FluidStateRenderState state, PoseStack stack) {
//        stack.pushPose();
//
//        stack.translate(0.425F, -0.25F, 5.0F);
//        stack.mulPose(Axis.XP.rotationDegrees(-30.0F));
//        stack.mulPose(Axis.YP.rotationDegrees(45.0F));
//        stack.scale(-0.6F, -0.6F, -0.6F);
//
//        ChunkSectionLayer renderType = ItemBlockRenderTypes.getRenderLayer(state.fluidState());
//        BufferBuilder builder = Tesselator.getInstance().begin(renderType.pipeline().getVertexFormatMode(), renderType.pipeline().getVertexFormat());
//        Minecraft.getInstance().getBlockRenderer().renderLiquid(BlockPos.ZERO, FakeLevel.of(state.fluidState()), builder, state.fluidState().createLegacyBlock(), state.fluidState());
//
//        stack.popPose();
    }

    @Override
    protected String getTextureLabel() {
        return "FluidState";
    }
}
