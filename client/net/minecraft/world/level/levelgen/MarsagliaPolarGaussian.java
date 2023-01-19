package net.minecraft.world.level.levelgen;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class MarsagliaPolarGaussian {
   public final RandomSource randomSource;
   private double nextNextGaussian;
   private boolean haveNextNextGaussian;

   public MarsagliaPolarGaussian(RandomSource var1) {
      super();
      this.randomSource = var1;
   }

   public void reset() {
      this.haveNextNextGaussian = false;
   }

   public double nextGaussian() {
      if (this.haveNextNextGaussian) {
         this.haveNextNextGaussian = false;
         return this.nextNextGaussian;
      } else {
         double var1;
         double var3;
         double var5;
         do {
            var1 = 2.0 * this.randomSource.nextDouble() - 1.0;
            var3 = 2.0 * this.randomSource.nextDouble() - 1.0;
            var5 = Mth.square(var1) + Mth.square(var3);
         } while(var5 >= 1.0 || var5 == 0.0);

         double var7 = Math.sqrt(-2.0 * Math.log(var5) / var5);
         this.nextNextGaussian = var3 * var7;
         this.haveNextNextGaussian = true;
         return var1 * var7;
      }
   }
}
