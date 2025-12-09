package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;
import org.joml.Quaternionfc;

public class ItemInHandLayer<S extends ArmedEntityRenderState, M extends EntityModel<S> & ArmedModel> extends RenderLayer<S, M> {
   public ItemInHandLayer(RenderLayerParent<S, M> var1) {
      super(var1);
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, S var4, float var5, float var6) {
      this.submitArmWithItem(var4, var4.rightHandItemState, var4.rightHandItemStack, HumanoidArm.RIGHT, var1, var2, var3);
      this.submitArmWithItem(var4, var4.leftHandItemState, var4.leftHandItemStack, HumanoidArm.LEFT, var1, var2, var3);
   }

   protected void submitArmWithItem(S var1, ItemStackRenderState var2, ItemStack var3, HumanoidArm var4, PoseStack var5, SubmitNodeCollector var6, int var7) {
      if (!var2.isEmpty()) {
         var5.pushPose();
         ((ArmedModel)this.getParentModel()).translateToHand(var1, var4, var5);
         var5.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-90.0F));
         var5.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
         boolean var8 = var4 == HumanoidArm.LEFT;
         var5.translate((float)(var8 ? -1 : 1) / 16.0F, 0.125F, -0.625F);
         if (var1.attackTime > 0.0F && var1.mainArm == var4 && var1.swingAnimationType == SwingAnimationType.STAB) {
            SpearAnimations.thirdPersonAttackItem(var1, var5);
         }

         float var9 = var1.ticksUsingItem(var4);
         if (var9 != 0.0F) {
            (var4 == HumanoidArm.RIGHT ? var1.rightArmPose : var1.leftArmPose).animateUseItem(var1, var5, var9, var4, var3);
         }

         var2.submit(var5, var6, var7, OverlayTexture.NO_OVERLAY, var1.outlineColor);
         var5.popPose();
      }
   }
}
