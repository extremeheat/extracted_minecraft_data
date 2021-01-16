package com.google.common.graph;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

final class DirectedNetworkConnections<N, E> extends AbstractDirectedNetworkConnections<N, E> {
   protected DirectedNetworkConnections(Map<E, N> var1, Map<E, N> var2, int var3) {
      super(var1, var2, var3);
   }

   static <N, E> DirectedNetworkConnections<N, E> of() {
      return new DirectedNetworkConnections(HashBiMap.create(2), HashBiMap.create(2), 0);
   }

   static <N, E> DirectedNetworkConnections<N, E> ofImmutable(Map<E, N> var0, Map<E, N> var1, int var2) {
      return new DirectedNetworkConnections(ImmutableBiMap.copyOf(var0), ImmutableBiMap.copyOf(var1), var2);
   }

   public Set<N> predecessors() {
      return Collections.unmodifiableSet(((BiMap)this.inEdgeMap).values());
   }

   public Set<N> successors() {
      return Collections.unmodifiableSet(((BiMap)this.outEdgeMap).values());
   }

   public Set<E> edgesConnecting(Object var1) {
      return new EdgesConnecting(((BiMap)this.outEdgeMap).inverse(), var1);
   }
}
