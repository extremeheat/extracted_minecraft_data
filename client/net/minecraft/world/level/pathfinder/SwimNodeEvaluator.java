package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.EnumMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SwimNodeEvaluator extends NodeEvaluator {
   private final boolean allowBreaching;
   private final Long2ObjectMap<PathType> pathTypesByPosCache = new Long2ObjectOpenHashMap();

   public SwimNodeEvaluator(boolean var1) {
      super();
      this.allowBreaching = var1;
   }

   @Override
   public void prepare(PathNavigationRegion var1, Mob var2) {
      super.prepare(var1, var2);
      this.pathTypesByPosCache.clear();
   }

   @Override
   public void done() {
      super.done();
      this.pathTypesByPosCache.clear();
   }

   @Override
   public Node getStart() {
      return this.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5), Mth.floor(this.mob.getBoundingBox().minZ));
   }

   @Override
   public Target getTarget(double var1, double var3, double var5) {
      return this.getTargetNodeAt(var1, var3, var5);
   }

   @Override
   public int getNeighbors(Node[] var1, Node var2) {
      int var3 = 0;
      EnumMap var4 = Maps.newEnumMap(Direction.class);

      for(Direction var8 : Direction.values()) {
         Node var9 = this.findAcceptedNode(var2.x + var8.getStepX(), var2.y + var8.getStepY(), var2.z + var8.getStepZ());
         var4.put(var8, var9);
         if (this.isNodeValid(var9)) {
            var1[var3++] = var9;
         }
      }

      for(Direction var11 : Direction.Plane.HORIZONTAL) {
         Direction var12 = var11.getClockWise();
         if (hasMalus((Node)var4.get(var11)) && hasMalus((Node)var4.get(var12))) {
            Node var13 = this.findAcceptedNode(var2.x + var11.getStepX() + var12.getStepX(), var2.y, var2.z + var11.getStepZ() + var12.getStepZ());
            if (this.isNodeValid(var13)) {
               var1[var3++] = var13;
            }
         }
      }

      return var3;
   }

   protected boolean isNodeValid(@Nullable Node var1) {
      return var1 != null && !var1.closed;
   }

   private static boolean hasMalus(@Nullable Node var0) {
      return var0 != null && var0.costMalus >= 0.0F;
   }

   @Nullable
   protected Node findAcceptedNode(int var1, int var2, int var3) {
      Node var4 = null;
      PathType var5 = this.getCachedBlockType(var1, var2, var3);
      if (this.allowBreaching && var5 == PathType.BREACH || var5 == PathType.WATER) {
         float var6 = this.mob.getPathfindingMalus(var5);
         if (var6 >= 0.0F) {
            var4 = this.getNode(var1, var2, var3);
            var4.type = var5;
            var4.costMalus = Math.max(var4.costMalus, var6);
            if (this.currentContext.level().getFluidState(new BlockPos(var1, var2, var3)).isEmpty()) {
               var4.costMalus += 8.0F;
            }
         }
      }

      return var4;
   }

   protected PathType getCachedBlockType(int var1, int var2, int var3) {
      return (PathType)this.pathTypesByPosCache
         .computeIfAbsent(BlockPos.asLong(var1, var2, var3), var4 -> this.getPathType(this.currentContext, var1, var2, var3));
   }

   @Override
   public PathType getPathType(PathfindingContext var1, int var2, int var3, int var4) {
      return this.getPathTypeOfMob(var1, var2, var3, var4, this.mob);
   }

   @Override
   public PathType getPathTypeOfMob(PathfindingContext var1, int var2, int var3, int var4, Mob var5) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

      for(int var7 = var2; var7 < var2 + this.entityWidth; ++var7) {
         for(int var8 = var3; var8 < var3 + this.entityHeight; ++var8) {
            for(int var9 = var4; var9 < var4 + this.entityDepth; ++var9) {
               BlockState var10 = var1.getBlockState(var6.set(var7, var8, var9));
               FluidState var11 = var10.getFluidState();
               if (var11.isEmpty() && var10.isPathfindable(PathComputationType.WATER) && var10.isAir()) {
                  return PathType.BREACH;
               }

               if (!var11.is(FluidTags.WATER)) {
                  return PathType.BLOCKED;
               }
            }
         }
      }

      BlockState var12 = var1.getBlockState(var6);
      return var12.isPathfindable(PathComputationType.WATER) ? PathType.WATER : PathType.BLOCKED;
   }
}
