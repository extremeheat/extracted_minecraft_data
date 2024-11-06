package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VillagerLikeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class CrossedArmsItemLayer<S extends HoldingEntityRenderState, M extends EntityModel<S> & VillagerLikeModel> extends RenderLayer<S, M> {
   public CrossedArmsItemLayer(RenderLayerParent<S, M> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      ItemStackRenderState var7 = var4.heldItem;
      if (!var7.isEmpty()) {
         var1.pushPose();
         this.applyTranslation(var4, var1);
         var7.render(var1, var2, var3, OverlayTexture.NO_OVERLAY);
         var1.popPose();
      }
   }

   protected void applyTranslation(S var1, PoseStack var2) {
      ((VillagerLikeModel)this.getParentModel()).translateToArms(var2);
      var2.mulPose(Axis.XP.rotation(0.75F));
      var2.scale(1.07F, 1.07F, 1.07F);
      var2.translate(0.0F, 0.13F, -0.34F);
      var2.mulPose(Axis.XP.rotation(3.1415927F));
   }
}
