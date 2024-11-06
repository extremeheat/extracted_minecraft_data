package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class PlayerItemInHandLayer<S extends PlayerRenderState, M extends EntityModel<S> & ArmedModel & HeadedModel> extends ItemInHandLayer<S, M> {
   private static final float X_ROT_MIN = -0.5235988F;
   private static final float X_ROT_MAX = 1.5707964F;

   public PlayerItemInHandLayer(RenderLayerParent<S, M> var1) {
      super(var1);
   }

   protected void renderArmWithItem(S var1, ItemStackRenderState var2, HumanoidArm var3, PoseStack var4, MultiBufferSource var5, int var6) {
      if (!var2.isEmpty()) {
         if (var1.attackTime < 1.0E-5F && var1.mainArm == var3 && !var1.heldOnHead.isEmpty()) {
            this.renderItemHeldToEye(var1.heldOnHead, var3, var4, var5, var6);
         } else {
            super.renderArmWithItem(var1, var2, var3, var4, var5, var6);
         }

      }
   }

   private void renderItemHeldToEye(ItemStackRenderState var1, HumanoidArm var2, PoseStack var3, MultiBufferSource var4, int var5) {
      var3.pushPose();
      this.getParentModel().root().translateAndRotate(var3);
      ModelPart var6 = ((HeadedModel)this.getParentModel()).getHead();
      float var7 = var6.xRot;
      var6.xRot = Mth.clamp(var6.xRot, -0.5235988F, 1.5707964F);
      var6.translateAndRotate(var3);
      var6.xRot = var7;
      CustomHeadLayer.translateToHead(var3, CustomHeadLayer.Transforms.DEFAULT);
      boolean var8 = var2 == HumanoidArm.LEFT;
      var3.translate((var8 ? -2.5F : 2.5F) / 16.0F, -0.0625F, 0.0F);
      var1.render(var3, var4, var5, OverlayTexture.NO_OVERLAY);
      var3.popPose();
   }
}
