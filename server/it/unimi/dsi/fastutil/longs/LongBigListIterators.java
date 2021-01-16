package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class LongBigListIterators {
   public static final LongBigListIterators.EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new LongBigListIterators.EmptyBigListIterator();

   private LongBigListIterators() {
      super();
   }

   public static LongBigListIterator singleton(long var0) {
      return new LongBigListIterators.SingletonBigListIterator(var0);
   }

   public static LongBigListIterator unmodifiable(LongBigListIterator var0) {
      return new LongBigListIterators.UnmodifiableBigListIterator(var0);
   }

   public static LongBigListIterator asBigListIterator(LongListIterator var0) {
      return new LongBigListIterators.BigListIteratorListIterator(var0);
   }

   public static class BigListIteratorListIterator implements LongBigListIterator {
      protected final LongListIterator i;

      protected BigListIteratorListIterator(LongListIterator var1) {
         super();
         this.i = var1;
      }

      private int intDisplacement(long var1) {
         if (var1 >= -2147483648L && var1 <= 2147483647L) {
            return (int)var1;
         } else {
            throw new IndexOutOfBoundsException("This big iterator is restricted to 32-bit displacements");
         }
      }

      public void set(long var1) {
         this.i.set(var1);
      }

      public void add(long var1) {
         this.i.add(var1);
      }

      public int back(int var1) {
         return this.i.back(var1);
      }

      public long back(long var1) {
         return (long)this.i.back(this.intDisplacement(var1));
      }

      public void remove() {
         this.i.remove();
      }

      public int skip(int var1) {
         return this.i.skip(var1);
      }

      public long skip(long var1) {
         return (long)this.i.skip(this.intDisplacement(var1));
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public long nextLong() {
         return this.i.nextLong();
      }

      public long previousLong() {
         return this.i.previousLong();
      }

      public long nextIndex() {
         return (long)this.i.nextIndex();
      }

      public long previousIndex() {
         return (long)this.i.previousIndex();
      }
   }

   public static class UnmodifiableBigListIterator implements LongBigListIterator {
      protected final LongBigListIterator i;

      public UnmodifiableBigListIterator(LongBigListIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public long nextLong() {
         return this.i.nextLong();
      }

      public long previousLong() {
         return this.i.previousLong();
      }

      public long nextIndex() {
         return this.i.nextIndex();
      }

      public long previousIndex() {
         return this.i.previousIndex();
      }
   }

   private static class SingletonBigListIterator implements LongBigListIterator {
      private final long element;
      private int curr;

      public SingletonBigListIterator(long var1) {
         super();
         this.element = var1;
      }

      public boolean hasNext() {
         return this.curr == 0;
      }

      public boolean hasPrevious() {
         return this.curr == 1;
      }

      public long nextLong() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      public long previousLong() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 0;
            return this.element;
         }
      }

      public long nextIndex() {
         return (long)this.curr;
      }

      public long previousIndex() {
         return (long)(this.curr - 1);
      }
   }

   public static class EmptyBigListIterator implements LongBigListIterator, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigListIterator() {
         super();
      }

      public boolean hasNext() {
         return false;
      }

      public boolean hasPrevious() {
         return false;
      }

      public long nextLong() {
         throw new NoSuchElementException();
      }

      public long previousLong() {
         throw new NoSuchElementException();
      }

      public long nextIndex() {
         return 0L;
      }

      public long previousIndex() {
         return -1L;
      }

      public long skip(long var1) {
         return 0L;
      }

      public long back(long var1) {
         return 0L;
      }

      public Object clone() {
         return LongBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      private Object readResolve() {
         return LongBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }
   }
}
