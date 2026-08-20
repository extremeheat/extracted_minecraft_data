package net.minecraft.client.model.monster.zombie;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;

public class ZombieModel<S extends ZombieRenderState> extends HumanoidModel<S> {
   public ZombieModel(final ModelPart root) {
      super(root);
   }

   protected void setupAttackAnimation(final S state) {
      super.setupAttackAnimation(state);
      AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, state.isAggressive, state);
   }
}
