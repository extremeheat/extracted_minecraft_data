package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class WeightedList<U> {
   protected final List<WeightedList.WeightedEntry<U>> entries;
   private final Random random;

   public WeightedList() {
      this(Lists.newArrayList());
   }

   private WeightedList(List<WeightedList.WeightedEntry<U>> var1) {
      super();
      this.random = new Random();
      this.entries = Lists.newArrayList(var1);
   }

   public static <U> Codec<WeightedList<U>> codec(Codec<U> var0) {
      return WeightedList.WeightedEntry.codec(var0).listOf().xmap(WeightedList::new, (var0x) -> {
         return var0x.entries;
      });
   }

   public WeightedList<U> add(U var1, int var2) {
      this.entries.add(new WeightedList.WeightedEntry(var1, var2));
      return this;
   }

   public WeightedList<U> shuffle() {
      return this.shuffle(this.random);
   }

   public WeightedList<U> shuffle(Random var1) {
      this.entries.forEach((var1x) -> {
         var1x.setRandom(var1.nextFloat());
      });
      this.entries.sort(Comparator.comparingDouble((var0) -> {
         return ((WeightedList.WeightedEntry)var0).getRandWeight();
      }));
      return this;
   }

   public boolean isEmpty() {
      return this.entries.isEmpty();
   }

   public Stream<U> stream() {
      return this.entries.stream().map(WeightedList.WeightedEntry::getData);
   }

   public U getOne(Random var1) {
      return this.shuffle(var1).stream().findFirst().orElseThrow(RuntimeException::new);
   }

   public String toString() {
      return "WeightedList[" + this.entries + "]";
   }

   public static class WeightedEntry<T> {
      private final T data;
      private final int weight;
      private double randWeight;

      private WeightedEntry(T var1, int var2) {
         super();
         this.weight = var2;
         this.data = var1;
      }

      private double getRandWeight() {
         return this.randWeight;
      }

      private void setRandom(float var1) {
         this.randWeight = -Math.pow((double)var1, (double)(1.0F / (float)this.weight));
      }

      public T getData() {
         return this.data;
      }

      public String toString() {
         return "" + this.weight + ":" + this.data;
      }

      public static <E> Codec<WeightedList.WeightedEntry<E>> codec(final Codec<E> var0) {
         return new Codec<WeightedList.WeightedEntry<E>>() {
            public <T> DataResult<Pair<WeightedList.WeightedEntry<E>, T>> decode(DynamicOps<T> var1, T var2) {
               Dynamic var3 = new Dynamic(var1, var2);
               OptionalDynamic var10000 = var3.get("data");
               Codec var10001 = var0;
               var10001.getClass();
               return var10000.flatMap(var10001::parse).map((var1x) -> {
                  return new WeightedList.WeightedEntry(var1x, var3.get("weight").asInt(1));
               }).map((var1x) -> {
                  return Pair.of(var1x, var1.empty());
               });
            }

            public <T> DataResult<T> encode(WeightedList.WeightedEntry<E> var1, DynamicOps<T> var2, T var3) {
               return var2.mapBuilder().add("weight", var2.createInt(var1.weight)).add("data", var0.encodeStart(var2, var1.data)).build(var3);
            }

            // $FF: synthetic method
            public DataResult encode(Object var1, DynamicOps var2, Object var3) {
               return this.encode((WeightedList.WeightedEntry)var1, var2, var3);
            }
         };
      }

      // $FF: synthetic method
      WeightedEntry(Object var1, int var2, Object var3) {
         this(var1, var2);
      }
   }
}
