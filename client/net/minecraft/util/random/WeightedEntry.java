package net.minecraft.util.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;

public interface WeightedEntry {
   Weight getWeight();

   static <T> WeightedEntry.Wrapper<T> wrap(T var0, int var1) {
      return new WeightedEntry.Wrapper<>((T)var0, Weight.of(var1));
   }

   public static class IntrusiveBase implements WeightedEntry {
      private final Weight weight;

      public IntrusiveBase(int var1) {
         super();
         this.weight = Weight.of(var1);
      }

      public IntrusiveBase(Weight var1) {
         super();
         this.weight = var1;
      }

      @Override
      public Weight getWeight() {
         return this.weight;
      }
   }

   public static record Wrapper<T>(T a, Weight b) implements WeightedEntry {
      private final T data;
      private final Weight weight;

      public Wrapper(T var1, Weight var2) {
         super();
         this.data = (T)var1;
         this.weight = var2;
      }

      @Override
      public Weight getWeight() {
         return this.weight;
      }

      public static <E> Codec<WeightedEntry.Wrapper<E>> codec(Codec<E> var0) {
         return RecordCodecBuilder.create(
            var1 -> var1.group(
                     var0.fieldOf("data").forGetter(WeightedEntry.Wrapper::data), Weight.CODEC.fieldOf("weight").forGetter(WeightedEntry.Wrapper::weight)
                  )
                  .apply(var1, WeightedEntry.Wrapper::new)
         );
      }
   }
}
