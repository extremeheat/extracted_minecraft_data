package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.errorprone.annotations.CompatibleWith;
import javax.annotation.Nullable;

@Beta
public interface ValueGraph<N, V> extends Graph<N> {
   V edgeValue(@CompatibleWith("N") Object var1, @CompatibleWith("N") Object var2);

   V edgeValueOrDefault(@CompatibleWith("N") Object var1, @CompatibleWith("N") Object var2, @Nullable V var3);

   boolean equals(@Nullable Object var1);

   int hashCode();
}
