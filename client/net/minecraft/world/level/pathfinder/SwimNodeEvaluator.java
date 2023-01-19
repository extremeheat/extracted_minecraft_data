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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SwimNodeEvaluator extends NodeEvaluator {
   private final boolean allowBreaching;
   private final Long2ObjectMap<BlockPathTypes> pathTypesByPosCache = new Long2ObjectOpenHashMap();

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
      return this.getNode(
         Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5), Mth.floor(this.mob.getBoundingBox().minZ)
      );
   }

   @Override
   public Target getGoal(double var1, double var3, double var5) {
      return this.getTargetFromNode(this.getNode(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5)));
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
         Node var13 = this.findAcceptedNode(var2.x + var11.getStepX() + var12.getStepX(), var2.y, var2.z + var11.getStepZ() + var12.getStepZ());
         if (this.isDiagonalNodeValid(var13, (Node)var4.get(var11), (Node)var4.get(var12))) {
            var1[var3++] = var13;
         }
      }

      return var3;
   }

   protected boolean isNodeValid(@Nullable Node var1) {
      return var1 != null && !var1.closed;
   }

   protected boolean isDiagonalNodeValid(@Nullable Node var1, @Nullable Node var2, @Nullable Node var3) {
      return this.isNodeValid(var1) && var2 != null && var2.costMalus >= 0.0F && var3 != null && var3.costMalus >= 0.0F;
   }

   @Nullable
   protected Node findAcceptedNode(int var1, int var2, int var3) {
      Node var4 = null;
      BlockPathTypes var5 = this.getCachedBlockType(var1, var2, var3);
      if (this.allowBreaching && var5 == BlockPathTypes.BREACH || var5 == BlockPathTypes.WATER) {
         float var6 = this.mob.getPathfindingMalus(var5);
         if (var6 >= 0.0F) {
            var4 = this.getNode(var1, var2, var3);
            var4.type = var5;
            var4.costMalus = Math.max(var4.costMalus, var6);
            if (this.level.getFluidState(new BlockPos(var1, var2, var3)).isEmpty()) {
               var4.costMalus += 8.0F;
            }
         }
      }

      return var4;
   }

   protected BlockPathTypes getCachedBlockType(int var1, int var2, int var3) {
      return (BlockPathTypes)this.pathTypesByPosCache
         .computeIfAbsent(BlockPos.asLong(var1, var2, var3), var4 -> this.getBlockPathType(this.level, var1, var2, var3));
   }

   @Override
   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4) {
      return this.getBlockPathType(
         var1, var2, var3, var4, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors()
      );
   }

   @Override
   public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4, Mob var5, int var6, int var7, int var8, boolean var9, boolean var10) {
      BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

      for(int var12 = var2; var12 < var2 + var6; ++var12) {
         for(int var13 = var3; var13 < var3 + var7; ++var13) {
            for(int var14 = var4; var14 < var4 + var8; ++var14) {
               FluidState var15 = var1.getFluidState(var11.set(var12, var13, var14));
               BlockState var16 = var1.getBlockState(var11.set(var12, var13, var14));
               if (var15.isEmpty() && var16.isPathfindable(var1, var11.below(), PathComputationType.WATER) && var16.isAir()) {
                  return BlockPathTypes.BREACH;
               }

               if (!var15.is(FluidTags.WATER)) {
                  return BlockPathTypes.BLOCKED;
               }
            }
         }
      }

      BlockState var17 = var1.getBlockState(var11);
      return var17.isPathfindable(var1, var11, PathComputationType.WATER) ? BlockPathTypes.WATER : BlockPathTypes.BLOCKED;
   }
}
