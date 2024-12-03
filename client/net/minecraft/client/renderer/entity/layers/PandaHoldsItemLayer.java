package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.PandaRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

public class PandaHoldsItemLayer extends RenderLayer<PandaRenderState, PandaModel> {
   public PandaHoldsItemLayer(RenderLayerParent<PandaRenderState, PandaModel> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, PandaRenderState var4, float var5, float var6) {
      ItemStackRenderState var7 = var4.heldItem;
      if (!var7.isEmpty() && var4.isSitting && !var4.isScared) {
         float var8 = -0.6F;
         float var9 = 1.4F;
         if (var4.isEating) {
            var8 -= 0.2F * Mth.sin(var4.ageInTicks * 0.6F) + 0.2F;
            var9 -= 0.09F * Mth.sin(var4.ageInTicks * 0.6F);
         }

         var1.pushPose();
         var1.translate(0.1F, var9, var8);
         var7.render(var1, var2, var3, OverlayTexture.NO_OVERLAY);
         var1.popPose();
      }
   }
}
