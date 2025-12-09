package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;

public class ArmedEntityRenderState extends LivingEntityRenderState {
   public HumanoidArm mainArm;
   public HumanoidModel.ArmPose rightArmPose;
   public final ItemStackRenderState rightHandItemState;
   public ItemStack rightHandItemStack;
   public HumanoidModel.ArmPose leftArmPose;
   public final ItemStackRenderState leftHandItemState;
   public ItemStack leftHandItemStack;
   public SwingAnimationType swingAnimationType;
   public float attackTime;

   public ArmedEntityRenderState() {
      super();
      this.mainArm = HumanoidArm.RIGHT;
      this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
      this.rightHandItemState = new ItemStackRenderState();
      this.rightHandItemStack = ItemStack.EMPTY;
      this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
      this.leftHandItemState = new ItemStackRenderState();
      this.leftHandItemStack = ItemStack.EMPTY;
      this.swingAnimationType = SwingAnimationType.WHACK;
   }

   public ItemStackRenderState getMainHandItemState() {
      return this.mainArm == HumanoidArm.RIGHT ? this.rightHandItemState : this.leftHandItemState;
   }

   public ItemStack getMainHandItemStack() {
      return this.mainArm == HumanoidArm.RIGHT ? this.rightHandItemStack : this.leftHandItemStack;
   }

   public ItemStack getUseItemStackForArm(HumanoidArm var1) {
      return var1 == HumanoidArm.RIGHT ? this.rightHandItemStack : this.leftHandItemStack;
   }

   public float ticksUsingItem(HumanoidArm var1) {
      return 0.0F;
   }

   public static void extractArmedEntityRenderState(LivingEntity var0, ArmedEntityRenderState var1, ItemModelResolver var2, float var3) {
      var1.mainArm = var0.getMainArm();
      ItemStack var4 = var0.getMainHandItem();
      var1.swingAnimationType = var4.getSwingAnimation().type();
      var1.attackTime = var0.getAttackAnim(var3);
      var2.updateForLiving(var1.rightHandItemState, var0.getItemHeldByArm(HumanoidArm.RIGHT), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, var0);
      var2.updateForLiving(var1.leftHandItemState, var0.getItemHeldByArm(HumanoidArm.LEFT), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, var0);
      var1.leftHandItemStack = var0.getItemHeldByArm(HumanoidArm.LEFT).copy();
      var1.rightHandItemStack = var0.getItemHeldByArm(HumanoidArm.RIGHT).copy();
   }
}
