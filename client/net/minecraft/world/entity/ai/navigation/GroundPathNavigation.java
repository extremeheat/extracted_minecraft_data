package net.minecraft.world.entity.ai.navigation;

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
         return Mth.floor(this.mob.getY() + 0.5);
      }
   }

   protected void trimPath() {
      super.trimPath();
      if (this.avoidSun) {
         if (this.level.canSeeSky(new BlockPos(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()))) {
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

   protected boolean hasValidPathType(BlockPathTypes var1) {
      if (var1 == BlockPathTypes.WATER) {
         return false;
      } else if (var1 == BlockPathTypes.LAVA) {
         return false;
      } else {
         return var1 != BlockPathTypes.OPEN;
      }
   }

   public void setCanOpenDoors(boolean var1) {
      this.nodeEvaluator.setCanOpenDoors(var1);
   }

   public boolean canPassDoors() {
      return this.nodeEvaluator.canPassDoors();
   }

   public void setCanPassDoors(boolean var1) {
      this.nodeEvaluator.setCanPassDoors(var1);
   }

   public boolean canOpenDoors() {
      return this.nodeEvaluator.canPassDoors();
   }

   public void setAvoidSun(boolean var1) {
      this.avoidSun = var1;
   }
}
