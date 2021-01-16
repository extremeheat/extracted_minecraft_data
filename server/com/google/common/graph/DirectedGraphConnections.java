package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

final class DirectedGraphConnections<N, V> implements GraphConnections<N, V> {
   private static final Object PRED = new Object();
   private final Map<N, Object> adjacentNodeValues;
   private int predecessorCount;
   private int successorCount;

   private DirectedGraphConnections(Map<N, Object> var1, int var2, int var3) {
      super();
      this.adjacentNodeValues = (Map)Preconditions.checkNotNull(var1);
      this.predecessorCount = Graphs.checkNonNegative(var2);
      this.successorCount = Graphs.checkNonNegative(var3);
      Preconditions.checkState(var2 <= var1.size() && var3 <= var1.size());
   }

   static <N, V> DirectedGraphConnections<N, V> of() {
      byte var0 = 4;
      return new DirectedGraphConnections(new HashMap(var0, 1.0F), 0, 0);
   }

   static <N, V> DirectedGraphConnections<N, V> ofImmutable(Set<N> var0, Map<N, V> var1) {
      HashMap var2 = new HashMap();
      var2.putAll(var1);
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         Object var5 = var2.put(var4, PRED);
         if (var5 != null) {
            var2.put(var4, new DirectedGraphConnections.PredAndSucc(var5));
         }
      }

      return new DirectedGraphConnections(ImmutableMap.copyOf((Map)var2), var0.size(), var1.size());
   }

   public Set<N> adjacentNodes() {
      return Collections.unmodifiableSet(this.adjacentNodeValues.keySet());
   }

   public Set<N> predecessors() {
      return new AbstractSet<N>() {
         public UnmodifiableIterator<N> iterator() {
            final Iterator var1 = DirectedGraphConnections.this.adjacentNodeValues.entrySet().iterator();
            return new AbstractIterator<N>() {
               protected N computeNext() {
                  while(true) {
                     if (var1.hasNext()) {
                        Entry var1x = (Entry)var1.next();
                        if (!DirectedGraphConnections.isPredecessor(var1x.getValue())) {
                           continue;
                        }

                        return var1x.getKey();
                     }

                     return this.endOfData();
                  }
               }
            };
         }

         public int size() {
            return DirectedGraphConnections.this.predecessorCount;
         }

         public boolean contains(@Nullable Object var1) {
            return DirectedGraphConnections.isPredecessor(DirectedGraphConnections.this.adjacentNodeValues.get(var1));
         }
      };
   }

   public Set<N> successors() {
      return new AbstractSet<N>() {
         public UnmodifiableIterator<N> iterator() {
            final Iterator var1 = DirectedGraphConnections.this.adjacentNodeValues.entrySet().iterator();
            return new AbstractIterator<N>() {
               protected N computeNext() {
                  while(true) {
                     if (var1.hasNext()) {
                        Entry var1x = (Entry)var1.next();
                        if (!DirectedGraphConnections.isSuccessor(var1x.getValue())) {
                           continue;
                        }

                        return var1x.getKey();
                     }

                     return this.endOfData();
                  }
               }
            };
         }

         public int size() {
            return DirectedGraphConnections.this.successorCount;
         }

         public boolean contains(@Nullable Object var1) {
            return DirectedGraphConnections.isSuccessor(DirectedGraphConnections.this.adjacentNodeValues.get(var1));
         }
      };
   }

   public V value(Object var1) {
      Object var2 = this.adjacentNodeValues.get(var1);
      if (var2 == PRED) {
         return null;
      } else {
         return var2 instanceof DirectedGraphConnections.PredAndSucc ? ((DirectedGraphConnections.PredAndSucc)var2).successorValue : var2;
      }
   }

   public void removePredecessor(Object var1) {
      Object var2 = this.adjacentNodeValues.get(var1);
      if (var2 == PRED) {
         this.adjacentNodeValues.remove(var1);
         Graphs.checkNonNegative(--this.predecessorCount);
      } else if (var2 instanceof DirectedGraphConnections.PredAndSucc) {
         this.adjacentNodeValues.put(var1, ((DirectedGraphConnections.PredAndSucc)var2).successorValue);
         Graphs.checkNonNegative(--this.predecessorCount);
      }

   }

   public V removeSuccessor(Object var1) {
      Object var2 = this.adjacentNodeValues.get(var1);
      if (var2 != null && var2 != PRED) {
         if (var2 instanceof DirectedGraphConnections.PredAndSucc) {
            this.adjacentNodeValues.put(var1, PRED);
            Graphs.checkNonNegative(--this.successorCount);
            return ((DirectedGraphConnections.PredAndSucc)var2).successorValue;
         } else {
            this.adjacentNodeValues.remove(var1);
            Graphs.checkNonNegative(--this.successorCount);
            return var2;
         }
      } else {
         return null;
      }
   }

   public void addPredecessor(N var1, V var2) {
      Object var3 = this.adjacentNodeValues.put(var1, PRED);
      if (var3 == null) {
         Graphs.checkPositive(++this.predecessorCount);
      } else if (var3 instanceof DirectedGraphConnections.PredAndSucc) {
         this.adjacentNodeValues.put(var1, var3);
      } else if (var3 != PRED) {
         this.adjacentNodeValues.put(var1, new DirectedGraphConnections.PredAndSucc(var3));
         Graphs.checkPositive(++this.predecessorCount);
      }

   }

   public V addSuccessor(N var1, V var2) {
      Object var3 = this.adjacentNodeValues.put(var1, var2);
      if (var3 == null) {
         Graphs.checkPositive(++this.successorCount);
         return null;
      } else if (var3 instanceof DirectedGraphConnections.PredAndSucc) {
         this.adjacentNodeValues.put(var1, new DirectedGraphConnections.PredAndSucc(var2));
         return ((DirectedGraphConnections.PredAndSucc)var3).successorValue;
      } else if (var3 == PRED) {
         this.adjacentNodeValues.put(var1, new DirectedGraphConnections.PredAndSucc(var2));
         Graphs.checkPositive(++this.successorCount);
         return null;
      } else {
         return var3;
      }
   }

   private static boolean isPredecessor(@Nullable Object var0) {
      return var0 == PRED || var0 instanceof DirectedGraphConnections.PredAndSucc;
   }

   private static boolean isSuccessor(@Nullable Object var0) {
      return var0 != PRED && var0 != null;
   }

   private static final class PredAndSucc {
      private final Object successorValue;

      PredAndSucc(Object var1) {
         super();
         this.successorValue = var1;
      }
   }
}
