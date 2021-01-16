package it.unimi.dsi.fastutil.floats;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class FloatPriorityQueues {
   private FloatPriorityQueues() {
      super();
   }

   public static FloatPriorityQueue synchronize(FloatPriorityQueue var0) {
      return new FloatPriorityQueues.SynchronizedPriorityQueue(var0);
   }

   public static FloatPriorityQueue synchronize(FloatPriorityQueue var0, Object var1) {
      return new FloatPriorityQueues.SynchronizedPriorityQueue(var0, var1);
   }

   public static class SynchronizedPriorityQueue implements FloatPriorityQueue {
      protected final FloatPriorityQueue q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(FloatPriorityQueue var1, Object var2) {
         super();
         this.q = var1;
         this.sync = var2;
      }

      protected SynchronizedPriorityQueue(FloatPriorityQueue var1) {
         super();
         this.q = var1;
         this.sync = this;
      }

      public void enqueue(float var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      public float dequeueFloat() {
         synchronized(this.sync) {
            return this.q.dequeueFloat();
         }
      }

      public float firstFloat() {
         synchronized(this.sync) {
            return this.q.firstFloat();
         }
      }

      public float lastFloat() {
         synchronized(this.sync) {
            return this.q.lastFloat();
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

      public FloatComparator comparator() {
         synchronized(this.sync) {
            return this.q.comparator();
         }
      }

      /** @deprecated */
      @Deprecated
      public void enqueue(Float var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float dequeue() {
         synchronized(this.sync) {
            return this.q.dequeue();
         }
      }

      /** @deprecated */
      @Deprecated
      public Float first() {
         synchronized(this.sync) {
            return this.q.first();
         }
      }

      /** @deprecated */
      @Deprecated
      public Float last() {
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
