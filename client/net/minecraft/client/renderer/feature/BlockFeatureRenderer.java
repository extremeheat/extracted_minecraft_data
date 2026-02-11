package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BakedQuadOutput;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4fc;

public class BlockFeatureRenderer {
   private final PoseStack poseStack = new PoseStack();

   public BlockFeatureRenderer() {
      super();
   }

   public void renderSolid(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final BlockRenderDispatcher blockRenderDispatcher, final OutlineBufferSource outlineBufferSource) {
      this.renderMovingBlockSubmits(nodeCollection, bufferSource, blockRenderDispatcher, false);
      this.renderBlockSubmits(nodeCollection, bufferSource, blockRenderDispatcher, outlineBufferSource, false);
      this.renderBlockModelSubmits(nodeCollection, bufferSource, outlineBufferSource, false);
   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final BlockRenderDispatcher blockRenderDispatcher, final OutlineBufferSource outlineBufferSource) {
      this.renderMovingBlockSubmits(nodeCollection, bufferSource, blockRenderDispatcher, true);
      this.renderBlockSubmits(nodeCollection, bufferSource, blockRenderDispatcher, outlineBufferSource, true);
      this.renderBlockModelSubmits(nodeCollection, bufferSource, outlineBufferSource, true);
   }

   private void renderMovingBlockSubmits(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final BlockRenderDispatcher blockRenderDispatcher, final boolean translucent) {
      BakedQuadOutput output = (pose, quad, brightness, color, lightmapCoord, overlayCoords) -> {
         VertexConsumer buffer = bufferSource.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(quad.spriteInfo().layer()));
         buffer.putBulkData(pose, quad, brightness, color, lightmapCoord, overlayCoords);
      };

      for(SubmitNodeStorage.MovingBlockSubmit submit : nodeCollection.getMovingBlockSubmits()) {
         MovingBlockRenderState movingBlockRenderState = submit.movingBlockRenderState();
         BlockState blockState = movingBlockRenderState.blockState;
         BlockStateModel model = blockRenderDispatcher.getBlockModel(blockState);
         if (model.hasTranslucency() == translucent) {
            List<BlockModelPart> parts = model.collectParts(RandomSource.create(blockState.getSeed(movingBlockRenderState.randomSeedPos)));
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose((Matrix4fc)submit.pose());
            BakedQuadOutput blockOutput;
            if (ItemBlockRenderTypes.forceOpaque(blockState)) {
               blockOutput = (pose, quad, brightness, color, lightmapCoord, overlayCoords) -> {
                  VertexConsumer buffer = bufferSource.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(ChunkSectionLayer.SOLID));
                  buffer.putBulkData(pose, quad, brightness, color, lightmapCoord, overlayCoords);
               };
            } else {
               blockOutput = output;
            }

            blockRenderDispatcher.getModelRenderer().tesselateBlock(movingBlockRenderState, parts, blockState, movingBlockRenderState.blockPos, poseStack, blockOutput, false, OverlayTexture.NO_OVERLAY);
         }
      }

   }

   private void renderBlockSubmits(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final BlockRenderDispatcher blockRenderDispatcher, final OutlineBufferSource outlineBufferSource, final boolean translucent) {
      for(SubmitNodeStorage.BlockSubmit submit : nodeCollection.getBlockSubmits()) {
         BlockStateModel model = blockRenderDispatcher.getBlockModel(submit.state());
         if (model.hasTranslucency() == translucent) {
            this.poseStack.pushPose();
            this.poseStack.last().set(submit.pose());
            blockRenderDispatcher.renderSingleBlock(submit.state(), this.poseStack, bufferSource, submit.lightCoords(), submit.overlayCoords());
            if (submit.outlineColor() != 0) {
               outlineBufferSource.setColor(submit.outlineColor());
               blockRenderDispatcher.renderSingleBlock(submit.state(), this.poseStack, outlineBufferSource, submit.lightCoords(), submit.overlayCoords());
            }

            this.poseStack.popPose();
         }
      }

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

            BakedQuadOutput output = (pose, quad, brightness, color, lightmapCoord, overlayCoords) -> {
               buffer.putBulkData(pose, quad, brightness, color, lightmapCoord, overlayCoords);
               if (outlineBuffer != null) {
                  outlineBuffer.putBulkData(pose, quad, brightness, color, lightmapCoord, overlayCoords);
               }

            };
            ModelBlockRenderer.renderModel(submit.pose(), output, submit.model(), submit.tintColor(), submit.lightCoords(), submit.overlayCoords());
         }
      }

   }
}
