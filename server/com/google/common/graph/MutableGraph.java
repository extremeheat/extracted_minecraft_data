package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;

@Beta
public interface MutableGraph<N> extends Graph<N> {
   @CanIgnoreReturnValue
   boolean addNode(N var1);

   @CanIgnoreReturnValue
   boolean putEdge(N var1, N var2);

   @CanIgnoreReturnValue
   boolean removeNode(@CompatibleWith("N") Object var1);

   @CanIgnoreReturnValue
   boolean removeEdge(@CompatibleWith("N") Object var1, @CompatibleWith("N") Object var2);
}
