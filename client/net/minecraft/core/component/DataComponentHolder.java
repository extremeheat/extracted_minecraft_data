package net.minecraft.core.component;

import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface DataComponentHolder {
   DataComponentMap getComponents();

   @Nullable
   default <T> T get(DataComponentType<? extends T> var1) {
      return this.getComponents().get(var1);
   }

   default <T> Stream<T> getAllOfType(Class<? extends T> var1) {
      return this.getComponents().stream().map(TypedDataComponent::value).filter((var1x) -> {
         return var1.isAssignableFrom(var1x.getClass());
      }).map((var0) -> {
         return var0;
      });
   }

   default <T> T getOrDefault(DataComponentType<? extends T> var1, T var2) {
      return this.getComponents().getOrDefault(var1, var2);
   }

   default boolean has(DataComponentType<?> var1) {
      return this.getComponents().has(var1);
   }
}
