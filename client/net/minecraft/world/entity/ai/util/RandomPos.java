package net.minecraft.world.entity.ai.util;

import java.util.Random;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.Vec3;

public class RandomPos {
   @Nullable
   public static Vec3 getPos(PathfinderMob var0, int var1, int var2) {
      return generateRandomPos(var0, var1, var2, (Vec3)null);
   }

   @Nullable
   public static Vec3 getLandPos(PathfinderMob var0, int var1, int var2) {
      var0.getClass();
      return getLandPos(var0, var1, var2, var0::getWalkTargetValue);
   }

   @Nullable
   public static Vec3 getLandPos(PathfinderMob var0, int var1, int var2, ToDoubleFunction<BlockPos> var3) {
      return generateRandomPos(var0, var1, var2, (Vec3)null, false, 0.0D, var3);
   }

   @Nullable
   public static Vec3 getPosTowards(PathfinderMob var0, int var1, int var2, Vec3 var3) {
      Vec3 var4 = var3.subtract(var0.x, var0.y, var0.z);
      return generateRandomPos(var0, var1, var2, var4);
   }

   @Nullable
   public static Vec3 getPosTowards(PathfinderMob var0, int var1, int var2, Vec3 var3, double var4) {
      Vec3 var6 = var3.subtract(var0.x, var0.y, var0.z);
      var0.getClass();
      return generateRandomPos(var0, var1, var2, var6, true, var4, var0::getWalkTargetValue);
   }

   @Nullable
   public static Vec3 getLandPosAvoid(PathfinderMob var0, int var1, int var2, Vec3 var3) {
      Vec3 var4 = (new Vec3(var0.x, var0.y, var0.z)).subtract(var3);
      var0.getClass();
      return generateRandomPos(var0, var1, var2, var4, false, 1.5707963705062866D, var0::getWalkTargetValue);
   }

   @Nullable
   public static Vec3 getPosAvoid(PathfinderMob var0, int var1, int var2, Vec3 var3) {
      Vec3 var4 = (new Vec3(var0.x, var0.y, var0.z)).subtract(var3);
      return generateRandomPos(var0, var1, var2, var4);
   }

   @Nullable
   private static Vec3 generateRandomPos(PathfinderMob var0, int var1, int var2, @Nullable Vec3 var3) {
      var0.getClass();
      return generateRandomPos(var0, var1, var2, var3, true, 1.5707963705062866D, var0::getWalkTargetValue);
   }

   @Nullable
   private static Vec3 generateRandomPos(PathfinderMob var0, int var1, int var2, @Nullable Vec3 var3, boolean var4, double var5, ToDoubleFunction<BlockPos> var7) {
      PathNavigation var8 = var0.getNavigation();
      Random var9 = var0.getRandom();
      boolean var10;
      if (var0.hasRestriction()) {
         var10 = var0.getRestrictCenter().closerThan(var0.position(), (double)(var0.getRestrictRadius() + (float)var1) + 1.0D);
      } else {
         var10 = false;
      }

      boolean var11 = false;
      double var12 = -1.0D / 0.0;
      BlockPos var14 = new BlockPos(var0);

      for(int var15 = 0; var15 < 10; ++var15) {
         BlockPos var16 = getRandomDelta(var9, var1, var2, var3, var5);
         if (var16 != null) {
            int var17 = var16.getX();
            int var18 = var16.getY();
            int var19 = var16.getZ();
            BlockPos var20;
            if (var0.hasRestriction() && var1 > 1) {
               var20 = var0.getRestrictCenter();
               if (var0.x > (double)var20.getX()) {
                  var17 -= var9.nextInt(var1 / 2);
               } else {
                  var17 += var9.nextInt(var1 / 2);
               }

               if (var0.z > (double)var20.getZ()) {
                  var19 -= var9.nextInt(var1 / 2);
               } else {
                  var19 += var9.nextInt(var1 / 2);
               }
            }

            var20 = new BlockPos((double)var17 + var0.x, (double)var18 + var0.y, (double)var19 + var0.z);
            if ((!var10 || var0.isWithinRestriction(var20)) && var8.isStableDestination(var20)) {
               if (!var4) {
                  var20 = moveAboveSolid(var20, var0);
                  if (isWaterDestination(var20, var0)) {
                     continue;
                  }
               }

               double var21 = var7.applyAsDouble(var20);
               if (var21 > var12) {
                  var12 = var21;
                  var14 = var20;
                  var11 = true;
               }
            }
         }
      }

      if (var11) {
         return new Vec3(var14);
      } else {
         return null;
      }
   }

   @Nullable
   private static BlockPos getRandomDelta(Random var0, int var1, int var2, @Nullable Vec3 var3, double var4) {
      if (var3 != null && var4 < 3.141592653589793D) {
         double var17 = Mth.atan2(var3.z, var3.x) - 1.5707963705062866D;
         double var18 = var17 + (double)(2.0F * var0.nextFloat() - 1.0F) * var4;
         double var10 = Math.sqrt(var0.nextDouble()) * (double)Mth.SQRT_OF_TWO * (double)var1;
         double var12 = -var10 * Math.sin(var18);
         double var14 = var10 * Math.cos(var18);
         if (Math.abs(var12) <= (double)var1 && Math.abs(var14) <= (double)var1) {
            int var16 = var0.nextInt(2 * var2 + 1) - var2;
            return new BlockPos(var12, (double)var16, var14);
         } else {
            return null;
         }
      } else {
         int var6 = var0.nextInt(2 * var1 + 1) - var1;
         int var7 = var0.nextInt(2 * var2 + 1) - var2;
         int var8 = var0.nextInt(2 * var1 + 1) - var1;
         return new BlockPos(var6, var7, var8);
      }
   }

   private static BlockPos moveAboveSolid(BlockPos var0, PathfinderMob var1) {
      if (!var1.level.getBlockState(var0).getMaterial().isSolid()) {
         return var0;
      } else {
         BlockPos var2;
         for(var2 = var0.above(); var2.getY() < var1.level.getMaxBuildHeight() && var1.level.getBlockState(var2).getMaterial().isSolid(); var2 = var2.above()) {
         }

         return var2;
      }
   }

   private static boolean isWaterDestination(BlockPos var0, PathfinderMob var1) {
      return var1.level.getFluidState(var0).is(FluidTags.WATER);
   }
}
