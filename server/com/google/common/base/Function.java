package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;

@FunctionalInterface
@GwtCompatible
public interface Function<F, T> extends java.util.function.Function<F, T> {
   @Nullable
   @CanIgnoreReturnValue
   T apply(@Nullable F var1);

   boolean equals(@Nullable Object var1);
}
