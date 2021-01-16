package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.math.IntMath;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

abstract class AbstractDirectedNetworkConnections<N, E> implements NetworkConnections<N, E> {
   protected final Map<E, N> inEdgeMap;
   protected final Map<E, N> outEdgeMap;
   private int selfLoopCount;

   protected AbstractDirectedNetworkConnections(Map<E, N> var1, Map<E, N> var2, int var3) {
      super();
      this.inEdgeMap = (Map)Preconditions.checkNotNull(var1);
      this.outEdgeMap = (Map)Preconditions.checkNotNull(var2);
      this.selfLoopCount = Graphs.checkNonNegative(var3);
      Preconditions.checkState(var3 <= var1.size() && var3 <= var2.size());
   }

   public Set<N> adjacentNodes() {
      return Sets.union(this.predecessors(), this.successors());
   }

   public Set<E> incidentEdges() {
      return new AbstractSet<E>() {
         public UnmodifiableIterator<E> iterator() {
            Object var1 = AbstractDirectedNetworkConnections.this.selfLoopCount == 0 ? Iterables.concat(AbstractDirectedNetworkConnections.this.inEdgeMap.keySet(), AbstractDirectedNetworkConnections.this.outEdgeMap.keySet()) : Sets.union(AbstractDirectedNetworkConnections.this.inEdgeMap.keySet(), AbstractDirectedNetworkConnections.this.outEdgeMap.keySet());
            return Iterators.unmodifiableIterator(((Iterable)var1).iterator());
         }

         public int size() {
            return IntMath.saturatedAdd(AbstractDirectedNetworkConnections.this.inEdgeMap.size(), AbstractDirectedNetworkConnections.this.outEdgeMap.size() - AbstractDirectedNetworkConnections.this.selfLoopCount);
         }

         public boolean contains(@Nullable Object var1) {
            return AbstractDirectedNetworkConnections.this.inEdgeMap.containsKey(var1) || AbstractDirectedNetworkConnections.this.outEdgeMap.containsKey(var1);
         }
      };
   }

   public Set<E> inEdges() {
      return Collections.unmodifiableSet(this.inEdgeMap.keySet());
   }

   public Set<E> outEdges() {
      return Collections.unmodifiableSet(this.outEdgeMap.keySet());
   }

   public N oppositeNode(Object var1) {
      return Preconditions.checkNotNull(this.outEdgeMap.get(var1));
   }

   public N removeInEdge(Object var1, boolean var2) {
      if (var2) {
         Graphs.checkNonNegative(--this.selfLoopCount);
      }

      Object var3 = this.inEdgeMap.remove(var1);
      return Preconditions.checkNotNull(var3);
   }

   public N removeOutEdge(Object var1) {
      Object var2 = this.outEdgeMap.remove(var1);
      return Preconditions.checkNotNull(var2);
   }

   public void addInEdge(E var1, N var2, boolean var3) {
      if (var3) {
         Graphs.checkPositive(++this.selfLoopCount);
      }

      Object var4 = this.inEdgeMap.put(var1, var2);
      Preconditions.checkState(var4 == null);
   }

   public void addOutEdge(E var1, N var2) {
      Object var3 = this.outEdgeMap.put(var1, var2);
      Preconditions.checkState(var3 == null);
   }
}
