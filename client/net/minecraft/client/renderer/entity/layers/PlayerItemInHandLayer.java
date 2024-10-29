package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PlayerItemInHandLayer<S extends PlayerRenderState, M extends EntityModel<S> & ArmedModel & HeadedModel> extends ItemInHandLayer<S, M> {
   private final ItemRenderer itemRenderer;
   private static final float X_ROT_MIN = -0.5235988F;
   private static final float X_ROT_MAX = 1.5707964F;

   public PlayerItemInHandLayer(RenderLayerParent<S, M> var1, ItemRenderer var2) {
      super(var1, var2);
      this.itemRenderer = var2;
   }

   protected void renderArmWithItem(S var1, @Nullable BakedModel var2, ItemStack var3, ItemDisplayContext var4, HumanoidArm var5, PoseStack var6, MultiBufferSource var7, int var8) {
      if (var2 != null) {
         InteractionHand var9 = var5 == var1.mainArm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
         if (var1.isUsingItem && var1.useItemHand == var9 && var1.attackTime < 1.0E-5F && var3.is(Items.SPYGLASS)) {
            this.renderArmWithSpyglass(var2, var3, var5, var6, var7, var8);
         } else {
            super.renderArmWithItem(var1, var2, var3, var4, var5, var6, var7, var8);
         }

      }
   }

   private void renderArmWithSpyglass(BakedModel var1, ItemStack var2, HumanoidArm var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      this.getParentModel().root().translateAndRotate(var4);
      ModelPart var7 = ((HeadedModel)this.getParentModel()).getHead();
      float var8 = var7.xRot;
      var7.xRot = Mth.clamp(var7.xRot, -0.5235988F, 1.5707964F);
      var7.translateAndRotate(var4);
      var7.xRot = var8;
      CustomHeadLayer.translateToHead(var4, CustomHeadLayer.Transforms.DEFAULT);
      boolean var9 = var3 == HumanoidArm.LEFT;
      var4.translate((var9 ? -2.5F : 2.5F) / 16.0F, -0.0625F, 0.0F);
      this.itemRenderer.render(var2, ItemDisplayContext.HEAD, false, var4, var5, var6, OverlayTexture.NO_OVERLAY, var1);
      var4.popPose();
   }
}
