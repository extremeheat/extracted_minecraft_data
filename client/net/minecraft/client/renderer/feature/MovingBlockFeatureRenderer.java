package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4fc;

public class MovingBlockFeatureRenderer {
   public MovingBlockFeatureRenderer() {
      super();
   }

   public void renderSolid(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context) {
      this.render(nodeCollection, context, false);
   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context) {
      this.render(nodeCollection, context, true);
   }

   private void render(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context, final boolean translucent) {
      PoseStack poseStack = new PoseStack();
      MultiBufferSource bufferSource = context.bufferSource();
      BlockQuadOutput output = (x, y, z, quad, instance) -> putBakedQuad(poseStack, bufferSource, x, y, z, quad, instance, quad.materialInfo().layer());
      BlockQuadOutput solidOutput = (x, y, z, quad, instance) -> putBakedQuad(poseStack, bufferSource, x, y, z, quad, instance, ChunkSectionLayer.SOLID);
      Minecraft minecraft = Minecraft.getInstance();
      boolean ambientOcclusion = context.options().ambientOcclusion;
      boolean cutoutLeaves = context.options().cutoutLeaves;
      ModelBlockRenderer blockRenderer = new ModelBlockRenderer(ambientOcclusion, false, minecraft.getBlockColors());

      for(Submit submit : nodeCollection.getMovingBlockSubmits()) {
         MovingBlockRenderState movingBlockRenderState = submit.movingBlockRenderState();
         BlockState blockState = movingBlockRenderState.blockState;
         BlockStateModel model = context.blockStateModelSet().get(blockState);
         if (model.hasMaterialFlag(1) == translucent) {
            poseStack.setIdentity();
            poseStack.mulPose(submit.pose());
            BlockQuadOutput blockOutput = ModelBlockRenderer.forceOpaque(cutoutLeaves, blockState) ? solidOutput : output;
            long blockSeed = blockState.getSeed(movingBlockRenderState.randomSeedPos);
            blockRenderer.tesselateBlock(blockOutput, 0.0F, 0.0F, 0.0F, movingBlockRenderState, movingBlockRenderState.blockPos, blockState, model, blockSeed);
         }
      }

   }

   private static void putBakedQuad(final PoseStack poseStack, final MultiBufferSource bufferSource, final float x, final float y, final float z, final BakedQuad quad, final QuadInstance instance, final ChunkSectionLayer layer) {
      poseStack.pushPose();
      poseStack.translate(x, y, z);
      RenderType var10001;
      switch (layer) {
         case SOLID -> var10001 = RenderTypes.solidMovingBlock();
         case CUTOUT -> var10001 = RenderTypes.cutoutMovingBlock();
         case TRANSLUCENT -> var10001 = RenderTypes.translucentMovingBlock();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      VertexConsumer buffer = bufferSource.getBuffer(var10001);
      buffer.putBakedQuad(poseStack.last(), quad, instance);
      poseStack.popPose();
   }

   public static record Submit(Matrix4fc pose, MovingBlockRenderState movingBlockRenderState) {
      public Submit {
         super();
      }
   }
}
