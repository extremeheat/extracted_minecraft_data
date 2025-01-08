package net.minecraft.util.random;

import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;

public class WeightedRandom {
   private WeightedRandom() {
      super();
   }

   public static <T> int getTotalWeight(List<T> var0, ToIntFunction<T> var1) {
      long var2 = 0L;

      for(Object var5 : var0) {
         var2 += (long)var1.applyAsInt(var5);
      }

      if (var2 > 2147483647L) {
         throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
      } else {
         return (int)var2;
      }
   }

   public static <T> Optional<T> getRandomItem(RandomSource var0, List<T> var1, int var2, ToIntFunction<T> var3) {
      if (var2 < 0) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("Negative total weight in getRandomItem"));
      } else if (var2 == 0) {
         return Optional.empty();
      } else {
         int var4 = var0.nextInt(var2);
         return getWeightedItem(var1, var4, var3);
      }
   }

   public static <T> Optional<T> getWeightedItem(List<T> var0, int var1, ToIntFunction<T> var2) {
      for(Object var4 : var0) {
         var1 -= var2.applyAsInt(var4);
         if (var1 < 0) {
            return Optional.of(var4);
         }
      }

      return Optional.empty();
   }

   public static <T> Optional<T> getRandomItem(RandomSource var0, List<T> var1, ToIntFunction<T> var2) {
      return getRandomItem(var0, var1, getTotalWeight(var1, var2), var2);
   }
}
