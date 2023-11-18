package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.phys.Vec3;

public class LlamaFollowCaravanGoal extends Goal {
   public final Llama llama;
   private double speedModifier;
   private static final int CARAVAN_LIMIT = 8;
   private int distCheckCounter;

   public LlamaFollowCaravanGoal(Llama var1, double var2) {
      super();
      this.llama = var1;
      this.speedModifier = var2;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   @Override
   public boolean canUse() {
      if (!this.llama.isLeashed() && !this.llama.inCaravan()) {
         List var1 = this.llama.level().getEntities(this.llama, this.llama.getBoundingBox().inflate(9.0, 4.0, 9.0), var0 -> {
            EntityType var1x = var0.getType();
            return var1x == EntityType.LLAMA || var1x == EntityType.TRADER_LLAMA;
         });
         Llama var2 = null;
         double var3 = 1.7976931348623157E308;

         for(Entity var6 : var1) {
            Llama var7 = (Llama)var6;
            if (var7.inCaravan() && !var7.hasCaravanTail()) {
               double var8 = this.llama.distanceToSqr(var7);
               if (!(var8 > var3)) {
                  var3 = var8;
                  var2 = var7;
               }
            }
         }

         if (var2 == null) {
            for(Entity var11 : var1) {
               Llama var12 = (Llama)var11;
               if (var12.isLeashed() && !var12.hasCaravanTail()) {
                  double var13 = this.llama.distanceToSqr(var12);
                  if (!(var13 > var3)) {
                     var3 = var13;
                     var2 = var12;
                  }
               }
            }
         }

         if (var2 == null) {
            return false;
         } else if (var3 < 4.0) {
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

   @Override
   public boolean canContinueToUse() {
      if (this.llama.inCaravan() && this.llama.getCaravanHead().isAlive() && this.firstIsLeashed(this.llama, 0)) {
         double var1 = this.llama.distanceToSqr(this.llama.getCaravanHead());
         if (var1 > 676.0) {
            if (this.speedModifier <= 3.0) {
               this.speedModifier *= 1.2;
               this.distCheckCounter = reducedTickDelay(40);
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

   @Override
   public void stop() {
      this.llama.leaveCaravan();
      this.speedModifier = 2.1;
   }

   @Override
   public void tick() {
      if (this.llama.inCaravan()) {
         if (!(this.llama.getLeashHolder() instanceof LeashFenceKnotEntity)) {
            Llama var1 = this.llama.getCaravanHead();
            double var2 = (double)this.llama.distanceTo(var1);
            float var4 = 2.0F;
            Vec3 var5 = new Vec3(var1.getX() - this.llama.getX(), var1.getY() - this.llama.getY(), var1.getZ() - this.llama.getZ())
               .normalize()
               .scale(Math.max(var2 - 2.0, 0.0));
            this.llama.getNavigation().moveTo(this.llama.getX() + var5.x, this.llama.getY() + var5.y, this.llama.getZ() + var5.z, this.speedModifier);
         }
      }
   }

   private boolean firstIsLeashed(Llama var1, int var2) {
      if (var2 > 8) {
         return false;
      } else if (var1.inCaravan()) {
         return var1.getCaravanHead().isLeashed() ? true : this.firstIsLeashed(var1.getCaravanHead(), ++var2);
      } else {
         return false;
      }
   }
}
