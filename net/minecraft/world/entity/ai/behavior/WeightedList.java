package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class WeightedList {
   protected final List entries;
   private final Random random;

   public WeightedList(Random var1) {
      this.entries = Lists.newArrayList();
      this.random = var1;
   }

   public WeightedList() {
      this(new Random());
   }

   public WeightedList(Dynamic var1, Function var2) {
      this();
      var1.asStream().forEach((var2x) -> {
         var2x.get("data").map((var3) -> {
            Object var4 = var2.apply(var3);
            int var5 = var2x.get("weight").asInt(1);
            return this.add(var4, var5);
         });
      });
   }

   public Object serialize(DynamicOps var1, Function var2) {
      return var1.createList(this.streamEntries().map((var2x) -> {
         return var1.createMap(ImmutableMap.builder().put(var1.createString("data"), ((Dynamic)var2.apply(var2x.getData())).getValue()).put(var1.createString("weight"), var1.createInt(var2x.getWeight())).build());
      }));
   }

   public WeightedList add(Object var1, int var2) {
      this.entries.add(new WeightedList.WeightedEntry(var1, var2));
      return this;
   }

   public WeightedList shuffle() {
      return this.shuffle(this.random);
   }

   public WeightedList shuffle(Random var1) {
      this.entries.forEach((var1x) -> {
         var1x.setRandom(var1.nextFloat());
      });
      this.entries.sort(Comparator.comparingDouble((var0) -> {
         return ((WeightedList.WeightedEntry)var0).getRandWeight();
      }));
      return this;
   }

   public Stream stream() {
      return this.entries.stream().map(WeightedList.WeightedEntry::getData);
   }

   public Stream streamEntries() {
      return this.entries.stream();
   }

   public Object getOne(Random var1) {
      return this.shuffle(var1).stream().findFirst().orElseThrow(RuntimeException::new);
   }

   public String toString() {
      return "WeightedList[" + this.entries + "]";
   }

   public class WeightedEntry {
      private final Object data;
      private final int weight;
      private double randWeight;

      private WeightedEntry(Object var2, int var3) {
         this.weight = var3;
         this.data = var2;
      }

      private double getRandWeight() {
         return this.randWeight;
      }

      private void setRandom(float var1) {
         this.randWeight = -Math.pow((double)var1, (double)(1.0F / (float)this.weight));
      }

      public Object getData() {
         return this.data;
      }

      public int getWeight() {
         return this.weight;
      }

      public String toString() {
         return "" + this.weight + ":" + this.data;
      }

      // $FF: synthetic method
      WeightedEntry(Object var2, int var3, Object var4) {
         this(var2, var3);
      }
   }
}
