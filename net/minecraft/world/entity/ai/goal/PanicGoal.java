package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class PanicGoal extends Goal {
   protected final PathfinderMob mob;
   protected final double speedModifier;
   protected double posX;
   protected double posY;
   protected double posZ;

   public PanicGoal(PathfinderMob var1, double var2) {
      this.mob = var1;
      this.speedModifier = var2;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.getLastHurtByMob() == null && !this.mob.isOnFire()) {
         return false;
      } else {
         if (this.mob.isOnFire()) {
            BlockPos var1 = this.lookForWater(this.mob.level, this.mob, 5, 4);
            if (var1 != null) {
               this.posX = (double)var1.getX();
               this.posY = (double)var1.getY();
               this.posZ = (double)var1.getZ();
               return true;
            }
         }

         return this.findRandomPosition();
      }
   }

   protected boolean findRandomPosition() {
      Vec3 var1 = RandomPos.getPos(this.mob, 5, 4);
      if (var1 == null) {
         return false;
      } else {
         this.posX = var1.x;
         this.posY = var1.y;
         this.posZ = var1.z;
         return true;
      }
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
   }

   public boolean canContinueToUse() {
      return !this.mob.getNavigation().isDone();
   }

   @Nullable
   protected BlockPos lookForWater(BlockGetter var1, Entity var2, int var3, int var4) {
      BlockPos var5 = new BlockPos(var2);
      int var6 = var5.getX();
      int var7 = var5.getY();
      int var8 = var5.getZ();
      float var9 = (float)(var3 * var3 * var4 * 2);
      BlockPos var10 = null;
      BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

      for(int var12 = var6 - var3; var12 <= var6 + var3; ++var12) {
         for(int var13 = var7 - var4; var13 <= var7 + var4; ++var13) {
            for(int var14 = var8 - var3; var14 <= var8 + var3; ++var14) {
               var11.set(var12, var13, var14);
               if (var1.getFluidState(var11).is(FluidTags.WATER)) {
                  float var15 = (float)((var12 - var6) * (var12 - var6) + (var13 - var7) * (var13 - var7) + (var14 - var8) * (var14 - var8));
                  if (var15 < var9) {
                     var9 = var15;
                     var10 = new BlockPos(var11);
                  }
               }
            }
         }
      }

      return var10;
   }
}
