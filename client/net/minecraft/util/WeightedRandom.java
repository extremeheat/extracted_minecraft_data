package net.minecraft.util;

import java.util.Collection;
import java.util.Random;

public class WeightedRandom {
   public static int func_76272_a(Collection var0) {
      int var1 = 0;

      for(WeightedRandom$Item var3 : var0) {
         var1 += var3.field_76292_a;
      }

      return var1;
   }

   public static WeightedRandom$Item func_76273_a(Random var0, Collection var1, int var2) {
      if (var2 <= 0) {
         throw new IllegalArgumentException();
      } else {
         int var3 = var0.nextInt(var2);

         for(WeightedRandom$Item var5 : var1) {
            var3 -= var5.field_76292_a;
            if (var3 < 0) {
               return var5;
            }
         }

         return null;
      }
   }

   public static WeightedRandom$Item func_76271_a(Random var0, Collection var1) {
      return func_76273_a(var0, var1, func_76272_a(var1));
   }

   public static int func_76270_a(WeightedRandom$Item[] var0) {
      int var1 = 0;

      for(WeightedRandom$Item var5 : var0) {
         var1 += var5.field_76292_a;
      }

      return var1;
   }

   public static WeightedRandom$Item func_76269_a(Random var0, WeightedRandom$Item[] var1, int var2) {
      if (var2 <= 0) {
         throw new IllegalArgumentException();
      } else {
         int var3 = var0.nextInt(var2);

         for(WeightedRandom$Item var7 : var1) {
            var3 -= var7.field_76292_a;
            if (var3 < 0) {
               return var7;
            }
         }

         return null;
      }
   }

   public static WeightedRandom$Item func_76274_a(Random var0, WeightedRandom$Item[] var1) {
      return func_76269_a(var0, var1, func_76270_a(var1));
   }
}
