package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class WeightedList<U> {
   private final List<WeightedList<U>.WeightedEntry<? extends U>> entries;
   private final Random random;

   public WeightedList() {
      this(new Random());
   }

   public WeightedList(Random var1) {
      super();
      this.entries = Lists.newArrayList();
      this.random = var1;
   }

   public void add(U var1, int var2) {
      this.entries.add(new WeightedList.WeightedEntry(var1, var2));
   }

   public void shuffle() {
      this.entries.forEach((var1) -> {
         var1.setRandom(this.random.nextFloat());
      });
      this.entries.sort(Comparator.comparingDouble(WeightedList.WeightedEntry::getRandWeight));
   }

   public Stream<? extends U> stream() {
      return this.entries.stream().map(WeightedList.WeightedEntry::getData);
   }

   public String toString() {
      return "WeightedList[" + this.entries + "]";
   }

   class WeightedEntry<T> {
      private final T data;
      private final int weight;
      private double randWeight;

      private WeightedEntry(T var2, int var3) {
         super();
         this.weight = var3;
         this.data = var2;
      }

      public double getRandWeight() {
         return this.randWeight;
      }

      public void setRandom(float var1) {
         this.randWeight = -Math.pow((double)var1, (double)(1.0F / (float)this.weight));
      }

      public T getData() {
         return this.data;
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
