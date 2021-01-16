package com.google.common.graph;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;

class ConfigurableValueGraph<N, V> extends AbstractValueGraph<N, V> {
   private final boolean isDirected;
   private final boolean allowsSelfLoops;
   private final ElementOrder<N> nodeOrder;
   protected final MapIteratorCache<N, GraphConnections<N, V>> nodeConnections;
   protected long edgeCount;

   ConfigurableValueGraph(AbstractGraphBuilder<? super N> var1) {
      this(var1, var1.nodeOrder.createMap((Integer)var1.expectedNodeCount.or((int)10)), 0L);
   }

   ConfigurableValueGraph(AbstractGraphBuilder<? super N> var1, Map<N, GraphConnections<N, V>> var2, long var3) {
      super();
      this.isDirected = var1.directed;
      this.allowsSelfLoops = var1.allowsSelfLoops;
      this.nodeOrder = var1.nodeOrder.cast();
      this.nodeConnections = (MapIteratorCache)(var2 instanceof TreeMap ? new MapRetrievalCache(var2) : new MapIteratorCache(var2));
      this.edgeCount = Graphs.checkNonNegative(var3);
   }

   public Set<N> nodes() {
      return this.nodeConnections.unmodifiableKeySet();
   }

   public boolean isDirected() {
      return this.isDirected;
   }

   public boolean allowsSelfLoops() {
      return this.allowsSelfLoops;
   }

   public ElementOrder<N> nodeOrder() {
      return this.nodeOrder;
   }

   public Set<N> adjacentNodes(Object var1) {
      return this.checkedConnections(var1).adjacentNodes();
   }

   public Set<N> predecessors(Object var1) {
      return this.checkedConnections(var1).predecessors();
   }

   public Set<N> successors(Object var1) {
      return this.checkedConnections(var1).successors();
   }

   public V edgeValueOrDefault(Object var1, Object var2, @Nullable V var3) {
      GraphConnections var4 = (GraphConnections)this.nodeConnections.get(var1);
      if (var4 == null) {
         return var3;
      } else {
         Object var5 = var4.value(var2);
         return var5 == null ? var3 : var5;
      }
   }

   protected long edgeCount() {
      return this.edgeCount;
   }

   protected final GraphConnections<N, V> checkedConnections(Object var1) {
      GraphConnections var2 = (GraphConnections)this.nodeConnections.get(var1);
      if (var2 == null) {
         Preconditions.checkNotNull(var1);
         throw new IllegalArgumentException(String.format("Node %s is not an element of this graph.", var1));
      } else {
         return var2;
      }
   }

   protected final boolean containsNode(@Nullable Object var1) {
      return this.nodeConnections.containsKey(var1);
   }
}
