package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;

public class PathfindingContext {
   private final CollisionGetter level;
   @Nullable
   private final PathTypeCache cache;
   private final BlockPos mobPosition;
   private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

   public PathfindingContext(CollisionGetter var1, Mob var2) {
      super();
      this.level = var1;
      if (var2.level() instanceof ServerLevel var3) {
         this.cache = var3.getPathTypeCache();
      } else {
         this.cache = null;
      }

      this.mobPosition = var2.blockPosition();
   }

   public PathType getPathTypeFromState(int var1, int var2, int var3) {
      BlockPos.MutableBlockPos var4 = this.mutablePos.set(var1, var2, var3);
      return this.cache == null ? WalkNodeEvaluator.getPathTypeFromState(this.level, var4) : this.cache.getOrCompute(this.level, var4);
   }

   public BlockState getBlockState(BlockPos var1) {
      return this.level.getBlockState(var1);
   }

   public CollisionGetter level() {
      return this.level;
   }

   public BlockPos mobPosition() {
      return this.mobPosition;
   }
}