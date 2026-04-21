package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class BlockModelFeatureRenderer {
   private static final Direction[] DIRECTIONS = Direction.values();
   private final QuadInstance quadInstance = new QuadInstance();

   public BlockModelFeatureRenderer() {
      super();
   }

   public void renderSolid(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context) {
      this.renderModels(nodeCollection, context, false);
   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context) {
      this.renderModels(nodeCollection, context, true);
   }

   private void renderModels(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context, final boolean translucent) {
      for(Submit submit : nodeCollection.getBlockModelSubmits()) {
         if (submit.renderType().hasBlending() == translucent) {
            VertexConsumer buffer = context.bufferSource().getBuffer(submit.renderType());
            VertexConsumer wrappedBuffer = (VertexConsumer)(submit.sheetedDecalPose() != null ? new SheetedDecalTextureGenerator(buffer, submit.sheetedDecalPose(), 1.0F) : buffer);
            VertexConsumer outlineBuffer;
            if (submit.outlineColor() != 0) {
               context.outlineBufferSource().setColor(submit.outlineColor());
               outlineBuffer = context.outlineBufferSource().getBuffer(submit.renderType());
            } else {
               outlineBuffer = null;
            }

            this.quadInstance.setLightCoords(submit.lightCoords());
            this.quadInstance.setOverlayCoords(submit.overlayCoords());

            for(BlockStateModelPart part : submit.modelParts()) {
               putPartQuads(part, submit.pose(), this.quadInstance, submit.tintLayers(), wrappedBuffer, outlineBuffer);
            }
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

   public static record Submit(PoseStack.Pose pose, RenderType renderType, List<BlockStateModelPart> modelParts, int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor, PoseStack.@Nullable Pose sheetedDecalPose) {
      public Submit {
         super();
      }
   }
}
