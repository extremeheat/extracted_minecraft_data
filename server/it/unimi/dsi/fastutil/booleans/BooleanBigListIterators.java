package it.unimi.dsi.fastutil.booleans;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class BooleanBigListIterators {
   public static final BooleanBigListIterators.EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new BooleanBigListIterators.EmptyBigListIterator();

   private BooleanBigListIterators() {
      super();
   }

   public static BooleanBigListIterator singleton(boolean var0) {
      return new BooleanBigListIterators.SingletonBigListIterator(var0);
   }

   public static BooleanBigListIterator unmodifiable(BooleanBigListIterator var0) {
      return new BooleanBigListIterators.UnmodifiableBigListIterator(var0);
   }

   public static BooleanBigListIterator asBigListIterator(BooleanListIterator var0) {
      return new BooleanBigListIterators.BigListIteratorListIterator(var0);
   }

   public static class BigListIteratorListIterator implements BooleanBigListIterator {
      protected final BooleanListIterator i;

      protected BigListIteratorListIterator(BooleanListIterator var1) {
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

      public void set(boolean var1) {
         this.i.set(var1);
      }

      public void add(boolean var1) {
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

      public boolean nextBoolean() {
         return this.i.nextBoolean();
      }

      public boolean previousBoolean() {
         return this.i.previousBoolean();
      }

      public long nextIndex() {
         return (long)this.i.nextIndex();
      }

      public long previousIndex() {
         return (long)this.i.previousIndex();
      }
   }

   public static class UnmodifiableBigListIterator implements BooleanBigListIterator {
      protected final BooleanBigListIterator i;

      public UnmodifiableBigListIterator(BooleanBigListIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public boolean nextBoolean() {
         return this.i.nextBoolean();
      }

      public boolean previousBoolean() {
         return this.i.previousBoolean();
      }

      public long nextIndex() {
         return this.i.nextIndex();
      }

      public long previousIndex() {
         return this.i.previousIndex();
      }
   }

   private static class SingletonBigListIterator implements BooleanBigListIterator {
      private final boolean element;
      private int curr;

      public SingletonBigListIterator(boolean var1) {
         super();
         this.element = var1;
      }

      public boolean hasNext() {
         return this.curr == 0;
      }

      public boolean hasPrevious() {
         return this.curr == 1;
      }

      public boolean nextBoolean() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      public boolean previousBoolean() {
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

   public static class EmptyBigListIterator implements BooleanBigListIterator, Serializable, Cloneable {
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

      public boolean nextBoolean() {
         throw new NoSuchElementException();
      }

      public boolean previousBoolean() {
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
         return BooleanBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      private Object readResolve() {
         return BooleanBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }
   }
}
