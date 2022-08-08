package net.minecraft.util.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public interface WeightedEntry {
   Weight getWeight();

   static <T> Wrapper<T> wrap(T var0, int var1) {
      return new Wrapper(var0, Weight.of(var1));
   }

   public static class Wrapper<T> implements WeightedEntry {
      private final T data;
      private final Weight weight;

      Wrapper(T var1, Weight var2) {
         super();
         this.data = var1;
         this.weight = var2;
      }

      public T getData() {
         return this.data;
      }

      public Weight getWeight() {
         return this.weight;
      }

      public static <E> Codec<Wrapper<E>> codec(Codec<E> var0) {
         return RecordCodecBuilder.create((var1) -> {
            return var1.group(var0.fieldOf("data").forGetter(Wrapper::getData), Weight.CODEC.fieldOf("weight").forGetter(Wrapper::getWeight)).apply(var1, Wrapper::new);
         });
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
