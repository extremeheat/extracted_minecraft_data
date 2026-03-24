package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.OptionsRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class BlockFeatureRenderer {
   private static final Direction[] DIRECTIONS = Direction.values();
   private static final int[] NO_TINT = new int[0];
   private final QuadInstance quadInstance = new QuadInstance();
   private final RandomSource random = RandomSource.createThreadLocalInstance(0L);
   private final List<BlockStateModelPart> parts = new ArrayList();

   public BlockFeatureRenderer() {
      super();
   }

   public void renderSolid(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final BlockStateModelSet blockStateModelSet, final OutlineBufferSource outlineBufferSource, final OptionsRenderState optionsState) {
      this.renderMovingBlockSubmits(nodeCollection, bufferSource, blockStateModelSet, optionsState, false);
      this.renderBlockModelSubmits(nodeCollection, bufferSource, outlineBufferSource, false);
   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final BlockStateModelSet blockStateModelSet, final OutlineBufferSource outlineBufferSource, final MultiBufferSource.BufferSource crumblingBufferSource, final OptionsRenderState optionsState) {
      this.renderMovingBlockSubmits(nodeCollection, bufferSource, blockStateModelSet, optionsState, true);
      this.renderBlockModelSubmits(nodeCollection, bufferSource, outlineBufferSource, true);
      this.renderBreakingBlockModelSubmits(nodeCollection, crumblingBufferSource);
   }

   private void renderMovingBlockSubmits(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final BlockStateModelSet blockStateModelSet, final OptionsRenderState optionsState, final boolean translucent) {
      PoseStack poseStack = new PoseStack();
      BlockQuadOutput output = (x, y, z, quad, instance) -> putBakedQuad(poseStack, bufferSource, x, y, z, quad, instance, quad.materialInfo().layer());
      BlockQuadOutput solidOutput = (x, y, z, quad, instance) -> putBakedQuad(poseStack, bufferSource, x, y, z, quad, instance, ChunkSectionLayer.SOLID);
      Minecraft minecraft = Minecraft.getInstance();
      boolean ambientOcclusion = optionsState.ambientOcclusion;
      boolean cutoutLeaves = optionsState.cutoutLeaves;
      ModelBlockRenderer blockRenderer = new ModelBlockRenderer(ambientOcclusion, false, minecraft.getBlockColors());

      for(SubmitNodeStorage.MovingBlockSubmit submit : nodeCollection.getMovingBlockSubmits()) {
         MovingBlockRenderState movingBlockRenderState = submit.movingBlockRenderState();
         BlockState blockState = movingBlockRenderState.blockState;
         BlockStateModel model = blockStateModelSet.get(blockState);
         if (model.hasMaterialFlag(1) == translucent) {
            poseStack.setIdentity();
            poseStack.mulPose(submit.pose());
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

            this.quadInstance.setLightCoords(submit.lightCoords());
            this.quadInstance.setOverlayCoords(submit.overlayCoords());

            for(BlockStateModelPart part : submit.modelParts()) {
               putPartQuads(part, submit.pose(), this.quadInstance, submit.tintLayers(), buffer, outlineBuffer);
            }
         }
      }

   }

   private void renderBreakingBlockModelSubmits(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource) {
      this.quadInstance.setLightCoords(15728880);
      this.quadInstance.setOverlayCoords(OverlayTexture.NO_OVERLAY);

      for(SubmitNodeStorage.BreakingBlockModelSubmit submit : nodeCollection.getBreakingBlockModelSubmits()) {
         VertexConsumer buffer = new SheetedDecalTextureGenerator(bufferSource.getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(submit.progress())), submit.pose(), 1.0F);
         this.random.setSeed(submit.seed());

         try {
            submit.model().collectParts(this.random, this.parts);

            for(BlockStateModelPart part : this.parts) {
               putPartQuads(part, submit.pose(), this.quadInstance, NO_TINT, buffer, (VertexConsumer)null);
            }
         } finally {
            this.parts.clear();
         }
      }

   }

   private static void putPartQuads(final BlockStateModelPart part, final PoseStack.Pose pose, final QuadInstance quadInstance, final int[] tintLayers, final VertexConsumer buffer, final @Nullable VertexConsumer outlineBuffer) {
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
      int tintIndex = quad.materialInfo().tintIndex();
      boolean tintColor = tintIndex != -1 && tintIndex < tintLayers.length;
      instance.setColor(tintColor ? tintLayers[tintIndex] : -1);
      buffer.putBakedQuad(pose, quad, instance);
      if (outlineBuffer != null) {
         outlineBuffer.putBakedQuad(pose, quad, instance);
      }

   }
}
