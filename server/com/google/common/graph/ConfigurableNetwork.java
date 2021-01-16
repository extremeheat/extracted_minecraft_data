package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;

class ConfigurableNetwork<N, E> extends AbstractNetwork<N, E> {
   private final boolean isDirected;
   private final boolean allowsParallelEdges;
   private final boolean allowsSelfLoops;
   private final ElementOrder<N> nodeOrder;
   private final ElementOrder<E> edgeOrder;
   protected final MapIteratorCache<N, NetworkConnections<N, E>> nodeConnections;
   protected final MapIteratorCache<E, N> edgeToReferenceNode;

   ConfigurableNetwork(NetworkBuilder<? super N, ? super E> var1) {
      this(var1, var1.nodeOrder.createMap((Integer)var1.expectedNodeCount.or((int)10)), var1.edgeOrder.createMap((Integer)var1.expectedEdgeCount.or((int)20)));
   }

   ConfigurableNetwork(NetworkBuilder<? super N, ? super E> var1, Map<N, NetworkConnections<N, E>> var2, Map<E, N> var3) {
      super();
      this.isDirected = var1.directed;
      this.allowsParallelEdges = var1.allowsParallelEdges;
      this.allowsSelfLoops = var1.allowsSelfLoops;
      this.nodeOrder = var1.nodeOrder.cast();
      this.edgeOrder = var1.edgeOrder.cast();
      this.nodeConnections = (MapIteratorCache)(var2 instanceof TreeMap ? new MapRetrievalCache(var2) : new MapIteratorCache(var2));
      this.edgeToReferenceNode = new MapIteratorCache(var3);
   }

   public Set<N> nodes() {
      return this.nodeConnections.unmodifiableKeySet();
   }

   public Set<E> edges() {
      return this.edgeToReferenceNode.unmodifiableKeySet();
   }

   public boolean isDirected() {
      return this.isDirected;
   }

   public boolean allowsParallelEdges() {
      return this.allowsParallelEdges;
   }

   public boolean allowsSelfLoops() {
      return this.allowsSelfLoops;
   }

   public ElementOrder<N> nodeOrder() {
      return this.nodeOrder;
   }

   public ElementOrder<E> edgeOrder() {
      return this.edgeOrder;
   }

   public Set<E> incidentEdges(Object var1) {
      return this.checkedConnections(var1).incidentEdges();
   }

   public EndpointPair<N> incidentNodes(Object var1) {
      Object var2 = this.checkedReferenceNode(var1);
      Object var3 = ((NetworkConnections)this.nodeConnections.get(var2)).oppositeNode(var1);
      return EndpointPair.of((Network)this, var2, var3);
   }

   public Set<N> adjacentNodes(Object var1) {
      return this.checkedConnections(var1).adjacentNodes();
   }

   public Set<E> edgesConnecting(Object var1, Object var2) {
      NetworkConnections var3 = this.checkedConnections(var1);
      if (!this.allowsSelfLoops && var1 == var2) {
         return ImmutableSet.of();
      } else {
         Preconditions.checkArgument(this.containsNode(var2), "Node %s is not an element of this graph.", var2);
         return var3.edgesConnecting(var2);
      }
   }

   public Set<E> inEdges(Object var1) {
      return this.checkedConnections(var1).inEdges();
   }

   public Set<E> outEdges(Object var1) {
      return this.checkedConnections(var1).outEdges();
   }

   public Set<N> predecessors(Object var1) {
      return this.checkedConnections(var1).predecessors();
   }

   public Set<N> successors(Object var1) {
      return this.checkedConnections(var1).successors();
   }

   protected final NetworkConnections<N, E> checkedConnections(Object var1) {
      NetworkConnections var2 = (NetworkConnections)this.nodeConnections.get(var1);
      if (var2 == null) {
         Preconditions.checkNotNull(var1);
         throw new IllegalArgumentException(String.format("Node %s is not an element of this graph.", var1));
      } else {
         return var2;
      }
   }

   protected final N checkedReferenceNode(Object var1) {
      Object var2 = this.edgeToReferenceNode.get(var1);
      if (var2 == null) {
         Preconditions.checkNotNull(var1);
         throw new IllegalArgumentException(String.format("Edge %s is not an element of this graph.", var1));
      } else {
         return var2;
      }
   }

   protected final boolean containsNode(@Nullable Object var1) {
      return this.nodeConnections.containsKey(var1);
   }

   protected final boolean containsEdge(@Nullable Object var1) {
      return this.edgeToReferenceNode.containsKey(var1);
   }
}
