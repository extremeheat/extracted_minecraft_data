package net.minecraft.util.random;

import java.util.Iterator;
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

      WeightedEntry var4;
      for(Iterator var3 = var0.iterator(); var3.hasNext(); var1 += (long)var4.getWeight().asInt()) {
         var4 = (WeightedEntry)var3.next();
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
      Iterator var2 = var0.iterator();

      WeightedEntry var3;
      do {
         if (!var2.hasNext()) {
            return Optional.empty();
         }

         var3 = (WeightedEntry)var2.next();
         var1 -= var3.getWeight().asInt();
      } while(var1 >= 0);

      return Optional.of(var3);
   }

   public static <T extends WeightedEntry> Optional<T> getRandomItem(RandomSource var0, List<T> var1) {
      return getRandomItem(var0, var1, getTotalWeight(var1));
   }
}
