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

final class DirectedMultiNetworkConnections<N, E> extends AbstractDirectedNetworkConnections<N, E> {
   @LazyInit
   private transient Reference<Multiset<N>> predecessorsReference;
   @LazyInit
   private transient Reference<Multiset<N>> successorsReference;

   private DirectedMultiNetworkConnections(Map<E, N> var1, Map<E, N> var2, int var3) {
      super(var1, var2, var3);
   }

   static <N, E> DirectedMultiNetworkConnections<N, E> of() {
      return new DirectedMultiNetworkConnections(new HashMap(2, 1.0F), new HashMap(2, 1.0F), 0);
   }

   static <N, E> DirectedMultiNetworkConnections<N, E> ofImmutable(Map<E, N> var0, Map<E, N> var1, int var2) {
      return new DirectedMultiNetworkConnections(ImmutableMap.copyOf(var0), ImmutableMap.copyOf(var1), var2);
   }

   public Set<N> predecessors() {
      return Collections.unmodifiableSet(this.predecessorsMultiset().elementSet());
   }

   private Multiset<N> predecessorsMultiset() {
      Object var1 = (Multiset)getReference(this.predecessorsReference);
      if (var1 == null) {
         var1 = HashMultiset.create(this.inEdgeMap.values());
         this.predecessorsReference = new SoftReference(var1);
      }

      return (Multiset)var1;
   }

   public Set<N> successors() {
      return Collections.unmodifiableSet(this.successorsMultiset().elementSet());
   }

   private Multiset<N> successorsMultiset() {
      Object var1 = (Multiset)getReference(this.successorsReference);
      if (var1 == null) {
         var1 = HashMultiset.create(this.outEdgeMap.values());
         this.successorsReference = new SoftReference(var1);
      }

      return (Multiset)var1;
   }

   public Set<E> edgesConnecting(final Object var1) {
      return new MultiEdgesConnecting<E>(this.outEdgeMap, var1) {
         public int size() {
            return DirectedMultiNetworkConnections.this.successorsMultiset().count(var1);
         }
      };
   }

   public N removeInEdge(Object var1, boolean var2) {
      Object var3 = super.removeInEdge(var1, var2);
      Multiset var4 = (Multiset)getReference(this.predecessorsReference);
      if (var4 != null) {
         Preconditions.checkState(var4.remove(var3));
      }

      return var3;
   }

   public N removeOutEdge(Object var1) {
      Object var2 = super.removeOutEdge(var1);
      Multiset var3 = (Multiset)getReference(this.successorsReference);
      if (var3 != null) {
         Preconditions.checkState(var3.remove(var2));
      }

      return var2;
   }

   public void addInEdge(E var1, N var2, boolean var3) {
      super.addInEdge(var1, var2, var3);
      Multiset var4 = (Multiset)getReference(this.predecessorsReference);
      if (var4 != null) {
         Preconditions.checkState(var4.add(var2));
      }

   }

   public void addOutEdge(E var1, N var2) {
      super.addOutEdge(var1, var2);
      Multiset var3 = (Multiset)getReference(this.successorsReference);
      if (var3 != null) {
         Preconditions.checkState(var3.add(var2));
      }

   }

   @Nullable
   private static <T> T getReference(@Nullable Reference<T> var0) {
      return var0 == null ? null : var0.get();
   }
}
