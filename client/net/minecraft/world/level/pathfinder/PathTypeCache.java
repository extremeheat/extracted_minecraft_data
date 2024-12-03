package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.HashCommon;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public class PathTypeCache {
   private static final int SIZE = 4096;
   private static final int MASK = 4095;
   private final long[] positions = new long[4096];
   private final PathType[] pathTypes = new PathType[4096];

   public PathTypeCache() {
      super();
   }

   public PathType getOrCompute(BlockGetter var1, BlockPos var2) {
      long var3 = var2.asLong();
      int var5 = index(var3);
      PathType var6 = this.get(var5, var3);
      return var6 != null ? var6 : this.compute(var1, var2, var5, var3);
   }

   @Nullable
   private PathType get(int var1, long var2) {
      return this.positions[var1] == var2 ? this.pathTypes[var1] : null;
   }

   private PathType compute(BlockGetter var1, BlockPos var2, int var3, long var4) {
      PathType var6 = WalkNodeEvaluator.getPathTypeFromState(var1, var2);
      this.positions[var3] = var4;
      this.pathTypes[var3] = var6;
      return var6;
   }

   public void invalidate(BlockPos var1) {
      long var2 = var1.asLong();
      int var4 = index(var2);
      if (this.positions[var4] == var2) {
         this.pathTypes[var4] = null;
      }

   }

   private static int index(long var0) {
      return (int)HashCommon.mix(var0) & 4095;
   }
}
