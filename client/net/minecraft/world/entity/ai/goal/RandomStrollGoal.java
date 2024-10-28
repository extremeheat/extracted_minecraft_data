package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class RandomStrollGoal extends Goal {
   public static final int DEFAULT_INTERVAL = 120;
   protected final PathfinderMob mob;
   protected double wantedX;
   protected double wantedY;
   protected double wantedZ;
   protected final double speedModifier;
   protected int interval;
   protected boolean forceTrigger;
   private final boolean checkNoActionTime;

   public RandomStrollGoal(PathfinderMob var1, double var2) {
      this(var1, var2, 120);
   }

   public RandomStrollGoal(PathfinderMob var1, double var2, int var4) {
      this(var1, var2, var4, true);
   }

   public RandomStrollGoal(PathfinderMob var1, double var2, int var4, boolean var5) {
      super();
      this.mob = var1;
      this.speedModifier = var2;
      this.interval = var4;
      this.checkNoActionTime = var5;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.hasControllingPassenger()) {
         return false;
      } else {
         if (!this.forceTrigger) {
            if (this.checkNoActionTime && this.mob.getNoActionTime() >= 100) {
               return false;
            }

            if (this.mob.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
               return false;
            }
         }

         Vec3 var1 = this.getPosition();
         if (var1 == null) {
            return false;
         } else {
            this.wantedX = var1.x;
            this.wantedY = var1.y;
            this.wantedZ = var1.z;
            this.forceTrigger = false;
            return true;
         }
      }
   }

   @Nullable
   protected Vec3 getPosition() {
      return DefaultRandomPos.getPos(this.mob, 10, 7);
   }

   public boolean canContinueToUse() {
      return !this.mob.getNavigation().isDone() && !this.mob.hasControllingPassenger();
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
   }

   public void stop() {
      this.mob.getNavigation().stop();
      super.stop();
   }

   public void trigger() {
      this.forceTrigger = true;
   }

   public void setInterval(int var1) {
      this.interval = var1;
   }
}
