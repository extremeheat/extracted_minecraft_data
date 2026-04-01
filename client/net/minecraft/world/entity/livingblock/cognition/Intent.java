package net.minecraft.world.entity.livingblock.cognition;

import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;

public interface Intent<V> {
   void update(V value);

   void clear();

   static <V> Intent<V> consuming(final Consumer<@Nullable V> update, final @Nullable V clearValue) {
      return new Intent<V>() {
         public void update(final V value) {
            update.accept(value);
         }

         public void clear() {
            update.accept(clearValue);
         }
      };
   }

   public static record None<V>() implements Intent<V> {
      public None() {
         super();
      }

      public void update(final V value) {
      }

      public void clear() {
      }
   }
}
