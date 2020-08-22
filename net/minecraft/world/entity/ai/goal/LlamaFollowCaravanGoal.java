package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.phys.Vec3;

public class LlamaFollowCaravanGoal extends Goal {
   public final Llama llama;
   private double speedModifier;
   private int distCheckCounter;

   public LlamaFollowCaravanGoal(Llama var1, double var2) {
      this.llama = var1;
      this.speedModifier = var2;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (!this.llama.isLeashed() && !this.llama.inCaravan()) {
         List var1 = this.llama.level.getEntities((Entity)this.llama, this.llama.getBoundingBox().inflate(9.0D, 4.0D, 9.0D), (var0) -> {
            EntityType var1 = var0.getType();
            return var1 == EntityType.LLAMA || var1 == EntityType.TRADER_LLAMA;
         });
         Llama var2 = null;
         double var3 = Double.MAX_VALUE;
         Iterator var5 = var1.iterator();

         Entity var6;
         Llama var7;
         double var8;
         while(var5.hasNext()) {
            var6 = (Entity)var5.next();
            var7 = (Llama)var6;
            if (var7.inCaravan() && !var7.hasCaravanTail()) {
               var8 = this.llama.distanceToSqr(var7);
               if (var8 <= var3) {
                  var3 = var8;
                  var2 = var7;
               }
            }
         }

         if (var2 == null) {
            var5 = var1.iterator();

            while(var5.hasNext()) {
               var6 = (Entity)var5.next();
               var7 = (Llama)var6;
               if (var7.isLeashed() && !var7.hasCaravanTail()) {
                  var8 = this.llama.distanceToSqr(var7);
                  if (var8 <= var3) {
                     var3 = var8;
                     var2 = var7;
                  }
               }
            }
         }

         if (var2 == null) {
            return false;
         } else if (var3 < 4.0D) {
            return false;
         } else if (!var2.isLeashed() && !this.firstIsLeashed(var2, 1)) {
            return false;
         } else {
            this.llama.joinCaravan(var2);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      if (this.llama.inCaravan() && this.llama.getCaravanHead().isAlive() && this.firstIsLeashed(this.llama, 0)) {
         double var1 = this.llama.distanceToSqr(this.llama.getCaravanHead());
         if (var1 > 676.0D) {
            if (this.speedModifier <= 3.0D) {
               this.speedModifier *= 1.2D;
               this.distCheckCounter = 40;
               return true;
            }

            if (this.distCheckCounter == 0) {
               return false;
            }
         }

         if (this.distCheckCounter > 0) {
            --this.distCheckCounter;
         }

         return true;
      } else {
         return false;
      }
   }

   public void stop() {
      this.llama.leaveCaravan();
      this.speedModifier = 2.1D;
   }

   public void tick() {
      if (this.llama.inCaravan()) {
         Llama var1 = this.llama.getCaravanHead();
         double var2 = (double)this.llama.distanceTo(var1);
         float var4 = 2.0F;
         Vec3 var5 = (new Vec3(var1.getX() - this.llama.getX(), var1.getY() - this.llama.getY(), var1.getZ() - this.llama.getZ())).normalize().scale(Math.max(var2 - 2.0D, 0.0D));
         this.llama.getNavigation().moveTo(this.llama.getX() + var5.x, this.llama.getY() + var5.y, this.llama.getZ() + var5.z, this.speedModifier);
      }
   }

   private boolean firstIsLeashed(Llama var1, int var2) {
      if (var2 > 8) {
         return false;
      } else if (var1.inCaravan()) {
         if (var1.getCaravanHead().isLeashed()) {
            return true;
         } else {
            Llama var10001 = var1.getCaravanHead();
            ++var2;
            return this.firstIsLeashed(var10001, var2);
         }
      } else {
         return false;
      }
   }
}
