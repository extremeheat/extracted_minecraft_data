package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class DoubleBigListIterators {
   public static final DoubleBigListIterators.EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new DoubleBigListIterators.EmptyBigListIterator();

   private DoubleBigListIterators() {
      super();
   }

   public static DoubleBigListIterator singleton(double var0) {
      return new DoubleBigListIterators.SingletonBigListIterator(var0);
   }

   public static DoubleBigListIterator unmodifiable(DoubleBigListIterator var0) {
      return new DoubleBigListIterators.UnmodifiableBigListIterator(var0);
   }

   public static DoubleBigListIterator asBigListIterator(DoubleListIterator var0) {
      return new DoubleBigListIterators.BigListIteratorListIterator(var0);
   }

   public static class BigListIteratorListIterator implements DoubleBigListIterator {
      protected final DoubleListIterator i;

      protected BigListIteratorListIterator(DoubleListIterator var1) {
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

      public void set(double var1) {
         this.i.set(var1);
      }

      public void add(double var1) {
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

      public double nextDouble() {
         return this.i.nextDouble();
      }

      public double previousDouble() {
         return this.i.previousDouble();
      }

      public long nextIndex() {
         return (long)this.i.nextIndex();
      }

      public long previousIndex() {
         return (long)this.i.previousIndex();
      }
   }

   public static class UnmodifiableBigListIterator implements DoubleBigListIterator {
      protected final DoubleBigListIterator i;

      public UnmodifiableBigListIterator(DoubleBigListIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public double nextDouble() {
         return this.i.nextDouble();
      }

      public double previousDouble() {
         return this.i.previousDouble();
      }

      public long nextIndex() {
         return this.i.nextIndex();
      }

      public long previousIndex() {
         return this.i.previousIndex();
      }
   }

   private static class SingletonBigListIterator implements DoubleBigListIterator {
      private final double element;
      private int curr;

      public SingletonBigListIterator(double var1) {
         super();
         this.element = var1;
      }

      public boolean hasNext() {
         return this.curr == 0;
      }

      public boolean hasPrevious() {
         return this.curr == 1;
      }

      public double nextDouble() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      public double previousDouble() {
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

   public static class EmptyBigListIterator implements DoubleBigListIterator, Serializable, Cloneable {
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

      public double nextDouble() {
         throw new NoSuchElementException();
      }

      public double previousDouble() {
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
         return DoubleBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      private Object readResolve() {
         return DoubleBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }
   }
}
