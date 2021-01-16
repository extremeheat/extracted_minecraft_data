package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;

@FunctionalInterface
@GwtCompatible
public interface Predicate<T> extends java.util.function.Predicate<T> {
   @CanIgnoreReturnValue
   boolean apply(@Nullable T var1);

   boolean equals(@Nullable Object var1);

   default boolean test(@Nullable T var1) {
      return this.apply(var1);
   }
}
