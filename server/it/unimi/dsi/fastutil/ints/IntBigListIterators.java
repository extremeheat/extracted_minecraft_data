package it.unimi.dsi.fastutil.ints;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class IntBigListIterators {
   public static final IntBigListIterators.EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new IntBigListIterators.EmptyBigListIterator();

   private IntBigListIterators() {
      super();
   }

   public static IntBigListIterator singleton(int var0) {
      return new IntBigListIterators.SingletonBigListIterator(var0);
   }

   public static IntBigListIterator unmodifiable(IntBigListIterator var0) {
      return new IntBigListIterators.UnmodifiableBigListIterator(var0);
   }

   public static IntBigListIterator asBigListIterator(IntListIterator var0) {
      return new IntBigListIterators.BigListIteratorListIterator(var0);
   }

   public static class BigListIteratorListIterator implements IntBigListIterator {
      protected final IntListIterator i;

      protected BigListIteratorListIterator(IntListIterator var1) {
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

      public void set(int var1) {
         this.i.set(var1);
      }

      public void add(int var1) {
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

      public int nextInt() {
         return this.i.nextInt();
      }

      public int previousInt() {
         return this.i.previousInt();
      }

      public long nextIndex() {
         return (long)this.i.nextIndex();
      }

      public long previousIndex() {
         return (long)this.i.previousIndex();
      }
   }

   public static class UnmodifiableBigListIterator implements IntBigListIterator {
      protected final IntBigListIterator i;

      public UnmodifiableBigListIterator(IntBigListIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public int nextInt() {
         return this.i.nextInt();
      }

      public int previousInt() {
         return this.i.previousInt();
      }

      public long nextIndex() {
         return this.i.nextIndex();
      }

      public long previousIndex() {
         return this.i.previousIndex();
      }
   }

   private static class SingletonBigListIterator implements IntBigListIterator {
      private final int element;
      private int curr;

      public SingletonBigListIterator(int var1) {
         super();
         this.element = var1;
      }

      public boolean hasNext() {
         return this.curr == 0;
      }

      public boolean hasPrevious() {
         return this.curr == 1;
      }

      public int nextInt() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      public int previousInt() {
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

   public static class EmptyBigListIterator implements IntBigListIterator, Serializable, Cloneable {
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

      public int nextInt() {
         throw new NoSuchElementException();
      }

      public int previousInt() {
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
         return IntBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      private Object readResolve() {
         return IntBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }
   }
}
