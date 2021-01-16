package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.errorprone.annotations.CompatibleWith;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public interface Graph<N> {
   Set<N> nodes();

   Set<EndpointPair<N>> edges();

   boolean isDirected();

   boolean allowsSelfLoops();

   ElementOrder<N> nodeOrder();

   Set<N> adjacentNodes(@CompatibleWith("N") Object var1);

   Set<N> predecessors(@CompatibleWith("N") Object var1);

   Set<N> successors(@CompatibleWith("N") Object var1);

   int degree(@CompatibleWith("N") Object var1);

   int inDegree(@CompatibleWith("N") Object var1);

   int outDegree(@CompatibleWith("N") Object var1);

   boolean equals(@Nullable Object var1);

   int hashCode();
}
