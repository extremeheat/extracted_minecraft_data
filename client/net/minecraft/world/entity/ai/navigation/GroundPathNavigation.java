package net.minecraft.world.entity.ai.navigation;

import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class GroundPathNavigation extends PathNavigation {
   private boolean avoidSun;

   public GroundPathNavigation(Mob var1, Level var2) {
      super(var1, var2);
   }

   protected PathFinder createPathFinder(int var1) {
      this.nodeEvaluator = new WalkNodeEvaluator();
      this.nodeEvaluator.setCanPassDoors(true);
      return new PathFinder(this.nodeEvaluator, var1);
   }

   protected boolean canUpdatePath() {
      return this.mob.isOnGround() || this.isInLiquid() || this.mob.isPassenger();
   }

   protected Vec3 getTempMobPos() {
      return new Vec3(this.mob.getX(), (double)this.getSurfaceY(), this.mob.getZ());
   }

   public Path createPath(BlockPos var1, int var2) {
      BlockPos var3;
      if (this.level.getBlockState(var1).isAir()) {
         for(var3 = var1.below(); var3.getY() > this.level.getMinBuildHeight() && this.level.getBlockState(var3).isAir(); var3 = var3.below()) {
         }

         if (var3.getY() > this.level.getMinBuildHeight()) {
            return super.createPath(var3.above(), var2);
         }

         while(var3.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState(var3).isAir()) {
            var3 = var3.above();
         }

         var1 = var3;
      }

      if (!this.level.getBlockState(var1).getMaterial().isSolid()) {
         return super.createPath(var1, var2);
      } else {
         for(var3 = var1.above(); var3.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState(var3).getMaterial().isSolid(); var3 = var3.above()) {
         }

         return super.createPath(var3, var2);
      }
   }

   public Path createPath(Entity var1, int var2) {
      return this.createPath(var1.blockPosition(), var2);
   }

   private int getSurfaceY() {
      if (this.mob.isInWater() && this.canFloat()) {
         int var1 = this.mob.getBlockY();
         BlockState var2 = this.level.getBlockState(new BlockPos(this.mob.getX(), (double)var1, this.mob.getZ()));
         int var3 = 0;

         do {
            if (!var2.is(Blocks.WATER)) {
               return var1;
            }

            ++var1;
            var2 = this.level.getBlockState(new BlockPos(this.mob.getX(), (double)var1, this.mob.getZ()));
            ++var3;
         } while(var3 <= 16);

         return this.mob.getBlockY();
      } else {
         return Mth.floor(this.mob.getY() + 0.5D);
      }
   }

   protected void trimPath() {
      super.trimPath();
      if (this.avoidSun) {
         if (this.level.canSeeSky(new BlockPos(this.mob.getX(), this.mob.getY() + 0.5D, this.mob.getZ()))) {
            return;
         }

         for(int var1 = 0; var1 < this.path.getNodeCount(); ++var1) {
            Node var2 = this.path.getNode(var1);
            if (this.level.canSeeSky(new BlockPos(var2.x, var2.y, var2.z))) {
               this.path.truncateNodes(var1);
               return;
            }
         }
      }

   }

   protected boolean canMoveDirectly(Vec3 var1, Vec3 var2, int var3, int var4, int var5) {
      int var6 = Mth.floor(var1.x);
      int var7 = Mth.floor(var1.z);
      double var8 = var2.x - var1.x;
      double var10 = var2.z - var1.z;
      double var12 = var8 * var8 + var10 * var10;
      if (var12 < 1.0E-8D) {
         return false;
      } else {
         double var14 = 1.0D / Math.sqrt(var12);
         var8 *= var14;
         var10 *= var14;
         var3 += 2;
         var5 += 2;
         if (!this.canWalkOn(var6, Mth.floor(var1.y), var7, var3, var4, var5, var1, var8, var10)) {
            return false;
         } else {
            var3 -= 2;
            var5 -= 2;
            double var16 = 1.0D / Math.abs(var8);
            double var18 = 1.0D / Math.abs(var10);
            double var20 = (double)var6 - var1.x;
            double var22 = (double)var7 - var1.z;
            if (var8 >= 0.0D) {
               ++var20;
            }

            if (var10 >= 0.0D) {
               ++var22;
            }

            var20 /= var8;
            var22 /= var10;
            int var24 = var8 < 0.0D ? -1 : 1;
            int var25 = var10 < 0.0D ? -1 : 1;
            int var26 = Mth.floor(var2.x);
            int var27 = Mth.floor(var2.z);
            int var28 = var26 - var6;
            int var29 = var27 - var7;

            do {
               if (var28 * var24 <= 0 && var29 * var25 <= 0) {
                  return true;
               }

               if (var20 < var22) {
                  var20 += var16;
                  var6 += var24;
                  var28 = var26 - var6;
               } else {
                  var22 += var18;
                  var7 += var25;
                  var29 = var27 - var7;
               }
            } while(this.canWalkOn(var6, Mth.floor(var1.y), var7, var3, var4, var5, var1, var8, var10));

            return false;
         }
      }
   }

   private boolean canWalkOn(int var1, int var2, int var3, int var4, int var5, int var6, Vec3 var7, double var8, double var10) {
      int var12 = var1 - var4 / 2;
      int var13 = var3 - var6 / 2;
      if (!this.canWalkAbove(var12, var2, var13, var4, var5, var6, var7, var8, var10)) {
         return false;
      } else {
         for(int var14 = var12; var14 < var12 + var4; ++var14) {
            for(int var15 = var13; var15 < var13 + var6; ++var15) {
               double var16 = (double)var14 + 0.5D - var7.x;
               double var18 = (double)var15 + 0.5D - var7.z;
               if (var16 * var8 + var18 * var10 >= 0.0D) {
                  BlockPathTypes var20 = this.nodeEvaluator.getBlockPathType(this.level, var14, var2 - 1, var15, this.mob, var4, var5, var6, true, true);
                  if (!this.hasValidPathType(var20)) {
                     return false;
                  }

                  var20 = this.nodeEvaluator.getBlockPathType(this.level, var14, var2, var15, this.mob, var4, var5, var6, true, true);
                  float var21 = this.mob.getPathfindingMalus(var20);
                  if (var21 < 0.0F || var21 >= 8.0F) {
                     return false;
                  }

                  if (var20 == BlockPathTypes.DAMAGE_FIRE || var20 == BlockPathTypes.DANGER_FIRE || var20 == BlockPathTypes.DAMAGE_OTHER) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   protected boolean hasValidPathType(BlockPathTypes var1) {
      if (var1 == BlockPathTypes.WATER) {
         return false;
      } else if (var1 == BlockPathTypes.LAVA) {
         return false;
      } else {
         return var1 != BlockPathTypes.OPEN;
      }
   }

   private boolean canWalkAbove(int var1, int var2, int var3, int var4, int var5, int var6, Vec3 var7, double var8, double var10) {
      Iterator var12 = BlockPos.betweenClosed(new BlockPos(var1, var2, var3), new BlockPos(var1 + var4 - 1, var2 + var5 - 1, var3 + var6 - 1)).iterator();

      BlockPos var13;
      double var14;
      double var16;
      do {
         if (!var12.hasNext()) {
            return true;
         }

         var13 = (BlockPos)var12.next();
         var14 = (double)var13.getX() + 0.5D - var7.x;
         var16 = (double)var13.getZ() + 0.5D - var7.z;
      } while(var14 * var8 + var16 * var10 < 0.0D || this.level.getBlockState(var13).isPathfindable(this.level, var13, PathComputationType.LAND));

      return false;
   }

   public void setCanOpenDoors(boolean var1) {
      this.nodeEvaluator.setCanOpenDoors(var1);
   }

   public boolean canOpenDoors() {
      return this.nodeEvaluator.canPassDoors();
   }

   public void setAvoidSun(boolean var1) {
      this.avoidSun = var1;
   }
}
