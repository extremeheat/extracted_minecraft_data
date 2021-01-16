package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Iterator;
import javax.annotation.Nullable;

@Beta
public final class ImmutableValueGraph<N, V> extends ImmutableGraph.ValueBackedImpl<N, V> implements ValueGraph<N, V> {
   private ImmutableValueGraph(ValueGraph<N, V> var1) {
      super(ValueGraphBuilder.from(var1), getNodeConnections(var1), (long)var1.edges().size());
   }

   public static <N, V> ImmutableValueGraph<N, V> copyOf(ValueGraph<N, V> var0) {
      return var0 instanceof ImmutableValueGraph ? (ImmutableValueGraph)var0 : new ImmutableValueGraph(var0);
   }

   /** @deprecated */
   @Deprecated
   public static <N, V> ImmutableValueGraph<N, V> copyOf(ImmutableValueGraph<N, V> var0) {
      return (ImmutableValueGraph)Preconditions.checkNotNull(var0);
   }

   private static <N, V> ImmutableMap<N, GraphConnections<N, V>> getNodeConnections(ValueGraph<N, V> var0) {
      ImmutableMap.Builder var1 = ImmutableMap.builder();
      Iterator var2 = var0.nodes().iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.put(var3, connectionsOf(var0, var3));
      }

      return var1.build();
   }

   private static <N, V> GraphConnections<N, V> connectionsOf(final ValueGraph<N, V> var0, final N var1) {
      Function var2 = new Function<N, V>() {
         public V apply(N var1x) {
            return var0.edgeValue(var1, var1x);
         }
      };
      return (GraphConnections)(var0.isDirected() ? DirectedGraphConnections.ofImmutable(var0.predecessors(var1), Maps.asMap(var0.successors(var1), var2)) : UndirectedGraphConnections.ofImmutable(Maps.asMap(var0.adjacentNodes(var1), var2)));
   }

   public V edgeValue(Object var1, Object var2) {
      return this.backingValueGraph.edgeValue(var1, var2);
   }

   public V edgeValueOrDefault(Object var1, Object var2, @Nullable V var3) {
      return this.backingValueGraph.edgeValueOrDefault(var1, var2, var3);
   }

   public String toString() {
      return this.backingValueGraph.toString();
   }
}
