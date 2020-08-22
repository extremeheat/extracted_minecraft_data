package net.minecraft.world.level.levelgen.synth;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public class PerlinNoise implements SurfaceNoise {
   private final ImprovedNoise[] noiseLevels;
   private final double highestFreqValueFactor;
   private final double highestFreqInputFactor;

   public PerlinNoise(WorldgenRandom var1, int var2, int var3) {
      this(var1, new IntRBTreeSet(IntStream.rangeClosed(-var2, var3).toArray()));
   }

   public PerlinNoise(WorldgenRandom var1, IntSortedSet var2) {
      if (var2.isEmpty()) {
         throw new IllegalArgumentException("Need some octaves!");
      } else {
         int var3 = -var2.firstInt();
         int var4 = var2.lastInt();
         int var5 = var3 + var4 + 1;
         if (var5 < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
         } else {
            ImprovedNoise var6 = new ImprovedNoise(var1);
            int var7 = var4;
            this.noiseLevels = new ImprovedNoise[var5];
            if (var4 >= 0 && var4 < var5 && var2.contains(0)) {
               this.noiseLevels[var4] = var6;
            }

            for(int var8 = var4 + 1; var8 < var5; ++var8) {
               if (var8 >= 0 && var2.contains(var7 - var8)) {
                  this.noiseLevels[var8] = new ImprovedNoise(var1);
               } else {
                  var1.consumeCount(262);
               }
            }

            if (var4 > 0) {
               long var12 = (long)(var6.noise(0.0D, 0.0D, 0.0D, 0.0D, 0.0D) * 9.223372036854776E18D);
               WorldgenRandom var10 = new WorldgenRandom(var12);

               for(int var11 = var7 - 1; var11 >= 0; --var11) {
                  if (var11 < var5 && var2.contains(var7 - var11)) {
                     this.noiseLevels[var11] = new ImprovedNoise(var10);
                  } else {
                     var10.consumeCount(262);
                  }
               }
            }

            this.highestFreqInputFactor = Math.pow(2.0D, (double)var4);
            this.highestFreqValueFactor = 1.0D / (Math.pow(2.0D, (double)var5) - 1.0D);
         }
      }
   }

   public double getValue(double var1, double var3, double var5) {
      return this.getValue(var1, var3, var5, 0.0D, 0.0D, false);
   }

   public double getValue(double var1, double var3, double var5, double var7, double var9, boolean var11) {
      double var12 = 0.0D;
      double var14 = this.highestFreqInputFactor;
      double var16 = this.highestFreqValueFactor;
      ImprovedNoise[] var18 = this.noiseLevels;
      int var19 = var18.length;

      for(int var20 = 0; var20 < var19; ++var20) {
         ImprovedNoise var21 = var18[var20];
         if (var21 != null) {
            var12 += var21.noise(wrap(var1 * var14), var11 ? -var21.yo : wrap(var3 * var14), wrap(var5 * var14), var7 * var14, var9 * var14) * var16;
         }

         var14 /= 2.0D;
         var16 *= 2.0D;
      }

      return var12;
   }

   @Nullable
   public ImprovedNoise getOctaveNoise(int var1) {
      return this.noiseLevels[var1];
   }

   public static double wrap(double var0) {
      return var0 - (double)Mth.lfloor(var0 / 3.3554432E7D + 0.5D) * 3.3554432E7D;
   }

   public double getSurfaceNoiseValue(double var1, double var3, double var5, double var7) {
      return this.getValue(var1, var3, 0.0D, var5, var7, false);
   }
}
