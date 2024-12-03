package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;

public class ArmedEntityRenderState extends LivingEntityRenderState {
   public HumanoidArm mainArm;
   public HumanoidModel.ArmPose rightArmPose;
   public final ItemStackRenderState rightHandItem;
   public HumanoidModel.ArmPose leftArmPose;
   public final ItemStackRenderState leftHandItem;

   public ArmedEntityRenderState() {
      super();
      this.mainArm = HumanoidArm.RIGHT;
      this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
      this.rightHandItem = new ItemStackRenderState();
      this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
      this.leftHandItem = new ItemStackRenderState();
   }

   public ItemStackRenderState getMainHandItem() {
      return this.mainArm == HumanoidArm.RIGHT ? this.rightHandItem : this.leftHandItem;
   }

   public static void extractArmedEntityRenderState(LivingEntity var0, ArmedEntityRenderState var1, ItemModelResolver var2) {
      var1.mainArm = var0.getMainArm();
      var2.updateForLiving(var1.rightHandItem, var0.getItemHeldByArm(HumanoidArm.RIGHT), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, var0);
      var2.updateForLiving(var1.leftHandItem, var0.getItemHeldByArm(HumanoidArm.LEFT), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, true, var0);
   }
}
