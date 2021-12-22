package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockUtil {
   public BlockUtil() {
      super();
   }

   public static BlockUtil.FoundRectangle getLargestRectangleAround(BlockPos var0, Direction.Axis var1, int var2, Direction.Axis var3, int var4, Predicate<BlockPos> var5) {
      BlockPos.MutableBlockPos var6 = var0.mutable();
      Direction var7 = Direction.get(Direction.AxisDirection.NEGATIVE, var1);
      Direction var8 = var7.getOpposite();
      Direction var9 = Direction.get(Direction.AxisDirection.NEGATIVE, var3);
      Direction var10 = var9.getOpposite();
      int var11 = getLimit(var5, var6.set(var0), var7, var2);
      int var12 = getLimit(var5, var6.set(var0), var8, var2);
      int var13 = var11;
      BlockUtil.IntBounds[] var14 = new BlockUtil.IntBounds[var11 + 1 + var12];
      var14[var11] = new BlockUtil.IntBounds(getLimit(var5, var6.set(var0), var9, var4), getLimit(var5, var6.set(var0), var10, var4));
      int var15 = var14[var11].min;

      int var16;
      BlockUtil.IntBounds var17;
      for(var16 = 1; var16 <= var11; ++var16) {
         var17 = var14[var13 - (var16 - 1)];
         var14[var13 - var16] = new BlockUtil.IntBounds(getLimit(var5, var6.set(var0).move(var7, var16), var9, var17.min), getLimit(var5, var6.set(var0).move(var7, var16), var10, var17.max));
      }

      for(var16 = 1; var16 <= var12; ++var16) {
         var17 = var14[var13 + var16 - 1];
         var14[var13 + var16] = new BlockUtil.IntBounds(getLimit(var5, var6.set(var0).move(var8, var16), var9, var17.min), getLimit(var5, var6.set(var0).move(var8, var16), var10, var17.max));
      }

      var16 = 0;
      int var26 = 0;
      int var18 = 0;
      int var19 = 0;
      int[] var20 = new int[var14.length];

      for(int var21 = var15; var21 >= 0; --var21) {
         BlockUtil.IntBounds var23;
         int var24;
         int var25;
         for(int var22 = 0; var22 < var14.length; ++var22) {
            var23 = var14[var22];
            var24 = var15 - var23.min;
            var25 = var15 + var23.max;
            var20[var22] = var21 >= var24 && var21 <= var25 ? var25 + 1 - var21 : 0;
         }

         Pair var27 = getMaxRectangleLocation(var20);
         var23 = (BlockUtil.IntBounds)var27.getFirst();
         var24 = 1 + var23.max - var23.min;
         var25 = (Integer)var27.getSecond();
         if (var24 * var25 > var18 * var19) {
            var16 = var23.min;
            var26 = var21;
            var18 = var24;
            var19 = var25;
         }
      }

      return new BlockUtil.FoundRectangle(var0.relative(var1, var16 - var13).relative(var3, var26 - var15), var18, var19);
   }

   private static int getLimit(Predicate<BlockPos> var0, BlockPos.MutableBlockPos var1, Direction var2, int var3) {
      int var4;
      for(var4 = 0; var4 < var3 && var0.test(var1.move(var2)); ++var4) {
      }

      return var4;
   }

   @VisibleForTesting
   static Pair<BlockUtil.IntBounds, Integer> getMaxRectangleLocation(int[] var0) {
      int var1 = 0;
      int var2 = 0;
      int var3 = 0;
      IntArrayList var4 = new IntArrayList();
      var4.push(0);

      for(int var5 = 1; var5 <= var0.length; ++var5) {
         int var6 = var5 == var0.length ? 0 : var0[var5];

         while(!var4.isEmpty()) {
            int var7 = var0[var4.topInt()];
            if (var6 >= var7) {
               var4.push(var5);
               break;
            }

            var4.popInt();
            int var8 = var4.isEmpty() ? 0 : var4.topInt() + 1;
            if (var7 * (var5 - var8) > var3 * (var2 - var1)) {
               var2 = var5;
               var1 = var8;
               var3 = var7;
            }
         }

         if (var4.isEmpty()) {
            var4.push(var5);
         }
      }

      return new Pair(new BlockUtil.IntBounds(var1, var2 - 1), var3);
   }

   public static Optional<BlockPos> getTopConnectedBlock(BlockGetter var0, BlockPos var1, Block var2, Direction var3, Block var4) {
      BlockPos.MutableBlockPos var5 = var1.mutable();

      BlockState var6;
      do {
         var5.move(var3);
         var6 = var0.getBlockState(var5);
      } while(var6.is(var2));

      return var6.is(var4) ? Optional.of(var5) : Optional.empty();
   }

   public static class IntBounds {
      public final int min;
      public final int max;

      public IntBounds(int var1, int var2) {
         super();
         this.min = var1;
         this.max = var2;
      }

      public String toString() {
         return "IntBounds{min=" + this.min + ", max=" + this.max + "}";
      }
   }

   public static class FoundRectangle {
      public final BlockPos minCorner;
      public final int axis1Size;
      public final int axis2Size;

      public FoundRectangle(BlockPos var1, int var2, int var3) {
         super();
         this.minCorner = var1;
         this.axis1Size = var2;
         this.axis2Size = var3;
      }
   }
}
