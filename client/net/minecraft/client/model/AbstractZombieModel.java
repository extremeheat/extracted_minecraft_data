package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;

public abstract class AbstractZombieModel<S extends ZombieRenderState> extends HumanoidModel<S> {
   protected AbstractZombieModel(ModelPart var1) {
      super(var1);
   }

   public void setupAnim(S var1) {
      super.setupAnim(var1);
      AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, var1.isAggressive, var1);
   }
}
