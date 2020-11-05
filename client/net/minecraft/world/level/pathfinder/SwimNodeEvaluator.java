package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SwimNodeEvaluator extends NodeEvaluator {
   private final boolean allowBreaching;

   public SwimNodeEvaluator(boolean var1) {
      super();
      this.allowBreaching = var1;
   }

   public Node getStart() {
      return super.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5D), Mth.floor(this.mob.getBoundingBox().minZ));
   }

   public Target getGoal(double var1, double var3, double var5) {
      return new Target(super.getNode(Mth.floor(var1 - (double)(this.mob.getBbWidth() / 2.0F)), Mth.floor(var3 + 0.5D), Mth.floor(var5 - (double)(this.mob.getBbWidth() / 2.0F))));
   }

   public int getNeighbors(Node[] var1, Node var2) {
      int var3 = 0;
      Direction[] var4 = Direction.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction var7 = var4[var6];
         Node var8 = this.getWaterNode(var2.x + var7.getStepX(), var2.y + var7.getStepY(), var2.z + var7.getStepZ());
         if (var8 != null && !var8.closed) {
            var1[var3++] = var8;
         }
      }

      return var3;
   }

   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4, Mob var5, int var6, int var7, int var8, boolean var9, boolean var10) {
      return this.getBlockPathType(var1, var2, var3, var4);
   }

   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4) {
      BlockPos var5 = new BlockPos(var2, var3, var4);
      FluidState var6 = var1.getFluidState(var5);
      BlockState var7 = var1.getBlockState(var5);
      if (var6.isEmpty() && var7.isPathfindable(var1, var5.below(), PathComputationType.WATER) && var7.isAir()) {
         return BlockPathTypes.BREACH;
      } else {
         return var6.is(FluidTags.WATER) && var7.isPathfindable(var1, var5, PathComputationType.WATER) ? BlockPathTypes.WATER : BlockPathTypes.BLOCKED;
      }
   }

   @Nullable
   private Node getWaterNode(int var1, int var2, int var3) {
      BlockPathTypes var4 = this.isFree(var1, var2, var3);
      return (!this.allowBreaching || var4 != BlockPathTypes.BREACH) && var4 != BlockPathTypes.WATER ? null : this.getNode(var1, var2, var3);
   }

   @Nullable
   protected Node getNode(int var1, int var2, int var3) {
      Node var4 = null;
      BlockPathTypes var5 = this.getBlockPathType(this.mob.level, var1, var2, var3);
      float var6 = this.mob.getPathfindingMalus(var5);
      if (var6 >= 0.0F) {
         var4 = super.getNode(var1, var2, var3);
         var4.type = var5;
         var4.costMalus = Math.max(var4.costMalus, var6);
         if (this.level.getFluidState(new BlockPos(var1, var2, var3)).isEmpty()) {
            var4.costMalus += 8.0F;
         }
      }

      return var5 == BlockPathTypes.OPEN ? var4 : var4;
   }

   private BlockPathTypes isFree(int var1, int var2, int var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();

      for(int var5 = var1; var5 < var1 + this.entityWidth; ++var5) {
         for(int var6 = var2; var6 < var2 + this.entityHeight; ++var6) {
            for(int var7 = var3; var7 < var3 + this.entityDepth; ++var7) {
               FluidState var8 = this.level.getFluidState(var4.set(var5, var6, var7));
               BlockState var9 = this.level.getBlockState(var4.set(var5, var6, var7));
               if (var8.isEmpty() && var9.isPathfindable(this.level, var4.below(), PathComputationType.WATER) && var9.isAir()) {
                  return BlockPathTypes.BREACH;
               }

               if (!var8.is(FluidTags.WATER)) {
                  return BlockPathTypes.BLOCKED;
               }
            }
         }
      }

      BlockState var10 = this.level.getBlockState(var4);
      if (var10.isPathfindable(this.level, var4, PathComputationType.WATER)) {
         return BlockPathTypes.WATER;
      } else {
         return BlockPathTypes.BLOCKED;
      }
   }
}
