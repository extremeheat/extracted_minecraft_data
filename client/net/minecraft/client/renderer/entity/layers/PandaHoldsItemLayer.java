package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.PandaRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PandaHoldsItemLayer extends RenderLayer<PandaRenderState, PandaModel> {
   private final ItemRenderer itemRenderer;

   public PandaHoldsItemLayer(RenderLayerParent<PandaRenderState, PandaModel> var1, ItemRenderer var2) {
      super(var1);
      this.itemRenderer = var2;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, PandaRenderState var4, float var5, float var6) {
      BakedModel var7 = var4.getMainHandItemModel();
      if (var7 != null && var4.isSitting && !var4.isScared) {
         float var8 = -0.6F;
         float var9 = 1.4F;
         if (var4.isEating) {
            var8 -= 0.2F * Mth.sin(var4.ageInTicks * 0.6F) + 0.2F;
            var9 -= 0.09F * Mth.sin(var4.ageInTicks * 0.6F);
         }

         var1.pushPose();
         var1.translate(0.1F, var9, var8);
         ItemStack var10 = var4.getMainHandItem();
         this.itemRenderer.render(var10, ItemDisplayContext.GROUND, false, var1, var2, var3, OverlayTexture.NO_OVERLAY, var7);
         var1.popPose();
      }
   }
}
