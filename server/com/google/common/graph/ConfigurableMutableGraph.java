package com.google.common.graph;

final class ConfigurableMutableGraph<N> extends ForwardingGraph<N> implements MutableGraph<N> {
   private final MutableValueGraph<N, GraphConstants.Presence> backingValueGraph;

   ConfigurableMutableGraph(AbstractGraphBuilder<? super N> var1) {
      super();
      this.backingValueGraph = new ConfigurableMutableValueGraph(var1);
   }

   protected Graph<N> delegate() {
      return this.backingValueGraph;
   }

   public boolean addNode(N var1) {
      return this.backingValueGraph.addNode(var1);
   }

   public boolean putEdge(N var1, N var2) {
      return this.backingValueGraph.putEdgeValue(var1, var2, GraphConstants.Presence.EDGE_EXISTS) == null;
   }

   public boolean removeNode(Object var1) {
      return this.backingValueGraph.removeNode(var1);
   }

   public boolean removeEdge(Object var1, Object var2) {
      return this.backingValueGraph.removeEdge(var1, var2) != null;
   }
}
