package net.minecraft.world.entity.livingblock.cognition;

public interface Prize<V> {
   V get();

   void forget();

   boolean exists();

   public static record None<V>() implements Prize<V> {
      public None() {
         super();
      }

      public V get() {
         throw new UnsupportedOperationException();
      }

      public void forget() {
      }

      public boolean exists() {
         return false;
      }
   }
}
