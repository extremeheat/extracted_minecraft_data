package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class FoxHeldItemLayer extends RenderLayer<FoxRenderState, FoxModel> {
   public FoxHeldItemLayer(RenderLayerParent<FoxRenderState, FoxModel> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, FoxRenderState var4, float var5, float var6) {
      ItemStackRenderState var7 = var4.heldItem;
      if (!var7.isEmpty()) {
         boolean var8 = var4.isSleeping;
         boolean var9 = var4.isBaby;
         var1.pushPose();
         var1.translate(((FoxModel)this.getParentModel()).head.x / 16.0F, ((FoxModel)this.getParentModel()).head.y / 16.0F, ((FoxModel)this.getParentModel()).head.z / 16.0F);
         if (var9) {
            float var10 = 0.75F;
            var1.scale(0.75F, 0.75F, 0.75F);
         }

         var1.mulPose(Axis.ZP.rotation(var4.headRollAngle));
         var1.mulPose(Axis.YP.rotationDegrees(var5));
         var1.mulPose(Axis.XP.rotationDegrees(var6));
         if (var4.isBaby) {
            if (var8) {
               var1.translate(0.4F, 0.26F, 0.15F);
            } else {
               var1.translate(0.06F, 0.26F, -0.5F);
            }
         } else if (var8) {
            var1.translate(0.46F, 0.26F, 0.22F);
         } else {
            var1.translate(0.06F, 0.27F, -0.5F);
         }

         var1.mulPose(Axis.XP.rotationDegrees(90.0F));
         if (var8) {
            var1.mulPose(Axis.ZP.rotationDegrees(90.0F));
         }

         var7.render(var1, var2, var3, OverlayTexture.NO_OVERLAY);
         var1.popPose();
      }
   }
}
