package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

@Beta
public final class GraphBuilder<N> extends AbstractGraphBuilder<N> {
   private GraphBuilder(boolean var1) {
      super(var1);
   }

   public static GraphBuilder<Object> directed() {
      return new GraphBuilder(true);
   }

   public static GraphBuilder<Object> undirected() {
      return new GraphBuilder(false);
   }

   public static <N> GraphBuilder<N> from(Graph<N> var0) {
      return (new GraphBuilder(var0.isDirected())).allowsSelfLoops(var0.allowsSelfLoops()).nodeOrder(var0.nodeOrder());
   }

   public GraphBuilder<N> allowsSelfLoops(boolean var1) {
      this.allowsSelfLoops = var1;
      return this;
   }

   public GraphBuilder<N> expectedNodeCount(int var1) {
      this.expectedNodeCount = Optional.of(Graphs.checkNonNegative(var1));
      return this;
   }

   public <N1 extends N> GraphBuilder<N1> nodeOrder(ElementOrder<N1> var1) {
      GraphBuilder var2 = this.cast();
      var2.nodeOrder = (ElementOrder)Preconditions.checkNotNull(var1);
      return var2;
   }

   public <N1 extends N> MutableGraph<N1> build() {
      return new ConfigurableMutableGraph(this);
   }

   private <N1 extends N> GraphBuilder<N1> cast() {
      return this;
   }
}
