package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;

final class ConfigurableMutableNetwork<N, E> extends ConfigurableNetwork<N, E> implements MutableNetwork<N, E> {
   ConfigurableMutableNetwork(NetworkBuilder<? super N, ? super E> var1) {
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
   private NetworkConnections<N, E> addNodeInternal(N var1) {
      NetworkConnections var2 = this.newConnections();
      Preconditions.checkState(this.nodeConnections.put(var1, var2) == null);
      return var2;
   }

   @CanIgnoreReturnValue
   public boolean addEdge(N var1, N var2, E var3) {
      Preconditions.checkNotNull(var1, "nodeU");
      Preconditions.checkNotNull(var2, "nodeV");
      Preconditions.checkNotNull(var3, "edge");
      if (this.containsEdge(var3)) {
         EndpointPair var7 = this.incidentNodes(var3);
         EndpointPair var8 = EndpointPair.of((Network)this, var1, var2);
         Preconditions.checkArgument(var7.equals(var8), "Edge %s already exists between the following nodes: %s, so it cannot be reused to connect the following nodes: %s.", var3, var7, var8);
         return false;
      } else {
         NetworkConnections var4 = (NetworkConnections)this.nodeConnections.get(var1);
         if (!this.allowsParallelEdges()) {
            Preconditions.checkArgument(var4 == null || !var4.successors().contains(var2), "Nodes %s and %s are already connected by a different edge. To construct a graph that allows parallel edges, call allowsParallelEdges(true) on the Builder.", var1, var2);
         }

         boolean var5 = var1.equals(var2);
         if (!this.allowsSelfLoops()) {
            Preconditions.checkArgument(!var5, "Cannot add self-loop edge on node %s, as self-loops are not allowed. To construct a graph that allows self-loops, call allowsSelfLoops(true) on the Builder.", var1);
         }

         if (var4 == null) {
            var4 = this.addNodeInternal(var1);
         }

         var4.addOutEdge(var3, var2);
         NetworkConnections var6 = (NetworkConnections)this.nodeConnections.get(var2);
         if (var6 == null) {
            var6 = this.addNodeInternal(var2);
         }

         var6.addInEdge(var3, var1, var5);
         this.edgeToReferenceNode.put(var3, var1);
         return true;
      }
   }

   @CanIgnoreReturnValue
   public boolean removeNode(Object var1) {
      Preconditions.checkNotNull(var1, "node");
      NetworkConnections var2 = (NetworkConnections)this.nodeConnections.get(var1);
      if (var2 == null) {
         return false;
      } else {
         UnmodifiableIterator var3 = ImmutableList.copyOf((Collection)var2.incidentEdges()).iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            this.removeEdge(var4);
         }

         this.nodeConnections.remove(var1);
         return true;
      }
   }

   @CanIgnoreReturnValue
   public boolean removeEdge(Object var1) {
      Preconditions.checkNotNull(var1, "edge");
      Object var2 = this.edgeToReferenceNode.get(var1);
      if (var2 == null) {
         return false;
      } else {
         NetworkConnections var3 = (NetworkConnections)this.nodeConnections.get(var2);
         Object var4 = var3.oppositeNode(var1);
         NetworkConnections var5 = (NetworkConnections)this.nodeConnections.get(var4);
         var3.removeOutEdge(var1);
         var5.removeInEdge(var1, this.allowsSelfLoops() && var2.equals(var4));
         this.edgeToReferenceNode.remove(var1);
         return true;
      }
   }

   private NetworkConnections<N, E> newConnections() {
      return (NetworkConnections)(this.isDirected() ? (this.allowsParallelEdges() ? DirectedMultiNetworkConnections.of() : DirectedNetworkConnections.of()) : (this.allowsParallelEdges() ? UndirectedMultiNetworkConnections.of() : UndirectedNetworkConnections.of()));
   }
}
