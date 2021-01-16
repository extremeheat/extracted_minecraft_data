package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.LongPredicate;

public final class LongIterators {
   public static final LongIterators.EmptyIterator EMPTY_ITERATOR = new LongIterators.EmptyIterator();

   private LongIterators() {
      super();
   }

   public static LongListIterator singleton(long var0) {
      return new LongIterators.SingletonIterator(var0);
   }

   public static LongListIterator wrap(long[] var0, int var1, int var2) {
      LongArrays.ensureOffsetLength(var0, var1, var2);
      return new LongIterators.ArrayIterator(var0, var1, var2);
   }

   public static LongListIterator wrap(long[] var0) {
      return new LongIterators.ArrayIterator(var0, 0, var0.length);
   }

   public static int unwrap(LongIterator var0, long[] var1, int var2, int var3) {
      if (var3 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var3 + ") is negative");
      } else if (var2 >= 0 && var2 + var3 <= var1.length) {
         int var4;
         for(var4 = var3; var4-- != 0 && var0.hasNext(); var1[var2++] = var0.nextLong()) {
         }

         return var3 - var4 - 1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int unwrap(LongIterator var0, long[] var1) {
      return unwrap(var0, var1, 0, var1.length);
   }

   public static long[] unwrap(LongIterator var0, int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var1 + ") is negative");
      } else {
         long[] var2 = new long[16];

         int var3;
         for(var3 = 0; var1-- != 0 && var0.hasNext(); var2[var3++] = var0.nextLong()) {
            if (var3 == var2.length) {
               var2 = LongArrays.grow(var2, var3 + 1);
            }
         }

         return LongArrays.trim(var2, var3);
      }
   }

   public static long[] unwrap(LongIterator var0) {
      return unwrap(var0, 2147483647);
   }

   public static int unwrap(LongIterator var0, LongCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextLong());
         }

         return var2 - var3 - 1;
      }
   }

   public static long unwrap(LongIterator var0, LongCollection var1) {
      long var2;
      for(var2 = 0L; var0.hasNext(); ++var2) {
         var1.add(var0.nextLong());
      }

      return var2;
   }

   public static int pour(LongIterator var0, LongCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextLong());
         }

         return var2 - var3 - 1;
      }
   }

   public static int pour(LongIterator var0, LongCollection var1) {
      return pour(var0, var1, 2147483647);
   }

   public static LongList pour(LongIterator var0, int var1) {
      LongArrayList var2 = new LongArrayList();
      pour(var0, var2, var1);
      var2.trim();
      return var2;
   }

   public static LongList pour(LongIterator var0) {
      return pour(var0, 2147483647);
   }

   public static LongIterator asLongIterator(Iterator var0) {
      return (LongIterator)(var0 instanceof LongIterator ? (LongIterator)var0 : new LongIterators.IteratorWrapper(var0));
   }

   public static LongListIterator asLongIterator(ListIterator var0) {
      return (LongListIterator)(var0 instanceof LongListIterator ? (LongListIterator)var0 : new LongIterators.ListIteratorWrapper(var0));
   }

   public static boolean any(LongIterator var0, LongPredicate var1) {
      return indexOf(var0, var1) != -1;
   }

   public static boolean all(LongIterator var0, LongPredicate var1) {
      Objects.requireNonNull(var1);

      while(var0.hasNext()) {
         if (!var1.test(var0.nextLong())) {
            return false;
         }
      }

      return true;
   }

   public static int indexOf(LongIterator var0, LongPredicate var1) {
      Objects.requireNonNull(var1);

      for(int var2 = 0; var0.hasNext(); ++var2) {
         if (var1.test(var0.nextLong())) {
            return var2;
         }
      }

      return -1;
   }

   public static LongBidirectionalIterator fromTo(long var0, long var2) {
      return new LongIterators.IntervalIterator(var0, var2);
   }

   public static LongIterator concat(LongIterator[] var0) {
      return concat(var0, 0, var0.length);
   }

   public static LongIterator concat(LongIterator[] var0, int var1, int var2) {
      return new LongIterators.IteratorConcatenator(var0, var1, var2);
   }

   public static LongIterator unmodifiable(LongIterator var0) {
      return new LongIterators.UnmodifiableIterator(var0);
   }

   public static LongBidirectionalIterator unmodifiable(LongBidirectionalIterator var0) {
      return new LongIterators.UnmodifiableBidirectionalIterator(var0);
   }

   public static LongListIterator unmodifiable(LongListIterator var0) {
      return new LongIterators.UnmodifiableListIterator(var0);
   }

   public static LongIterator wrap(ByteIterator var0) {
      return new LongIterators.ByteIteratorWrapper(var0);
   }

   public static LongIterator wrap(ShortIterator var0) {
      return new LongIterators.ShortIteratorWrapper(var0);
   }

   public static LongIterator wrap(IntIterator var0) {
      return new LongIterators.IntIteratorWrapper(var0);
   }

   protected static class IntIteratorWrapper implements LongIterator {
      final IntIterator iterator;

      public IntIteratorWrapper(IntIterator var1) {
         super();
         this.iterator = var1;
      }

      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      /** @deprecated */
      @Deprecated
      public Long next() {
         return (long)this.iterator.nextInt();
      }

      public long nextLong() {
         return (long)this.iterator.nextInt();
      }

      public void remove() {
         this.iterator.remove();
      }

      public int skip(int var1) {
         return this.iterator.skip(var1);
      }
   }

   protected static class ShortIteratorWrapper implements LongIterator {
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
      public Long next() {
         return (long)this.iterator.nextShort();
      }

      public long nextLong() {
         return (long)this.iterator.nextShort();
      }

      public void remove() {
         this.iterator.remove();
      }

      public int skip(int var1) {
         return this.iterator.skip(var1);
      }
   }

   protected static class ByteIteratorWrapper implements LongIterator {
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
      public Long next() {
         return (long)this.iterator.nextByte();
      }

      public long nextLong() {
         return (long)this.iterator.nextByte();
      }

      public void remove() {
         this.iterator.remove();
      }

      public int skip(int var1) {
         return this.iterator.skip(var1);
      }
   }

   public static class UnmodifiableListIterator implements LongListIterator {
      protected final LongListIterator i;

      public UnmodifiableListIterator(LongListIterator var1) {
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

      public int nextIndex() {
         return this.i.nextIndex();
      }

      public int previousIndex() {
         return this.i.previousIndex();
      }
   }

   public static class UnmodifiableBidirectionalIterator implements LongBidirectionalIterator {
      protected final LongBidirectionalIterator i;

      public UnmodifiableBidirectionalIterator(LongBidirectionalIterator var1) {
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
   }

   public static class UnmodifiableIterator implements LongIterator {
      protected final LongIterator i;

      public UnmodifiableIterator(LongIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public long nextLong() {
         return this.i.nextLong();
      }
   }

   private static class IteratorConcatenator implements LongIterator {
      final LongIterator[] a;
      int offset;
      int length;
      int lastOffset = -1;

      public IteratorConcatenator(LongIterator[] var1, int var2, int var3) {
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

      public long nextLong() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            long var1 = this.a[this.lastOffset = this.offset].nextLong();
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

   private static class IntervalIterator implements LongBidirectionalIterator {
      private final long from;
      private final long to;
      long curr;

      public IntervalIterator(long var1, long var3) {
         super();
         this.from = this.curr = var1;
         this.to = var3;
      }

      public boolean hasNext() {
         return this.curr < this.to;
      }

      public boolean hasPrevious() {
         return this.curr > this.from;
      }

      public long nextLong() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return (long)(this.curr++);
         }
      }

      public long previousLong() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return --this.curr;
         }
      }

      public int skip(int var1) {
         if (this.curr + (long)var1 <= this.to) {
            this.curr += (long)var1;
            return var1;
         } else {
            var1 = (int)(this.to - this.curr);
            this.curr = this.to;
            return var1;
         }
      }

      public int back(int var1) {
         if (this.curr - (long)var1 >= this.from) {
            this.curr -= (long)var1;
            return var1;
         } else {
            var1 = (int)(this.curr - this.from);
            this.curr = this.from;
            return var1;
         }
      }
   }

   private static class ListIteratorWrapper implements LongListIterator {
      final ListIterator<Long> i;

      public ListIteratorWrapper(ListIterator<Long> var1) {
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

      public void set(long var1) {
         this.i.set(var1);
      }

      public void add(long var1) {
         this.i.add(var1);
      }

      public void remove() {
         this.i.remove();
      }

      public long nextLong() {
         return (Long)this.i.next();
      }

      public long previousLong() {
         return (Long)this.i.previous();
      }
   }

   private static class IteratorWrapper implements LongIterator {
      final Iterator<Long> i;

      public IteratorWrapper(Iterator<Long> var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public void remove() {
         this.i.remove();
      }

      public long nextLong() {
         return (Long)this.i.next();
      }
   }

   private static class ArrayIterator implements LongListIterator {
      private final long[] array;
      private final int offset;
      private final int length;
      private int curr;

      public ArrayIterator(long[] var1, int var2, int var3) {
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

      public long nextLong() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + this.curr++];
         }
      }

      public long previousLong() {
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

   private static class SingletonIterator implements LongListIterator {
      private final long element;
      private int curr;

      public SingletonIterator(long var1) {
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

      public int nextIndex() {
         return this.curr;
      }

      public int previousIndex() {
         return this.curr - 1;
      }
   }

   public static class EmptyIterator implements LongListIterator, Serializable, Cloneable {
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

      public long nextLong() {
         throw new NoSuchElementException();
      }

      public long previousLong() {
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
         return LongIterators.EMPTY_ITERATOR;
      }

      private Object readResolve() {
         return LongIterators.EMPTY_ITERATOR;
      }
   }
}
