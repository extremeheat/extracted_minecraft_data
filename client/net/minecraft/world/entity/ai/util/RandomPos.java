package net.minecraft.world.entity.ai.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class RandomPos {
   private static final int RANDOM_POS_ATTEMPTS = 10;

   public RandomPos() {
      super();
   }

   public static BlockPos generateRandomDirection(RandomSource var0, int var1, int var2) {
      int var3 = var0.nextInt(2 * var1 + 1) - var1;
      int var4 = var0.nextInt(2 * var2 + 1) - var2;
      int var5 = var0.nextInt(2 * var1 + 1) - var1;
      return new BlockPos(var3, var4, var5);
   }

   @Nullable
   public static BlockPos generateRandomDirectionWithinRadians(RandomSource var0, int var1, int var2, int var3, double var4, double var6, double var8) {
      double var10 = Mth.atan2(var6, var4) - 1.5707963705062866;
      double var12 = var10 + (double)(2.0F * var0.nextFloat() - 1.0F) * var8;
      double var14 = Math.sqrt(var0.nextDouble()) * (double)Mth.SQRT_OF_TWO * (double)var1;
      double var16 = -var14 * Math.sin(var12);
      double var18 = var14 * Math.cos(var12);
      if (!(Math.abs(var16) > (double)var1) && !(Math.abs(var18) > (double)var1)) {
         int var20 = var0.nextInt(2 * var2 + 1) - var2 + var3;
         return BlockPos.containing(var16, (double)var20, var18);
      } else {
         return null;
      }
   }

   @VisibleForTesting
   public static BlockPos moveUpOutOfSolid(BlockPos var0, int var1, Predicate<BlockPos> var2) {
      if (!var2.test(var0)) {
         return var0;
      } else {
         BlockPos var3;
         for(var3 = var0.above(); var3.getY() < var1 && var2.test(var3); var3 = var3.above()) {
         }

         return var3;
      }
   }

   @VisibleForTesting
   public static BlockPos moveUpToAboveSolid(BlockPos var0, int var1, int var2, Predicate<BlockPos> var3) {
      if (var1 < 0) {
         throw new IllegalArgumentException("aboveSolidAmount was " + var1 + ", expected >= 0");
      } else if (!var3.test(var0)) {
         return var0;
      } else {
         BlockPos var4;
         for(var4 = var0.above(); var4.getY() < var2 && var3.test(var4); var4 = var4.above()) {
         }

         BlockPos var5;
         BlockPos var6;
         for(var5 = var4; var5.getY() < var2 && var5.getY() - var4.getY() < var1; var5 = var6) {
            var6 = var5.above();
            if (var3.test(var6)) {
               break;
            }
         }

         return var5;
      }
   }

   @Nullable
   public static Vec3 generateRandomPos(PathfinderMob var0, Supplier<BlockPos> var1) {
      Objects.requireNonNull(var0);
      return generateRandomPos(var1, var0::getWalkTargetValue);
   }

   @Nullable
   public static Vec3 generateRandomPos(Supplier<BlockPos> var0, ToDoubleFunction<BlockPos> var1) {
      double var2 = -1.0 / 0.0;
      BlockPos var4 = null;

      for(int var5 = 0; var5 < 10; ++var5) {
         BlockPos var6 = (BlockPos)var0.get();
         if (var6 != null) {
            double var7 = var1.applyAsDouble(var6);
            if (var7 > var2) {
               var2 = var7;
               var4 = var6;
            }
         }
      }

      return var4 != null ? Vec3.atBottomCenterOf(var4) : null;
   }

   public static BlockPos generateRandomPosTowardDirection(PathfinderMob var0, int var1, RandomSource var2, BlockPos var3) {
      int var4 = var3.getX();
      int var5 = var3.getZ();
      if (var0.hasRestriction() && var1 > 1) {
         BlockPos var6 = var0.getRestrictCenter();
         if (var0.getX() > (double)var6.getX()) {
            var4 -= var2.nextInt(var1 / 2);
         } else {
            var4 += var2.nextInt(var1 / 2);
         }

         if (var0.getZ() > (double)var6.getZ()) {
            var5 -= var2.nextInt(var1 / 2);
         } else {
            var5 += var2.nextInt(var1 / 2);
         }
      }

      return BlockPos.containing((double)var4 + var0.getX(), (double)var3.getY() + var0.getY(), (double)var5 + var0.getZ());
   }
}
