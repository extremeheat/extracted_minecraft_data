package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class UndirectedGraphConnections<N, V> implements GraphConnections<N, V> {
   private final Map<N, V> adjacentNodeValues;

   private UndirectedGraphConnections(Map<N, V> var1) {
      super();
      this.adjacentNodeValues = (Map)Preconditions.checkNotNull(var1);
   }

   static <N, V> UndirectedGraphConnections<N, V> of() {
      return new UndirectedGraphConnections(new HashMap(2, 1.0F));
   }

   static <N, V> UndirectedGraphConnections<N, V> ofImmutable(Map<N, V> var0) {
      return new UndirectedGraphConnections(ImmutableMap.copyOf(var0));
   }

   public Set<N> adjacentNodes() {
      return Collections.unmodifiableSet(this.adjacentNodeValues.keySet());
   }

   public Set<N> predecessors() {
      return this.adjacentNodes();
   }

   public Set<N> successors() {
      return this.adjacentNodes();
   }

   public V value(Object var1) {
      return this.adjacentNodeValues.get(var1);
   }

   public void removePredecessor(Object var1) {
      this.removeSuccessor(var1);
   }

   public V removeSuccessor(Object var1) {
      return this.adjacentNodeValues.remove(var1);
   }

   public void addPredecessor(N var1, V var2) {
      this.addSuccessor(var1, var2);
   }

   public V addSuccessor(N var1, V var2) {
      return this.adjacentNodeValues.put(var1, var2);
   }
}
