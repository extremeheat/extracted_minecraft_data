package it.unimi.dsi.fastutil.chars;

import java.io.IOException;
import java.io.ObjectOutputStream;

public final class CharPriorityQueues {
   private CharPriorityQueues() {
      super();
   }

   public static CharPriorityQueue synchronize(CharPriorityQueue var0) {
      return new CharPriorityQueues.SynchronizedPriorityQueue(var0);
   }

   public static CharPriorityQueue synchronize(CharPriorityQueue var0, Object var1) {
      return new CharPriorityQueues.SynchronizedPriorityQueue(var0, var1);
   }

   public static class SynchronizedPriorityQueue implements CharPriorityQueue {
      protected final CharPriorityQueue q;
      protected final Object sync;

      protected SynchronizedPriorityQueue(CharPriorityQueue var1, Object var2) {
         super();
         this.q = var1;
         this.sync = var2;
      }

      protected SynchronizedPriorityQueue(CharPriorityQueue var1) {
         super();
         this.q = var1;
         this.sync = this;
      }

      public void enqueue(char var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      public char dequeueChar() {
         synchronized(this.sync) {
            return this.q.dequeueChar();
         }
      }

      public char firstChar() {
         synchronized(this.sync) {
            return this.q.firstChar();
         }
      }

      public char lastChar() {
         synchronized(this.sync) {
            return this.q.lastChar();
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

      public CharComparator comparator() {
         synchronized(this.sync) {
            return this.q.comparator();
         }
      }

      /** @deprecated */
      @Deprecated
      public void enqueue(Character var1) {
         synchronized(this.sync) {
            this.q.enqueue(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character dequeue() {
         synchronized(this.sync) {
            return this.q.dequeue();
         }
      }

      /** @deprecated */
      @Deprecated
      public Character first() {
         synchronized(this.sync) {
            return this.q.first();
         }
      }

      /** @deprecated */
      @Deprecated
      public Character last() {
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
