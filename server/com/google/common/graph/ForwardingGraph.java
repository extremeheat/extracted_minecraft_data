package com.google.common.graph;

import java.util.Set;

abstract class ForwardingGraph<N> extends AbstractGraph<N> {
   ForwardingGraph() {
      super();
   }

   protected abstract Graph<N> delegate();

   public Set<N> nodes() {
      return this.delegate().nodes();
   }

   public Set<EndpointPair<N>> edges() {
      return this.delegate().edges();
   }

   public boolean isDirected() {
      return this.delegate().isDirected();
   }

   public boolean allowsSelfLoops() {
      return this.delegate().allowsSelfLoops();
   }

   public ElementOrder<N> nodeOrder() {
      return this.delegate().nodeOrder();
   }

   public Set<N> adjacentNodes(Object var1) {
      return this.delegate().adjacentNodes(var1);
   }

   public Set<N> predecessors(Object var1) {
      return this.delegate().predecessors(var1);
   }

   public Set<N> successors(Object var1) {
      return this.delegate().successors(var1);
   }

   public int degree(Object var1) {
      return this.delegate().degree(var1);
   }

   public int inDegree(Object var1) {
      return this.delegate().inDegree(var1);
   }

   public int outDegree(Object var1) {
      return this.delegate().outDegree(var1);
   }
}
