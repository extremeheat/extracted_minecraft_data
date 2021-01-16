package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;

@Beta
public final class ImmutableNetwork<N, E> extends ConfigurableNetwork<N, E> {
   private ImmutableNetwork(Network<N, E> var1) {
      super(NetworkBuilder.from(var1), getNodeConnections(var1), getEdgeToReferenceNode(var1));
   }

   public static <N, E> ImmutableNetwork<N, E> copyOf(Network<N, E> var0) {
      return var0 instanceof ImmutableNetwork ? (ImmutableNetwork)var0 : new ImmutableNetwork(var0);
   }

   /** @deprecated */
   @Deprecated
   public static <N, E> ImmutableNetwork<N, E> copyOf(ImmutableNetwork<N, E> var0) {
      return (ImmutableNetwork)Preconditions.checkNotNull(var0);
   }

   public ImmutableGraph<N> asGraph() {
      final Graph var1 = super.asGraph();
      return new ImmutableGraph<N>() {
         protected Graph<N> delegate() {
            return var1;
         }
      };
   }

   private static <N, E> Map<N, NetworkConnections<N, E>> getNodeConnections(Network<N, E> var0) {
      ImmutableMap.Builder var1 = ImmutableMap.builder();
      Iterator var2 = var0.nodes().iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.put(var3, connectionsOf(var0, var3));
      }

      return var1.build();
   }

   private static <N, E> Map<E, N> getEdgeToReferenceNode(Network<N, E> var0) {
      ImmutableMap.Builder var1 = ImmutableMap.builder();
      Iterator var2 = var0.edges().iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.put(var3, var0.incidentNodes(var3).nodeU());
      }

      return var1.build();
   }

   private static <N, E> NetworkConnections<N, E> connectionsOf(Network<N, E> var0, N var1) {
      Map var2;
      if (var0.isDirected()) {
         var2 = Maps.asMap(var0.inEdges(var1), sourceNodeFn(var0));
         Map var3 = Maps.asMap(var0.outEdges(var1), targetNodeFn(var0));
         int var4 = var0.edgesConnecting(var1, var1).size();
         return (NetworkConnections)(var0.allowsParallelEdges() ? DirectedMultiNetworkConnections.ofImmutable(var2, var3, var4) : DirectedNetworkConnections.ofImmutable(var2, var3, var4));
      } else {
         var2 = Maps.asMap(var0.incidentEdges(var1), adjacentNodeFn(var0, var1));
         return (NetworkConnections)(var0.allowsParallelEdges() ? UndirectedMultiNetworkConnections.ofImmutable(var2) : UndirectedNetworkConnections.ofImmutable(var2));
      }
   }

   private static <N, E> Function<E, N> sourceNodeFn(final Network<N, E> var0) {
      return new Function<E, N>() {
         public N apply(E var1) {
            return var0.incidentNodes(var1).source();
         }
      };
   }

   private static <N, E> Function<E, N> targetNodeFn(final Network<N, E> var0) {
      return new Function<E, N>() {
         public N apply(E var1) {
            return var0.incidentNodes(var1).target();
         }
      };
   }

   private static <N, E> Function<E, N> adjacentNodeFn(final Network<N, E> var0, final N var1) {
      return new Function<E, N>() {
         public N apply(E var1x) {
            return var0.incidentNodes(var1x).adjacentNode(var1);
         }
      };
   }
}
