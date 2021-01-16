package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.IntPredicate;

public final class ShortIterators {
   public static final ShortIterators.EmptyIterator EMPTY_ITERATOR = new ShortIterators.EmptyIterator();

   private ShortIterators() {
      super();
   }

   public static ShortListIterator singleton(short var0) {
      return new ShortIterators.SingletonIterator(var0);
   }

   public static ShortListIterator wrap(short[] var0, int var1, int var2) {
      ShortArrays.ensureOffsetLength(var0, var1, var2);
      return new ShortIterators.ArrayIterator(var0, var1, var2);
   }

   public static ShortListIterator wrap(short[] var0) {
      return new ShortIterators.ArrayIterator(var0, 0, var0.length);
   }

   public static int unwrap(ShortIterator var0, short[] var1, int var2, int var3) {
      if (var3 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var3 + ") is negative");
      } else if (var2 >= 0 && var2 + var3 <= var1.length) {
         int var4;
         for(var4 = var3; var4-- != 0 && var0.hasNext(); var1[var2++] = var0.nextShort()) {
         }

         return var3 - var4 - 1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int unwrap(ShortIterator var0, short[] var1) {
      return unwrap(var0, var1, 0, var1.length);
   }

   public static short[] unwrap(ShortIterator var0, int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var1 + ") is negative");
      } else {
         short[] var2 = new short[16];

         int var3;
         for(var3 = 0; var1-- != 0 && var0.hasNext(); var2[var3++] = var0.nextShort()) {
            if (var3 == var2.length) {
               var2 = ShortArrays.grow(var2, var3 + 1);
            }
         }

         return ShortArrays.trim(var2, var3);
      }
   }

   public static short[] unwrap(ShortIterator var0) {
      return unwrap(var0, 2147483647);
   }

   public static int unwrap(ShortIterator var0, ShortCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextShort());
         }

         return var2 - var3 - 1;
      }
   }

   public static long unwrap(ShortIterator var0, ShortCollection var1) {
      long var2;
      for(var2 = 0L; var0.hasNext(); ++var2) {
         var1.add(var0.nextShort());
      }

      return var2;
   }

   public static int pour(ShortIterator var0, ShortCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextShort());
         }

         return var2 - var3 - 1;
      }
   }

   public static int pour(ShortIterator var0, ShortCollection var1) {
      return pour(var0, var1, 2147483647);
   }

   public static ShortList pour(ShortIterator var0, int var1) {
      ShortArrayList var2 = new ShortArrayList();
      pour(var0, var2, var1);
      var2.trim();
      return var2;
   }

   public static ShortList pour(ShortIterator var0) {
      return pour(var0, 2147483647);
   }

   public static ShortIterator asShortIterator(Iterator var0) {
      return (ShortIterator)(var0 instanceof ShortIterator ? (ShortIterator)var0 : new ShortIterators.IteratorWrapper(var0));
   }

   public static ShortListIterator asShortIterator(ListIterator var0) {
      return (ShortListIterator)(var0 instanceof ShortListIterator ? (ShortListIterator)var0 : new ShortIterators.ListIteratorWrapper(var0));
   }

   public static boolean any(ShortIterator var0, IntPredicate var1) {
      return indexOf(var0, var1) != -1;
   }

   public static boolean all(ShortIterator var0, IntPredicate var1) {
      Objects.requireNonNull(var1);

      while(var0.hasNext()) {
         if (!var1.test(var0.nextShort())) {
            return false;
         }
      }

      return true;
   }

   public static int indexOf(ShortIterator var0, IntPredicate var1) {
      Objects.requireNonNull(var1);

      for(int var2 = 0; var0.hasNext(); ++var2) {
         if (var1.test(var0.nextShort())) {
            return var2;
         }
      }

      return -1;
   }

   public static ShortListIterator fromTo(short var0, short var1) {
      return new ShortIterators.IntervalIterator(var0, var1);
   }

   public static ShortIterator concat(ShortIterator[] var0) {
      return concat(var0, 0, var0.length);
   }

   public static ShortIterator concat(ShortIterator[] var0, int var1, int var2) {
      return new ShortIterators.IteratorConcatenator(var0, var1, var2);
   }

   public static ShortIterator unmodifiable(ShortIterator var0) {
      return new ShortIterators.UnmodifiableIterator(var0);
   }

   public static ShortBidirectionalIterator unmodifiable(ShortBidirectionalIterator var0) {
      return new ShortIterators.UnmodifiableBidirectionalIterator(var0);
   }

   public static ShortListIterator unmodifiable(ShortListIterator var0) {
      return new ShortIterators.UnmodifiableListIterator(var0);
   }

   public static ShortIterator wrap(ByteIterator var0) {
      return new ShortIterators.ByteIteratorWrapper(var0);
   }

   protected static class ByteIteratorWrapper implements ShortIterator {
      final ByteIterator iterator;

      public ByteIteratorWrapper(ByteIterator var1) {
         super();
         this.iterator = var1;
      }

      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      /** @deprecated */
      @Deprecated
      public Short next() {
         return (short)this.iterator.nextByte();
      }

      public short nextShort() {
         return (short)this.iterator.nextByte();
      }

      public void remove() {
         this.iterator.remove();
      }

      public int skip(int var1) {
         return this.iterator.skip(var1);
      }
   }

   public static class UnmodifiableListIterator implements ShortListIterator {
      protected final ShortListIterator i;

      public UnmodifiableListIterator(ShortListIterator var1) {
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

      public int nextIndex() {
         return this.i.nextIndex();
      }

      public int previousIndex() {
         return this.i.previousIndex();
      }
   }

   public static class UnmodifiableBidirectionalIterator implements ShortBidirectionalIterator {
      protected final ShortBidirectionalIterator i;

      public UnmodifiableBidirectionalIterator(ShortBidirectionalIterator var1) {
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
   }

   public static class UnmodifiableIterator implements ShortIterator {
      protected final ShortIterator i;

      public UnmodifiableIterator(ShortIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public short nextShort() {
         return this.i.nextShort();
      }
   }

   private static class IteratorConcatenator implements ShortIterator {
      final ShortIterator[] a;
      int offset;
      int length;
      int lastOffset = -1;

      public IteratorConcatenator(ShortIterator[] var1, int var2, int var3) {
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

      public short nextShort() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            short var1 = this.a[this.lastOffset = this.offset].nextShort();
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

   private static class IntervalIterator implements ShortListIterator {
      private final short from;
      private final short to;
      short curr;

      public IntervalIterator(short var1, short var2) {
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

      public short nextShort() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            short var10002 = this.curr;
            this.curr = (short)(var10002 + 1);
            return var10002;
         }
      }

      public short previousShort() {
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
            this.curr = (short)(this.curr + var1);
            return var1;
         } else {
            var1 = this.to - this.curr;
            this.curr = this.to;
            return var1;
         }
      }

      public int back(int var1) {
         if (this.curr - var1 >= this.from) {
            this.curr = (short)(this.curr - var1);
            return var1;
         } else {
            var1 = this.curr - this.from;
            this.curr = this.from;
            return var1;
         }
      }
   }

   private static class ListIteratorWrapper implements ShortListIterator {
      final ListIterator<Short> i;

      public ListIteratorWrapper(ListIterator<Short> var1) {
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

      public void set(short var1) {
         this.i.set(var1);
      }

      public void add(short var1) {
         this.i.add(var1);
      }

      public void remove() {
         this.i.remove();
      }

      public short nextShort() {
         return (Short)this.i.next();
      }

      public short previousShort() {
         return (Short)this.i.previous();
      }
   }

   private static class IteratorWrapper implements ShortIterator {
      final Iterator<Short> i;

      public IteratorWrapper(Iterator<Short> var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public void remove() {
         this.i.remove();
      }

      public short nextShort() {
         return (Short)this.i.next();
      }
   }

   private static class ArrayIterator implements ShortListIterator {
      private final short[] array;
      private final int offset;
      private final int length;
      private int curr;

      public ArrayIterator(short[] var1, int var2, int var3) {
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

      public short nextShort() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + this.curr++];
         }
      }

      public short previousShort() {
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

   private static class SingletonIterator implements ShortListIterator {
      private final short element;
      private int curr;

      public SingletonIterator(short var1) {
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

      public int nextIndex() {
         return this.curr;
      }

      public int previousIndex() {
         return this.curr - 1;
      }
   }

   public static class EmptyIterator implements ShortListIterator, Serializable, Cloneable {
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

      public short nextShort() {
         throw new NoSuchElementException();
      }

      public short previousShort() {
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
         return ShortIterators.EMPTY_ITERATOR;
      }

      private Object readResolve() {
         return ShortIterators.EMPTY_ITERATOR;
      }
   }
}
