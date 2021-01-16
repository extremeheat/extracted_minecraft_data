package io.netty.handler.codec.http2;

import io.netty.util.collection.IntCollections;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.EmptyPriorityQueue;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.PriorityQueue;
import io.netty.util.internal.PriorityQueueNode;
import io.netty.util.internal.SystemPropertyUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class WeightedFairQueueByteDistributor implements StreamByteDistributor {
   static final int INITIAL_CHILDREN_MAP_SIZE = Math.max(1, SystemPropertyUtil.getInt("io.netty.http2.childrenMapSize", 2));
   private static final int DEFAULT_MAX_STATE_ONLY_SIZE = 5;
   private final Http2Connection.PropertyKey stateKey;
   private final IntObjectMap<WeightedFairQueueByteDistributor.State> stateOnlyMap;
   private final PriorityQueue<WeightedFairQueueByteDistributor.State> stateOnlyRemovalQueue;
   private final Http2Connection connection;
   private final WeightedFairQueueByteDistributor.State connectionState;
   private int allocationQuantum;
   private final int maxStateOnlySize;

   public WeightedFairQueueByteDistributor(Http2Connection var1) {
      this(var1, 5);
   }

   public WeightedFairQueueByteDistributor(Http2Connection var1, int var2) {
      super();
      this.allocationQuantum = 1024;
      if (var2 < 0) {
         throw new IllegalArgumentException("maxStateOnlySize: " + var2 + " (expected: >0)");
      } else {
         if (var2 == 0) {
            this.stateOnlyMap = IntCollections.emptyMap();
            this.stateOnlyRemovalQueue = EmptyPriorityQueue.instance();
         } else {
            this.stateOnlyMap = new IntObjectHashMap(var2);
            this.stateOnlyRemovalQueue = new DefaultPriorityQueue(WeightedFairQueueByteDistributor.StateOnlyComparator.INSTANCE, var2 + 2);
         }

         this.maxStateOnlySize = var2;
         this.connection = var1;
         this.stateKey = var1.newKey();
         Http2Stream var3 = var1.connectionStream();
         var3.setProperty(this.stateKey, this.connectionState = new WeightedFairQueueByteDistributor.State(var3, 16));
         var1.addListener(new Http2ConnectionAdapter() {
            public void onStreamAdded(Http2Stream var1) {
               WeightedFairQueueByteDistributor.State var2 = (WeightedFairQueueByteDistributor.State)WeightedFairQueueByteDistributor.this.stateOnlyMap.remove(var1.id());
               if (var2 == null) {
                  var2 = WeightedFairQueueByteDistributor.this.new State(var1);
                  ArrayList var3 = new ArrayList(1);
                  WeightedFairQueueByteDistributor.this.connectionState.takeChild(var2, false, var3);
                  WeightedFairQueueByteDistributor.this.notifyParentChanged(var3);
               } else {
                  WeightedFairQueueByteDistributor.this.stateOnlyRemovalQueue.removeTyped(var2);
                  var2.stream = var1;
               }

               switch(var1.state()) {
               case RESERVED_REMOTE:
               case RESERVED_LOCAL:
                  var2.setStreamReservedOrActivated();
               default:
                  var1.setProperty(WeightedFairQueueByteDistributor.this.stateKey, var2);
               }
            }

            public void onStreamActive(Http2Stream var1) {
               WeightedFairQueueByteDistributor.this.state(var1).setStreamReservedOrActivated();
            }

            public void onStreamClosed(Http2Stream var1) {
               WeightedFairQueueByteDistributor.this.state(var1).close();
            }

            public void onStreamRemoved(Http2Stream var1) {
               WeightedFairQueueByteDistributor.State var2 = WeightedFairQueueByteDistributor.this.state(var1);
               var2.stream = null;
               if (WeightedFairQueueByteDistributor.this.maxStateOnlySize == 0) {
                  var2.parent.removeChild(var2);
               } else {
                  if (WeightedFairQueueByteDistributor.this.stateOnlyRemovalQueue.size() == WeightedFairQueueByteDistributor.this.maxStateOnlySize) {
                     WeightedFairQueueByteDistributor.State var3 = (WeightedFairQueueByteDistributor.State)WeightedFairQueueByteDistributor.this.stateOnlyRemovalQueue.peek();
                     if (WeightedFairQueueByteDistributor.StateOnlyComparator.INSTANCE.compare(var3, var2) >= 0) {
                        var2.parent.removeChild(var2);
                        return;
                     }

                     WeightedFairQueueByteDistributor.this.stateOnlyRemovalQueue.poll();
                     var3.parent.removeChild(var3);
                     WeightedFairQueueByteDistributor.this.stateOnlyMap.remove(var3.streamId);
                  }

                  WeightedFairQueueByteDistributor.this.stateOnlyRemovalQueue.add(var2);
                  WeightedFairQueueByteDistributor.this.stateOnlyMap.put(var2.streamId, var2);
               }
            }
         });
      }
   }

   public void updateStreamableBytes(StreamByteDistributor.StreamState var1) {
      this.state(var1.stream()).updateStreamableBytes(Http2CodecUtil.streamableBytes(var1), var1.hasFrame() && var1.windowSize() >= 0);
   }

   public void updateDependencyTree(int var1, int var2, short var3, boolean var4) {
      WeightedFairQueueByteDistributor.State var5 = this.state(var1);
      if (var5 == null) {
         if (this.maxStateOnlySize == 0) {
            return;
         }

         var5 = new WeightedFairQueueByteDistributor.State(var1);
         this.stateOnlyRemovalQueue.add(var5);
         this.stateOnlyMap.put(var1, var5);
      }

      WeightedFairQueueByteDistributor.State var6 = this.state(var2);
      ArrayList var7;
      if (var6 == null) {
         if (this.maxStateOnlySize == 0) {
            return;
         }

         var6 = new WeightedFairQueueByteDistributor.State(var2);
         this.stateOnlyRemovalQueue.add(var6);
         this.stateOnlyMap.put(var2, var6);
         var7 = new ArrayList(1);
         this.connectionState.takeChild(var6, false, var7);
         this.notifyParentChanged(var7);
      }

      if (var5.activeCountForTree != 0 && var5.parent != null) {
         WeightedFairQueueByteDistributor.State var10000 = var5.parent;
         var10000.totalQueuedWeights += (long)(var3 - var5.weight);
      }

      var5.weight = var3;
      if (var6 != var5.parent || var4 && var6.children.size() != 1) {
         if (var6.isDescendantOf(var5)) {
            var7 = new ArrayList(2 + (var4 ? var6.children.size() : 0));
            var5.parent.takeChild(var6, false, var7);
         } else {
            var7 = new ArrayList(1 + (var4 ? var6.children.size() : 0));
         }

         var6.takeChild(var5, var4, var7);
         this.notifyParentChanged(var7);
      }

      while(this.stateOnlyRemovalQueue.size() > this.maxStateOnlySize) {
         WeightedFairQueueByteDistributor.State var8 = (WeightedFairQueueByteDistributor.State)this.stateOnlyRemovalQueue.poll();
         var8.parent.removeChild(var8);
         this.stateOnlyMap.remove(var8.streamId);
      }

   }

   public boolean distribute(int var1, StreamByteDistributor.Writer var2) throws Http2Exception {
      if (this.connectionState.activeCountForTree == 0) {
         return false;
      } else {
         int var3;
         do {
            var3 = this.connectionState.activeCountForTree;
            var1 -= this.distributeToChildren(var1, var2, this.connectionState);
         } while(this.connectionState.activeCountForTree != 0 && (var1 > 0 || var3 != this.connectionState.activeCountForTree));

         return this.connectionState.activeCountForTree != 0;
      }
   }

   public void allocationQuantum(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("allocationQuantum must be > 0");
      } else {
         this.allocationQuantum = var1;
      }
   }

   private int distribute(int var1, StreamByteDistributor.Writer var2, WeightedFairQueueByteDistributor.State var3) throws Http2Exception {
      if (var3.isActive()) {
         int var4 = Math.min(var1, var3.streamableBytes);
         var3.write(var4, var2);
         if (var4 == 0 && var1 != 0) {
            var3.updateStreamableBytes(var3.streamableBytes, false);
         }

         return var4;
      } else {
         return this.distributeToChildren(var1, var2, var3);
      }
   }

   private int distributeToChildren(int var1, StreamByteDistributor.Writer var2, WeightedFairQueueByteDistributor.State var3) throws Http2Exception {
      long var4 = var3.totalQueuedWeights;
      WeightedFairQueueByteDistributor.State var6 = var3.pollPseudoTimeQueue();
      WeightedFairQueueByteDistributor.State var7 = var3.peekPseudoTimeQueue();
      var6.setDistributing();

      int var9;
      try {
         assert var7 == null || var7.pseudoTimeToWrite >= var6.pseudoTimeToWrite : "nextChildState[" + var7.streamId + "].pseudoTime(" + var7.pseudoTimeToWrite + ") <  childState[" + var6.streamId + "].pseudoTime(" + var6.pseudoTimeToWrite + ")";

         int var8 = this.distribute(var7 == null ? var1 : Math.min(var1, (int)Math.min((var7.pseudoTimeToWrite - var6.pseudoTimeToWrite) * (long)var6.weight / var4 + (long)this.allocationQuantum, 2147483647L)), var2, var6);
         var3.pseudoTime += (long)var8;
         var6.updatePseudoTime(var3, var8, var4);
         var9 = var8;
      } finally {
         var6.unsetDistributing();
         if (var6.activeCountForTree != 0) {
            var3.offerPseudoTimeQueue(var6);
         }

      }

      return var9;
   }

   private WeightedFairQueueByteDistributor.State state(Http2Stream var1) {
      return (WeightedFairQueueByteDistributor.State)var1.getProperty(this.stateKey);
   }

   private WeightedFairQueueByteDistributor.State state(int var1) {
      Http2Stream var2 = this.connection.stream(var1);
      return var2 != null ? this.state(var2) : (WeightedFairQueueByteDistributor.State)this.stateOnlyMap.get(var1);
   }

   boolean isChild(int var1, int var2, short var3) {
      WeightedFairQueueByteDistributor.State var4 = this.state(var2);
      WeightedFairQueueByteDistributor.State var5;
      return var4.children.containsKey(var1) && (var5 = this.state(var1)).parent == var4 && var5.weight == var3;
   }

   int numChildren(int var1) {
      WeightedFairQueueByteDistributor.State var2 = this.state(var1);
      return var2 == null ? 0 : var2.children.size();
   }

   void notifyParentChanged(List<WeightedFairQueueByteDistributor.ParentChangedEvent> var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         WeightedFairQueueByteDistributor.ParentChangedEvent var3 = (WeightedFairQueueByteDistributor.ParentChangedEvent)var1.get(var2);
         this.stateOnlyRemovalQueue.priorityChanged(var3.state);
         if (var3.state.parent != null && var3.state.activeCountForTree != 0) {
            var3.state.parent.offerAndInitializePseudoTime(var3.state);
            var3.state.parent.activeCountChangeForTree(var3.state.activeCountForTree);
         }
      }

   }

   private static final class ParentChangedEvent {
      final WeightedFairQueueByteDistributor.State state;
      final WeightedFairQueueByteDistributor.State oldParent;

      ParentChangedEvent(WeightedFairQueueByteDistributor.State var1, WeightedFairQueueByteDistributor.State var2) {
         super();
         this.state = var1;
         this.oldParent = var2;
      }
   }

   private final class State implements PriorityQueueNode {
      private static final byte STATE_IS_ACTIVE = 1;
      private static final byte STATE_IS_DISTRIBUTING = 2;
      private static final byte STATE_STREAM_ACTIVATED = 4;
      Http2Stream stream;
      WeightedFairQueueByteDistributor.State parent;
      IntObjectMap<WeightedFairQueueByteDistributor.State> children;
      private final PriorityQueue<WeightedFairQueueByteDistributor.State> pseudoTimeQueue;
      final int streamId;
      int streamableBytes;
      int dependencyTreeDepth;
      int activeCountForTree;
      private int pseudoTimeQueueIndex;
      private int stateOnlyQueueIndex;
      long pseudoTimeToWrite;
      long pseudoTime;
      long totalQueuedWeights;
      private byte flags;
      short weight;

      State(int var2) {
         this(var2, (Http2Stream)null, 0);
      }

      State(Http2Stream var2) {
         this(var2, 0);
      }

      State(Http2Stream var2, int var3) {
         this(var2.id(), var2, var3);
      }

      State(int var2, Http2Stream var3, int var4) {
         super();
         this.children = IntCollections.emptyMap();
         this.pseudoTimeQueueIndex = -1;
         this.stateOnlyQueueIndex = -1;
         this.weight = 16;
         this.stream = var3;
         this.streamId = var2;
         this.pseudoTimeQueue = new DefaultPriorityQueue(WeightedFairQueueByteDistributor.StatePseudoTimeComparator.INSTANCE, var4);
      }

      boolean isDescendantOf(WeightedFairQueueByteDistributor.State var1) {
         for(WeightedFairQueueByteDistributor.State var2 = this.parent; var2 != null; var2 = var2.parent) {
            if (var2 == var1) {
               return true;
            }
         }

         return false;
      }

      void takeChild(WeightedFairQueueByteDistributor.State var1, boolean var2, List<WeightedFairQueueByteDistributor.ParentChangedEvent> var3) {
         this.takeChild((Iterator)null, var1, var2, var3);
      }

      void takeChild(Iterator<IntObjectMap.PrimitiveEntry<WeightedFairQueueByteDistributor.State>> var1, WeightedFairQueueByteDistributor.State var2, boolean var3, List<WeightedFairQueueByteDistributor.ParentChangedEvent> var4) {
         WeightedFairQueueByteDistributor.State var5 = var2.parent;
         if (var5 != this) {
            var4.add(new WeightedFairQueueByteDistributor.ParentChangedEvent(var2, var5));
            var2.setParent(this);
            if (var1 != null) {
               var1.remove();
            } else if (var5 != null) {
               var5.children.remove(var2.streamId);
            }

            this.initChildrenIfEmpty();
            WeightedFairQueueByteDistributor.State var6 = (WeightedFairQueueByteDistributor.State)this.children.put(var2.streamId, var2);

            assert var6 == null : "A stream with the same stream ID was already in the child map.";
         }

         if (var3 && !this.children.isEmpty()) {
            Iterator var7 = this.removeAllChildrenExcept(var2).entries().iterator();

            while(var7.hasNext()) {
               var2.takeChild(var7, (WeightedFairQueueByteDistributor.State)((IntObjectMap.PrimitiveEntry)var7.next()).value(), false, var4);
            }
         }

      }

      void removeChild(WeightedFairQueueByteDistributor.State var1) {
         if (this.children.remove(var1.streamId) != null) {
            ArrayList var2 = new ArrayList(1 + var1.children.size());
            var2.add(new WeightedFairQueueByteDistributor.ParentChangedEvent(var1, var1.parent));
            var1.setParent((WeightedFairQueueByteDistributor.State)null);
            Iterator var3 = var1.children.entries().iterator();

            while(var3.hasNext()) {
               this.takeChild(var3, (WeightedFairQueueByteDistributor.State)((IntObjectMap.PrimitiveEntry)var3.next()).value(), false, var2);
            }

            WeightedFairQueueByteDistributor.this.notifyParentChanged(var2);
         }

      }

      private IntObjectMap<WeightedFairQueueByteDistributor.State> removeAllChildrenExcept(WeightedFairQueueByteDistributor.State var1) {
         var1 = (WeightedFairQueueByteDistributor.State)this.children.remove(var1.streamId);
         IntObjectMap var2 = this.children;
         this.initChildren();
         if (var1 != null) {
            this.children.put(var1.streamId, var1);
         }

         return var2;
      }

      private void setParent(WeightedFairQueueByteDistributor.State var1) {
         if (this.activeCountForTree != 0 && this.parent != null) {
            this.parent.removePseudoTimeQueue(this);
            this.parent.activeCountChangeForTree(-this.activeCountForTree);
         }

         this.parent = var1;
         this.dependencyTreeDepth = var1 == null ? 2147483647 : var1.dependencyTreeDepth + 1;
      }

      private void initChildrenIfEmpty() {
         if (this.children == IntCollections.emptyMap()) {
            this.initChildren();
         }

      }

      private void initChildren() {
         this.children = new IntObjectHashMap(WeightedFairQueueByteDistributor.INITIAL_CHILDREN_MAP_SIZE);
      }

      void write(int var1, StreamByteDistributor.Writer var2) throws Http2Exception {
         assert this.stream != null;

         try {
            var2.write(this.stream, var1);
         } catch (Throwable var4) {
            throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, var4, "byte distribution write error");
         }
      }

      void activeCountChangeForTree(int var1) {
         assert this.activeCountForTree + var1 >= 0;

         this.activeCountForTree += var1;
         if (this.parent != null) {
            assert this.activeCountForTree != var1 || this.pseudoTimeQueueIndex == -1 || this.parent.pseudoTimeQueue.containsTyped(this) : "State[" + this.streamId + "].activeCountForTree changed from 0 to " + var1 + " is in a pseudoTimeQueue, but not in parent[ " + this.parent.streamId + "]'s pseudoTimeQueue";

            if (this.activeCountForTree == 0) {
               this.parent.removePseudoTimeQueue(this);
            } else if (this.activeCountForTree == var1 && !this.isDistributing()) {
               this.parent.offerAndInitializePseudoTime(this);
            }

            this.parent.activeCountChangeForTree(var1);
         }

      }

      void updateStreamableBytes(int var1, boolean var2) {
         if (this.isActive() != var2) {
            if (var2) {
               this.activeCountChangeForTree(1);
               this.setActive();
            } else {
               this.activeCountChangeForTree(-1);
               this.unsetActive();
            }
         }

         this.streamableBytes = var1;
      }

      void updatePseudoTime(WeightedFairQueueByteDistributor.State var1, int var2, long var3) {
         assert this.streamId != 0 && var2 >= 0;

         this.pseudoTimeToWrite = Math.min(this.pseudoTimeToWrite, var1.pseudoTime) + (long)var2 * var3 / (long)this.weight;
      }

      void offerAndInitializePseudoTime(WeightedFairQueueByteDistributor.State var1) {
         var1.pseudoTimeToWrite = this.pseudoTime;
         this.offerPseudoTimeQueue(var1);
      }

      void offerPseudoTimeQueue(WeightedFairQueueByteDistributor.State var1) {
         this.pseudoTimeQueue.offer(var1);
         this.totalQueuedWeights += (long)var1.weight;
      }

      WeightedFairQueueByteDistributor.State pollPseudoTimeQueue() {
         WeightedFairQueueByteDistributor.State var1 = (WeightedFairQueueByteDistributor.State)this.pseudoTimeQueue.poll();
         this.totalQueuedWeights -= (long)var1.weight;
         return var1;
      }

      void removePseudoTimeQueue(WeightedFairQueueByteDistributor.State var1) {
         if (this.pseudoTimeQueue.removeTyped(var1)) {
            this.totalQueuedWeights -= (long)var1.weight;
         }

      }

      WeightedFairQueueByteDistributor.State peekPseudoTimeQueue() {
         return (WeightedFairQueueByteDistributor.State)this.pseudoTimeQueue.peek();
      }

      void close() {
         this.updateStreamableBytes(0, false);
         this.stream = null;
      }

      boolean wasStreamReservedOrActivated() {
         return (this.flags & 4) != 0;
      }

      void setStreamReservedOrActivated() {
         this.flags = (byte)(this.flags | 4);
      }

      boolean isActive() {
         return (this.flags & 1) != 0;
      }

      private void setActive() {
         this.flags = (byte)(this.flags | 1);
      }

      private void unsetActive() {
         this.flags &= -2;
      }

      boolean isDistributing() {
         return (this.flags & 2) != 0;
      }

      void setDistributing() {
         this.flags = (byte)(this.flags | 2);
      }

      void unsetDistributing() {
         this.flags &= -3;
      }

      public int priorityQueueIndex(DefaultPriorityQueue<?> var1) {
         return var1 == WeightedFairQueueByteDistributor.this.stateOnlyRemovalQueue ? this.stateOnlyQueueIndex : this.pseudoTimeQueueIndex;
      }

      public void priorityQueueIndex(DefaultPriorityQueue<?> var1, int var2) {
         if (var1 == WeightedFairQueueByteDistributor.this.stateOnlyRemovalQueue) {
            this.stateOnlyQueueIndex = var2;
         } else {
            this.pseudoTimeQueueIndex = var2;
         }

      }

      public String toString() {
         StringBuilder var1 = new StringBuilder(256 * (this.activeCountForTree > 0 ? this.activeCountForTree : 1));
         this.toString(var1);
         return var1.toString();
      }

      private void toString(StringBuilder var1) {
         var1.append("{streamId ").append(this.streamId).append(" streamableBytes ").append(this.streamableBytes).append(" activeCountForTree ").append(this.activeCountForTree).append(" pseudoTimeQueueIndex ").append(this.pseudoTimeQueueIndex).append(" pseudoTimeToWrite ").append(this.pseudoTimeToWrite).append(" pseudoTime ").append(this.pseudoTime).append(" flags ").append(this.flags).append(" pseudoTimeQueue.size() ").append(this.pseudoTimeQueue.size()).append(" stateOnlyQueueIndex ").append(this.stateOnlyQueueIndex).append(" parent.streamId ").append(this.parent == null ? -1 : this.parent.streamId).append("} [");
         if (!this.pseudoTimeQueue.isEmpty()) {
            Iterator var2 = this.pseudoTimeQueue.iterator();

            while(var2.hasNext()) {
               WeightedFairQueueByteDistributor.State var3 = (WeightedFairQueueByteDistributor.State)var2.next();
               var3.toString(var1);
               var1.append(", ");
            }

            var1.setLength(var1.length() - 2);
         }

         var1.append(']');
      }
   }

   private static final class StatePseudoTimeComparator implements Comparator<WeightedFairQueueByteDistributor.State>, Serializable {
      private static final long serialVersionUID = -1437548640227161828L;
      static final WeightedFairQueueByteDistributor.StatePseudoTimeComparator INSTANCE = new WeightedFairQueueByteDistributor.StatePseudoTimeComparator();

      private StatePseudoTimeComparator() {
         super();
      }

      public int compare(WeightedFairQueueByteDistributor.State var1, WeightedFairQueueByteDistributor.State var2) {
         return MathUtil.compare(var1.pseudoTimeToWrite, var2.pseudoTimeToWrite);
      }
   }

   private static final class StateOnlyComparator implements Comparator<WeightedFairQueueByteDistributor.State>, Serializable {
      private static final long serialVersionUID = -4806936913002105966L;
      static final WeightedFairQueueByteDistributor.StateOnlyComparator INSTANCE = new WeightedFairQueueByteDistributor.StateOnlyComparator();

      private StateOnlyComparator() {
         super();
      }

      public int compare(WeightedFairQueueByteDistributor.State var1, WeightedFairQueueByteDistributor.State var2) {
         boolean var3 = var1.wasStreamReservedOrActivated();
         if (var3 != var2.wasStreamReservedOrActivated()) {
            return var3 ? -1 : 1;
         } else {
            int var4 = var2.dependencyTreeDepth - var1.dependencyTreeDepth;
            return var4 != 0 ? var4 : var1.streamId - var2.streamId;
         }
      }
   }
}
