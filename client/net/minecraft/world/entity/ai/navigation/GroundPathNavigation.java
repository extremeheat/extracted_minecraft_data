package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class GroundPathNavigation extends PathNavigation {
   private boolean avoidSun;
   private boolean canPathToTargetsBelowSurface;

   public GroundPathNavigation(Mob var1, Level var2) {
      super(var1, var2);
   }

   protected PathFinder createPathFinder(int var1) {
      this.nodeEvaluator = new WalkNodeEvaluator();
      return new PathFinder(this.nodeEvaluator, var1);
   }

   protected boolean canUpdatePath() {
      return this.mob.onGround() || this.mob.isInLiquid() || this.mob.isPassenger();
   }

   protected Vec3 getTempMobPos() {
      return new Vec3(this.mob.getX(), (double)this.getSurfaceY(), this.mob.getZ());
   }

   public Path createPath(BlockPos var1, int var2) {
      LevelChunk var3 = this.level.getChunkSource().getChunkNow(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ()));
      if (var3 == null) {
         return null;
      } else {
         if (!this.canPathToTargetsBelowSurface) {
            var1 = this.findSurfacePosition(var3, var1, var2);
         }

         return super.createPath(var1, var2);
      }
   }

   final BlockPos findSurfacePosition(LevelChunk var1, BlockPos var2, int var3) {
      if (var1.getBlockState((BlockPos)var2).isAir()) {
         BlockPos.MutableBlockPos var4 = ((BlockPos)var2).mutable().move(Direction.DOWN);

         while(var4.getY() >= this.level.getMinY() && var1.getBlockState(var4).isAir()) {
            var4.move(Direction.DOWN);
         }

         if (var4.getY() >= this.level.getMinY()) {
            return var4.above();
         }

         var4.setY(((BlockPos)var2).getY() + 1);

         while(var4.getY() <= this.level.getMaxY() && var1.getBlockState(var4).isAir()) {
            var4.move(Direction.UP);
         }

         var2 = var4;
      }

      if (!var1.getBlockState((BlockPos)var2).isSolid()) {
         return (BlockPos)var2;
      } else {
         BlockPos.MutableBlockPos var5 = ((BlockPos)var2).mutable().move(Direction.UP);

         while(var5.getY() <= this.level.getMaxY() && var1.getBlockState(var5).isSolid()) {
            var5.move(Direction.UP);
         }

         return var5.immutable();
      }
   }

   public Path createPath(Entity var1, int var2) {
      return this.createPath(var1.blockPosition(), var2);
   }

   private int getSurfaceY() {
      if (this.mob.isInWater() && this.canFloat()) {
         int var1 = this.mob.getBlockY();
         BlockState var2 = this.level.getBlockState(BlockPos.containing(this.mob.getX(), (double)var1, this.mob.getZ()));
         int var3 = 0;

         while(var2.is(Blocks.WATER)) {
            ++var1;
            var2 = this.level.getBlockState(BlockPos.containing(this.mob.getX(), (double)var1, this.mob.getZ()));
            ++var3;
            if (var3 > 16) {
               return this.mob.getBlockY();
            }
         }

         return var1;
      } else {
         return Mth.floor(this.mob.getY() + 0.5);
      }
   }

   protected void trimPath() {
      super.trimPath();
      if (this.avoidSun) {
         if (this.level.canSeeSky(BlockPos.containing(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()))) {
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

   public boolean canNavigateGround() {
      return true;
   }

   protected boolean hasValidPathType(PathType var1) {
      if (var1 == PathType.WATER) {
         return false;
      } else if (var1 == PathType.LAVA) {
         return false;
      } else {
         return var1 != PathType.OPEN;
      }
   }

   public void setAvoidSun(boolean var1) {
      this.avoidSun = var1;
   }

   public void setCanWalkOverFences(boolean var1) {
      this.nodeEvaluator.setCanWalkOverFences(var1);
   }

   public void setCanPathToTargetsBelowSurface(boolean var1) {
      this.canPathToTargetsBelowSurface = var1;
   }
}
