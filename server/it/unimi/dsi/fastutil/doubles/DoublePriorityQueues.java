package it.unimi.dsi.fastutil.doubles;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class DoublePriorityQueues {
   private DoublePriorityQueues() {
      super();
   }

   public static DoublePriorityQueue synchronize(DoublePriorityQueue var0) {
      return new DoublePriorityQueues.SynchronizedPriorityQueue(var0);
   }

   public static DoublePriorityQueue synchronize(DoublePriorityQueue var0, Object var1) {
      return new DoublePriorityQueues.SynchronizedPriorityQueue(var0, var1);
   }

   public static class SynchronizedPriorityQueue implements DoublePriorityQueue {
      protected final DoublePriorityQueue q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(DoublePriorityQueue var1, Object var2) {
         super();
         this.q = var1;
         this.sync = var2;
      }

      protected SynchronizedPriorityQueue(DoublePriorityQueue var1) {
         super();
         this.q = var1;
         this.sync = this;
      }

      public void enqueue(double var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      public double dequeueDouble() {
         synchronized(this.sync) {
            return this.q.dequeueDouble();
         }
      }

      public double firstDouble() {
         synchronized(this.sync) {
            return this.q.firstDouble();
         }
      }

      public double lastDouble() {
         synchronized(this.sync) {
            return this.q.lastDouble();
         }
      }

      public boolean isEmpty() {
         synchronized(this.sync) {
            return this.q.isEmpty();
         }
      }

      public int size() {
         synchronized(this.sync) {
            return this.q.size();
         }
      }

      public void clear() {
         synchronized(this.sync) {
            this.q.clear();
         }
      }

      public void changed() {
         synchronized(this.sync) {
            this.q.changed();
         }
      }

      public DoubleComparator comparator() {
         synchronized(this.sync) {
            return this.q.comparator();
         }
      }

      /** @deprecated */
      @Deprecated
      public void enqueue(Double var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double dequeue() {
         synchronized(this.sync) {
            return this.q.dequeue();
         }
      }

      /** @deprecated */
      @Deprecated
      public Double first() {
         synchronized(this.sync) {
            return this.q.first();
         }
      }

      /** @deprecated */
      @Deprecated
      public Double last() {
         synchronized(this.sync) {
            return this.q.last();
         }
      }

      public int hashCode() {
         synchronized(this.sync) {
            return this.q.hashCode();
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.sync) {
               return this.q.equals(var1);
            }
         }
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         synchronized(this.sync) {
            var1.defaultWriteObject();
         }
      }
   }
}
