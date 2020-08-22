package net.minecraft.world.level.block.state.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

public class BlockPattern {
   private final Predicate[][][] pattern;
   private final int depth;
   private final int height;
   private final int width;

   public BlockPattern(Predicate[][][] var1) {
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

   @Nullable
   private BlockPattern.BlockPatternMatch matches(BlockPos var1, Direction var2, Direction var3, LoadingCache var4) {
      for(int var5 = 0; var5 < this.width; ++var5) {
         for(int var6 = 0; var6 < this.height; ++var6) {
            for(int var7 = 0; var7 < this.depth; ++var7) {
               if (!this.pattern[var7][var6][var5].test(var4.getUnchecked(translateAndRotate(var1, var2, var3, var5, var6, var7)))) {
                  return null;
               }
            }
         }
      }

      return new BlockPattern.BlockPatternMatch(var1, var2, var3, var4, this.width, this.height, this.depth);
   }

   @Nullable
   public BlockPattern.BlockPatternMatch find(LevelReader var1, BlockPos var2) {
      LoadingCache var3 = createLevelCache(var1, false);
      int var4 = Math.max(Math.max(this.width, this.height), this.depth);
      Iterator var5 = BlockPos.betweenClosed(var2, var2.offset(var4 - 1, var4 - 1, var4 - 1)).iterator();

      while(var5.hasNext()) {
         BlockPos var6 = (BlockPos)var5.next();
         Direction[] var7 = Direction.values();
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Direction var10 = var7[var9];
            Direction[] var11 = Direction.values();
            int var12 = var11.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               Direction var14 = var11[var13];
               if (var14 != var10 && var14 != var10.getOpposite()) {
                  BlockPattern.BlockPatternMatch var15 = this.matches(var6, var10, var14, var3);
                  if (var15 != null) {
                     return var15;
                  }
               }
            }
         }
      }

      return null;
   }

   public static LoadingCache createLevelCache(LevelReader var0, boolean var1) {
      return CacheBuilder.newBuilder().build(new BlockPattern.BlockCacheLoader(var0, var1));
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

   public static class PortalInfo {
      public final Vec3 pos;
      public final Vec3 speed;
      public final int angle;

      public PortalInfo(Vec3 var1, Vec3 var2, int var3) {
         this.pos = var1;
         this.speed = var2;
         this.angle = var3;
      }
   }

   public static class BlockPatternMatch {
      private final BlockPos frontTopLeft;
      private final Direction forwards;
      private final Direction up;
      private final LoadingCache cache;
      private final int width;
      private final int height;
      private final int depth;

      public BlockPatternMatch(BlockPos var1, Direction var2, Direction var3, LoadingCache var4, int var5, int var6, int var7) {
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

      public BlockInWorld getBlock(int var1, int var2, int var3) {
         return (BlockInWorld)this.cache.getUnchecked(BlockPattern.translateAndRotate(this.frontTopLeft, this.getForwards(), this.getUp(), var1, var2, var3));
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
      }

      public BlockPattern.PortalInfo getPortalOutput(Direction var1, BlockPos var2, double var3, Vec3 var5, double var6) {
         Direction var8 = this.getForwards();
         Direction var9 = var8.getClockWise();
         double var12 = (double)(this.getFrontTopLeft().getY() + 1) - var3 * (double)this.getHeight();
         double var10;
         double var14;
         if (var9 == Direction.NORTH) {
            var10 = (double)var2.getX() + 0.5D;
            var14 = (double)(this.getFrontTopLeft().getZ() + 1) - (1.0D - var6) * (double)this.getWidth();
         } else if (var9 == Direction.SOUTH) {
            var10 = (double)var2.getX() + 0.5D;
            var14 = (double)this.getFrontTopLeft().getZ() + (1.0D - var6) * (double)this.getWidth();
         } else if (var9 == Direction.WEST) {
            var10 = (double)(this.getFrontTopLeft().getX() + 1) - (1.0D - var6) * (double)this.getWidth();
            var14 = (double)var2.getZ() + 0.5D;
         } else {
            var10 = (double)this.getFrontTopLeft().getX() + (1.0D - var6) * (double)this.getWidth();
            var14 = (double)var2.getZ() + 0.5D;
         }

         double var16;
         double var18;
         if (var8.getOpposite() == var1) {
            var16 = var5.x;
            var18 = var5.z;
         } else if (var8.getOpposite() == var1.getOpposite()) {
            var16 = -var5.x;
            var18 = -var5.z;
         } else if (var8.getOpposite() == var1.getClockWise()) {
            var16 = -var5.z;
            var18 = var5.x;
         } else {
            var16 = var5.z;
            var18 = -var5.x;
         }

         int var20 = (var8.get2DDataValue() - var1.getOpposite().get2DDataValue()) * 90;
         return new BlockPattern.PortalInfo(new Vec3(var10, var12, var14), new Vec3(var16, var5.y, var18), var20);
      }
   }

   static class BlockCacheLoader extends CacheLoader {
      private final LevelReader level;
      private final boolean loadChunks;

      public BlockCacheLoader(LevelReader var1, boolean var2) {
         this.level = var1;
         this.loadChunks = var2;
      }

      public BlockInWorld load(BlockPos var1) throws Exception {
         return new BlockInWorld(this.level, var1, this.loadChunks);
      }

      // $FF: synthetic method
      public Object load(Object var1) throws Exception {
         return this.load((BlockPos)var1);
      }
   }
}
