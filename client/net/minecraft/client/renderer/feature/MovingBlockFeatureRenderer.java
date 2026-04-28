package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4fc;

public class MovingBlockFeatureRenderer extends RenderTypeFeatureRenderer<Submit> {
   public static final FeatureRendererType<Submit> TYPE = FeatureRendererType.<Submit>create("Moving Block");
   private final PoseStack poseStack = new PoseStack();
   private final BlockQuadOutput quadOutput = (x, y, z, quad, instance) -> this.putBakedQuad(this.poseStack, x, y, z, quad, instance, quad.materialInfo().layer());
   private final BlockQuadOutput solidQuadOutput = (x, y, z, quad, instance) -> this.putBakedQuad(this.poseStack, x, y, z, quad, instance, ChunkSectionLayer.SOLID);

   public MovingBlockFeatureRenderer() {
      super();
   }

   protected void buildGroup(final FeatureFrameContext context, final List<Submit> submits) {
      boolean ambientOcclusion = context.options().ambientOcclusion;
      boolean cutoutLeaves = context.options().cutoutLeaves;
      ModelBlockRenderer blockRenderer = new ModelBlockRenderer(ambientOcclusion, false, context.blockColors());

      for(Submit submit : submits) {
         MovingBlockRenderState movingBlockRenderState = submit.movingBlockRenderState();
         BlockState blockState = movingBlockRenderState.blockState;
         BlockStateModel model = context.blockStateModelSet().get(blockState);
         this.poseStack.setIdentity();
         this.poseStack.mulPose(submit.pose());
         BlockQuadOutput blockOutput = ModelBlockRenderer.forceOpaque(cutoutLeaves, blockState) ? this.solidQuadOutput : this.quadOutput;
         long blockSeed = blockState.getSeed(movingBlockRenderState.randomSeedPos);
         blockRenderer.tesselateBlock(blockOutput, 0.0F, 0.0F, 0.0F, movingBlockRenderState, movingBlockRenderState.blockPos, blockState, model, blockSeed);
      }

   }

   private void putBakedQuad(final PoseStack poseStack, final float x, final float y, final float z, final BakedQuad quad, final QuadInstance instance, final ChunkSectionLayer layer) {
      poseStack.pushPose();
      poseStack.translate(x, y, z);
      RenderType var10001;
      switch (layer) {
         case SOLID -> var10001 = RenderTypes.solidMovingBlock();
         case CUTOUT -> var10001 = RenderTypes.cutoutMovingBlock();
         case TRANSLUCENT -> var10001 = RenderTypes.translucentMovingBlock();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      VertexConsumer buffer = this.getVertexBuilder(var10001);
      buffer.putBakedQuad(poseStack.last(), quad, instance);
      poseStack.popPose();
   }

   public static record Submit(Matrix4fc pose, MovingBlockRenderState movingBlockRenderState) implements TranslucentSubmit {
      public Submit {
         super();
      }

      public float distanceToCameraSq() {
         return TranslucentSubmit.computeDistanceToCameraSq(this.pose, 0.5F, 0.5F, 0.5F);
      }

      public FeatureRendererType<Submit> featureType() {
         return MovingBlockFeatureRenderer.TYPE;
      }
   }
}
