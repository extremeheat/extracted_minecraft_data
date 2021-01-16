package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class ShortBigListIterators {
   public static final ShortBigListIterators.EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new ShortBigListIterators.EmptyBigListIterator();

   private ShortBigListIterators() {
      super();
   }

   public static ShortBigListIterator singleton(short var0) {
      return new ShortBigListIterators.SingletonBigListIterator(var0);
   }

   public static ShortBigListIterator unmodifiable(ShortBigListIterator var0) {
      return new ShortBigListIterators.UnmodifiableBigListIterator(var0);
   }

   public static ShortBigListIterator asBigListIterator(ShortListIterator var0) {
      return new ShortBigListIterators.BigListIteratorListIterator(var0);
   }

   public static class BigListIteratorListIterator implements ShortBigListIterator {
      protected final ShortListIterator i;

      protected BigListIteratorListIterator(ShortListIterator var1) {
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

      public void set(short var1) {
         this.i.set(var1);
      }

      public void add(short var1) {
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

      public short nextShort() {
         return this.i.nextShort();
      }

      public short previousShort() {
         return this.i.previousShort();
      }

      public long nextIndex() {
         return (long)this.i.nextIndex();
      }

      public long previousIndex() {
         return (long)this.i.previousIndex();
      }
   }

   public static class UnmodifiableBigListIterator implements ShortBigListIterator {
      protected final ShortBigListIterator i;

      public UnmodifiableBigListIterator(ShortBigListIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public short nextShort() {
         return this.i.nextShort();
      }

      public short previousShort() {
         return this.i.previousShort();
      }

      public long nextIndex() {
         return this.i.nextIndex();
      }

      public long previousIndex() {
         return this.i.previousIndex();
      }
   }

   private static class SingletonBigListIterator implements ShortBigListIterator {
      private final short element;
      private int curr;

      public SingletonBigListIterator(short var1) {
         super();
         this.element = var1;
      }

      public boolean hasNext() {
         return this.curr == 0;
      }

      public boolean hasPrevious() {
         return this.curr == 1;
      }

      public short nextShort() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      public short previousShort() {
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

   public static class EmptyBigListIterator implements ShortBigListIterator, Serializable, Cloneable {
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

      public short nextShort() {
         throw new NoSuchElementException();
      }

      public short previousShort() {
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
         return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      private Object readResolve() {
         return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }
   }
}
