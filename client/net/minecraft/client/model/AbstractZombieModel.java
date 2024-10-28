package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;

public abstract class AbstractZombieModel<T extends Monster> extends HumanoidModel<T> {
   protected AbstractZombieModel(ModelPart var1) {
      super(var1);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim((LivingEntity)var1, var2, var3, var4, var5, var6);
      AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, this.isAggressive(var1), this.attackTime, var4);
   }

   public abstract boolean isAggressive(T var1);
}
