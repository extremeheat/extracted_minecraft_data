package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class ItemFeatureRenderer {
   private final PoseStack poseStack = new PoseStack();

   public ItemFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeCollection var1, MultiBufferSource.BufferSource var2, OutlineBufferSource var3) {
      for(SubmitNodeStorage.ItemSubmit var5 : var1.getItemSubmits()) {
         this.poseStack.pushPose();
         this.poseStack.last().set(var5.pose());
         ItemRenderer.renderItem(var5.displayContext(), this.poseStack, var2, var5.lightCoords(), var5.overlayCoords(), var5.tintLayers(), var5.quads(), var5.renderType(), var5.foilType());
         if (var5.outlineColor() != 0) {
            var3.setColor(var5.outlineColor());
            ItemRenderer.renderItem(var5.displayContext(), this.poseStack, var3, var5.lightCoords(), var5.overlayCoords(), var5.tintLayers(), var5.quads(), var5.renderType(), var5.foilType());
         }

         this.poseStack.popPose();
      }

   }
}
