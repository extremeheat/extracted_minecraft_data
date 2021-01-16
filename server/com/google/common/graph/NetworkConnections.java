package com.google.common.graph;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Set;

interface NetworkConnections<N, E> {
   Set<N> adjacentNodes();

   Set<N> predecessors();

   Set<N> successors();

   Set<E> incidentEdges();

   Set<E> inEdges();

   Set<E> outEdges();

   Set<E> edgesConnecting(Object var1);

   N oppositeNode(Object var1);

   @CanIgnoreReturnValue
   N removeInEdge(Object var1, boolean var2);

   @CanIgnoreReturnValue
   N removeOutEdge(Object var1);

   void addInEdge(E var1, N var2, boolean var3);

   void addOutEdge(E var1, N var2);
}
