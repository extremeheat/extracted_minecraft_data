package net.minecraft.util;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleUtils {
   public ParticleUtils() {
      super();
   }

   public static void spawnParticlesOnBlockFaces(Level var0, BlockPos var1, ParticleOptions var2, IntProvider var3) {
      Direction[] var4 = Direction.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction var7 = var4[var6];
         spawnParticlesOnBlockFace(var0, var1, var2, var3, var7, () -> {
            return getRandomSpeedRanges(var0.random);
         }, 0.55);
      }

   }

   public static void spawnParticlesOnBlockFace(Level var0, BlockPos var1, ParticleOptions var2, IntProvider var3, Direction var4, Supplier<Vec3> var5, double var6) {
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
}
