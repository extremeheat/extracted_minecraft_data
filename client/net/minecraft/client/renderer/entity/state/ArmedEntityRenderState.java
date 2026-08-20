package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class ArmedEntityRenderState extends LivingEntityRenderState {
   public HumanoidArm mainArm;
   public HumanoidModel.ArmPose rightArmPose;
   public final ItemStackRenderState rightHandItemState;
   public ItemStack rightHandItemStack;
   public HumanoidModel.ArmPose leftArmPose;
   public final ItemStackRenderState leftHandItemState;
   public ItemStack leftHandItemStack;
   public LivingEntity.@Nullable SwingDescription currentSwing;
   public float swingAnimation;

   public ArmedEntityRenderState() {
      super();
      this.mainArm = HumanoidArm.RIGHT;
      this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
      this.rightHandItemState = new ItemStackRenderState();
      this.rightHandItemStack = ItemStack.EMPTY;
      this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
      this.leftHandItemState = new ItemStackRenderState();
      this.leftHandItemStack = ItemStack.EMPTY;
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

   public HumanoidModel.ArmPose getArmPose(final HumanoidArm arm) {
      HumanoidModel.ArmPose var10000;
      switch (arm) {
         case LEFT -> var10000 = this.leftArmPose;
         case RIGHT -> var10000 = this.rightArmPose;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public float ticksUsingItem(final HumanoidArm arm) {
      return 0.0F;
   }

   public static void extractArmedEntityRenderState(final LivingEntity entity, final ArmedEntityRenderState state, final ItemModelResolver itemModelResolver, final float partialTicks) {
      state.mainArm = entity.getMainArm();
      state.currentSwing = entity.getCurrentSwing();
      state.swingAnimation = entity.getSwingAnimation(partialTicks);
      itemModelResolver.updateForLiving(state.rightHandItemState, entity.getItemHeldByArm(HumanoidArm.RIGHT), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, entity);
      itemModelResolver.updateForLiving(state.leftHandItemState, entity.getItemHeldByArm(HumanoidArm.LEFT), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, entity);
      state.leftHandItemStack = entity.getItemHeldByArm(HumanoidArm.LEFT).copy();
      state.rightHandItemStack = entity.getItemHeldByArm(HumanoidArm.RIGHT).copy();
   }
}
