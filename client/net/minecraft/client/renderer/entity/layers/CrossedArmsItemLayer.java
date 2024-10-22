package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CrossedArmsItemLayer<S extends LivingEntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
   private final ItemRenderer itemRenderer;

   public CrossedArmsItemLayer(RenderLayerParent<S, M> var1, ItemRenderer var2) {
      super(var1);
      this.itemRenderer = var2;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      BakedModel var7 = var4.getMainHandItemModel();
      if (var7 != null) {
         var1.pushPose();
         var1.translate(0.0F, 0.4F, -0.4F);
         var1.mulPose(Axis.XP.rotationDegrees(180.0F));
         ItemStack var8 = var4.getMainHandItem();
         this.itemRenderer.render(var8, ItemDisplayContext.GROUND, false, var1, var2, var3, OverlayTexture.NO_OVERLAY, var7);
         var1.popPose();
      }
   }
}
