package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;

public abstract class AbstractZombieModel<S extends ZombieRenderState> extends HumanoidModel<S> {
   protected AbstractZombieModel(ModelPart var1) {
      super(var1);
   }

   public void setupAnim(S var1) {
      super.setupAnim((HumanoidRenderState)var1);
      float var2 = var1.attackTime;
      AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, var1.isAggressive, var2, var1.ageInTicks);
   }
}
