package net.minecraft.util;

import java.util.List;
import java.util.Random;
import net.minecraft.Util;

public class WeighedRandom {
   public static int getTotalWeight(List<? extends WeighedRandom.WeighedRandomItem> var0) {
      int var1 = 0;
      int var2 = 0;

      for(int var3 = var0.size(); var2 < var3; ++var2) {
         WeighedRandom.WeighedRandomItem var4 = (WeighedRandom.WeighedRandomItem)var0.get(var2);
         var1 += var4.weight;
      }

      return var1;
   }

   public static <T extends WeighedRandom.WeighedRandomItem> T getRandomItem(Random var0, List<T> var1, int var2) {
      if (var2 <= 0) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException());
      } else {
         int var3 = var0.nextInt(var2);
         return getWeightedItem(var1, var3);
      }
   }

   public static <T extends WeighedRandom.WeighedRandomItem> T getWeightedItem(List<T> var0, int var1) {
      int var2 = 0;

      for(int var3 = var0.size(); var2 < var3; ++var2) {
         WeighedRandom.WeighedRandomItem var4 = (WeighedRandom.WeighedRandomItem)var0.get(var2);
         var1 -= var4.weight;
         if (var1 < 0) {
            return var4;
         }
      }

      return null;
   }

   public static <T extends WeighedRandom.WeighedRandomItem> T getRandomItem(Random var0, List<T> var1) {
      return getRandomItem(var0, var1, getTotalWeight(var1));
   }

   public static class WeighedRandomItem {
      protected final int weight;

      public WeighedRandomItem(int var1) {
         super();
         this.weight = var1;
      }
   }
}
