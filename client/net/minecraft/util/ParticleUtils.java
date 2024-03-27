package net.minecraft.util;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ParticleUtils {
   public ParticleUtils() {
      super();
   }

   public static void spawnParticlesOnBlockFaces(Level var0, BlockPos var1, ParticleOptions var2, IntProvider var3) {
      for(Direction var7 : Direction.values()) {
         spawnParticlesOnBlockFace(var0, var1, var2, var3, var7, () -> getRandomSpeedRanges(var0.random), 0.55);
      }
   }

   public static void spawnParticlesOnBlockFace(
      Level var0, BlockPos var1, ParticleOptions var2, IntProvider var3, Direction var4, Supplier<Vec3> var5, double var6
   ) {
      int var8 = var3.sample(var0.random);

      for(int var9 = 0; var9 < var8; ++var9) {
         spawnParticleOnFace(var0, var1, var4, var2, (Vec3)var5.get(), var6);
      }
   }

   private static Vec3 getRandomSpeedRanges(RandomSource var0) {
      return new Vec3(Mth.nextDouble(var0, -0.5, 0.5), Mth.nextDouble(var0, -0.5, 0.5), Mth.nextDouble(var0, -0.5, 0.5));
   }

   public static void spawnParticlesAlongAxis(Direction.Axis var0, Level var1, BlockPos var2, double var3, ParticleOptions var5, UniformInt var6) {
      Vec3 var7 = Vec3.atCenterOf(var2);
      boolean var8 = var0 == Direction.Axis.X;
      boolean var9 = var0 == Direction.Axis.Y;
      boolean var10 = var0 == Direction.Axis.Z;
      int var11 = var6.sample(var1.random);

      for(int var12 = 0; var12 < var11; ++var12) {
         double var13 = var7.x + Mth.nextDouble(var1.random, -1.0, 1.0) * (var8 ? 0.5 : var3);
         double var15 = var7.y + Mth.nextDouble(var1.random, -1.0, 1.0) * (var9 ? 0.5 : var3);
         double var17 = var7.z + Mth.nextDouble(var1.random, -1.0, 1.0) * (var10 ? 0.5 : var3);
         double var19 = var8 ? Mth.nextDouble(var1.random, -1.0, 1.0) : 0.0;
         double var21 = var9 ? Mth.nextDouble(var1.random, -1.0, 1.0) : 0.0;
         double var23 = var10 ? Mth.nextDouble(var1.random, -1.0, 1.0) : 0.0;
         var1.addParticle(var5, var13, var15, var17, var19, var21, var23);
      }
   }

   public static void spawnParticleOnFace(Level var0, BlockPos var1, Direction var2, ParticleOptions var3, Vec3 var4, double var5) {
      Vec3 var7 = Vec3.atCenterOf(var1);
      int var8 = var2.getStepX();
      int var9 = var2.getStepY();
      int var10 = var2.getStepZ();
      double var11 = var7.x + (var8 == 0 ? Mth.nextDouble(var0.random, -0.5, 0.5) : (double)var8 * var5);
      double var13 = var7.y + (var9 == 0 ? Mth.nextDouble(var0.random, -0.5, 0.5) : (double)var9 * var5);
      double var15 = var7.z + (var10 == 0 ? Mth.nextDouble(var0.random, -0.5, 0.5) : (double)var10 * var5);
      double var17 = var8 == 0 ? var4.x() : 0.0;
      double var19 = var9 == 0 ? var4.y() : 0.0;
      double var21 = var10 == 0 ? var4.z() : 0.0;
      var0.addParticle(var3, var11, var13, var15, var17, var19, var21);
   }

   public static void spawnParticleBelow(Level var0, BlockPos var1, RandomSource var2, ParticleOptions var3) {
      double var4 = (double)var1.getX() + var2.nextDouble();
      double var6 = (double)var1.getY() - 0.05;
      double var8 = (double)var1.getZ() + var2.nextDouble();
      var0.addParticle(var3, var4, var6, var8, 0.0, 0.0, 0.0);
   }

   public static void spawnParticleInBlock(LevelAccessor var0, BlockPos var1, int var2, ParticleOptions var3) {
      double var4 = 0.5;
      BlockState var6 = var0.getBlockState(var1);
      double var7 = var6.isAir() ? 1.0 : var6.getShape(var0, var1).max(Direction.Axis.Y);
      spawnParticles(var0, var1, var2, 0.5, var7, true, var3);
   }

   public static void spawnParticles(LevelAccessor var0, BlockPos var1, int var2, double var3, double var5, boolean var7, ParticleOptions var8) {
      RandomSource var9 = var0.getRandom();

      for(int var10 = 0; var10 < var2; ++var10) {
         double var11 = var9.nextGaussian() * 0.02;
         double var13 = var9.nextGaussian() * 0.02;
         double var15 = var9.nextGaussian() * 0.02;
         double var17 = 0.5 - var3;
         double var19 = (double)var1.getX() + var17 + var9.nextDouble() * var3 * 2.0;
         double var21 = (double)var1.getY() + var9.nextDouble() * var5;
         double var23 = (double)var1.getZ() + var17 + var9.nextDouble() * var3 * 2.0;
         if (var7 || !var0.getBlockState(BlockPos.containing(var19, var21, var23).below()).isAir()) {
            var0.addParticle(var8, var19, var21, var23, var11, var13, var15);
         }
      }
   }

   public static void spawnSmashAttackParticles(LevelAccessor var0, BlockPos var1, int var2) {
      Vec3 var3 = var1.getCenter().add(0.0, 0.5, 0.0);
      BlockParticleOption var4 = new BlockParticleOption(ParticleTypes.DUST_PILLAR, var0.getBlockState(var1));

      for(int var5 = 0; (float)var5 < (float)var2 / 3.0F; ++var5) {
         double var6 = var3.x + var0.getRandom().nextGaussian() / 2.0;
         double var8 = var3.y;
         double var10 = var3.z + var0.getRandom().nextGaussian() / 2.0;
         double var12 = var0.getRandom().nextGaussian() * 0.20000000298023224;
         double var14 = var0.getRandom().nextGaussian() * 0.20000000298023224;
         double var16 = var0.getRandom().nextGaussian() * 0.20000000298023224;
         var0.addParticle(var4, var6, var8, var10, var12, var14, var16);
      }

      for(int var18 = 0; (float)var18 < (float)var2 / 1.5F; ++var18) {
         double var19 = var3.x + 3.5 * Math.cos((double)var18) + var0.getRandom().nextGaussian() / 2.0;
         double var20 = var3.y;
         double var21 = var3.z + 3.5 * Math.sin((double)var18) + var0.getRandom().nextGaussian() / 2.0;
         double var22 = var0.getRandom().nextGaussian() * 0.05000000074505806;
         double var23 = var0.getRandom().nextGaussian() * 0.05000000074505806;
         double var24 = var0.getRandom().nextGaussian() * 0.05000000074505806;
         var0.addParticle(var4, var19, var20, var21, var22, var23, var24);
      }
   }
}
