package net.minecraft.util.random;

import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;

public class WeightedRandom {
   private WeightedRandom() {
      super();
   }

   public static int getTotalWeight(List<? extends WeightedEntry> var0) {
      long var1 = 0L;

      for(WeightedEntry var4 : var0) {
         var1 += (long)var4.getWeight().asInt();
      }

      if (var1 > 2147483647L) {
         throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
      } else {
         return (int)var1;
      }
   }

   public static <T extends WeightedEntry> Optional<T> getRandomItem(RandomSource var0, List<T> var1, int var2) {
      if (var2 < 0) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("Negative total weight in getRandomItem"));
      } else if (var2 == 0) {
         return Optional.empty();
      } else {
         int var3 = var0.nextInt(var2);
         return getWeightedItem(var1, var3);
      }
   }

   public static <T extends WeightedEntry> Optional<T> getWeightedItem(List<T> var0, int var1) {
      for(WeightedEntry var3 : var0) {
         var1 -= var3.getWeight().asInt();
         if (var1 < 0) {
            return Optional.of(var3);
         }
      }

      return Optional.empty();
   }

   public static <T extends WeightedEntry> Optional<T> getRandomItem(RandomSource var0, List<T> var1) {
      return getRandomItem(var0, var1, getTotalWeight(var1));
   }
}
