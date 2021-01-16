package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class FlyingPathNavigation extends PathNavigation {
   public FlyingPathNavigation(Mob var1, Level var2) {
      super(var1, var2);
   }

   protected PathFinder createPathFinder(int var1) {
      this.nodeEvaluator = new FlyNodeEvaluator();
      this.nodeEvaluator.setCanPassDoors(true);
      return new PathFinder(this.nodeEvaluator, var1);
   }

   protected boolean canUpdatePath() {
      return this.canFloat() && this.isInLiquid() || !this.mob.isPassenger();
   }

   protected Vec3 getTempMobPos() {
      return this.mob.position();
   }

   public Path createPath(Entity var1, int var2) {
      return this.createPath(var1.blockPosition(), var2);
   }

   public void tick() {
      ++this.tick;
      if (this.hasDelayedRecomputation) {
         this.recomputePath();
      }

      if (!this.isDone()) {
         Vec3 var1;
         if (this.canUpdatePath()) {
            this.followThePath();
         } else if (this.path != null && !this.path.isDone()) {
            var1 = this.path.getNextEntityPos(this.mob);
            if (Mth.floor(this.mob.getX()) == Mth.floor(var1.x) && Mth.floor(this.mob.getY()) == Mth.floor(var1.y) && Mth.floor(this.mob.getZ()) == Mth.floor(var1.z)) {
               this.path.advance();
            }
         }

         DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
         if (!this.isDone()) {
            var1 = this.path.getNextEntityPos(this.mob);
            this.mob.getMoveControl().setWantedPosition(var1.x, var1.y, var1.z, this.speedModifier);
         }
      }
   }

   protected boolean canMoveDirectly(Vec3 var1, Vec3 var2, int var3, int var4, int var5) {
      int var6 = Mth.floor(var1.x);
      int var7 = Mth.floor(var1.y);
      int var8 = Mth.floor(var1.z);
      double var9 = var2.x - var1.x;
      double var11 = var2.y - var1.y;
      double var13 = var2.z - var1.z;
      double var15 = var9 * var9 + var11 * var11 + var13 * var13;
      if (var15 < 1.0E-8D) {
         return false;
      } else {
         double var17 = 1.0D / Math.sqrt(var15);
         var9 *= var17;
         var11 *= var17;
         var13 *= var17;
         double var19 = 1.0D / Math.abs(var9);
         double var21 = 1.0D / Math.abs(var11);
         double var23 = 1.0D / Math.abs(var13);
         double var25 = (double)var6 - var1.x;
         double var27 = (double)var7 - var1.y;
         double var29 = (double)var8 - var1.z;
         if (var9 >= 0.0D) {
            ++var25;
         }

         if (var11 >= 0.0D) {
            ++var27;
         }

         if (var13 >= 0.0D) {
            ++var29;
         }

         var25 /= var9;
         var27 /= var11;
         var29 /= var13;
         int var31 = var9 < 0.0D ? -1 : 1;
         int var32 = var11 < 0.0D ? -1 : 1;
         int var33 = var13 < 0.0D ? -1 : 1;
         int var34 = Mth.floor(var2.x);
         int var35 = Mth.floor(var2.y);
         int var36 = Mth.floor(var2.z);
         int var37 = var34 - var6;
         int var38 = var35 - var7;
         int var39 = var36 - var8;

         while(true) {
            while(var37 * var31 > 0 || var38 * var32 > 0 || var39 * var33 > 0) {
               if (var25 < var29 && var25 <= var27) {
                  var25 += var19;
                  var6 += var31;
                  var37 = var34 - var6;
               } else if (var27 < var25 && var27 <= var29) {
                  var27 += var21;
                  var7 += var32;
                  var38 = var35 - var7;
               } else {
                  var29 += var23;
                  var8 += var33;
                  var39 = var36 - var8;
               }
            }

            return true;
         }
      }
   }

   public void setCanOpenDoors(boolean var1) {
      this.nodeEvaluator.setCanOpenDoors(var1);
   }

   public void setCanPassDoors(boolean var1) {
      this.nodeEvaluator.setCanPassDoors(var1);
   }

   public boolean isStableDestination(BlockPos var1) {
      return this.level.getBlockState(var1).entityCanStandOn(this.level, var1, this.mob);
   }
}
