package net.minecraft.world.entity.ai.goal.target;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.raid.Raider;
import org.jspecify.annotations.Nullable;

public class NearestHealableRaiderTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
   private static final int DEFAULT_COOLDOWN = 200;
   private int cooldown = 0;

   public NearestHealableRaiderTargetGoal(final Raider raider, final Class<T> targetType, final boolean mustSee, final TargetingConditions.@Nullable Selector subselector) {
      super(raider, targetType, 500, mustSee, false, subselector);
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
