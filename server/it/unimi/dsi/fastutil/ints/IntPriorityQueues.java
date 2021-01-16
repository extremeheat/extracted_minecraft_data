package it.unimi.dsi.fastutil.ints;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class IntPriorityQueues {
   private IntPriorityQueues() {
      super();
   }

   public static IntPriorityQueue synchronize(IntPriorityQueue var0) {
      return new IntPriorityQueues.SynchronizedPriorityQueue(var0);
   }

   public static IntPriorityQueue synchronize(IntPriorityQueue var0, Object var1) {
      return new IntPriorityQueues.SynchronizedPriorityQueue(var0, var1);
   }

   public static class SynchronizedPriorityQueue implements IntPriorityQueue {
      protected final IntPriorityQueue q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(IntPriorityQueue var1, Object var2) {
         super();
         this.q = var1;
         this.sync = var2;
      }

      protected SynchronizedPriorityQueue(IntPriorityQueue var1) {
         super();
         this.q = var1;
         this.sync = this;
      }

      public void enqueue(int var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      public int dequeueInt() {
         synchronized(this.sync) {
            return this.q.dequeueInt();
         }
      }

      public int firstInt() {
         synchronized(this.sync) {
            return this.q.firstInt();
         }
      }

      public int lastInt() {
         synchronized(this.sync) {
            return this.q.lastInt();
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

      public IntComparator comparator() {
         synchronized(this.sync) {
            return this.q.comparator();
         }
      }

      /** @deprecated */
      @Deprecated
      public void enqueue(Integer var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer dequeue() {
         synchronized(this.sync) {
            return this.q.dequeue();
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer first() {
         synchronized(this.sync) {
            return this.q.first();
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer last() {
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
