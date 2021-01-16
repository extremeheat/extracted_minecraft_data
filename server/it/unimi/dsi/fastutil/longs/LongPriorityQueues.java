package it.unimi.dsi.fastutil.longs;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class LongPriorityQueues {
   private LongPriorityQueues() {
      super();
   }

   public static LongPriorityQueue synchronize(LongPriorityQueue var0) {
      return new LongPriorityQueues.SynchronizedPriorityQueue(var0);
   }

   public static LongPriorityQueue synchronize(LongPriorityQueue var0, Object var1) {
      return new LongPriorityQueues.SynchronizedPriorityQueue(var0, var1);
   }

   public static class SynchronizedPriorityQueue implements LongPriorityQueue {
      protected final LongPriorityQueue q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(LongPriorityQueue var1, Object var2) {
         super();
         this.q = var1;
         this.sync = var2;
      }

      protected SynchronizedPriorityQueue(LongPriorityQueue var1) {
         super();
         this.q = var1;
         this.sync = this;
      }

      public void enqueue(long var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      public long dequeueLong() {
         synchronized(this.sync) {
            return this.q.dequeueLong();
         }
      }

      public long firstLong() {
         synchronized(this.sync) {
            return this.q.firstLong();
         }
      }

      public long lastLong() {
         synchronized(this.sync) {
            return this.q.lastLong();
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

      public LongComparator comparator() {
         synchronized(this.sync) {
            return this.q.comparator();
         }
      }

      /** @deprecated */
      @Deprecated
      public void enqueue(Long var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long dequeue() {
         synchronized(this.sync) {
            return this.q.dequeue();
         }
      }

      /** @deprecated */
      @Deprecated
      public Long first() {
         synchronized(this.sync) {
            return this.q.first();
         }
      }

      /** @deprecated */
      @Deprecated
      public Long last() {
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
