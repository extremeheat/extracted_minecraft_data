package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Iterator;

@Beta
public abstract class ImmutableGraph<N> extends ForwardingGraph<N> {
   ImmutableGraph() {
      super();
   }

   public static <N> ImmutableGraph<N> copyOf(Graph<N> var0) {
      return (ImmutableGraph)(var0 instanceof ImmutableGraph ? (ImmutableGraph)var0 : new ImmutableGraph.ValueBackedImpl(GraphBuilder.from(var0), getNodeConnections(var0), (long)var0.edges().size()));
   }

   /** @deprecated */
   @Deprecated
   public static <N> ImmutableGraph<N> copyOf(ImmutableGraph<N> var0) {
      return (ImmutableGraph)Preconditions.checkNotNull(var0);
   }

   private static <N> ImmutableMap<N, GraphConnections<N, GraphConstants.Presence>> getNodeConnections(Graph<N> var0) {
      ImmutableMap.Builder var1 = ImmutableMap.builder();
      Iterator var2 = var0.nodes().iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.put(var3, connectionsOf(var0, var3));
      }

      return var1.build();
   }

   private static <N> GraphConnections<N, GraphConstants.Presence> connectionsOf(Graph<N> var0, N var1) {
      Function var2 = Functions.constant(GraphConstants.Presence.EDGE_EXISTS);
      return (GraphConnections)(var0.isDirected() ? DirectedGraphConnections.ofImmutable(var0.predecessors(var1), Maps.asMap(var0.successors(var1), var2)) : UndirectedGraphConnections.ofImmutable(Maps.asMap(var0.adjacentNodes(var1), var2)));
   }

   static class ValueBackedImpl<N, V> extends ImmutableGraph<N> {
      protected final ValueGraph<N, V> backingValueGraph;

      ValueBackedImpl(AbstractGraphBuilder<? super N> var1, ImmutableMap<N, GraphConnections<N, V>> var2, long var3) {
         super();
         this.backingValueGraph = new ConfigurableValueGraph(var1, var2, var3);
      }

      protected Graph<N> delegate() {
         return this.backingValueGraph;
      }
   }
}
