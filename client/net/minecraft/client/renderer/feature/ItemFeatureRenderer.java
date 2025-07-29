package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;

public class ItemFeatureRenderer {
   private final PoseStack poseStack = new PoseStack();

   public ItemFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeStorage var1, MultiBufferSource.BufferSource var2) {
      for(SubmitNodeStorage.ItemSubmit var4 : var1.getItemSubmits()) {
         this.poseStack.pushPose();
         this.poseStack.last().set(var4.pose());
         var4.state().render(this.poseStack, var2, var4.lightCoords(), var4.overlayCoords());
         this.poseStack.popPose();
      }

   }
}
