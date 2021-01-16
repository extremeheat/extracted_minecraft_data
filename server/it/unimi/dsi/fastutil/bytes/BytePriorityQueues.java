package it.unimi.dsi.fastutil.bytes;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class BytePriorityQueues {
   private BytePriorityQueues() {
      super();
   }

   public static BytePriorityQueue synchronize(BytePriorityQueue var0) {
      return new BytePriorityQueues.SynchronizedPriorityQueue(var0);
   }

   public static BytePriorityQueue synchronize(BytePriorityQueue var0, Object var1) {
      return new BytePriorityQueues.SynchronizedPriorityQueue(var0, var1);
   }

   public static class SynchronizedPriorityQueue implements BytePriorityQueue {
      protected final BytePriorityQueue q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(BytePriorityQueue var1, Object var2) {
         super();
         this.q = var1;
         this.sync = var2;
      }

      protected SynchronizedPriorityQueue(BytePriorityQueue var1) {
         super();
         this.q = var1;
         this.sync = this;
      }

      public void enqueue(byte var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      public byte dequeueByte() {
         synchronized(this.sync) {
            return this.q.dequeueByte();
         }
      }

      public byte firstByte() {
         synchronized(this.sync) {
            return this.q.firstByte();
         }
      }

      public byte lastByte() {
         synchronized(this.sync) {
            return this.q.lastByte();
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

      public ByteComparator comparator() {
         synchronized(this.sync) {
            return this.q.comparator();
         }
      }

      /** @deprecated */
      @Deprecated
      public void enqueue(Byte var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte dequeue() {
         synchronized(this.sync) {
            return this.q.dequeue();
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte first() {
         synchronized(this.sync) {
            return this.q.first();
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte last() {
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
