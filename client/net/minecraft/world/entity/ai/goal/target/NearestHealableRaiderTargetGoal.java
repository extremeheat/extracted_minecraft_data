package net.minecraft.world.entity.ai.goal.target;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raider;

public class NearestHealableRaiderTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
   private static final int DEFAULT_COOLDOWN = 200;
   private int cooldown = 0;

   public NearestHealableRaiderTargetGoal(Raider var1, Class<T> var2, boolean var3, @Nullable Predicate<LivingEntity> var4) {
      super(var1, var2, 500, var3, false, var4);
   }

   public int getCooldown() {
      return this.cooldown;
   }

   public void decrementCooldown() {
      --this.cooldown;
   }

   public boolean canUse() {
      if (this.cooldown <= 0 && this.mob.getRandom().nextBoolean()) {
         if (!((Raider)this.mob).hasActiveRaid()) {
            return false;
         } else {
            this.findTarget();
            return this.target != null;
         }
      } else {
         return false;
      }
   }

   public void start() {
      this.cooldown = reducedTickDelay(200);
      super.start();
   }
}
