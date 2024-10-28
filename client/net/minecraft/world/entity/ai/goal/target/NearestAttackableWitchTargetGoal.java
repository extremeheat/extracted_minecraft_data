package net.minecraft.world.entity.ai.goal.target;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raider;

public class NearestAttackableWitchTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
   private boolean canAttack = true;

   public NearestAttackableWitchTargetGoal(Raider var1, Class<T> var2, int var3, boolean var4, boolean var5, @Nullable Predicate<LivingEntity> var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public void setCanAttack(boolean var1) {
      this.canAttack = var1;
   }

   public boolean canUse() {
      return this.canAttack && super.canUse();
   }
}
