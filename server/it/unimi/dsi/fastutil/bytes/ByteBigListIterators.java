package it.unimi.dsi.fastutil.bytes;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class ByteBigListIterators {
   public static final ByteBigListIterators.EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new ByteBigListIterators.EmptyBigListIterator();

   private ByteBigListIterators() {
      super();
   }

   public static ByteBigListIterator singleton(byte var0) {
      return new ByteBigListIterators.SingletonBigListIterator(var0);
   }

   public static ByteBigListIterator unmodifiable(ByteBigListIterator var0) {
      return new ByteBigListIterators.UnmodifiableBigListIterator(var0);
   }

   public static ByteBigListIterator asBigListIterator(ByteListIterator var0) {
      return new ByteBigListIterators.BigListIteratorListIterator(var0);
   }

   public static class BigListIteratorListIterator implements ByteBigListIterator {
      protected final ByteListIterator i;

      protected BigListIteratorListIterator(ByteListIterator var1) {
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

      public void set(byte var1) {
         this.i.set(var1);
      }

      public void add(byte var1) {
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

      public byte nextByte() {
         return this.i.nextByte();
      }

      public byte previousByte() {
         return this.i.previousByte();
      }

      public long nextIndex() {
         return (long)this.i.nextIndex();
      }

      public long previousIndex() {
         return (long)this.i.previousIndex();
      }
   }

   public static class UnmodifiableBigListIterator implements ByteBigListIterator {
      protected final ByteBigListIterator i;

      public UnmodifiableBigListIterator(ByteBigListIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public byte nextByte() {
         return this.i.nextByte();
      }

      public byte previousByte() {
         return this.i.previousByte();
      }

      public long nextIndex() {
         return this.i.nextIndex();
      }

      public long previousIndex() {
         return this.i.previousIndex();
      }
   }

   private static class SingletonBigListIterator implements ByteBigListIterator {
      private final byte element;
      private int curr;

      public SingletonBigListIterator(byte var1) {
         super();
         this.element = var1;
      }

      public boolean hasNext() {
         return this.curr == 0;
      }

      public boolean hasPrevious() {
         return this.curr == 1;
      }

      public byte nextByte() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      public byte previousByte() {
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

   public static class EmptyBigListIterator implements ByteBigListIterator, Serializable, Cloneable {
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

      public byte nextByte() {
         throw new NoSuchElementException();
      }

      public byte previousByte() {
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
         return ByteBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      private Object readResolve() {
         return ByteBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }
   }
}
