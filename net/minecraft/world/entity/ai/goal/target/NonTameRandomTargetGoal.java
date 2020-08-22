package net.minecraft.world.entity.ai.goal.target;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.TamableAnimal;

public class NonTameRandomTargetGoal extends NearestAttackableTargetGoal {
   private final TamableAnimal tamableMob;

   public NonTameRandomTargetGoal(TamableAnimal var1, Class var2, boolean var3, @Nullable Predicate var4) {
      super(var1, var2, 10, var3, false, var4);
      this.tamableMob = var1;
   }

   public boolean canUse() {
      return !this.tamableMob.isTame() && super.canUse();
   }

   public boolean canContinueToUse() {
      return this.targetConditions != null ? this.targetConditions.test(this.mob, this.target) : super.canContinueToUse();
   }
}
