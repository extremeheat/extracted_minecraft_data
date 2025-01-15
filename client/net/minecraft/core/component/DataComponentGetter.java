package net.minecraft.core.component;

import javax.annotation.Nullable;

public interface DataComponentGetter {
   @Nullable
   <T> T get(DataComponentType<? extends T> var1);

   default <T> T getOrDefault(DataComponentType<? extends T> var1, T var2) {
      // $FF: Couldn't be decompiled
   }

   @Nullable
   default <T> TypedDataComponent<T> getTyped(DataComponentType<T> var1) {
      Object var2 = this.get(var1);
      return var2 != null ? new TypedDataComponent(var1, var2) : null;
   }
}
