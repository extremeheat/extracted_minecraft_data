package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.util.RandomSource;

public class ShufflingList<U> implements Iterable<U> {
   protected final List<WeightedEntry<U>> entries;
   private final RandomSource random = RandomSource.create();

   public ShufflingList() {
      super();
      this.entries = Lists.newArrayList();
   }

   private ShufflingList(List<WeightedEntry<U>> var1) {
      super();
      this.entries = Lists.newArrayList(var1);
   }

   public static <U> Codec<ShufflingList<U>> codec(Codec<U> var0) {
      return ShufflingList.WeightedEntry.codec(var0).listOf().xmap(ShufflingList::new, (var0x) -> var0x.entries);
   }

   public ShufflingList<U> add(U var1, int var2) {
      this.entries.add(new WeightedEntry(var1, var2));
      return this;
   }

   public ShufflingList<U> shuffle() {
      this.entries.forEach((var1) -> var1.setRandom(this.random.nextFloat()));
      this.entries.sort(Comparator.comparingDouble(WeightedEntry::getRandWeight));
      return this;
   }

   public Stream<U> stream() {
      return this.entries.stream().map(WeightedEntry::getData);
   }

   public Iterator<U> iterator() {
      return Iterators.transform(this.entries.iterator(), WeightedEntry::getData);
   }

   public String toString() {
      return "ShufflingList[" + String.valueOf(this.entries) + "]";
   }

   public static class WeightedEntry<T> {
      final T data;
      final int weight;
      private double randWeight;

      WeightedEntry(T var1, int var2) {
         super();
         this.weight = var2;
         this.data = var1;
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

      public String toString() {
         int var10000 = this.weight;
         return var10000 + ":" + String.valueOf(this.data);
      }

      public static <E> Codec<WeightedEntry<E>> codec(final Codec<E> var0) {
         return new Codec<WeightedEntry<E>>() {
            public <T> DataResult<Pair<WeightedEntry<E>, T>> decode(DynamicOps<T> var1, T var2) {
               Dynamic var3 = new Dynamic(var1, var2);
               OptionalDynamic var10000 = var3.get("data");
               Codec var10001 = var0;
               Objects.requireNonNull(var10001);
               return var10000.flatMap(var10001::parse).map((var1x) -> new WeightedEntry(var1x, var3.get("weight").asInt(1))).map((var1x) -> Pair.of(var1x, var1.empty()));
            }

            public <T> DataResult<T> encode(WeightedEntry<E> var1, DynamicOps<T> var2, T var3) {
               return var2.mapBuilder().add("weight", var2.createInt(var1.weight)).add("data", var0.encodeStart(var2, var1.data)).build(var3);
            }

            // $FF: synthetic method
            public DataResult encode(final Object var1, final DynamicOps var2, final Object var3) {
               return this.encode((WeightedEntry)var1, var2, var3);
            }
         };
      }
   }
}
