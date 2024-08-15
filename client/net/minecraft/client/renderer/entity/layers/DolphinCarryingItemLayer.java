package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.DolphinRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DolphinCarryingItemLayer extends RenderLayer<DolphinRenderState, DolphinModel> {
   private final ItemRenderer itemRenderer;

   public DolphinCarryingItemLayer(RenderLayerParent<DolphinRenderState, DolphinModel> var1, ItemRenderer var2) {
      super(var1);
      this.itemRenderer = var2;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, DolphinRenderState var4, float var5, float var6) {
      ItemStack var7 = var4.getMainHandItem();
      BakedModel var8 = var4.getMainHandItemModel();
      if (var8 != null) {
         var1.pushPose();
         float var9 = 1.0F;
         float var10 = -1.0F;
         float var11 = Mth.abs(var4.xRot) / 60.0F;
         if (var4.xRot < 0.0F) {
            var1.translate(0.0F, 1.0F - var11 * 0.5F, -1.0F + var11 * 0.5F);
         } else {
            var1.translate(0.0F, 1.0F + var11 * 0.8F, -1.0F + var11 * 0.2F);
         }

         this.itemRenderer.render(var7, ItemDisplayContext.GROUND, false, var1, var2, var3, OverlayTexture.NO_OVERLAY, var8);
         var1.popPose();
      }
   }
}
