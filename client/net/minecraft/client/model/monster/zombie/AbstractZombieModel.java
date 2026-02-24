package net.minecraft.client.model.monster.zombie;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractZombieModel<S extends ZombieRenderState> extends HumanoidModel<S> {
   protected AbstractZombieModel(final ModelPart root) {
      super(root);
   }

   public void setupAnim(final S state) {
      super.setupAnim(state);
      if (state.isBaby && state.getMainHandItemStack() != ItemStack.EMPTY) {
         this.rightArm.xRot = 0.0F;
         this.leftArm.xRot = 0.0F;
      } else {
         AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, state.isAggressive, state);
      }
   }
}
