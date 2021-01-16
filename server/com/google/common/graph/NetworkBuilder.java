package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

@Beta
public final class NetworkBuilder<N, E> extends AbstractGraphBuilder<N> {
   boolean allowsParallelEdges = false;
   ElementOrder<? super E> edgeOrder = ElementOrder.insertion();
   Optional<Integer> expectedEdgeCount = Optional.absent();

   private NetworkBuilder(boolean var1) {
      super(var1);
   }

   public static NetworkBuilder<Object, Object> directed() {
      return new NetworkBuilder(true);
   }

   public static NetworkBuilder<Object, Object> undirected() {
      return new NetworkBuilder(false);
   }

   public static <N, E> NetworkBuilder<N, E> from(Network<N, E> var0) {
      return (new NetworkBuilder(var0.isDirected())).allowsParallelEdges(var0.allowsParallelEdges()).allowsSelfLoops(var0.allowsSelfLoops()).nodeOrder(var0.nodeOrder()).edgeOrder(var0.edgeOrder());
   }

   public NetworkBuilder<N, E> allowsParallelEdges(boolean var1) {
      this.allowsParallelEdges = var1;
      return this;
   }

   public NetworkBuilder<N, E> allowsSelfLoops(boolean var1) {
      this.allowsSelfLoops = var1;
      return this;
   }

   public NetworkBuilder<N, E> expectedNodeCount(int var1) {
      this.expectedNodeCount = Optional.of(Graphs.checkNonNegative(var1));
      return this;
   }

   public NetworkBuilder<N, E> expectedEdgeCount(int var1) {
      this.expectedEdgeCount = Optional.of(Graphs.checkNonNegative(var1));
      return this;
   }

   public <N1 extends N> NetworkBuilder<N1, E> nodeOrder(ElementOrder<N1> var1) {
      NetworkBuilder var2 = this.cast();
      var2.nodeOrder = (ElementOrder)Preconditions.checkNotNull(var1);
      return var2;
   }

   public <E1 extends E> NetworkBuilder<N, E1> edgeOrder(ElementOrder<E1> var1) {
      NetworkBuilder var2 = this.cast();
      var2.edgeOrder = (ElementOrder)Preconditions.checkNotNull(var1);
      return var2;
   }

   public <N1 extends N, E1 extends E> MutableNetwork<N1, E1> build() {
      return new ConfigurableMutableNetwork(this);
   }

   private <N1 extends N, E1 extends E> NetworkBuilder<N1, E1> cast() {
      return this;
   }
}
