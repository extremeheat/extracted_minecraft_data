package net.minecraft.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleUtils {
   public ParticleUtils() {
      super();
   }

   public static void spawnParticlesOnBlockFaces(Level var0, BlockPos var1, ParticleOptions var2, UniformInt var3) {
      Direction[] var4 = Direction.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction var7 = var4[var6];
         int var8 = var3.sample(var0.random);

         for(int var9 = 0; var9 < var8; ++var9) {
            spawnParticleOnFace(var0, var1, var7, var2);
         }
      }

   }

   public static void spawnParticlesAlongAxis(Direction.Axis var0, Level var1, BlockPos var2, double var3, ParticleOptions var5, UniformInt var6) {
      Vec3 var7 = Vec3.atCenterOf(var2);
      boolean var8 = var0 == Direction.Axis.field_500;
      boolean var9 = var0 == Direction.Axis.field_501;
      boolean var10 = var0 == Direction.Axis.field_502;
      int var11 = var6.sample(var1.random);

      for(int var12 = 0; var12 < var11; ++var12) {
         double var13 = var7.field_414 + Mth.nextDouble(var1.random, -1.0D, 1.0D) * (var8 ? 0.5D : var3);
         double var15 = var7.field_415 + Mth.nextDouble(var1.random, -1.0D, 1.0D) * (var9 ? 0.5D : var3);
         double var17 = var7.field_416 + Mth.nextDouble(var1.random, -1.0D, 1.0D) * (var10 ? 0.5D : var3);
         double var19 = var8 ? Mth.nextDouble(var1.random, -1.0D, 1.0D) : 0.0D;
         double var21 = var9 ? Mth.nextDouble(var1.random, -1.0D, 1.0D) : 0.0D;
         double var23 = var10 ? Mth.nextDouble(var1.random, -1.0D, 1.0D) : 0.0D;
         var1.addParticle(var5, var13, var15, var17, var19, var21, var23);
      }

   }

   public static void spawnParticleOnFace(Level var0, BlockPos var1, Direction var2, ParticleOptions var3) {
      Vec3 var4 = Vec3.atCenterOf(var1);
      int var5 = var2.getStepX();
      int var6 = var2.getStepY();
      int var7 = var2.getStepZ();
      double var8 = var4.field_414 + (var5 == 0 ? Mth.nextDouble(var0.random, -0.5D, 0.5D) : (double)var5 * 0.55D);
      double var10 = var4.field_415 + (var6 == 0 ? Mth.nextDouble(var0.random, -0.5D, 0.5D) : (double)var6 * 0.55D);
      double var12 = var4.field_416 + (var7 == 0 ? Mth.nextDouble(var0.random, -0.5D, 0.5D) : (double)var7 * 0.55D);
      double var14 = var5 == 0 ? Mth.nextDouble(var0.random, -1.0D, 1.0D) : 0.0D;
      double var16 = var6 == 0 ? Mth.nextDouble(var0.random, -1.0D, 1.0D) : 0.0D;
      double var18 = var7 == 0 ? Mth.nextDouble(var0.random, -1.0D, 1.0D) : 0.0D;
      var0.addParticle(var3, var8, var10, var12, var14, var16, var18);
   }
}
