package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.math.IntMath;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public abstract class AbstractNetwork<N, E> implements Network<N, E> {
   public AbstractNetwork() {
      super();
   }

   public Graph<N> asGraph() {
      return new AbstractGraph<N>() {
         public Set<N> nodes() {
            return AbstractNetwork.this.nodes();
         }

         public Set<EndpointPair<N>> edges() {
            return (Set)(AbstractNetwork.this.allowsParallelEdges() ? super.edges() : new AbstractSet<EndpointPair<N>>() {
               public Iterator<EndpointPair<N>> iterator() {
                  return Iterators.transform(AbstractNetwork.this.edges().iterator(), new Function<E, EndpointPair<N>>() {
                     public EndpointPair<N> apply(E var1) {
                        return AbstractNetwork.this.incidentNodes(var1);
                     }
                  });
               }

               public int size() {
                  return AbstractNetwork.this.edges().size();
               }

               public boolean contains(@Nullable Object var1) {
                  if (!(var1 instanceof EndpointPair)) {
                     return false;
                  } else {
                     EndpointPair var2 = (EndpointPair)var1;
                     return isDirected() == var2.isOrdered() && nodes().contains(var2.nodeU()) && successors(var2.nodeU()).contains(var2.nodeV());
                  }
               }
            });
         }

         public ElementOrder<N> nodeOrder() {
            return AbstractNetwork.this.nodeOrder();
         }

         public boolean isDirected() {
            return AbstractNetwork.this.isDirected();
         }

         public boolean allowsSelfLoops() {
            return AbstractNetwork.this.allowsSelfLoops();
         }

         public Set<N> adjacentNodes(Object var1) {
            return AbstractNetwork.this.adjacentNodes(var1);
         }

         public Set<N> predecessors(Object var1) {
            return AbstractNetwork.this.predecessors(var1);
         }

         public Set<N> successors(Object var1) {
            return AbstractNetwork.this.successors(var1);
         }
      };
   }

   public int degree(Object var1) {
      return this.isDirected() ? IntMath.saturatedAdd(this.inEdges(var1).size(), this.outEdges(var1).size()) : IntMath.saturatedAdd(this.incidentEdges(var1).size(), this.edgesConnecting(var1, var1).size());
   }

   public int inDegree(Object var1) {
      return this.isDirected() ? this.inEdges(var1).size() : this.degree(var1);
   }

   public int outDegree(Object var1) {
      return this.isDirected() ? this.outEdges(var1).size() : this.degree(var1);
   }

   public Set<E> adjacentEdges(Object var1) {
      EndpointPair var2 = this.incidentNodes(var1);
      Sets.SetView var3 = Sets.union(this.incidentEdges(var2.nodeU()), this.incidentEdges(var2.nodeV()));
      return Sets.difference(var3, ImmutableSet.of(var1));
   }

   public String toString() {
      String var1 = String.format("isDirected: %s, allowsParallelEdges: %s, allowsSelfLoops: %s", this.isDirected(), this.allowsParallelEdges(), this.allowsSelfLoops());
      return String.format("%s, nodes: %s, edges: %s", var1, this.nodes(), this.edgeIncidentNodesMap());
   }

   private Map<E, EndpointPair<N>> edgeIncidentNodesMap() {
      Function var1 = new Function<E, EndpointPair<N>>() {
         public EndpointPair<N> apply(E var1) {
            return AbstractNetwork.this.incidentNodes(var1);
         }
      };
      return Maps.asMap(this.edges(), var1);
   }
}
