package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CrossbowItem;

public abstract class HumanoidMobRenderer<T extends Mob, S extends HumanoidRenderState, M extends HumanoidModel<S>> extends AgeableMobRenderer<T, S, M> {
   public HumanoidMobRenderer(EntityRendererProvider.Context var1, M var2, float var3) {
      this(var1, var2, var2, var3);
   }

   public HumanoidMobRenderer(EntityRendererProvider.Context var1, M var2, M var3, float var4) {
      this(var1, var2, var3, var4, CustomHeadLayer.Transforms.DEFAULT);
   }

   public HumanoidMobRenderer(EntityRendererProvider.Context var1, M var2, M var3, float var4, CustomHeadLayer.Transforms var5) {
      super(var1, var2, var3, var4);
      this.addLayer(new CustomHeadLayer(this, var1.getModelSet(), var5, var1.getItemRenderer()));
      this.addLayer(new WingsLayer(this, var1.getModelSet(), var1.getEquipmentRenderer()));
      this.addLayer(new ItemInHandLayer(this, var1.getItemRenderer()));
   }

   public void extractRenderState(T var1, S var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      extractHumanoidRenderState(var1, var2, var3);
   }

   public static void extractHumanoidRenderState(LivingEntity var0, HumanoidRenderState var1, float var2) {
      var1.isCrouching = var0.isCrouching();
      var1.isFallFlying = var0.isFallFlying();
      var1.isVisuallySwimming = var0.isVisuallySwimming();
      var1.isPassenger = var0.isPassenger();
      var1.speedValue = 1.0F;
      if (var1.isFallFlying) {
         var1.speedValue = (float)var0.getDeltaMovement().lengthSqr();
         var1.speedValue /= 0.2F;
         var1.speedValue *= var1.speedValue * var1.speedValue;
      }

      if (var1.speedValue < 1.0F) {
         var1.speedValue = 1.0F;
      }

      var1.attackTime = var0.getAttackAnim(var2);
      var1.swimAmount = var0.getSwimAmount(var2);
      var1.attackArm = getAttackArm(var0);
      var1.useItemHand = var0.getUsedItemHand();
      var1.maxCrossbowChargeDuration = (float)CrossbowItem.getChargeDuration(var0.getUseItem(), var0);
      var1.ticksUsingItem = var0.getTicksUsingItem();
      var1.isUsingItem = var0.isUsingItem();
      var1.elytraRotX = var0.elytraAnimationState.getRotX(var2);
      var1.elytraRotY = var0.elytraAnimationState.getRotY(var2);
      var1.elytraRotZ = var0.elytraAnimationState.getRotZ(var2);
      var1.chestItem = var0.getItemBySlot(EquipmentSlot.CHEST).copy();
      var1.legsItem = var0.getItemBySlot(EquipmentSlot.LEGS).copy();
      var1.feetItem = var0.getItemBySlot(EquipmentSlot.FEET).copy();
   }

   private static HumanoidArm getAttackArm(LivingEntity var0) {
      HumanoidArm var1 = var0.getMainArm();
      return var0.swingingArm == InteractionHand.MAIN_HAND ? var1 : var1.getOpposite();
   }
}
