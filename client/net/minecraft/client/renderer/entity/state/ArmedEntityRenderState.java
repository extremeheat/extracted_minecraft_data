package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;

public class ArmedEntityRenderState extends LivingEntityRenderState {
   public HumanoidArm mainArm;
   public HumanoidArm attackArm;
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
      this.attackArm = HumanoidArm.RIGHT;
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

   public ItemStack getUseItemStackForArm(final HumanoidArm arm) {
      return arm == HumanoidArm.RIGHT ? this.rightHandItemStack : this.leftHandItemStack;
   }

   public float ticksUsingItem(final HumanoidArm arm) {
      return 0.0F;
   }

   public static void extractArmedEntityRenderState(final LivingEntity entity, final ArmedEntityRenderState state, final ItemModelResolver itemModelResolver, final float partialTicks) {
      state.mainArm = entity.getMainArm();
      state.attackArm = entity.swingingArm != InteractionHand.OFF_HAND ? state.mainArm : state.mainArm.getOpposite();
      ItemStack itemStack = entity.getItemHeldByArm(state.attackArm);
      state.swingAnimationType = itemStack.getSwingAnimation().type();
      state.attackTime = entity.getAttackAnim(partialTicks);
      itemModelResolver.updateForLiving(state.rightHandItemState, entity.getItemHeldByArm(HumanoidArm.RIGHT), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, entity);
      itemModelResolver.updateForLiving(state.leftHandItemState, entity.getItemHeldByArm(HumanoidArm.LEFT), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, entity);
      state.leftHandItemStack = entity.getItemHeldByArm(HumanoidArm.LEFT).copy();
      state.rightHandItemStack = entity.getItemHeldByArm(HumanoidArm.RIGHT).copy();
   }
}
