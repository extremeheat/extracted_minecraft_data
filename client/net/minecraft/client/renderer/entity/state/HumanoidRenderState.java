package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class HumanoidRenderState extends ArmedEntityRenderState {
   public float swimAmount;
   public float speedValue = 1.0F;
   public float maxCrossbowChargeDuration;
   public float ticksUsingItem;
   public InteractionHand useItemHand;
   public boolean isCrouching;
   public boolean isFallFlying;
   public boolean isVisuallySwimming;
   public boolean isPassenger;
   public boolean isUsingItem;
   public float elytraRotX;
   public float elytraRotY;
   public float elytraRotZ;
   public ItemStack headEquipment;
   public ItemStack chestEquipment;
   public ItemStack legsEquipment;
   public ItemStack feetEquipment;

   public HumanoidRenderState() {
      super();
      this.useItemHand = InteractionHand.MAIN_HAND;
      this.headEquipment = ItemStack.EMPTY;
      this.chestEquipment = ItemStack.EMPTY;
      this.legsEquipment = ItemStack.EMPTY;
      this.feetEquipment = ItemStack.EMPTY;
   }

   public float ticksUsingItem(final HumanoidArm arm) {
      return this.isUsingItem && this.useItemHand == InteractionHand.MAIN_HAND == (arm == this.mainArm) ? this.ticksUsingItem : 0.0F;
   }
}
