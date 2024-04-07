package net.minecraft.world.level.levelgen.synth;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public class PerlinSimplexNoise {
   private final SimplexNoise[] noiseLevels;
   private final double highestFreqValueFactor;
   private final double highestFreqInputFactor;

   public PerlinSimplexNoise(RandomSource var1, List<Integer> var2) {
      this(var1, new IntRBTreeSet(var2));
   }

   private PerlinSimplexNoise(RandomSource var1, IntSortedSet var2) {
      super();
      if (var2.isEmpty()) {
         throw new IllegalArgumentException("Need some octaves!");
      } else {
         int var3 = -var2.firstInt();
         int var4 = var2.lastInt();
         int var5 = var3 + var4 + 1;
         if (var5 < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
         } else {
            SimplexNoise var6 = new SimplexNoise(var1);
            int var7 = var4;
            this.noiseLevels = new SimplexNoise[var5];
            if (var4 >= 0 && var4 < var5 && var2.contains(0)) {
               this.noiseLevels[var4] = var6;
            }

            for (int var8 = var4 + 1; var8 < var5; var8++) {
               if (var8 >= 0 && var2.contains(var7 - var8)) {
                  this.noiseLevels[var8] = new SimplexNoise(var1);
               } else {
                  var1.consumeCount(262);
               }
            }

            if (var4 > 0) {
               long var12 = (long)(var6.getValue(var6.xo, var6.yo, var6.zo) * 9.223372036854776E18);
               WorldgenRandom var10 = new WorldgenRandom(new LegacyRandomSource(var12));

               for (int var11 = var7 - 1; var11 >= 0; var11--) {
                  if (var11 < var5 && var2.contains(var7 - var11)) {
                     this.noiseLevels[var11] = new SimplexNoise(var10);
                  } else {
                     var10.consumeCount(262);
                  }
               }
            }

            this.highestFreqInputFactor = Math.pow(2.0, (double)var4);
            this.highestFreqValueFactor = 1.0 / (Math.pow(2.0, (double)var5) - 1.0);
         }
      }
   }

   public double getValue(double var1, double var3, boolean var5) {
      double var6 = 0.0;
      double var8 = this.highestFreqInputFactor;
      double var10 = this.highestFreqValueFactor;

      for (SimplexNoise var15 : this.noiseLevels) {
         if (var15 != null) {
            var6 += var15.getValue(var1 * var8 + (var5 ? var15.xo : 0.0), var3 * var8 + (var5 ? var15.yo : 0.0)) * var10;
         }

         var8 /= 2.0;
         var10 *= 2.0;
      }

      return var6;
   }
}
