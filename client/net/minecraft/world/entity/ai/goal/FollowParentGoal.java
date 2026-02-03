package net.minecraft.world.entity.ai.goal;

import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import org.jspecify.annotations.Nullable;

public class FollowParentGoal extends Goal {
   public static final int HORIZONTAL_SCAN_RANGE = 8;
   public static final int VERTICAL_SCAN_RANGE = 4;
   public static final int DONT_FOLLOW_IF_CLOSER_THAN = 3;
   private final Animal animal;
   private @Nullable Animal parent;
   private final double speedModifier;
   private int timeToRecalcPath;

   public FollowParentGoal(final Animal animal, final double speedModifier) {
      super();
      this.animal = animal;
      this.speedModifier = speedModifier;
   }

   public boolean canUse() {
      if (this.animal.getAge() >= 0) {
         return false;
      } else {
         List<? extends Animal> parents = this.animal.level().getEntitiesOfClass(this.animal.getClass(), this.animal.getBoundingBox().inflate(8.0, 4.0, 8.0));
         Animal closest = null;
         double closestDistSqr = 1.7976931348623157E308;

         for(Animal parent : parents) {
            if (parent.getAge() >= 0) {
               double distSqr = this.animal.distanceToSqr(parent);
               if (!(distSqr > closestDistSqr)) {
                  closestDistSqr = distSqr;
                  closest = parent;
               }
            }
         }

         if (closest == null) {
            return false;
         } else if (closestDistSqr < 9.0) {
            return false;
         } else {
            this.parent = closest;
            return true;
         }
      }
   }

   public boolean canContinueToUse() {
      if (this.animal.getAge() >= 0) {
         return false;
      } else if (!this.parent.isAlive()) {
         return false;
      } else {
         double distSqr = this.animal.distanceToSqr(this.parent);
         return !(distSqr < 9.0) && !(distSqr > 256.0);
      }
   }

   public void start() {
      this.timeToRecalcPath = 0;
   }

   public void stop() {
      this.parent = null;
   }

   public void tick() {
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = this.adjustedTickDelay(10);
         this.animal.getNavigation().moveTo((Entity)this.parent, this.speedModifier);
      }
   }
}
