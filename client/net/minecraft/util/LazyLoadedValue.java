package net.minecraft.util;

import com.google.common.base.Suppliers;
import java.util.Objects;
import java.util.function.Supplier;

/** @deprecated */
@Deprecated
public class LazyLoadedValue<T> {
   private final Supplier<T> factory;

   public LazyLoadedValue(Supplier<T> var1) {
      super();
      Objects.requireNonNull(var1);
      this.factory = Suppliers.memoize(var1::get);
   }

   public T get() {
      return this.factory.get();
   }
}
