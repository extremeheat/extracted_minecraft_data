package net.minecraft.world.entity.ai.goal;

import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;

public class FollowParentGoal extends Goal {
   private final Animal animal;
   private Animal parent;
   private final double speedModifier;
   private int timeToRecalcPath;

   public FollowParentGoal(Animal var1, double var2) {
      this.animal = var1;
      this.speedModifier = var2;
   }

   public boolean canUse() {
      if (this.animal.getAge() >= 0) {
         return false;
      } else {
         List var1 = this.animal.level.getEntitiesOfClass(this.animal.getClass(), this.animal.getBoundingBox().inflate(8.0D, 4.0D, 8.0D));
         Animal var2 = null;
         double var3 = Double.MAX_VALUE;
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            Animal var6 = (Animal)var5.next();
            if (var6.getAge() >= 0) {
               double var7 = this.animal.distanceToSqr(var6);
               if (var7 <= var3) {
                  var3 = var7;
                  var2 = var6;
               }
            }
         }

         if (var2 == null) {
            return false;
         } else if (var3 < 9.0D) {
            return false;
         } else {
            this.parent = var2;
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
         double var1 = this.animal.distanceToSqr(this.parent);
         return var1 >= 9.0D && var1 <= 256.0D;
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
         this.timeToRecalcPath = 10;
         this.animal.getNavigation().moveTo((Entity)this.parent, this.speedModifier);
      }
   }
}
