package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public abstract class HumanoidMobRenderer<T extends Mob, S extends HumanoidRenderState, M extends HumanoidModel<S>> extends AgeableMobRenderer<T, S, M> {
   public HumanoidMobRenderer(EntityRendererProvider.Context var1, M var2, float var3) {
      this(var1, var2, var2, var3);
   }

   public HumanoidMobRenderer(EntityRendererProvider.Context var1, M var2, M var3, float var4) {
      this(var1, var2, var3, var4, CustomHeadLayer.Transforms.DEFAULT);
   }

   public HumanoidMobRenderer(EntityRendererProvider.Context var1, M var2, M var3, float var4, CustomHeadLayer.Transforms var5) {
      super(var1, var2, var3, var4);
      this.addLayer(new CustomHeadLayer(this, var1.getModelSet(), var5));
      this.addLayer(new WingsLayer(this, var1.getModelSet(), var1.getEquipmentRenderer()));
      this.addLayer(new ItemInHandLayer(this));
   }

   protected HumanoidModel.ArmPose getArmPose(T var1, HumanoidArm var2) {
      return HumanoidModel.ArmPose.EMPTY;
   }

   public void extractRenderState(T var1, S var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      extractHumanoidRenderState(var1, var2, var3, this.itemModelResolver);
      var2.leftArmPose = this.getArmPose(var1, HumanoidArm.LEFT);
      var2.rightArmPose = this.getArmPose(var1, HumanoidArm.RIGHT);
   }

   public static void extractHumanoidRenderState(LivingEntity var0, HumanoidRenderState var1, float var2, ItemModelResolver var3) {
      ArmedEntityRenderState.extractArmedEntityRenderState(var0, var1, var3);
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
      var1.headEquipment = getEquipmentIfRenderable(var0, EquipmentSlot.HEAD);
      var1.chestEquipment = getEquipmentIfRenderable(var0, EquipmentSlot.CHEST);
      var1.legsEquipment = getEquipmentIfRenderable(var0, EquipmentSlot.LEGS);
      var1.feetEquipment = getEquipmentIfRenderable(var0, EquipmentSlot.FEET);
   }

   private static ItemStack getEquipmentIfRenderable(LivingEntity var0, EquipmentSlot var1) {
      ItemStack var2 = var0.getItemBySlot(var1);
      return HumanoidArmorLayer.shouldRender(var2, var1) ? var2.copy() : ItemStack.EMPTY;
   }

   private static HumanoidArm getAttackArm(LivingEntity var0) {
      HumanoidArm var1 = var0.getMainArm();
      return var0.swingingArm == InteractionHand.MAIN_HAND ? var1 : var1.getOpposite();
   }
}
