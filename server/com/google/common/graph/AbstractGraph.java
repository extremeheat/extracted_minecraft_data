package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public abstract class AbstractGraph<N> implements Graph<N> {
   public AbstractGraph() {
      super();
   }

   protected long edgeCount() {
      long var1 = 0L;

      Object var4;
      for(Iterator var3 = this.nodes().iterator(); var3.hasNext(); var1 += (long)this.degree(var4)) {
         var4 = var3.next();
      }

      Preconditions.checkState((var1 & 1L) == 0L);
      return var1 >>> 1;
   }

   public Set<EndpointPair<N>> edges() {
      return new AbstractSet<EndpointPair<N>>() {
         public UnmodifiableIterator<EndpointPair<N>> iterator() {
            return EndpointPairIterator.of(AbstractGraph.this);
         }

         public int size() {
            return Ints.saturatedCast(AbstractGraph.this.edgeCount());
         }

         public boolean contains(@Nullable Object var1) {
            if (!(var1 instanceof EndpointPair)) {
               return false;
            } else {
               EndpointPair var2 = (EndpointPair)var1;
               return AbstractGraph.this.isDirected() == var2.isOrdered() && AbstractGraph.this.nodes().contains(var2.nodeU()) && AbstractGraph.this.successors(var2.nodeU()).contains(var2.nodeV());
            }
         }
      };
   }

   public int degree(Object var1) {
      if (this.isDirected()) {
         return IntMath.saturatedAdd(this.predecessors(var1).size(), this.successors(var1).size());
      } else {
         Set var2 = this.adjacentNodes(var1);
         int var3 = this.allowsSelfLoops() && var2.contains(var1) ? 1 : 0;
         return IntMath.saturatedAdd(var2.size(), var3);
      }
   }

   public int inDegree(Object var1) {
      return this.isDirected() ? this.predecessors(var1).size() : this.degree(var1);
   }

   public int outDegree(Object var1) {
      return this.isDirected() ? this.successors(var1).size() : this.degree(var1);
   }

   public String toString() {
      String var1 = String.format("isDirected: %s, allowsSelfLoops: %s", this.isDirected(), this.allowsSelfLoops());
      return String.format("%s, nodes: %s, edges: %s", var1, this.nodes(), this.edges());
   }
}
