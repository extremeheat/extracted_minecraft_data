package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class HumanoidRenderState extends LivingEntityRenderState {
   public float swimAmount;
   public float attackTime;
   public float speedValue = 1.0F;
   public float maxCrossbowChargeDuration;
   public int ticksUsingItem;
   public HumanoidArm attackArm;
   public InteractionHand useItemHand;
   public boolean isCrouching;
   public boolean isFallFlying;
   public boolean isVisuallySwimming;
   public boolean isPassenger;
   public boolean isUsingItem;
   public float elytraRotX;
   public float elytraRotY;
   public float elytraRotZ;
   public ItemStack chestItem;
   public ItemStack legsItem;
   public ItemStack feetItem;

   public HumanoidRenderState() {
      super();
      this.attackArm = HumanoidArm.RIGHT;
      this.useItemHand = InteractionHand.MAIN_HAND;
      this.chestItem = ItemStack.EMPTY;
      this.legsItem = ItemStack.EMPTY;
      this.feetItem = ItemStack.EMPTY;
   }
}
