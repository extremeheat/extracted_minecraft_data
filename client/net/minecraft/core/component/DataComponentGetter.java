package net.minecraft.core.component;

import org.jspecify.annotations.Nullable;

public interface DataComponentGetter {
   <T> @Nullable T get(DataComponentType<? extends T> var1);

   default <T> T getOrDefault(DataComponentType<? extends T> var1, T var2) {
      Object var3 = this.get(var1);
      return var3 != null ? var3 : var2;
   }

   default <T> @Nullable TypedDataComponent<T> getTyped(DataComponentType<T> var1) {
      Object var2 = this.get(var1);
      return var2 != null ? new TypedDataComponent(var1, var2) : null;
   }
}
