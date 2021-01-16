package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;

abstract class EndpointPairIterator<N> extends AbstractIterator<EndpointPair<N>> {
   private final Graph<N> graph;
   private final Iterator<N> nodeIterator;
   protected N node;
   protected Iterator<N> successorIterator;

   static <N> EndpointPairIterator<N> of(Graph<N> var0) {
      return (EndpointPairIterator)(var0.isDirected() ? new EndpointPairIterator.Directed(var0) : new EndpointPairIterator.Undirected(var0));
   }

   private EndpointPairIterator(Graph<N> var1) {
      super();
      this.node = null;
      this.successorIterator = ImmutableSet.of().iterator();
      this.graph = var1;
      this.nodeIterator = var1.nodes().iterator();
   }

   protected final boolean advance() {
      Preconditions.checkState(!this.successorIterator.hasNext());
      if (!this.nodeIterator.hasNext()) {
         return false;
      } else {
         this.node = this.nodeIterator.next();
         this.successorIterator = this.graph.successors(this.node).iterator();
         return true;
      }
   }

   // $FF: synthetic method
   EndpointPairIterator(Graph var1, Object var2) {
      this(var1);
   }

   private static final class Undirected<N> extends EndpointPairIterator<N> {
      private Set<N> visitedNodes;

      private Undirected(Graph<N> var1) {
         super(var1, null);
         this.visitedNodes = Sets.newHashSetWithExpectedSize(var1.nodes().size());
      }

      protected EndpointPair<N> computeNext() {
         while(true) {
            if (this.successorIterator.hasNext()) {
               Object var1 = this.successorIterator.next();
               if (!this.visitedNodes.contains(var1)) {
                  return EndpointPair.unordered(this.node, var1);
               }
            } else {
               this.visitedNodes.add(this.node);
               if (!this.advance()) {
                  this.visitedNodes = null;
                  return (EndpointPair)this.endOfData();
               }
            }
         }
      }

      // $FF: synthetic method
      Undirected(Graph var1, Object var2) {
         this(var1);
      }
   }

   private static final class Directed<N> extends EndpointPairIterator<N> {
      private Directed(Graph<N> var1) {
         super(var1, null);
      }

      protected EndpointPair<N> computeNext() {
         do {
            if (this.successorIterator.hasNext()) {
               return EndpointPair.ordered(this.node, this.successorIterator.next());
            }
         } while(this.advance());

         return (EndpointPair)this.endOfData();
      }

      // $FF: synthetic method
      Directed(Graph var1, Object var2) {
         this(var1);
      }
   }
}
