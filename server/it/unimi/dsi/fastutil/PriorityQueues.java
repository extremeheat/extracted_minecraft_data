package it.unimi.dsi.fastutil;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class PriorityQueues {
   public static final PriorityQueues.EmptyPriorityQueue EMPTY_QUEUE = new PriorityQueues.EmptyPriorityQueue();

   private PriorityQueues() {
      super();
   }

   public static <K> PriorityQueue<K> emptyQueue() {
      return EMPTY_QUEUE;
   }

   public static <K> PriorityQueue<K> synchronize(PriorityQueue<K> var0) {
      return new PriorityQueues.SynchronizedPriorityQueue(var0);
   }

   public static <K> PriorityQueue<K> synchronize(PriorityQueue<K> var0, Object var1) {
      return new PriorityQueues.SynchronizedPriorityQueue(var0, var1);
   }

   public static class SynchronizedPriorityQueue<K> implements PriorityQueue<K>, Serializable {
      public static final long serialVersionUID = -7046029254386353129L;
      protected final PriorityQueue<K> q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(PriorityQueue<K> var1, Object var2) {
         super();
         this.q = var1;
         this.sync = var2;
      }

      protected SynchronizedPriorityQueue(PriorityQueue<K> var1) {
         super();
         this.q = var1;
         this.sync = this;
      }

      public void enqueue(K var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      public K dequeue() {
         synchronized(this.sync) {
            return this.q.dequeue();
         }
      }

      public K first() {
         synchronized(this.sync) {
            return this.q.first();
         }
      }

      public K last() {
         synchronized(this.sync) {
            return this.q.last();
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

      public Comparator<? super K> comparator() {
         synchronized(this.sync) {
            return this.q.comparator();
         }
      }

      public String toString() {
         synchronized(this.sync) {
            return this.q.toString();
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

   public static class EmptyPriorityQueue implements PriorityQueue, Serializable {
      private static final long serialVersionUID = 0L;

      protected EmptyPriorityQueue() {
         super();
      }

      public void enqueue(Object var1) {
         throw new UnsupportedOperationException();
      }

      public Object dequeue() {
         throw new NoSuchElementException();
      }

      public boolean isEmpty() {
         return true;
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public Object first() {
         throw new NoSuchElementException();
      }

      public Object last() {
         throw new NoSuchElementException();
      }

      public void changed() {
         throw new NoSuchElementException();
      }

      public Comparator<?> comparator() {
         return null;
      }

      public Object clone() {
         return PriorityQueues.EMPTY_QUEUE;
      }

      public int hashCode() {
         return 0;
      }

      public boolean equals(Object var1) {
         return var1 instanceof PriorityQueue && ((PriorityQueue)var1).isEmpty();
      }

      private Object readResolve() {
         return PriorityQueues.EMPTY_QUEUE;
      }
   }
}
