package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Targetable;
import net.minecraft.world.entity.monster.Creeper;
import org.jspecify.annotations.Nullable;

public class SwellGoal extends Goal {
   private final Creeper creeper;
   private @Nullable Entity target;

   public SwellGoal(final Creeper creeper) {
      super();
      this.creeper = creeper;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      Entity target = this.creeper.getTarget();
      boolean var10000;
      if (this.creeper.getSwellDir() <= 0) {
         label30: {
            if (target != null && target instanceof Targetable) {
               Targetable targetable = (Targetable)target;
               if (!targetable.isDeadOrDying() && this.creeper.distanceToSqr(target) < 9.0) {
                  break label30;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
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
      if (this.target != null) {
         label25: {
            Entity var2 = this.target;
            if (var2 instanceof Targetable) {
               Targetable targetable = (Targetable)var2;
               if (targetable.isDeadOrDying()) {
                  break label25;
               }
            }

            if (this.creeper.distanceToSqr(this.target) > 49.0) {
               this.creeper.setSwellDir(-1);
               return;
            }

            if (!this.creeper.getSensing().hasLineOfSight(this.target)) {
               this.creeper.setSwellDir(-1);
               return;
            }

            this.creeper.setSwellDir(1);
            return;
         }
      }

      this.creeper.setSwellDir(-1);
   }
}
