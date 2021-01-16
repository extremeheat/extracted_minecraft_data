package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public final class Graphs {
   private Graphs() {
      super();
   }

   public static boolean hasCycle(Graph<?> var0) {
      int var1 = var0.edges().size();
      if (var1 == 0) {
         return false;
      } else if (!var0.isDirected() && var1 >= var0.nodes().size()) {
         return true;
      } else {
         HashMap var2 = Maps.newHashMapWithExpectedSize(var0.nodes().size());
         Iterator var3 = var0.nodes().iterator();

         Object var4;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            var4 = var3.next();
         } while(!subgraphHasCycle(var0, var2, var4, (Object)null));

         return true;
      }
   }

   public static boolean hasCycle(Network<?, ?> var0) {
      return !var0.isDirected() && var0.allowsParallelEdges() && var0.edges().size() > var0.asGraph().edges().size() ? true : hasCycle(var0.asGraph());
   }

   private static boolean subgraphHasCycle(Graph<?> var0, Map<Object, Graphs.NodeVisitState> var1, Object var2, @Nullable Object var3) {
      Graphs.NodeVisitState var4 = (Graphs.NodeVisitState)var1.get(var2);
      if (var4 == Graphs.NodeVisitState.COMPLETE) {
         return false;
      } else if (var4 == Graphs.NodeVisitState.PENDING) {
         return true;
      } else {
         var1.put(var2, Graphs.NodeVisitState.PENDING);
         Iterator var5 = var0.successors(var2).iterator();

         Object var6;
         do {
            if (!var5.hasNext()) {
               var1.put(var2, Graphs.NodeVisitState.COMPLETE);
               return false;
            }

            var6 = var5.next();
         } while(!canTraverseWithoutReusingEdge(var0, var6, var3) || !subgraphHasCycle(var0, var1, var6, var2));

         return true;
      }
   }

   private static boolean canTraverseWithoutReusingEdge(Graph<?> var0, Object var1, @Nullable Object var2) {
      return var0.isDirected() || !Objects.equal(var2, var1);
   }

   public static <N> Graph<N> transitiveClosure(Graph<N> var0) {
      MutableGraph var1 = GraphBuilder.from(var0).allowsSelfLoops(true).build();
      if (var0.isDirected()) {
         Iterator var2 = var0.nodes().iterator();

         while(var2.hasNext()) {
            Object var3 = var2.next();
            Iterator var4 = reachableNodes(var0, var3).iterator();

            while(var4.hasNext()) {
               Object var5 = var4.next();
               var1.putEdge(var3, var5);
            }
         }

         return var1;
      } else {
         HashSet var11 = new HashSet();
         Iterator var12 = var0.nodes().iterator();

         while(true) {
            Object var13;
            do {
               if (!var12.hasNext()) {
                  return var1;
               }

               var13 = var12.next();
            } while(var11.contains(var13));

            Set var14 = reachableNodes(var0, var13);
            var11.addAll(var14);
            int var6 = 1;
            Iterator var7 = var14.iterator();

            while(var7.hasNext()) {
               Object var8 = var7.next();
               Iterator var9 = Iterables.limit(var14, var6++).iterator();

               while(var9.hasNext()) {
                  Object var10 = var9.next();
                  var1.putEdge(var8, var10);
               }
            }
         }
      }
   }

   public static <N> Set<N> reachableNodes(Graph<N> var0, Object var1) {
      Preconditions.checkArgument(var0.nodes().contains(var1), "Node %s is not an element of this graph.", var1);
      LinkedHashSet var2 = new LinkedHashSet();
      ArrayDeque var3 = new ArrayDeque();
      var2.add(var1);
      var3.add(var1);

      while(!var3.isEmpty()) {
         Object var4 = var3.remove();
         Iterator var5 = var0.successors(var4).iterator();

         while(var5.hasNext()) {
            Object var6 = var5.next();
            if (var2.add(var6)) {
               var3.add(var6);
            }
         }
      }

      return Collections.unmodifiableSet(var2);
   }

   public static boolean equivalent(@Nullable Graph<?> var0, @Nullable Graph<?> var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         return var0.isDirected() == var1.isDirected() && var0.nodes().equals(var1.nodes()) && var0.edges().equals(var1.edges());
      } else {
         return false;
      }
   }

   public static boolean equivalent(@Nullable ValueGraph<?, ?> var0, @Nullable ValueGraph<?, ?> var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         if (var0.isDirected() == var1.isDirected() && var0.nodes().equals(var1.nodes()) && var0.edges().equals(var1.edges())) {
            Iterator var2 = var0.edges().iterator();

            EndpointPair var3;
            do {
               if (!var2.hasNext()) {
                  return true;
               }

               var3 = (EndpointPair)var2.next();
            } while(var0.edgeValue(var3.nodeU(), var3.nodeV()).equals(var1.edgeValue(var3.nodeU(), var3.nodeV())));

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean equivalent(@Nullable Network<?, ?> var0, @Nullable Network<?, ?> var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         if (var0.isDirected() == var1.isDirected() && var0.nodes().equals(var1.nodes()) && var0.edges().equals(var1.edges())) {
            Iterator var2 = var0.edges().iterator();

            Object var3;
            do {
               if (!var2.hasNext()) {
                  return true;
               }

               var3 = var2.next();
            } while(var0.incidentNodes(var3).equals(var1.incidentNodes(var3)));

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static <N> Graph<N> transpose(Graph<N> var0) {
      if (!var0.isDirected()) {
         return var0;
      } else {
         return (Graph)(var0 instanceof Graphs.TransposedGraph ? ((Graphs.TransposedGraph)var0).graph : new Graphs.TransposedGraph(var0));
      }
   }

   public static <N, V> ValueGraph<N, V> transpose(ValueGraph<N, V> var0) {
      if (!var0.isDirected()) {
         return var0;
      } else {
         return (ValueGraph)(var0 instanceof Graphs.TransposedValueGraph ? ((Graphs.TransposedValueGraph)var0).graph : new Graphs.TransposedValueGraph(var0));
      }
   }

   public static <N, E> Network<N, E> transpose(Network<N, E> var0) {
      if (!var0.isDirected()) {
         return var0;
      } else {
         return (Network)(var0 instanceof Graphs.TransposedNetwork ? ((Graphs.TransposedNetwork)var0).network : new Graphs.TransposedNetwork(var0));
      }
   }

   public static <N> MutableGraph<N> inducedSubgraph(Graph<N> var0, Iterable<? extends N> var1) {
      MutableGraph var2 = GraphBuilder.from(var0).build();
      Iterator var3 = var1.iterator();

      Object var4;
      while(var3.hasNext()) {
         var4 = var3.next();
         var2.addNode(var4);
      }

      var3 = var2.nodes().iterator();

      while(var3.hasNext()) {
         var4 = var3.next();
         Iterator var5 = var0.successors(var4).iterator();

         while(var5.hasNext()) {
            Object var6 = var5.next();
            if (var2.nodes().contains(var6)) {
               var2.putEdge(var4, var6);
            }
         }
      }

      return var2;
   }

   public static <N, V> MutableValueGraph<N, V> inducedSubgraph(ValueGraph<N, V> var0, Iterable<? extends N> var1) {
      MutableValueGraph var2 = ValueGraphBuilder.from(var0).build();
      Iterator var3 = var1.iterator();

      Object var4;
      while(var3.hasNext()) {
         var4 = var3.next();
         var2.addNode(var4);
      }

      var3 = var2.nodes().iterator();

      while(var3.hasNext()) {
         var4 = var3.next();
         Iterator var5 = var0.successors(var4).iterator();

         while(var5.hasNext()) {
            Object var6 = var5.next();
            if (var2.nodes().contains(var6)) {
               var2.putEdgeValue(var4, var6, var0.edgeValue(var4, var6));
            }
         }
      }

      return var2;
   }

   public static <N, E> MutableNetwork<N, E> inducedSubgraph(Network<N, E> var0, Iterable<? extends N> var1) {
      MutableNetwork var2 = NetworkBuilder.from(var0).build();
      Iterator var3 = var1.iterator();

      Object var4;
      while(var3.hasNext()) {
         var4 = var3.next();
         var2.addNode(var4);
      }

      var3 = var2.nodes().iterator();

      while(var3.hasNext()) {
         var4 = var3.next();
         Iterator var5 = var0.outEdges(var4).iterator();

         while(var5.hasNext()) {
            Object var6 = var5.next();
            Object var7 = var0.incidentNodes(var6).adjacentNode(var4);
            if (var2.nodes().contains(var7)) {
               var2.addEdge(var4, var7, var6);
            }
         }
      }

      return var2;
   }

   public static <N> MutableGraph<N> copyOf(Graph<N> var0) {
      MutableGraph var1 = GraphBuilder.from(var0).expectedNodeCount(var0.nodes().size()).build();
      Iterator var2 = var0.nodes().iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.addNode(var3);
      }

      var2 = var0.edges().iterator();

      while(var2.hasNext()) {
         EndpointPair var4 = (EndpointPair)var2.next();
         var1.putEdge(var4.nodeU(), var4.nodeV());
      }

      return var1;
   }

   public static <N, V> MutableValueGraph<N, V> copyOf(ValueGraph<N, V> var0) {
      MutableValueGraph var1 = ValueGraphBuilder.from(var0).expectedNodeCount(var0.nodes().size()).build();
      Iterator var2 = var0.nodes().iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.addNode(var3);
      }

      var2 = var0.edges().iterator();

      while(var2.hasNext()) {
         EndpointPair var4 = (EndpointPair)var2.next();
         var1.putEdgeValue(var4.nodeU(), var4.nodeV(), var0.edgeValue(var4.nodeU(), var4.nodeV()));
      }

      return var1;
   }

   public static <N, E> MutableNetwork<N, E> copyOf(Network<N, E> var0) {
      MutableNetwork var1 = NetworkBuilder.from(var0).expectedNodeCount(var0.nodes().size()).expectedEdgeCount(var0.edges().size()).build();
      Iterator var2 = var0.nodes().iterator();

      Object var3;
      while(var2.hasNext()) {
         var3 = var2.next();
         var1.addNode(var3);
      }

      var2 = var0.edges().iterator();

      while(var2.hasNext()) {
         var3 = var2.next();
         EndpointPair var4 = var0.incidentNodes(var3);
         var1.addEdge(var4.nodeU(), var4.nodeV(), var3);
      }

      return var1;
   }

   @CanIgnoreReturnValue
   static int checkNonNegative(int var0) {
      Preconditions.checkArgument(var0 >= 0, "Not true that %s is non-negative.", var0);
      return var0;
   }

   @CanIgnoreReturnValue
   static int checkPositive(int var0) {
      Preconditions.checkArgument(var0 > 0, "Not true that %s is positive.", var0);
      return var0;
   }

   @CanIgnoreReturnValue
   static long checkNonNegative(long var0) {
      Preconditions.checkArgument(var0 >= 0L, "Not true that %s is non-negative.", var0);
      return var0;
   }

   @CanIgnoreReturnValue
   static long checkPositive(long var0) {
      Preconditions.checkArgument(var0 > 0L, "Not true that %s is positive.", var0);
      return var0;
   }

   private static enum NodeVisitState {
      PENDING,
      COMPLETE;

      private NodeVisitState() {
      }
   }

   private static class TransposedNetwork<N, E> extends AbstractNetwork<N, E> {
      private final Network<N, E> network;

      TransposedNetwork(Network<N, E> var1) {
         super();
         this.network = var1;
      }

      public Set<N> nodes() {
         return this.network.nodes();
      }

      public Set<E> edges() {
         return this.network.edges();
      }

      public boolean isDirected() {
         return this.network.isDirected();
      }

      public boolean allowsParallelEdges() {
         return this.network.allowsParallelEdges();
      }

      public boolean allowsSelfLoops() {
         return this.network.allowsSelfLoops();
      }

      public ElementOrder<N> nodeOrder() {
         return this.network.nodeOrder();
      }

      public ElementOrder<E> edgeOrder() {
         return this.network.edgeOrder();
      }

      public Set<N> adjacentNodes(Object var1) {
         return this.network.adjacentNodes(var1);
      }

      public Set<N> predecessors(Object var1) {
         return this.network.successors(var1);
      }

      public Set<N> successors(Object var1) {
         return this.network.predecessors(var1);
      }

      public Set<E> incidentEdges(Object var1) {
         return this.network.incidentEdges(var1);
      }

      public Set<E> inEdges(Object var1) {
         return this.network.outEdges(var1);
      }

      public Set<E> outEdges(Object var1) {
         return this.network.inEdges(var1);
      }

      public EndpointPair<N> incidentNodes(Object var1) {
         EndpointPair var2 = this.network.incidentNodes(var1);
         return EndpointPair.of(this.network, var2.nodeV(), var2.nodeU());
      }

      public Set<E> adjacentEdges(Object var1) {
         return this.network.adjacentEdges(var1);
      }

      public Set<E> edgesConnecting(Object var1, Object var2) {
         return this.network.edgesConnecting(var2, var1);
      }
   }

   private static class TransposedValueGraph<N, V> extends AbstractValueGraph<N, V> {
      private final ValueGraph<N, V> graph;

      TransposedValueGraph(ValueGraph<N, V> var1) {
         super();
         this.graph = var1;
      }

      public Set<N> nodes() {
         return this.graph.nodes();
      }

      protected long edgeCount() {
         return (long)this.graph.edges().size();
      }

      public boolean isDirected() {
         return this.graph.isDirected();
      }

      public boolean allowsSelfLoops() {
         return this.graph.allowsSelfLoops();
      }

      public ElementOrder<N> nodeOrder() {
         return this.graph.nodeOrder();
      }

      public Set<N> adjacentNodes(Object var1) {
         return this.graph.adjacentNodes(var1);
      }

      public Set<N> predecessors(Object var1) {
         return this.graph.successors(var1);
      }

      public Set<N> successors(Object var1) {
         return this.graph.predecessors(var1);
      }

      public V edgeValue(Object var1, Object var2) {
         return this.graph.edgeValue(var2, var1);
      }

      public V edgeValueOrDefault(Object var1, Object var2, @Nullable V var3) {
         return this.graph.edgeValueOrDefault(var2, var1, var3);
      }
   }

   private static class TransposedGraph<N> extends AbstractGraph<N> {
      private final Graph<N> graph;

      TransposedGraph(Graph<N> var1) {
         super();
         this.graph = var1;
      }

      public Set<N> nodes() {
         return this.graph.nodes();
      }

      protected long edgeCount() {
         return (long)this.graph.edges().size();
      }

      public boolean isDirected() {
         return this.graph.isDirected();
      }

      public boolean allowsSelfLoops() {
         return this.graph.allowsSelfLoops();
      }

      public ElementOrder<N> nodeOrder() {
         return this.graph.nodeOrder();
      }

      public Set<N> adjacentNodes(Object var1) {
         return this.graph.adjacentNodes(var1);
      }

      public Set<N> predecessors(Object var1) {
         return this.graph.successors(var1);
      }

      public Set<N> successors(Object var1) {
         return this.graph.predecessors(var1);
      }
   }
}
