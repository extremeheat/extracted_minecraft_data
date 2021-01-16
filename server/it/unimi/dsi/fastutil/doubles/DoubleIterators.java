package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.DoublePredicate;

public final class DoubleIterators {
   public static final DoubleIterators.EmptyIterator EMPTY_ITERATOR = new DoubleIterators.EmptyIterator();

   private DoubleIterators() {
      super();
   }

   public static DoubleListIterator singleton(double var0) {
      return new DoubleIterators.SingletonIterator(var0);
   }

   public static DoubleListIterator wrap(double[] var0, int var1, int var2) {
      DoubleArrays.ensureOffsetLength(var0, var1, var2);
      return new DoubleIterators.ArrayIterator(var0, var1, var2);
   }

   public static DoubleListIterator wrap(double[] var0) {
      return new DoubleIterators.ArrayIterator(var0, 0, var0.length);
   }

   public static int unwrap(DoubleIterator var0, double[] var1, int var2, int var3) {
      if (var3 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var3 + ") is negative");
      } else if (var2 >= 0 && var2 + var3 <= var1.length) {
         int var4;
         for(var4 = var3; var4-- != 0 && var0.hasNext(); var1[var2++] = var0.nextDouble()) {
         }

         return var3 - var4 - 1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int unwrap(DoubleIterator var0, double[] var1) {
      return unwrap(var0, var1, 0, var1.length);
   }

   public static double[] unwrap(DoubleIterator var0, int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var1 + ") is negative");
      } else {
         double[] var2 = new double[16];

         int var3;
         for(var3 = 0; var1-- != 0 && var0.hasNext(); var2[var3++] = var0.nextDouble()) {
            if (var3 == var2.length) {
               var2 = DoubleArrays.grow(var2, var3 + 1);
            }
         }

         return DoubleArrays.trim(var2, var3);
      }
   }

   public static double[] unwrap(DoubleIterator var0) {
      return unwrap(var0, 2147483647);
   }

   public static int unwrap(DoubleIterator var0, DoubleCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextDouble());
         }

         return var2 - var3 - 1;
      }
   }

   public static long unwrap(DoubleIterator var0, DoubleCollection var1) {
      long var2;
      for(var2 = 0L; var0.hasNext(); ++var2) {
         var1.add(var0.nextDouble());
      }

      return var2;
   }

   public static int pour(DoubleIterator var0, DoubleCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextDouble());
         }

         return var2 - var3 - 1;
      }
   }

   public static int pour(DoubleIterator var0, DoubleCollection var1) {
      return pour(var0, var1, 2147483647);
   }

   public static DoubleList pour(DoubleIterator var0, int var1) {
      DoubleArrayList var2 = new DoubleArrayList();
      pour(var0, var2, var1);
      var2.trim();
      return var2;
   }

   public static DoubleList pour(DoubleIterator var0) {
      return pour(var0, 2147483647);
   }

   public static DoubleIterator asDoubleIterator(Iterator var0) {
      return (DoubleIterator)(var0 instanceof DoubleIterator ? (DoubleIterator)var0 : new DoubleIterators.IteratorWrapper(var0));
   }

   public static DoubleListIterator asDoubleIterator(ListIterator var0) {
      return (DoubleListIterator)(var0 instanceof DoubleListIterator ? (DoubleListIterator)var0 : new DoubleIterators.ListIteratorWrapper(var0));
   }

   public static boolean any(DoubleIterator var0, DoublePredicate var1) {
      return indexOf(var0, var1) != -1;
   }

   public static boolean all(DoubleIterator var0, DoublePredicate var1) {
      Objects.requireNonNull(var1);

      while(var0.hasNext()) {
         if (!var1.test(var0.nextDouble())) {
            return false;
         }
      }

      return true;
   }

   public static int indexOf(DoubleIterator var0, DoublePredicate var1) {
      Objects.requireNonNull(var1);

      for(int var2 = 0; var0.hasNext(); ++var2) {
         if (var1.test(var0.nextDouble())) {
            return var2;
         }
      }

      return -1;
   }

   public static DoubleIterator concat(DoubleIterator[] var0) {
      return concat(var0, 0, var0.length);
   }

   public static DoubleIterator concat(DoubleIterator[] var0, int var1, int var2) {
      return new DoubleIterators.IteratorConcatenator(var0, var1, var2);
   }

   public static DoubleIterator unmodifiable(DoubleIterator var0) {
      return new DoubleIterators.UnmodifiableIterator(var0);
   }

   public static DoubleBidirectionalIterator unmodifiable(DoubleBidirectionalIterator var0) {
      return new DoubleIterators.UnmodifiableBidirectionalIterator(var0);
   }

   public static DoubleListIterator unmodifiable(DoubleListIterator var0) {
      return new DoubleIterators.UnmodifiableListIterator(var0);
   }

   public static DoubleIterator wrap(ByteIterator var0) {
      return new DoubleIterators.ByteIteratorWrapper(var0);
   }

   public static DoubleIterator wrap(ShortIterator var0) {
      return new DoubleIterators.ShortIteratorWrapper(var0);
   }

   public static DoubleIterator wrap(IntIterator var0) {
      return new DoubleIterators.IntIteratorWrapper(var0);
   }

   public static DoubleIterator wrap(FloatIterator var0) {
      return new DoubleIterators.FloatIteratorWrapper(var0);
   }

   protected static class FloatIteratorWrapper implements DoubleIterator {
      final FloatIterator iterator;

      public FloatIteratorWrapper(FloatIterator var1) {
         super();
         this.iterator = var1;
      }

      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      /** @deprecated */
      @Deprecated
      public Double next() {
         return (double)this.iterator.nextFloat();
      }

      public double nextDouble() {
         return (double)this.iterator.nextFloat();
      }

      public void remove() {
         this.iterator.remove();
      }

      public int skip(int var1) {
         return this.iterator.skip(var1);
      }
   }

   protected static class IntIteratorWrapper implements DoubleIterator {
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
      public Double next() {
         return (double)this.iterator.nextInt();
      }

      public double nextDouble() {
         return (double)this.iterator.nextInt();
      }

      public void remove() {
         this.iterator.remove();
      }

      public int skip(int var1) {
         return this.iterator.skip(var1);
      }
   }

   protected static class ShortIteratorWrapper implements DoubleIterator {
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
      public Double next() {
         return (double)this.iterator.nextShort();
      }

      public double nextDouble() {
         return (double)this.iterator.nextShort();
      }

      public void remove() {
         this.iterator.remove();
      }

      public int skip(int var1) {
         return this.iterator.skip(var1);
      }
   }

   protected static class ByteIteratorWrapper implements DoubleIterator {
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
      public Double next() {
         return (double)this.iterator.nextByte();
      }

      public double nextDouble() {
         return (double)this.iterator.nextByte();
      }

      public void remove() {
         this.iterator.remove();
      }

      public int skip(int var1) {
         return this.iterator.skip(var1);
      }
   }

   public static class UnmodifiableListIterator implements DoubleListIterator {
      protected final DoubleListIterator i;

      public UnmodifiableListIterator(DoubleListIterator var1) {
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

      public int nextIndex() {
         return this.i.nextIndex();
      }

      public int previousIndex() {
         return this.i.previousIndex();
      }
   }

   public static class UnmodifiableBidirectionalIterator implements DoubleBidirectionalIterator {
      protected final DoubleBidirectionalIterator i;

      public UnmodifiableBidirectionalIterator(DoubleBidirectionalIterator var1) {
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
   }

   public static class UnmodifiableIterator implements DoubleIterator {
      protected final DoubleIterator i;

      public UnmodifiableIterator(DoubleIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public double nextDouble() {
         return this.i.nextDouble();
      }
   }

   private static class IteratorConcatenator implements DoubleIterator {
      final DoubleIterator[] a;
      int offset;
      int length;
      int lastOffset = -1;

      public IteratorConcatenator(DoubleIterator[] var1, int var2, int var3) {
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

      public double nextDouble() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            double var1 = this.a[this.lastOffset = this.offset].nextDouble();
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

   private static class ListIteratorWrapper implements DoubleListIterator {
      final ListIterator<Double> i;

      public ListIteratorWrapper(ListIterator<Double> var1) {
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

      public void set(double var1) {
         this.i.set(var1);
      }

      public void add(double var1) {
         this.i.add(var1);
      }

      public void remove() {
         this.i.remove();
      }

      public double nextDouble() {
         return (Double)this.i.next();
      }

      public double previousDouble() {
         return (Double)this.i.previous();
      }
   }

   private static class IteratorWrapper implements DoubleIterator {
      final Iterator<Double> i;

      public IteratorWrapper(Iterator<Double> var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public void remove() {
         this.i.remove();
      }

      public double nextDouble() {
         return (Double)this.i.next();
      }
   }

   private static class ArrayIterator implements DoubleListIterator {
      private final double[] array;
      private final int offset;
      private final int length;
      private int curr;

      public ArrayIterator(double[] var1, int var2, int var3) {
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

      public double nextDouble() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + this.curr++];
         }
      }

      public double previousDouble() {
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

   private static class SingletonIterator implements DoubleListIterator {
      private final double element;
      private int curr;

      public SingletonIterator(double var1) {
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

      public int nextIndex() {
         return this.curr;
      }

      public int previousIndex() {
         return this.curr - 1;
      }
   }

   public static class EmptyIterator implements DoubleListIterator, Serializable, Cloneable {
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

      public double nextDouble() {
         throw new NoSuchElementException();
      }

      public double previousDouble() {
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
         return DoubleIterators.EMPTY_ITERATOR;
      }

      private Object readResolve() {
         return DoubleIterators.EMPTY_ITERATOR;
      }
   }
}
