package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ItemFeatureRenderer {
   private final PoseStack poseStack = new PoseStack();

   public ItemFeatureRenderer() {
      super();
   }

   public void renderSolid(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final OutlineBufferSource outlineBufferSource) {
      for(SubmitNodeStorage.ItemSubmit submit : nodeCollection.getItemSubmits()) {
         if (!submit.renderType().hasBlending()) {
            this.poseStack.pushPose();
            this.poseStack.last().set(submit.pose());
            ItemRenderer.renderItem(submit.displayContext(), this.poseStack, bufferSource, submit.lightCoords(), submit.overlayCoords(), submit.tintLayers(), submit.quads(), submit.renderType(), submit.foilType());
            if (submit.outlineColor() != 0) {
               outlineBufferSource.setColor(submit.outlineColor());
               ItemRenderer.renderItem(submit.displayContext(), this.poseStack, outlineBufferSource, submit.lightCoords(), submit.overlayCoords(), submit.tintLayers(), submit.quads(), submit.renderType(), ItemStackRenderState.FoilType.NONE);
            }

            this.poseStack.popPose();
         }
      }

   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final OutlineBufferSource outlineBufferSource) {
      for(SubmitNodeStorage.ItemSubmit submit : nodeCollection.getItemSubmits()) {
         if (submit.renderType().hasBlending()) {
            this.poseStack.pushPose();
            this.poseStack.last().set(submit.pose());
            ItemRenderer.renderItem(submit.displayContext(), this.poseStack, bufferSource, submit.lightCoords(), submit.overlayCoords(), submit.tintLayers(), submit.quads(), submit.renderType(), submit.foilType());
            if (submit.outlineColor() != 0) {
               outlineBufferSource.setColor(submit.outlineColor());
               ItemRenderer.renderItem(submit.displayContext(), this.poseStack, outlineBufferSource, submit.lightCoords(), submit.overlayCoords(), submit.tintLayers(), submit.quads(), submit.renderType(), ItemStackRenderState.FoilType.NONE);
            }

            this.poseStack.popPose();
         }
      }

   }
}
