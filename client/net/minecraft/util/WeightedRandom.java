package net.minecraft.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class WeightedRandom {
   public static int func_76272_a(Collection<? extends WeightedRandom.Item> var0) {
      int var1 = 0;

      WeightedRandom.Item var3;
      for(Iterator var2 = var0.iterator(); var2.hasNext(); var1 += var3.field_76292_a) {
         var3 = (WeightedRandom.Item)var2.next();
      }

      return var1;
   }

   public static <T extends WeightedRandom.Item> T func_76273_a(Random var0, Collection<T> var1, int var2) {
      if (var2 <= 0) {
         throw new IllegalArgumentException();
      } else {
         int var3 = var0.nextInt(var2);
         return func_180166_a(var1, var3);
      }
   }

   public static <T extends WeightedRandom.Item> T func_180166_a(Collection<T> var0, int var1) {
      Iterator var2 = var0.iterator();

      WeightedRandom.Item var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (WeightedRandom.Item)var2.next();
         var1 -= var3.field_76292_a;
      } while(var1 >= 0);

      return var3;
   }

   public static <T extends WeightedRandom.Item> T func_76271_a(Random var0, Collection<T> var1) {
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
