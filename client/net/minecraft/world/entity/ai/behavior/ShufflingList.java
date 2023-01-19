package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.RandomSource;

public class ShufflingList<U> {
   protected final List<ShufflingList.WeightedEntry<U>> entries;
   private final RandomSource random = RandomSource.create();

   public ShufflingList() {
      super();
      this.entries = Lists.newArrayList();
   }

   private ShufflingList(List<ShufflingList.WeightedEntry<U>> var1) {
      super();
      this.entries = Lists.newArrayList(var1);
   }

   public static <U> Codec<ShufflingList<U>> codec(Codec<U> var0) {
      return ShufflingList.WeightedEntry.codec(var0).listOf().xmap(ShufflingList::new, var0x -> var0x.entries);
   }

   public ShufflingList<U> add(U var1, int var2) {
      this.entries.add(new ShufflingList.WeightedEntry<>((U)var1, var2));
      return this;
   }

   public ShufflingList<U> shuffle() {
      this.entries.forEach(var1 -> var1.setRandom(this.random.nextFloat()));
      this.entries.sort(Comparator.comparingDouble(ShufflingList.WeightedEntry::getRandWeight));
      return this;
   }

   public Stream<U> stream() {
      return this.entries.stream().map(ShufflingList.WeightedEntry::getData);
   }

   @Override
   public String toString() {
      return "ShufflingList[" + this.entries + "]";
   }

   public static class WeightedEntry<T> {
      final T data;
      final int weight;
      private double randWeight;

      WeightedEntry(T var1, int var2) {
         super();
         this.weight = var2;
         this.data = (T)var1;
      }

      private double getRandWeight() {
         return this.randWeight;
      }

      void setRandom(float var1) {
         this.randWeight = -Math.pow((double)var1, (double)(1.0F / (float)this.weight));
      }

      public T getData() {
         return this.data;
      }

      public int getWeight() {
         return this.weight;
      }

      @Override
      public String toString() {
         return this.weight + ":" + this.data;
      }

      public static <E> Codec<ShufflingList.WeightedEntry<E>> codec(final Codec<E> var0) {
         return new Codec<ShufflingList.WeightedEntry<E>>() {
            public <T> DataResult<Pair<ShufflingList.WeightedEntry<E>, T>> decode(DynamicOps<T> var1, T var2) {
               Dynamic var3 = new Dynamic(var1, var2);
               return var3.get("data")
                  .flatMap(var0::parse)
                  .map(var1x -> new ShufflingList.WeightedEntry<>(var1x, var3.get("weight").asInt(1)))
                  .map(var1x -> Pair.of(var1x, var1.empty()));
            }

            public <T> DataResult<T> encode(ShufflingList.WeightedEntry<E> var1, DynamicOps<T> var2, T var3) {
               return var2.mapBuilder().add("weight", var2.createInt(var1.weight)).add("data", var0.encodeStart(var2, var1.data)).build(var3);
            }
         };
      }
   }
}
