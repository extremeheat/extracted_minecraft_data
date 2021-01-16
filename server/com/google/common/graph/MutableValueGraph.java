package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;

@Beta
public interface MutableValueGraph<N, V> extends ValueGraph<N, V> {
   @CanIgnoreReturnValue
   boolean addNode(N var1);

   @CanIgnoreReturnValue
   V putEdgeValue(N var1, N var2, V var3);

   @CanIgnoreReturnValue
   boolean removeNode(@CompatibleWith("N") Object var1);

   @CanIgnoreReturnValue
   V removeEdge(@CompatibleWith("N") Object var1, @CompatibleWith("N") Object var2);
}
