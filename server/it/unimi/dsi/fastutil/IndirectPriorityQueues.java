package it.unimi.dsi.fastutil;

import java.util.Comparator;
import java.util.NoSuchElementException;

public class IndirectPriorityQueues {
   public static final IndirectPriorityQueues.EmptyIndirectPriorityQueue EMPTY_QUEUE = new IndirectPriorityQueues.EmptyIndirectPriorityQueue();

   private IndirectPriorityQueues() {
      super();
   }

   public static <K> IndirectPriorityQueue<K> synchronize(IndirectPriorityQueue<K> var0) {
      return new IndirectPriorityQueues.SynchronizedIndirectPriorityQueue(var0);
   }

   public static <K> IndirectPriorityQueue<K> synchronize(IndirectPriorityQueue<K> var0, Object var1) {
      return new IndirectPriorityQueues.SynchronizedIndirectPriorityQueue(var0, var1);
   }

   public static class SynchronizedIndirectPriorityQueue<K> implements IndirectPriorityQueue<K> {
      public static final long serialVersionUID = -7046029254386353129L;
      protected final IndirectPriorityQueue<K> q;
      protected final Object sync;

      protected SynchronizedIndirectPriorityQueue(IndirectPriorityQueue<K> var1, Object var2) {
         super();
         this.q = var1;
         this.sync = var2;
      }

      protected SynchronizedIndirectPriorityQueue(IndirectPriorityQueue<K> var1) {
         super();
         this.q = var1;
         this.sync = this;
      }

      public void enqueue(int var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      public int dequeue() {
         synchronized(this.sync) {
            return this.q.dequeue();
         }
      }

      public boolean contains(int var1) {
         synchronized(this.sync) {
            return this.q.contains(var1);
         }
      }

      public int first() {
         synchronized(this.sync) {
            return this.q.first();
         }
      }

      public int last() {
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

      public void allChanged() {
         synchronized(this.sync) {
            this.q.allChanged();
         }
      }

      public void changed(int var1) {
         synchronized(this.sync) {
            this.q.changed(var1);
         }
      }

      public boolean remove(int var1) {
         synchronized(this.sync) {
            return this.q.remove(var1);
         }
      }

      public Comparator<? super K> comparator() {
         synchronized(this.sync) {
            return this.q.comparator();
         }
      }

      public int front(int[] var1) {
         return this.q.front(var1);
      }
   }

   public static class EmptyIndirectPriorityQueue implements IndirectPriorityQueue {
      protected EmptyIndirectPriorityQueue() {
         super();
      }

      public void enqueue(int var1) {
         throw new UnsupportedOperationException();
      }

      public int dequeue() {
         throw new NoSuchElementException();
      }

      public boolean isEmpty() {
         return true;
      }

      public int size() {
         return 0;
      }

      public boolean contains(int var1) {
         return false;
      }

      public void clear() {
      }

      public int first() {
         throw new NoSuchElementException();
      }

      public int last() {
         throw new NoSuchElementException();
      }

      public void changed() {
         throw new NoSuchElementException();
      }

      public void allChanged() {
      }

      public Comparator<?> comparator() {
         return null;
      }

      public void changed(int var1) {
         throw new IllegalArgumentException("Index " + var1 + " is not in the queue");
      }

      public boolean remove(int var1) {
         return false;
      }

      public int front(int[] var1) {
         return 0;
      }
   }
}
