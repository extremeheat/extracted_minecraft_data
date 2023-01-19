package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;

public class AmphibiousNodeEvaluator extends WalkNodeEvaluator {
   private final boolean prefersShallowSwimming;
   private float oldWalkableCost;
   private float oldWaterBorderCost;

   public AmphibiousNodeEvaluator(boolean var1) {
      super();
      this.prefersShallowSwimming = var1;
   }

   @Override
   public void prepare(PathNavigationRegion var1, Mob var2) {
      super.prepare(var1, var2);
      var2.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
      this.oldWalkableCost = var2.getPathfindingMalus(BlockPathTypes.WALKABLE);
      var2.setPathfindingMalus(BlockPathTypes.WALKABLE, 6.0F);
      this.oldWaterBorderCost = var2.getPathfindingMalus(BlockPathTypes.WATER_BORDER);
      var2.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 4.0F);
   }

   @Override
   public void done() {
      this.mob.setPathfindingMalus(BlockPathTypes.WALKABLE, this.oldWalkableCost);
      this.mob.setPathfindingMalus(BlockPathTypes.WATER_BORDER, this.oldWaterBorderCost);
      super.done();
   }

   @Nullable
   @Override
   public Node getStart() {
      return this.getStartNode(
         new BlockPos(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5), Mth.floor(this.mob.getBoundingBox().minZ))
      );
   }

   @Nullable
   @Override
   public Target getGoal(double var1, double var3, double var5) {
      return this.getTargetFromNode(this.getNode(Mth.floor(var1), Mth.floor(var3 + 0.5), Mth.floor(var5)));
   }

   @Override
   public int getNeighbors(Node[] var1, Node var2) {
      int var3 = super.getNeighbors(var1, var2);
      BlockPathTypes var5 = this.getCachedBlockType(this.mob, var2.x, var2.y + 1, var2.z);
      BlockPathTypes var6 = this.getCachedBlockType(this.mob, var2.x, var2.y, var2.z);
      int var4;
      if (this.mob.getPathfindingMalus(var5) >= 0.0F && var6 != BlockPathTypes.STICKY_HONEY) {
         var4 = Mth.floor(Math.max(1.0F, this.mob.maxUpStep));
      } else {
         var4 = 0;
      }

      double var7 = this.getFloorLevel(new BlockPos(var2.x, var2.y, var2.z));
      Node var9 = this.findAcceptedNode(var2.x, var2.y + 1, var2.z, Math.max(0, var4 - 1), var7, Direction.UP, var6);
      Node var10 = this.findAcceptedNode(var2.x, var2.y - 1, var2.z, var4, var7, Direction.DOWN, var6);
      if (this.isVerticalNeighborValid(var9, var2)) {
         var1[var3++] = var9;
      }

      if (this.isVerticalNeighborValid(var10, var2) && var6 != BlockPathTypes.TRAPDOOR) {
         var1[var3++] = var10;
      }

      for(int var11 = 0; var11 < var3; ++var11) {
         Node var12 = var1[var11];
         if (var12.type == BlockPathTypes.WATER && this.prefersShallowSwimming && var12.y < this.mob.level.getSeaLevel() - 10) {
            ++var12.costMalus;
         }
      }

      return var3;
   }

   private boolean isVerticalNeighborValid(@Nullable Node var1, Node var2) {
      return this.isNeighborValid(var1, var2) && var1.type == BlockPathTypes.WATER;
   }

   @Override
   protected double getFloorLevel(BlockPos var1) {
      return this.mob.isInWater() ? (double)var1.getY() + 0.5 : super.getFloorLevel(var1);
   }

   @Override
   protected boolean isAmphibious() {
      return true;
   }

   @Override
   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4) {
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();
      BlockPathTypes var6 = getBlockPathTypeRaw(var1, var5.set(var2, var3, var4));
      if (var6 == BlockPathTypes.WATER) {
         for(Direction var10 : Direction.values()) {
            BlockPathTypes var11 = getBlockPathTypeRaw(var1, var5.set(var2, var3, var4).move(var10));
            if (var11 == BlockPathTypes.BLOCKED) {
               return BlockPathTypes.WATER_BORDER;
            }
         }

         return BlockPathTypes.WATER;
      } else {
         return getBlockPathTypeStatic(var1, var5);
      }
   }
}
