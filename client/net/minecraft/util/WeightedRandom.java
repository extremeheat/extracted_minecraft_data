package net.minecraft.util;

import java.util.List;
import java.util.Random;

public class WeightedRandom {
   public static int func_76272_a(List<? extends WeightedRandom.Item> var0) {
      int var1 = 0;
      int var2 = 0;

      for(int var3 = var0.size(); var2 < var3; ++var2) {
         WeightedRandom.Item var4 = (WeightedRandom.Item)var0.get(var2);
         var1 += var4.field_76292_a;
      }

      return var1;
   }

   public static <T extends WeightedRandom.Item> T func_76273_a(Random var0, List<T> var1, int var2) {
      if (var2 <= 0) {
         throw new IllegalArgumentException();
      } else {
         int var3 = var0.nextInt(var2);
         return func_180166_a(var1, var3);
      }
   }

   public static <T extends WeightedRandom.Item> T func_180166_a(List<T> var0, int var1) {
      int var2 = 0;

      for(int var3 = var0.size(); var2 < var3; ++var2) {
         WeightedRandom.Item var4 = (WeightedRandom.Item)var0.get(var2);
         var1 -= var4.field_76292_a;
         if (var1 < 0) {
            return var4;
         }
      }

      return null;
   }

   public static <T extends WeightedRandom.Item> T func_76271_a(Random var0, List<T> var1) {
      return func_76273_a(var0, var1, func_76272_a(var1));
   }

   public static class Item {
      protected int field_76292_a;

      public Item(int var1) {
         super();
         this.field_76292_a = var1;
      }
   }
}
