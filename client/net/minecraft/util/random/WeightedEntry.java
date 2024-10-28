package net.minecraft.util.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public interface WeightedEntry {
   Weight getWeight();

   static <T> Wrapper<T> wrap(T var0, int var1) {
      return new Wrapper(var0, Weight.of(var1));
   }

   public static record Wrapper<T>(T data, Weight weight) implements WeightedEntry {
      public Wrapper(T data, Weight weight) {
         super();
         this.data = data;
         this.weight = weight;
      }

      public Weight getWeight() {
         return this.weight;
      }

      public static <E> Codec<Wrapper<E>> codec(Codec<E> var0) {
         return RecordCodecBuilder.create((var1) -> {
            return var1.group(var0.fieldOf("data").forGetter(Wrapper::data), Weight.CODEC.fieldOf("weight").forGetter(Wrapper::weight)).apply(var1, Wrapper::new);
         });
      }

      public T data() {
         return this.data;
      }

      public Weight weight() {
         return this.weight;
      }
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

      public Weight getWeight() {
         return this.weight;
      }
   }
}
