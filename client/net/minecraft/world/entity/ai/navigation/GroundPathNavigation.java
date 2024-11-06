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
      LevelChunk var3 = this.level.getChunkSource().getChunkNow(SectionPos.blockToSectionCoord(((BlockPos)var1).getX()), SectionPos.blockToSectionCoord(((BlockPos)var1).getZ()));
      if (var3 == null) {
         return null;
      } else {
         BlockPos.MutableBlockPos var4;
         if (var3.getBlockState((BlockPos)var1).isAir()) {
            var4 = ((BlockPos)var1).mutable().move(Direction.DOWN);

            while(var4.getY() > this.level.getMinY() && var3.getBlockState(var4).isAir()) {
               var4.move(Direction.DOWN);
            }

            if (var4.getY() > this.level.getMinY()) {
               return super.createPath(var4.above(), var2);
            }

            var4.setY(((BlockPos)var1).getY() + 1);

            while(var4.getY() <= this.level.getMaxY() && var3.getBlockState(var4).isAir()) {
               var4.move(Direction.UP);
            }

            var1 = var4;
         }

         if (!var3.getBlockState((BlockPos)var1).isSolid()) {
            return super.createPath((BlockPos)var1, var2);
         } else {
            var4 = ((BlockPos)var1).mutable().move(Direction.UP);

            while(var4.getY() <= this.level.getMaxY() && var3.getBlockState(var4).isSolid()) {
               var4.move(Direction.UP);
            }

            return super.createPath(var4.immutable(), var2);
         }
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

         do {
            if (!var2.is(Blocks.WATER)) {
               return var1;
            }

            ++var1;
            var2 = this.level.getBlockState(BlockPos.containing(this.mob.getX(), (double)var1, this.mob.getZ()));
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

   protected boolean hasValidPathType(PathType var1) {
      if (var1 == PathType.WATER) {
         return false;
      } else if (var1 == PathType.LAVA) {
         return false;
      } else {
         return var1 != PathType.OPEN;
      }
   }

   public void setCanOpenDoors(boolean var1) {
      this.nodeEvaluator.setCanOpenDoors(var1);
   }

   public void setAvoidSun(boolean var1) {
      this.avoidSun = var1;
   }

   public void setCanWalkOverFences(boolean var1) {
      this.nodeEvaluator.setCanWalkOverFences(var1);
   }
}
