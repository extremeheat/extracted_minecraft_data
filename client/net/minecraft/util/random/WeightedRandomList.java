package net.minecraft.util.random;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.RandomSource;

public class WeightedRandomList<E extends WeightedEntry> {
   private final int totalWeight;
   private final ImmutableList<E> items;

   WeightedRandomList(List<? extends E> var1) {
      super();
      this.items = ImmutableList.copyOf(var1);
      this.totalWeight = WeightedRandom.getTotalWeight(var1);
   }

   public static <E extends WeightedEntry> WeightedRandomList<E> create() {
      return new WeightedRandomList(ImmutableList.of());
   }

   @SafeVarargs
   public static <E extends WeightedEntry> WeightedRandomList<E> create(E... var0) {
      return new WeightedRandomList(ImmutableList.copyOf(var0));
   }

   public static <E extends WeightedEntry> WeightedRandomList<E> create(List<E> var0) {
      return new WeightedRandomList(var0);
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public Optional<E> getRandom(RandomSource var1) {
      if (this.totalWeight == 0) {
         return Optional.empty();
      } else {
         int var2 = var1.nextInt(this.totalWeight);
         return WeightedRandom.getWeightedItem(this.items, var2);
      }
   }

   public List<E> unwrap() {
      return this.items;
   }

   public static <E extends WeightedEntry> Codec<WeightedRandomList<E>> codec(Codec<E> var0) {
      return var0.listOf().xmap(WeightedRandomList::create, WeightedRandomList::unwrap);
   }

   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         WeightedRandomList var2 = (WeightedRandomList)var1;
         return this.totalWeight == var2.totalWeight && Objects.equals(this.items, var2.items);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.totalWeight, this.items});
   }
}
