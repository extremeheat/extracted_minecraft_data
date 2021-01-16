package it.unimi.dsi.fastutil.floats;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class FloatBigListIterators {
   public static final FloatBigListIterators.EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new FloatBigListIterators.EmptyBigListIterator();

   private FloatBigListIterators() {
      super();
   }

   public static FloatBigListIterator singleton(float var0) {
      return new FloatBigListIterators.SingletonBigListIterator(var0);
   }

   public static FloatBigListIterator unmodifiable(FloatBigListIterator var0) {
      return new FloatBigListIterators.UnmodifiableBigListIterator(var0);
   }

   public static FloatBigListIterator asBigListIterator(FloatListIterator var0) {
      return new FloatBigListIterators.BigListIteratorListIterator(var0);
   }

   public static class BigListIteratorListIterator implements FloatBigListIterator {
      protected final FloatListIterator i;

      protected BigListIteratorListIterator(FloatListIterator var1) {
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

      public void set(float var1) {
         this.i.set(var1);
      }

      public void add(float var1) {
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

      public float nextFloat() {
         return this.i.nextFloat();
      }

      public float previousFloat() {
         return this.i.previousFloat();
      }

      public long nextIndex() {
         return (long)this.i.nextIndex();
      }

      public long previousIndex() {
         return (long)this.i.previousIndex();
      }
   }

   public static class UnmodifiableBigListIterator implements FloatBigListIterator {
      protected final FloatBigListIterator i;

      public UnmodifiableBigListIterator(FloatBigListIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public float nextFloat() {
         return this.i.nextFloat();
      }

      public float previousFloat() {
         return this.i.previousFloat();
      }

      public long nextIndex() {
         return this.i.nextIndex();
      }

      public long previousIndex() {
         return this.i.previousIndex();
      }
   }

   private static class SingletonBigListIterator implements FloatBigListIterator {
      private final float element;
      private int curr;

      public SingletonBigListIterator(float var1) {
         super();
         this.element = var1;
      }

      public boolean hasNext() {
         return this.curr == 0;
      }

      public boolean hasPrevious() {
         return this.curr == 1;
      }

      public float nextFloat() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      public float previousFloat() {
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

   public static class EmptyBigListIterator implements FloatBigListIterator, Serializable, Cloneable {
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

      public float nextFloat() {
         throw new NoSuchElementException();
      }

      public float previousFloat() {
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
         return FloatBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      private Object readResolve() {
         return FloatBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }
   }
}
