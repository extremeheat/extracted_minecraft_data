package net.minecraft.world.entity.ai.goal;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.animal.Animal;

public class FollowParentGoal extends Goal {
   public static final int HORIZONTAL_SCAN_RANGE = 8;
   public static final int VERTICAL_SCAN_RANGE = 4;
   public static final int DONT_FOLLOW_IF_CLOSER_THAN = 3;
   private final Animal animal;
   @Nullable
   private Animal parent;
   private final double speedModifier;
   private int timeToRecalcPath;

   public FollowParentGoal(Animal var1, double var2) {
      super();
      this.animal = var1;
      this.speedModifier = var2;
   }

   @Override
   public boolean canUse() {
      if (this.animal.getAge() >= 0) {
         return false;
      } else {
         List var1 = this.animal.level.getEntitiesOfClass(this.animal.getClass(), this.animal.getBoundingBox().inflate(8.0, 4.0, 8.0));
         Animal var2 = null;
         double var3 = 1.7976931348623157E308;

         for(Animal var6 : var1) {
            if (var6.getAge() >= 0) {
               double var7 = this.animal.distanceToSqr(var6);
               if (!(var7 > var3)) {
                  var3 = var7;
                  var2 = var6;
               }
            }
         }

         if (var2 == null) {
            return false;
         } else if (var3 < 9.0) {
            return false;
         } else {
            this.parent = var2;
            return true;
         }
      }
   }

   @Override
   public boolean canContinueToUse() {
      if (this.animal.getAge() >= 0) {
         return false;
      } else if (!this.parent.isAlive()) {
         return false;
      } else {
         double var1 = this.animal.distanceToSqr(this.parent);
         return !(var1 < 9.0) && !(var1 > 256.0);
      }
   }

   @Override
   public void start() {
      this.timeToRecalcPath = 0;
   }

   @Override
   public void stop() {
      this.parent = null;
   }

   @Override
   public void tick() {
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = this.adjustedTickDelay(10);
         this.animal.getNavigation().moveTo(this.parent, this.speedModifier);
      }
   }
}
