package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import org.jspecify.annotations.Nullable;

public class SwellGoal extends Goal {
   private final Creeper creeper;
   private @Nullable LivingEntity target;

   public SwellGoal(final Creeper creeper) {
      super();
      this.creeper = creeper;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      LivingEntity target = this.creeper.getTarget();
      return this.creeper.getSwellDir() > 0 || target != null && !target.isDeadOrDying() && this.creeper.distanceToSqr(target) < 9.0;
   }

   public void start() {
      this.creeper.getNavigation().stop();
      this.target = this.creeper.getTarget();
   }

   public void stop() {
      this.target = null;
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      if (this.target != null && !this.target.isDeadOrDying()) {
         if (this.creeper.distanceToSqr(this.target) > 49.0) {
            this.creeper.setSwellDir(-1);
         } else if (!this.creeper.getSensing().hasLineOfSight(this.target)) {
            this.creeper.setSwellDir(-1);
         } else {
            this.creeper.setSwellDir(1);
         }
      } else {
         this.creeper.setSwellDir(-1);
      }
   }
}
