package it.unimi.dsi.fastutil.shorts;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class ShortPriorityQueues {
   private ShortPriorityQueues() {
      super();
   }

   public static ShortPriorityQueue synchronize(ShortPriorityQueue var0) {
      return new ShortPriorityQueues.SynchronizedPriorityQueue(var0);
   }

   public static ShortPriorityQueue synchronize(ShortPriorityQueue var0, Object var1) {
      return new ShortPriorityQueues.SynchronizedPriorityQueue(var0, var1);
   }

   public static class SynchronizedPriorityQueue implements ShortPriorityQueue {
      protected final ShortPriorityQueue q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(ShortPriorityQueue var1, Object var2) {
         super();
         this.q = var1;
         this.sync = var2;
      }

      protected SynchronizedPriorityQueue(ShortPriorityQueue var1) {
         super();
         this.q = var1;
         this.sync = this;
      }

      public void enqueue(short var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      public short dequeueShort() {
         synchronized(this.sync) {
            return this.q.dequeueShort();
         }
      }

      public short firstShort() {
         synchronized(this.sync) {
            return this.q.firstShort();
         }
      }

      public short lastShort() {
         synchronized(this.sync) {
            return this.q.lastShort();
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

      public ShortComparator comparator() {
         synchronized(this.sync) {
            return this.q.comparator();
         }
      }

      /** @deprecated */
      @Deprecated
      public void enqueue(Short var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short dequeue() {
         synchronized(this.sync) {
            return this.q.dequeue();
         }
      }

      /** @deprecated */
      @Deprecated
      public Short first() {
         synchronized(this.sync) {
            return this.q.first();
         }
      }

      /** @deprecated */
      @Deprecated
      public Short last() {
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
