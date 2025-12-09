package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class PlayerItemInHandLayer<S extends AvatarRenderState, M extends EntityModel<S> & ArmedModel & HeadedModel> extends ItemInHandLayer<S, M> {
   private static final float X_ROT_MIN = -0.5235988F;
   private static final float X_ROT_MAX = 1.5707964F;

   public PlayerItemInHandLayer(RenderLayerParent<S, M> var1) {
      super(var1);
   }

   protected void submitArmWithItem(S var1, ItemStackRenderState var2, ItemStack var3, HumanoidArm var4, PoseStack var5, SubmitNodeCollector var6, int var7) {
      if (!var2.isEmpty()) {
         InteractionHand var8 = var4 == var1.mainArm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
         if (var1.isUsingItem && var1.useItemHand == var8 && var1.attackTime < 1.0E-5F && !var1.heldOnHead.isEmpty()) {
            this.renderItemHeldToEye(var1, var4, var5, var6, var7);
         } else {
            super.submitArmWithItem(var1, var2, var3, var4, var5, var6, var7);
         }

      }
   }

   private void renderItemHeldToEye(S var1, HumanoidArm var2, PoseStack var3, SubmitNodeCollector var4, int var5) {
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
      var1.heldOnHead.submit(var3, var4, var5, OverlayTexture.NO_OVERLAY, var1.outlineColor);
      var3.popPose();
   }
}
