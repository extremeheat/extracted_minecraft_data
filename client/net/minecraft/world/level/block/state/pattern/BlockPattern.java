package net.minecraft.world.level.block.state.pattern;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;

public class BlockPattern {
   private final Predicate<BlockInWorld>[][][] pattern;
   private final int depth;
   private final int height;
   private final int width;

   public BlockPattern(Predicate<BlockInWorld>[][][] var1) {
      super();
      this.pattern = var1;
      this.depth = var1.length;
      if (this.depth > 0) {
         this.height = var1[0].length;
         if (this.height > 0) {
            this.width = var1[0][0].length;
         } else {
            this.width = 0;
         }
      } else {
         this.height = 0;
         this.width = 0;
      }

   }

   public int getDepth() {
      return this.depth;
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   @VisibleForTesting
   public Predicate<BlockInWorld>[][][] getPattern() {
      return this.pattern;
   }

   @Nullable
   @VisibleForTesting
   public BlockPatternMatch matches(LevelReader var1, BlockPos var2, Direction var3, Direction var4) {
      LoadingCache var5 = createLevelCache(var1, false);
      return this.matches(var2, var3, var4, var5);
   }

   @Nullable
   private BlockPatternMatch matches(BlockPos var1, Direction var2, Direction var3, LoadingCache<BlockPos, BlockInWorld> var4) {
      for(int var5 = 0; var5 < this.width; ++var5) {
         for(int var6 = 0; var6 < this.height; ++var6) {
            for(int var7 = 0; var7 < this.depth; ++var7) {
               if (!this.pattern[var7][var6][var5].test((BlockInWorld)var4.getUnchecked(translateAndRotate(var1, var2, var3, var5, var6, var7)))) {
                  return null;
               }
            }
         }
      }

      return new BlockPatternMatch(var1, var2, var3, var4, this.width, this.height, this.depth);
   }

   @Nullable
   public BlockPatternMatch find(LevelReader var1, BlockPos var2) {
      LoadingCache var3 = createLevelCache(var1, false);
      int var4 = Math.max(Math.max(this.width, this.height), this.depth);

      for(BlockPos var6 : BlockPos.betweenClosed(var2, var2.offset(var4 - 1, var4 - 1, var4 - 1))) {
         for(Direction var10 : Direction.values()) {
            for(Direction var14 : Direction.values()) {
               if (var14 != var10 && var14 != var10.getOpposite()) {
                  BlockPatternMatch var15 = this.matches(var6, var10, var14, var3);
                  if (var15 != null) {
                     return var15;
                  }
               }
            }
         }
      }

      return null;
   }

   public static LoadingCache<BlockPos, BlockInWorld> createLevelCache(LevelReader var0, boolean var1) {
      return CacheBuilder.newBuilder().build(new BlockCacheLoader(var0, var1));
   }

   protected static BlockPos translateAndRotate(BlockPos var0, Direction var1, Direction var2, int var3, int var4, int var5) {
      if (var1 != var2 && var1 != var2.getOpposite()) {
         Vec3i var6 = new Vec3i(var1.getStepX(), var1.getStepY(), var1.getStepZ());
         Vec3i var7 = new Vec3i(var2.getStepX(), var2.getStepY(), var2.getStepZ());
         Vec3i var8 = var6.cross(var7);
         return var0.offset(var7.getX() * -var4 + var8.getX() * var3 + var6.getX() * var5, var7.getY() * -var4 + var8.getY() * var3 + var6.getY() * var5, var7.getZ() * -var4 + var8.getZ() * var3 + var6.getZ() * var5);
      } else {
         throw new IllegalArgumentException("Invalid forwards & up combination");
      }
   }

   static class BlockCacheLoader extends CacheLoader<BlockPos, BlockInWorld> {
      private final LevelReader level;
      private final boolean loadChunks;

      public BlockCacheLoader(LevelReader var1, boolean var2) {
         super();
         this.level = var1;
         this.loadChunks = var2;
      }

      public BlockInWorld load(BlockPos var1) {
         return new BlockInWorld(this.level, var1, this.loadChunks);
      }

      // $FF: synthetic method
      public Object load(final Object var1) throws Exception {
         return this.load((BlockPos)var1);
      }
   }

   public static class BlockPatternMatch {
      private final BlockPos frontTopLeft;
      private final Direction forwards;
      private final Direction up;
      private final LoadingCache<BlockPos, BlockInWorld> cache;
      private final int width;
      private final int height;
      private final int depth;

      public BlockPatternMatch(BlockPos var1, Direction var2, Direction var3, LoadingCache<BlockPos, BlockInWorld> var4, int var5, int var6, int var7) {
         super();
         this.frontTopLeft = var1;
         this.forwards = var2;
         this.up = var3;
         this.cache = var4;
         this.width = var5;
         this.height = var6;
         this.depth = var7;
      }

      public BlockPos getFrontTopLeft() {
         return this.frontTopLeft;
      }

      public Direction getForwards() {
         return this.forwards;
      }

      public Direction getUp() {
         return this.up;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public int getDepth() {
         return this.depth;
      }

      public BlockInWorld getBlock(int var1, int var2, int var3) {
         return (BlockInWorld)this.cache.getUnchecked(BlockPattern.translateAndRotate(this.frontTopLeft, this.getForwards(), this.getUp(), var1, var2, var3));
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
      }
   }
}
