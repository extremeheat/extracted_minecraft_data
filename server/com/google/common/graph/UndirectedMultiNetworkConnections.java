package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class UndirectedMultiNetworkConnections<N, E> extends AbstractUndirectedNetworkConnections<N, E> {
   @LazyInit
   private transient Reference<Multiset<N>> adjacentNodesReference;

   private UndirectedMultiNetworkConnections(Map<E, N> var1) {
      super(var1);
   }

   static <N, E> UndirectedMultiNetworkConnections<N, E> of() {
      return new UndirectedMultiNetworkConnections(new HashMap(2, 1.0F));
   }

   static <N, E> UndirectedMultiNetworkConnections<N, E> ofImmutable(Map<E, N> var0) {
      return new UndirectedMultiNetworkConnections(ImmutableMap.copyOf(var0));
   }

   public Set<N> adjacentNodes() {
      return Collections.unmodifiableSet(this.adjacentNodesMultiset().elementSet());
   }

   private Multiset<N> adjacentNodesMultiset() {
      Object var1 = (Multiset)getReference(this.adjacentNodesReference);
      if (var1 == null) {
         var1 = HashMultiset.create(this.incidentEdgeMap.values());
         this.adjacentNodesReference = new SoftReference(var1);
      }

      return (Multiset)var1;
   }

   public Set<E> edgesConnecting(final Object var1) {
      return new MultiEdgesConnecting<E>(this.incidentEdgeMap, var1) {
         public int size() {
            return UndirectedMultiNetworkConnections.this.adjacentNodesMultiset().count(var1);
         }
      };
   }

   public N removeInEdge(Object var1, boolean var2) {
      return !var2 ? this.removeOutEdge(var1) : null;
   }

   public N removeOutEdge(Object var1) {
      Object var2 = super.removeOutEdge(var1);
      Multiset var3 = (Multiset)getReference(this.adjacentNodesReference);
      if (var3 != null) {
         Preconditions.checkState(var3.remove(var2));
      }

      return var2;
   }

   public void addInEdge(E var1, N var2, boolean var3) {
      if (!var3) {
         this.addOutEdge(var1, var2);
      }

   }

   public void addOutEdge(E var1, N var2) {
      super.addOutEdge(var1, var2);
      Multiset var3 = (Multiset)getReference(this.adjacentNodesReference);
      if (var3 != null) {
         Preconditions.checkState(var3.add(var2));
      }

   }

   @Nullable
   private static <T> T getReference(@Nullable Reference<T> var0) {
      return var0 == null ? null : var0.get();
   }
}
