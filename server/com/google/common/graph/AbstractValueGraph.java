package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Map;

@Beta
public abstract class AbstractValueGraph<N, V> extends AbstractGraph<N> implements ValueGraph<N, V> {
   public AbstractValueGraph() {
      super();
   }

   public V edgeValue(Object var1, Object var2) {
      Object var3 = this.edgeValueOrDefault(var1, var2, (Object)null);
      if (var3 == null) {
         Preconditions.checkArgument(this.nodes().contains(var1), "Node %s is not an element of this graph.", var1);
         Preconditions.checkArgument(this.nodes().contains(var2), "Node %s is not an element of this graph.", var2);
         throw new IllegalArgumentException(String.format("Edge connecting %s to %s is not present in this graph.", var1, var2));
      } else {
         return var3;
      }
   }

   public String toString() {
      String var1 = String.format("isDirected: %s, allowsSelfLoops: %s", this.isDirected(), this.allowsSelfLoops());
      return String.format("%s, nodes: %s, edges: %s", var1, this.nodes(), this.edgeValueMap());
   }

   private Map<EndpointPair<N>, V> edgeValueMap() {
      Function var1 = new Function<EndpointPair<N>, V>() {
         public V apply(EndpointPair<N> var1) {
            return AbstractValueGraph.this.edgeValue(var1.nodeU(), var1.nodeV());
         }
      };
      return Maps.asMap(this.edges(), var1);
   }
}
