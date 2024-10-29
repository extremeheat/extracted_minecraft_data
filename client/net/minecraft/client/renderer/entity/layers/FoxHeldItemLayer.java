package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class FoxHeldItemLayer extends RenderLayer<FoxRenderState, FoxModel> {
   private final ItemRenderer itemRenderer;

   public FoxHeldItemLayer(RenderLayerParent<FoxRenderState, FoxModel> var1, ItemRenderer var2) {
      super(var1);
      this.itemRenderer = var2;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, FoxRenderState var4, float var5, float var6) {
      BakedModel var7 = var4.getMainHandItemModel();
      ItemStack var8 = var4.getMainHandItem();
      if (var7 != null && !var8.isEmpty()) {
         boolean var9 = var4.isSleeping;
         boolean var10 = var4.isBaby;
         var1.pushPose();
         var1.translate(((FoxModel)this.getParentModel()).head.x / 16.0F, ((FoxModel)this.getParentModel()).head.y / 16.0F, ((FoxModel)this.getParentModel()).head.z / 16.0F);
         if (var10) {
            float var11 = 0.75F;
            var1.scale(0.75F, 0.75F, 0.75F);
         }

         var1.mulPose(Axis.ZP.rotation(var4.headRollAngle));
         var1.mulPose(Axis.YP.rotationDegrees(var5));
         var1.mulPose(Axis.XP.rotationDegrees(var6));
         if (var4.isBaby) {
            if (var9) {
               var1.translate(0.4F, 0.26F, 0.15F);
            } else {
               var1.translate(0.06F, 0.26F, -0.5F);
            }
         } else if (var9) {
            var1.translate(0.46F, 0.26F, 0.22F);
         } else {
            var1.translate(0.06F, 0.27F, -0.5F);
         }

         var1.mulPose(Axis.XP.rotationDegrees(90.0F));
         if (var9) {
            var1.mulPose(Axis.ZP.rotationDegrees(90.0F));
         }

         this.itemRenderer.render(var8, ItemDisplayContext.GROUND, false, var1, var2, var3, OverlayTexture.NO_OVERLAY, var7);
         var1.popPose();
      }
   }
}
