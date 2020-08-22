package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

public class SwellGoal extends Goal {
   private final Creeper creeper;
   private LivingEntity target;

   public SwellGoal(Creeper var1) {
      this.creeper = var1;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      LivingEntity var1 = this.creeper.getTarget();
      return this.creeper.getSwellDir() > 0 || var1 != null && this.creeper.distanceToSqr(var1) < 9.0D;
   }

   public void start() {
      this.creeper.getNavigation().stop();
      this.target = this.creeper.getTarget();
   }

   public void stop() {
      this.target = null;
   }

   public void tick() {
      if (this.target == null) {
         this.creeper.setSwellDir(-1);
      } else if (this.creeper.distanceToSqr(this.target) > 49.0D) {
         this.creeper.setSwellDir(-1);
      } else if (!this.creeper.getSensing().canSee(this.target)) {
         this.creeper.setSwellDir(-1);
      } else {
         this.creeper.setSwellDir(1);
      }
   }
}
