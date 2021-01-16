package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.IntPredicate;

public final class IntIterators {
   public static final IntIterators.EmptyIterator EMPTY_ITERATOR = new IntIterators.EmptyIterator();

   private IntIterators() {
      super();
   }

   public static IntListIterator singleton(int var0) {
      return new IntIterators.SingletonIterator(var0);
   }

   public static IntListIterator wrap(int[] var0, int var1, int var2) {
      IntArrays.ensureOffsetLength(var0, var1, var2);
      return new IntIterators.ArrayIterator(var0, var1, var2);
   }

   public static IntListIterator wrap(int[] var0) {
      return new IntIterators.ArrayIterator(var0, 0, var0.length);
   }

   public static int unwrap(IntIterator var0, int[] var1, int var2, int var3) {
      if (var3 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var3 + ") is negative");
      } else if (var2 >= 0 && var2 + var3 <= var1.length) {
         int var4;
         for(var4 = var3; var4-- != 0 && var0.hasNext(); var1[var2++] = var0.nextInt()) {
         }

         return var3 - var4 - 1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int unwrap(IntIterator var0, int[] var1) {
      return unwrap(var0, var1, 0, var1.length);
   }

   public static int[] unwrap(IntIterator var0, int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var1 + ") is negative");
      } else {
         int[] var2 = new int[16];

         int var3;
         for(var3 = 0; var1-- != 0 && var0.hasNext(); var2[var3++] = var0.nextInt()) {
            if (var3 == var2.length) {
               var2 = IntArrays.grow(var2, var3 + 1);
            }
         }

         return IntArrays.trim(var2, var3);
      }
   }

   public static int[] unwrap(IntIterator var0) {
      return unwrap(var0, 2147483647);
   }

   public static int unwrap(IntIterator var0, IntCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextInt());
         }

         return var2 - var3 - 1;
      }
   }

   public static long unwrap(IntIterator var0, IntCollection var1) {
      long var2;
      for(var2 = 0L; var0.hasNext(); ++var2) {
         var1.add(var0.nextInt());
      }

      return var2;
   }

   public static int pour(IntIterator var0, IntCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextInt());
         }

         return var2 - var3 - 1;
      }
   }

   public static int pour(IntIterator var0, IntCollection var1) {
      return pour(var0, var1, 2147483647);
   }

   public static IntList pour(IntIterator var0, int var1) {
      IntArrayList var2 = new IntArrayList();
      pour(var0, var2, var1);
      var2.trim();
      return var2;
   }

   public static IntList pour(IntIterator var0) {
      return pour(var0, 2147483647);
   }

   public static IntIterator asIntIterator(Iterator var0) {
      return (IntIterator)(var0 instanceof IntIterator ? (IntIterator)var0 : new IntIterators.IteratorWrapper(var0));
   }

   public static IntListIterator asIntIterator(ListIterator var0) {
      return (IntListIterator)(var0 instanceof IntListIterator ? (IntListIterator)var0 : new IntIterators.ListIteratorWrapper(var0));
   }

   public static boolean any(IntIterator var0, IntPredicate var1) {
      return indexOf(var0, var1) != -1;
   }

   public static boolean all(IntIterator var0, IntPredicate var1) {
      Objects.requireNonNull(var1);

      while(var0.hasNext()) {
         if (!var1.test(var0.nextInt())) {
            return false;
         }
      }

      return true;
   }

   public static int indexOf(IntIterator var0, IntPredicate var1) {
      Objects.requireNonNull(var1);

      for(int var2 = 0; var0.hasNext(); ++var2) {
         if (var1.test(var0.nextInt())) {
            return var2;
         }
      }

      return -1;
   }

   public static IntListIterator fromTo(int var0, int var1) {
      return new IntIterators.IntervalIterator(var0, var1);
   }

   public static IntIterator concat(IntIterator[] var0) {
      return concat(var0, 0, var0.length);
   }

   public static IntIterator concat(IntIterator[] var0, int var1, int var2) {
      return new IntIterators.IteratorConcatenator(var0, var1, var2);
   }

   public static IntIterator unmodifiable(IntIterator var0) {
      return new IntIterators.UnmodifiableIterator(var0);
   }

   public static IntBidirectionalIterator unmodifiable(IntBidirectionalIterator var0) {
      return new IntIterators.UnmodifiableBidirectionalIterator(var0);
   }

   public static IntListIterator unmodifiable(IntListIterator var0) {
      return new IntIterators.UnmodifiableListIterator(var0);
   }

   public static IntIterator wrap(ByteIterator var0) {
      return new IntIterators.ByteIteratorWrapper(var0);
   }

   public static IntIterator wrap(ShortIterator var0) {
      return new IntIterators.ShortIteratorWrapper(var0);
   }

   protected static class ShortIteratorWrapper implements IntIterator {
      final ShortIterator iterator;

      public ShortIteratorWrapper(ShortIterator var1) {
         super();
         this.iterator = var1;
      }

      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      /** @deprecated */
      @Deprecated
      public Integer next() {
         return Integer.valueOf(this.iterator.nextShort());
      }

      public int nextInt() {
         return this.iterator.nextShort();
      }

      public void remove() {
         this.iterator.remove();
      }

      public int skip(int var1) {
         return this.iterator.skip(var1);
      }
   }

   protected static class ByteIteratorWrapper implements IntIterator {
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
      public Integer next() {
         return Integer.valueOf(this.iterator.nextByte());
      }

      public int nextInt() {
         return this.iterator.nextByte();
      }

      public void remove() {
         this.iterator.remove();
      }

      public int skip(int var1) {
         return this.iterator.skip(var1);
      }
   }

   public static class UnmodifiableListIterator implements IntListIterator {
      protected final IntListIterator i;

      public UnmodifiableListIterator(IntListIterator var1) {
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

      public int nextIndex() {
         return this.i.nextIndex();
      }

      public int previousIndex() {
         return this.i.previousIndex();
      }
   }

   public static class UnmodifiableBidirectionalIterator implements IntBidirectionalIterator {
      protected final IntBidirectionalIterator i;

      public UnmodifiableBidirectionalIterator(IntBidirectionalIterator var1) {
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
   }

   public static class UnmodifiableIterator implements IntIterator {
      protected final IntIterator i;

      public UnmodifiableIterator(IntIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public int nextInt() {
         return this.i.nextInt();
      }
   }

   private static class IteratorConcatenator implements IntIterator {
      final IntIterator[] a;
      int offset;
      int length;
      int lastOffset = -1;

      public IteratorConcatenator(IntIterator[] var1, int var2, int var3) {
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

      public int nextInt() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            int var1 = this.a[this.lastOffset = this.offset].nextInt();
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

   private static class IntervalIterator implements IntListIterator {
      private final int from;
      private final int to;
      int curr;

      public IntervalIterator(int var1, int var2) {
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

      public int nextInt() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.curr++;
         }
      }

      public int previousInt() {
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
            this.curr += var1;
            return var1;
         } else {
            var1 = this.to - this.curr;
            this.curr = this.to;
            return var1;
         }
      }

      public int back(int var1) {
         if (this.curr - var1 >= this.from) {
            this.curr -= var1;
            return var1;
         } else {
            var1 = this.curr - this.from;
            this.curr = this.from;
            return var1;
         }
      }
   }

   private static class ListIteratorWrapper implements IntListIterator {
      final ListIterator<Integer> i;

      public ListIteratorWrapper(ListIterator<Integer> var1) {
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

      public void set(int var1) {
         this.i.set(var1);
      }

      public void add(int var1) {
         this.i.add(var1);
      }

      public void remove() {
         this.i.remove();
      }

      public int nextInt() {
         return (Integer)this.i.next();
      }

      public int previousInt() {
         return (Integer)this.i.previous();
      }
   }

   private static class IteratorWrapper implements IntIterator {
      final Iterator<Integer> i;

      public IteratorWrapper(Iterator<Integer> var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public void remove() {
         this.i.remove();
      }

      public int nextInt() {
         return (Integer)this.i.next();
      }
   }

   private static class ArrayIterator implements IntListIterator {
      private final int[] array;
      private final int offset;
      private final int length;
      private int curr;

      public ArrayIterator(int[] var1, int var2, int var3) {
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

      public int nextInt() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + this.curr++];
         }
      }

      public int previousInt() {
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

   private static class SingletonIterator implements IntListIterator {
      private final int element;
      private int curr;

      public SingletonIterator(int var1) {
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

      public int nextIndex() {
         return this.curr;
      }

      public int previousIndex() {
         return this.curr - 1;
      }
   }

   public static class EmptyIterator implements IntListIterator, Serializable, Cloneable {
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

      public int nextInt() {
         throw new NoSuchElementException();
      }

      public int previousInt() {
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
         return IntIterators.EMPTY_ITERATOR;
      }

      private Object readResolve() {
         return IntIterators.EMPTY_ITERATOR;
      }
   }
}
