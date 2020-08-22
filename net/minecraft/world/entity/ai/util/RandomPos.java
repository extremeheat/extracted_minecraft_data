package net.minecraft.world.entity.ai.util;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class RandomPos {
   @Nullable
   public static Vec3 getPos(PathfinderMob var0, int var1, int var2) {
      return generateRandomPos(var0, var1, var2, 0, (Vec3)null, true, 1.5707963705062866D, var0::getWalkTargetValue, false, 0, 0, true);
   }

   @Nullable
   public static Vec3 getAirPos(PathfinderMob var0, int var1, int var2, int var3, @Nullable Vec3 var4, double var5) {
      return generateRandomPos(var0, var1, var2, var3, var4, true, var5, var0::getWalkTargetValue, true, 0, 0, false);
   }

   @Nullable
   public static Vec3 getLandPos(PathfinderMob var0, int var1, int var2) {
      var0.getClass();
      return getLandPos(var0, var1, var2, var0::getWalkTargetValue);
   }

   @Nullable
   public static Vec3 getLandPos(PathfinderMob var0, int var1, int var2, ToDoubleFunction var3) {
      return generateRandomPos(var0, var1, var2, 0, (Vec3)null, false, 0.0D, var3, true, 0, 0, true);
   }

   @Nullable
   public static Vec3 getAboveLandPos(PathfinderMob var0, int var1, int var2, Vec3 var3, float var4, int var5, int var6) {
      return generateRandomPos(var0, var1, var2, 0, var3, false, (double)var4, var0::getWalkTargetValue, true, var5, var6, true);
   }

   @Nullable
   public static Vec3 getPosTowards(PathfinderMob var0, int var1, int var2, Vec3 var3) {
      Vec3 var4 = var3.subtract(var0.getX(), var0.getY(), var0.getZ());
      return generateRandomPos(var0, var1, var2, 0, var4, true, 1.5707963705062866D, var0::getWalkTargetValue, false, 0, 0, true);
   }

   @Nullable
   public static Vec3 getPosTowards(PathfinderMob var0, int var1, int var2, Vec3 var3, double var4) {
      Vec3 var6 = var3.subtract(var0.getX(), var0.getY(), var0.getZ());
      return generateRandomPos(var0, var1, var2, 0, var6, true, var4, var0::getWalkTargetValue, false, 0, 0, true);
   }

   @Nullable
   public static Vec3 getAirPosTowards(PathfinderMob var0, int var1, int var2, int var3, Vec3 var4, double var5) {
      Vec3 var7 = var4.subtract(var0.getX(), var0.getY(), var0.getZ());
      return generateRandomPos(var0, var1, var2, var3, var7, false, var5, var0::getWalkTargetValue, true, 0, 0, false);
   }

   @Nullable
   public static Vec3 getPosAvoid(PathfinderMob var0, int var1, int var2, Vec3 var3) {
      Vec3 var4 = var0.position().subtract(var3);
      return generateRandomPos(var0, var1, var2, 0, var4, true, 1.5707963705062866D, var0::getWalkTargetValue, false, 0, 0, true);
   }

   @Nullable
   public static Vec3 getLandPosAvoid(PathfinderMob var0, int var1, int var2, Vec3 var3) {
      Vec3 var4 = var0.position().subtract(var3);
      return generateRandomPos(var0, var1, var2, 0, var4, false, 1.5707963705062866D, var0::getWalkTargetValue, true, 0, 0, true);
   }

   @Nullable
   private static Vec3 generateRandomPos(PathfinderMob var0, int var1, int var2, int var3, @Nullable Vec3 var4, boolean var5, double var6, ToDoubleFunction var8, boolean var9, int var10, int var11, boolean var12) {
      PathNavigation var13 = var0.getNavigation();
      Random var14 = var0.getRandom();
      boolean var15;
      if (var0.hasRestriction()) {
         var15 = var0.getRestrictCenter().closerThan(var0.position(), (double)(var0.getRestrictRadius() + (float)var1) + 1.0D);
      } else {
         var15 = false;
      }

      boolean var16 = false;
      double var17 = Double.NEGATIVE_INFINITY;
      BlockPos var19 = new BlockPos(var0);

      for(int var20 = 0; var20 < 10; ++var20) {
         BlockPos var21 = getRandomDelta(var14, var1, var2, var3, var4, var6);
         if (var21 != null) {
            int var22 = var21.getX();
            int var23 = var21.getY();
            int var24 = var21.getZ();
            BlockPos var25;
            if (var0.hasRestriction() && var1 > 1) {
               var25 = var0.getRestrictCenter();
               if (var0.getX() > (double)var25.getX()) {
                  var22 -= var14.nextInt(var1 / 2);
               } else {
                  var22 += var14.nextInt(var1 / 2);
               }

               if (var0.getZ() > (double)var25.getZ()) {
                  var24 -= var14.nextInt(var1 / 2);
               } else {
                  var24 += var14.nextInt(var1 / 2);
               }
            }

            var25 = new BlockPos((double)var22 + var0.getX(), (double)var23 + var0.getY(), (double)var24 + var0.getZ());
            if (var25.getY() >= 0 && var25.getY() <= var0.level.getMaxBuildHeight() && (!var15 || var0.isWithinRestriction(var25)) && (!var12 || var13.isStableDestination(var25))) {
               if (var9) {
                  var25 = moveUpToAboveSolid(var25, var14.nextInt(var10 + 1) + var11, var0.level.getMaxBuildHeight(), (var1x) -> {
                     return var0.level.getBlockState(var1x).getMaterial().isSolid();
                  });
               }

               if (var5 || !var0.level.getFluidState(var25).is(FluidTags.WATER)) {
                  BlockPathTypes var26 = WalkNodeEvaluator.getBlockPathTypeStatic(var0.level, var25.getX(), var25.getY(), var25.getZ());
                  if (var0.getPathfindingMalus(var26) == 0.0F) {
                     double var27 = var8.applyAsDouble(var25);
                     if (var27 > var17) {
                        var17 = var27;
                        var19 = var25;
                        var16 = true;
                     }
                  }
               }
            }
         }
      }

      if (var16) {
         return new Vec3(var19);
      } else {
         return null;
      }
   }

   @Nullable
   private static BlockPos getRandomDelta(Random var0, int var1, int var2, int var3, @Nullable Vec3 var4, double var5) {
      if (var4 != null && var5 < 3.141592653589793D) {
         double var18 = Mth.atan2(var4.z, var4.x) - 1.5707963705062866D;
         double var19 = var18 + (double)(2.0F * var0.nextFloat() - 1.0F) * var5;
         double var11 = Math.sqrt(var0.nextDouble()) * (double)Mth.SQRT_OF_TWO * (double)var1;
         double var13 = -var11 * Math.sin(var19);
         double var15 = var11 * Math.cos(var19);
         if (Math.abs(var13) <= (double)var1 && Math.abs(var15) <= (double)var1) {
            int var17 = var0.nextInt(2 * var2 + 1) - var2 + var3;
            return new BlockPos(var13, (double)var17, var15);
         } else {
            return null;
         }
      } else {
         int var7 = var0.nextInt(2 * var1 + 1) - var1;
         int var8 = var0.nextInt(2 * var2 + 1) - var2 + var3;
         int var9 = var0.nextInt(2 * var1 + 1) - var1;
         return new BlockPos(var7, var8, var9);
      }
   }

   static BlockPos moveUpToAboveSolid(BlockPos var0, int var1, int var2, Predicate var3) {
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
}
