package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;

final class ConfigurableMutableValueGraph<N, V> extends ConfigurableValueGraph<N, V> implements MutableValueGraph<N, V> {
   ConfigurableMutableValueGraph(AbstractGraphBuilder<? super N> var1) {
      super(var1);
   }

   @CanIgnoreReturnValue
   public boolean addNode(N var1) {
      Preconditions.checkNotNull(var1, "node");
      if (this.containsNode(var1)) {
         return false;
      } else {
         this.addNodeInternal(var1);
         return true;
      }
   }

   @CanIgnoreReturnValue
   private GraphConnections<N, V> addNodeInternal(N var1) {
      GraphConnections var2 = this.newConnections();
      Preconditions.checkState(this.nodeConnections.put(var1, var2) == null);
      return var2;
   }

   @CanIgnoreReturnValue
   public V putEdgeValue(N var1, N var2, V var3) {
      Preconditions.checkNotNull(var1, "nodeU");
      Preconditions.checkNotNull(var2, "nodeV");
      Preconditions.checkNotNull(var3, "value");
      if (!this.allowsSelfLoops()) {
         Preconditions.checkArgument(!var1.equals(var2), "Cannot add self-loop edge on node %s, as self-loops are not allowed. To construct a graph that allows self-loops, call allowsSelfLoops(true) on the Builder.", var1);
      }

      GraphConnections var4 = (GraphConnections)this.nodeConnections.get(var1);
      if (var4 == null) {
         var4 = this.addNodeInternal(var1);
      }

      Object var5 = var4.addSuccessor(var2, var3);
      GraphConnections var6 = (GraphConnections)this.nodeConnections.get(var2);
      if (var6 == null) {
         var6 = this.addNodeInternal(var2);
      }

      var6.addPredecessor(var1, var3);
      if (var5 == null) {
         Graphs.checkPositive(++this.edgeCount);
      }

      return var5;
   }

   @CanIgnoreReturnValue
   public boolean removeNode(Object var1) {
      Preconditions.checkNotNull(var1, "node");
      GraphConnections var2 = (GraphConnections)this.nodeConnections.get(var1);
      if (var2 == null) {
         return false;
      } else {
         if (this.allowsSelfLoops() && var2.removeSuccessor(var1) != null) {
            var2.removePredecessor(var1);
            --this.edgeCount;
         }

         Iterator var3;
         Object var4;
         for(var3 = var2.successors().iterator(); var3.hasNext(); --this.edgeCount) {
            var4 = var3.next();
            ((GraphConnections)this.nodeConnections.getWithoutCaching(var4)).removePredecessor(var1);
         }

         if (this.isDirected()) {
            for(var3 = var2.predecessors().iterator(); var3.hasNext(); --this.edgeCount) {
               var4 = var3.next();
               Preconditions.checkState(((GraphConnections)this.nodeConnections.getWithoutCaching(var4)).removeSuccessor(var1) != null);
            }
         }

         this.nodeConnections.remove(var1);
         Graphs.checkNonNegative(this.edgeCount);
         return true;
      }
   }

   @CanIgnoreReturnValue
   public V removeEdge(Object var1, Object var2) {
      Preconditions.checkNotNull(var1, "nodeU");
      Preconditions.checkNotNull(var2, "nodeV");
      GraphConnections var3 = (GraphConnections)this.nodeConnections.get(var1);
      GraphConnections var4 = (GraphConnections)this.nodeConnections.get(var2);
      if (var3 != null && var4 != null) {
         Object var5 = var3.removeSuccessor(var2);
         if (var5 != null) {
            var4.removePredecessor(var1);
            Graphs.checkNonNegative(--this.edgeCount);
         }

         return var5;
      } else {
         return null;
      }
   }

   private GraphConnections<N, V> newConnections() {
      return (GraphConnections)(this.isDirected() ? DirectedGraphConnections.of() : UndirectedGraphConnections.of());
   }
}
