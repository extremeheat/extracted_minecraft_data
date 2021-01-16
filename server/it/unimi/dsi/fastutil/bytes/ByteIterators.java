package it.unimi.dsi.fastutil.bytes;

import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.IntPredicate;

public final class ByteIterators {
   public static final ByteIterators.EmptyIterator EMPTY_ITERATOR = new ByteIterators.EmptyIterator();

   private ByteIterators() {
      super();
   }

   public static ByteListIterator singleton(byte var0) {
      return new ByteIterators.SingletonIterator(var0);
   }

   public static ByteListIterator wrap(byte[] var0, int var1, int var2) {
      ByteArrays.ensureOffsetLength(var0, var1, var2);
      return new ByteIterators.ArrayIterator(var0, var1, var2);
   }

   public static ByteListIterator wrap(byte[] var0) {
      return new ByteIterators.ArrayIterator(var0, 0, var0.length);
   }

   public static int unwrap(ByteIterator var0, byte[] var1, int var2, int var3) {
      if (var3 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var3 + ") is negative");
      } else if (var2 >= 0 && var2 + var3 <= var1.length) {
         int var4;
         for(var4 = var3; var4-- != 0 && var0.hasNext(); var1[var2++] = var0.nextByte()) {
         }

         return var3 - var4 - 1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int unwrap(ByteIterator var0, byte[] var1) {
      return unwrap(var0, var1, 0, var1.length);
   }

   public static byte[] unwrap(ByteIterator var0, int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var1 + ") is negative");
      } else {
         byte[] var2 = new byte[16];

         int var3;
         for(var3 = 0; var1-- != 0 && var0.hasNext(); var2[var3++] = var0.nextByte()) {
            if (var3 == var2.length) {
               var2 = ByteArrays.grow(var2, var3 + 1);
            }
         }

         return ByteArrays.trim(var2, var3);
      }
   }

   public static byte[] unwrap(ByteIterator var0) {
      return unwrap(var0, 2147483647);
   }

   public static int unwrap(ByteIterator var0, ByteCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextByte());
         }

         return var2 - var3 - 1;
      }
   }

   public static long unwrap(ByteIterator var0, ByteCollection var1) {
      long var2;
      for(var2 = 0L; var0.hasNext(); ++var2) {
         var1.add(var0.nextByte());
      }

      return var2;
   }

   public static int pour(ByteIterator var0, ByteCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextByte());
         }

         return var2 - var3 - 1;
      }
   }

   public static int pour(ByteIterator var0, ByteCollection var1) {
      return pour(var0, var1, 2147483647);
   }

   public static ByteList pour(ByteIterator var0, int var1) {
      ByteArrayList var2 = new ByteArrayList();
      pour(var0, var2, var1);
      var2.trim();
      return var2;
   }

   public static ByteList pour(ByteIterator var0) {
      return pour(var0, 2147483647);
   }

   public static ByteIterator asByteIterator(Iterator var0) {
      return (ByteIterator)(var0 instanceof ByteIterator ? (ByteIterator)var0 : new ByteIterators.IteratorWrapper(var0));
   }

   public static ByteListIterator asByteIterator(ListIterator var0) {
      return (ByteListIterator)(var0 instanceof ByteListIterator ? (ByteListIterator)var0 : new ByteIterators.ListIteratorWrapper(var0));
   }

   public static boolean any(ByteIterator var0, IntPredicate var1) {
      return indexOf(var0, var1) != -1;
   }

   public static boolean all(ByteIterator var0, IntPredicate var1) {
      Objects.requireNonNull(var1);

      while(var0.hasNext()) {
         if (!var1.test(var0.nextByte())) {
            return false;
         }
      }

      return true;
   }

   public static int indexOf(ByteIterator var0, IntPredicate var1) {
      Objects.requireNonNull(var1);

      for(int var2 = 0; var0.hasNext(); ++var2) {
         if (var1.test(var0.nextByte())) {
            return var2;
         }
      }

      return -1;
   }

   public static ByteListIterator fromTo(byte var0, byte var1) {
      return new ByteIterators.IntervalIterator(var0, var1);
   }

   public static ByteIterator concat(ByteIterator[] var0) {
      return concat(var0, 0, var0.length);
   }

   public static ByteIterator concat(ByteIterator[] var0, int var1, int var2) {
      return new ByteIterators.IteratorConcatenator(var0, var1, var2);
   }

   public static ByteIterator unmodifiable(ByteIterator var0) {
      return new ByteIterators.UnmodifiableIterator(var0);
   }

   public static ByteBidirectionalIterator unmodifiable(ByteBidirectionalIterator var0) {
      return new ByteIterators.UnmodifiableBidirectionalIterator(var0);
   }

   public static ByteListIterator unmodifiable(ByteListIterator var0) {
      return new ByteIterators.UnmodifiableListIterator(var0);
   }

   public static class UnmodifiableListIterator implements ByteListIterator {
      protected final ByteListIterator i;

      public UnmodifiableListIterator(ByteListIterator var1) {
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

      public int nextIndex() {
         return this.i.nextIndex();
      }

      public int previousIndex() {
         return this.i.previousIndex();
      }
   }

   public static class UnmodifiableBidirectionalIterator implements ByteBidirectionalIterator {
      protected final ByteBidirectionalIterator i;

      public UnmodifiableBidirectionalIterator(ByteBidirectionalIterator var1) {
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
   }

   public static class UnmodifiableIterator implements ByteIterator {
      protected final ByteIterator i;

      public UnmodifiableIterator(ByteIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public byte nextByte() {
         return this.i.nextByte();
      }
   }

   private static class IteratorConcatenator implements ByteIterator {
      final ByteIterator[] a;
      int offset;
      int length;
      int lastOffset = -1;

      public IteratorConcatenator(ByteIterator[] var1, int var2, int var3) {
         super();
         this.a = var1;
         this.offset = var2;
         this.length = var3;
         this.advance();
      }

      private void advance() {
         while(this.length != 0 && !this.a[this.offset].hasNext()) {
            --this.length;
            ++this.offset;
         }

      }

      public boolean hasNext() {
         return this.length > 0;
      }

      public byte nextByte() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            byte var1 = this.a[this.lastOffset = this.offset].nextByte();
            this.advance();
            return var1;
         }
      }

      public void remove() {
         if (this.lastOffset == -1) {
            throw new IllegalStateException();
         } else {
            this.a[this.lastOffset].remove();
         }
      }

      public int skip(int var1) {
         this.lastOffset = -1;

         int var2;
         for(var2 = 0; var2 < var1 && this.length != 0; ++this.offset) {
            var2 += this.a[this.offset].skip(var1 - var2);
            if (this.a[this.offset].hasNext()) {
               break;
            }

            --this.length;
         }

         return var2;
      }
   }

   private static class IntervalIterator implements ByteListIterator {
      private final byte from;
      private final byte to;
      byte curr;

      public IntervalIterator(byte var1, byte var2) {
         super();
         this.from = this.curr = var1;
         this.to = var2;
      }

      public boolean hasNext() {
         return this.curr < this.to;
      }

      public boolean hasPrevious() {
         return this.curr > this.from;
      }

      public byte nextByte() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            byte var10002 = this.curr;
            this.curr = (byte)(var10002 + 1);
            return var10002;
         }
      }

      public byte previousByte() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return --this.curr;
         }
      }

      public int nextIndex() {
         return this.curr - this.from;
      }

      public int previousIndex() {
         return this.curr - this.from - 1;
      }

      public int skip(int var1) {
         if (this.curr + var1 <= this.to) {
            this.curr = (byte)(this.curr + var1);
            return var1;
         } else {
            var1 = this.to - this.curr;
            this.curr = this.to;
            return var1;
         }
      }

      public int back(int var1) {
         if (this.curr - var1 >= this.from) {
            this.curr = (byte)(this.curr - var1);
            return var1;
         } else {
            var1 = this.curr - this.from;
            this.curr = this.from;
            return var1;
         }
      }
   }

   private static class ListIteratorWrapper implements ByteListIterator {
      final ListIterator<Byte> i;

      public ListIteratorWrapper(ListIterator<Byte> var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public int nextIndex() {
         return this.i.nextIndex();
      }

      public int previousIndex() {
         return this.i.previousIndex();
      }

      public void set(byte var1) {
         this.i.set(var1);
      }

      public void add(byte var1) {
         this.i.add(var1);
      }

      public void remove() {
         this.i.remove();
      }

      public byte nextByte() {
         return (Byte)this.i.next();
      }

      public byte previousByte() {
         return (Byte)this.i.previous();
      }
   }

   private static class IteratorWrapper implements ByteIterator {
      final Iterator<Byte> i;

      public IteratorWrapper(Iterator<Byte> var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public void remove() {
         this.i.remove();
      }

      public byte nextByte() {
         return (Byte)this.i.next();
      }
   }

   private static class ArrayIterator implements ByteListIterator {
      private final byte[] array;
      private final int offset;
      private final int length;
      private int curr;

      public ArrayIterator(byte[] var1, int var2, int var3) {
         super();
         this.array = var1;
         this.offset = var2;
         this.length = var3;
      }

      public boolean hasNext() {
         return this.curr < this.length;
      }

      public boolean hasPrevious() {
         return this.curr > 0;
      }

      public byte nextByte() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + this.curr++];
         }
      }

      public byte previousByte() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + --this.curr];
         }
      }

      public int skip(int var1) {
         if (var1 <= this.length - this.curr) {
            this.curr += var1;
            return var1;
         } else {
            var1 = this.length - this.curr;
            this.curr = this.length;
            return var1;
         }
      }

      public int back(int var1) {
         if (var1 <= this.curr) {
            this.curr -= var1;
            return var1;
         } else {
            var1 = this.curr;
            this.curr = 0;
            return var1;
         }
      }

      public int nextIndex() {
         return this.curr;
      }

      public int previousIndex() {
         return this.curr - 1;
      }
   }

   private static class SingletonIterator implements ByteListIterator {
      private final byte element;
      private int curr;

      public SingletonIterator(byte var1) {
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

      public int nextIndex() {
         return this.curr;
      }

      public int previousIndex() {
         return this.curr - 1;
      }
   }

   public static class EmptyIterator implements ByteListIterator, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyIterator() {
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

      public int nextIndex() {
         return 0;
      }

      public int previousIndex() {
         return -1;
      }

      public int skip(int var1) {
         return 0;
      }

      public int back(int var1) {
         return 0;
      }

      public Object clone() {
         return ByteIterators.EMPTY_ITERATOR;
      }

      private Object readResolve() {
         return ByteIterators.EMPTY_ITERATOR;
      }
   }
}
