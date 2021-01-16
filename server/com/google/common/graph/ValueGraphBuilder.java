package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

@Beta
public final class ValueGraphBuilder<N, V> extends AbstractGraphBuilder<N> {
   private ValueGraphBuilder(boolean var1) {
      super(var1);
   }

   public static ValueGraphBuilder<Object, Object> directed() {
      return new ValueGraphBuilder(true);
   }

   public static ValueGraphBuilder<Object, Object> undirected() {
      return new ValueGraphBuilder(false);
   }

   public static <N> ValueGraphBuilder<N, Object> from(Graph<N> var0) {
      return (new ValueGraphBuilder(var0.isDirected())).allowsSelfLoops(var0.allowsSelfLoops()).nodeOrder(var0.nodeOrder());
   }

   public ValueGraphBuilder<N, V> allowsSelfLoops(boolean var1) {
      this.allowsSelfLoops = var1;
      return this;
   }

   public ValueGraphBuilder<N, V> expectedNodeCount(int var1) {
      this.expectedNodeCount = Optional.of(Graphs.checkNonNegative(var1));
      return this;
   }

   public <N1 extends N> ValueGraphBuilder<N1, V> nodeOrder(ElementOrder<N1> var1) {
      ValueGraphBuilder var2 = this.cast();
      var2.nodeOrder = (ElementOrder)Preconditions.checkNotNull(var1);
      return var2;
   }

   public <N1 extends N, V1 extends V> MutableValueGraph<N1, V1> build() {
      return new ConfigurableMutableValueGraph(this);
   }

   private <N1 extends N, V1 extends V> ValueGraphBuilder<N1, V1> cast() {
      return this;
   }
}
