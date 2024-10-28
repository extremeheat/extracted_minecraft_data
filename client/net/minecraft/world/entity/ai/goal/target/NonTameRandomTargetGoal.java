package net.minecraft.world.entity.ai.goal.target;

import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class NonTameRandomTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
   private final TamableAnimal tamableMob;

   public NonTameRandomTargetGoal(TamableAnimal var1, Class<T> var2, boolean var3, @Nullable TargetingConditions.Selector var4) {
      super(var1, var2, 10, var3, false, var4);
      this.tamableMob = var1;
   }

   public boolean canUse() {
      return !this.tamableMob.isTame() && super.canUse();
   }

   public boolean canContinueToUse() {
      return this.targetConditions != null ? this.targetConditions.test(getServerLevel(this.mob), this.mob, this.target) : super.canContinueToUse();
   }
}
