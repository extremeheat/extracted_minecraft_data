package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.DolphinRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

public class DolphinCarryingItemLayer extends RenderLayer<DolphinRenderState, DolphinModel> {
   public DolphinCarryingItemLayer(RenderLayerParent<DolphinRenderState, DolphinModel> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, DolphinRenderState var4, float var5, float var6) {
      ItemStackRenderState var7 = var4.heldItem;
      if (!var7.isEmpty()) {
         var1.pushPose();
         float var8 = 1.0F;
         float var9 = -1.0F;
         float var10 = Mth.abs(var4.xRot) / 60.0F;
         if (var4.xRot < 0.0F) {
            var1.translate(0.0F, 1.0F - var10 * 0.5F, -1.0F + var10 * 0.5F);
         } else {
            var1.translate(0.0F, 1.0F + var10 * 0.8F, -1.0F + var10 * 0.2F);
         }

         var7.render(var1, var2, var3, OverlayTexture.NO_OVERLAY);
         var1.popPose();
      }
   }
}
