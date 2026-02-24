package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

public class BlockFeatureRenderer {
   private static final Direction[] DIRECTIONS = Direction.values();
   private static final long MODEL_SEED = 42L;
   private final RandomSource random = RandomSource.create();

   public BlockFeatureRenderer() {
      super();
   }

   public void renderSolid(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final BlockRenderDispatcher blockRenderDispatcher, final OutlineBufferSource outlineBufferSource) {
      this.renderMovingBlockSubmits(nodeCollection, bufferSource, blockRenderDispatcher, false);
      this.renderBlockModelSubmits(nodeCollection, bufferSource, outlineBufferSource, false);
   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final BlockRenderDispatcher blockRenderDispatcher, final OutlineBufferSource outlineBufferSource) {
      this.renderMovingBlockSubmits(nodeCollection, bufferSource, blockRenderDispatcher, true);
      this.renderBlockModelSubmits(nodeCollection, bufferSource, outlineBufferSource, true);
   }

   private void renderMovingBlockSubmits(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final BlockRenderDispatcher blockRenderDispatcher, final boolean translucent) {
      PoseStack poseStack = new PoseStack();
      BlockQuadOutput output = (x, y, z, quad, instance) -> putBakedQuad(poseStack, bufferSource, x, y, z, quad, instance, quad.spriteInfo().layer());
      BlockQuadOutput solidOutput = (x, y, z, quad, instance) -> putBakedQuad(poseStack, bufferSource, x, y, z, quad, instance, ChunkSectionLayer.SOLID);
      Minecraft minecraft = Minecraft.getInstance();
      boolean ambientOcclusion = (Boolean)minecraft.options.ambientOcclusion().get();
      boolean cutoutLeaves = (Boolean)minecraft.options.cutoutLeaves().get();
      ModelBlockRenderer blockRenderer = new ModelBlockRenderer(ambientOcclusion, false, minecraft.getBlockColors());

      for(SubmitNodeStorage.MovingBlockSubmit submit : nodeCollection.getMovingBlockSubmits()) {
         MovingBlockRenderState movingBlockRenderState = submit.movingBlockRenderState();
         BlockState blockState = movingBlockRenderState.blockState;
         BlockStateModel model = blockRenderDispatcher.getBlockModel(blockState);
         if (model.hasTranslucency() == translucent) {
            poseStack.setIdentity();
            poseStack.mulPose((Matrix4fc)submit.pose());
            BlockQuadOutput blockOutput = ModelBlockRenderer.forceOpaque(cutoutLeaves, blockState) ? solidOutput : output;
            long blockSeed = blockState.getSeed(movingBlockRenderState.randomSeedPos);
            blockRenderer.tesselateBlock(blockOutput, 0.0F, 0.0F, 0.0F, movingBlockRenderState, movingBlockRenderState.blockPos, blockState, model, blockSeed);
         }
      }

   }

   private static void putBakedQuad(final PoseStack poseStack, final MultiBufferSource.BufferSource bufferSource, final float x, final float y, final float z, final BakedQuad quad, final QuadInstance instance, final ChunkSectionLayer layer) {
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

   private void renderBlockModelSubmits(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final OutlineBufferSource outlineBufferSource, final boolean translucent) {
      for(SubmitNodeStorage.BlockModelSubmit submit : nodeCollection.getBlockModelSubmits()) {
         if (submit.renderType().hasBlending() == translucent) {
            VertexConsumer buffer = bufferSource.getBuffer(submit.renderType());
            VertexConsumer outlineBuffer;
            if (submit.outlineColor() != 0) {
               outlineBufferSource.setColor(submit.outlineColor());
               outlineBuffer = outlineBufferSource.getBuffer(submit.renderType());
            } else {
               outlineBuffer = null;
            }

            QuadInstance quadInstance = new QuadInstance();
            quadInstance.setLightCoords(submit.lightCoords());
            quadInstance.setOverlayCoords(submit.overlayCoords());
            this.random.setSeed(42L);

            for(BlockModelPart part : submit.model().collectParts(this.random)) {
               putPartQuads(part, submit.pose(), quadInstance, submit.tintLayers(), buffer, outlineBuffer);
            }
         }
      }

   }

   private static void putPartQuads(final BlockModelPart part, final PoseStack.Pose pose, final QuadInstance quadInstance, final int[] tintLayers, final VertexConsumer buffer, final @Nullable VertexConsumer outlineBuffer) {
      for(Direction direction : DIRECTIONS) {
         for(BakedQuad quad : part.getQuads(direction)) {
            putQuad(pose, quad, quadInstance, tintLayers, buffer, outlineBuffer);
         }
      }

      for(BakedQuad quad : part.getQuads((Direction)null)) {
         putQuad(pose, quad, quadInstance, tintLayers, buffer, outlineBuffer);
      }

   }

   private static void putQuad(final PoseStack.Pose pose, final BakedQuad quad, final QuadInstance instance, final int[] tintLayers, final VertexConsumer buffer, final @Nullable VertexConsumer outlineBuffer) {
      int tintIndex = quad.tintIndex();
      boolean tintColor = tintIndex != -1 && tintIndex < tintLayers.length;
      instance.setColor(tintColor ? tintLayers[tintIndex] : -1);
      buffer.putBakedQuad(pose, quad, instance);
      if (outlineBuffer != null) {
         outlineBuffer.putBakedQuad(pose, quad, instance);
      }

   }
}
