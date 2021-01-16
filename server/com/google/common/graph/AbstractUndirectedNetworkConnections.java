package com.google.common.graph;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

abstract class AbstractUndirectedNetworkConnections<N, E> implements NetworkConnections<N, E> {
   protected final Map<E, N> incidentEdgeMap;

   protected AbstractUndirectedNetworkConnections(Map<E, N> var1) {
      super();
      this.incidentEdgeMap = (Map)Preconditions.checkNotNull(var1);
   }

   public Set<N> predecessors() {
      return this.adjacentNodes();
   }

   public Set<N> successors() {
      return this.adjacentNodes();
   }

   public Set<E> incidentEdges() {
      return Collections.unmodifiableSet(this.incidentEdgeMap.keySet());
   }

   public Set<E> inEdges() {
      return this.incidentEdges();
   }

   public Set<E> outEdges() {
      return this.incidentEdges();
   }

   public N oppositeNode(Object var1) {
      return Preconditions.checkNotNull(this.incidentEdgeMap.get(var1));
   }

   public N removeInEdge(Object var1, boolean var2) {
      return !var2 ? this.removeOutEdge(var1) : null;
   }

   public N removeOutEdge(Object var1) {
      Object var2 = this.incidentEdgeMap.remove(var1);
      return Preconditions.checkNotNull(var2);
   }

   public void addInEdge(E var1, N var2, boolean var3) {
      if (!var3) {
         this.addOutEdge(var1, var2);
      }

   }

   public void addOutEdge(E var1, N var2) {
      Object var3 = this.incidentEdgeMap.put(var1, var2);
      Preconditions.checkState(var3 == null);
   }
}
