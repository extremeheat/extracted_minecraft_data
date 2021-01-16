package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.DoublePredicate;

public final class FloatIterators {
   public static final FloatIterators.EmptyIterator EMPTY_ITERATOR = new FloatIterators.EmptyIterator();

   private FloatIterators() {
      super();
   }

   public static FloatListIterator singleton(float var0) {
      return new FloatIterators.SingletonIterator(var0);
   }

   public static FloatListIterator wrap(float[] var0, int var1, int var2) {
      FloatArrays.ensureOffsetLength(var0, var1, var2);
      return new FloatIterators.ArrayIterator(var0, var1, var2);
   }

   public static FloatListIterator wrap(float[] var0) {
      return new FloatIterators.ArrayIterator(var0, 0, var0.length);
   }

   public static int unwrap(FloatIterator var0, float[] var1, int var2, int var3) {
      if (var3 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var3 + ") is negative");
      } else if (var2 >= 0 && var2 + var3 <= var1.length) {
         int var4;
         for(var4 = var3; var4-- != 0 && var0.hasNext(); var1[var2++] = var0.nextFloat()) {
         }

         return var3 - var4 - 1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int unwrap(FloatIterator var0, float[] var1) {
      return unwrap(var0, var1, 0, var1.length);
   }

   public static float[] unwrap(FloatIterator var0, int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var1 + ") is negative");
      } else {
         float[] var2 = new float[16];

         int var3;
         for(var3 = 0; var1-- != 0 && var0.hasNext(); var2[var3++] = var0.nextFloat()) {
            if (var3 == var2.length) {
               var2 = FloatArrays.grow(var2, var3 + 1);
            }
         }

         return FloatArrays.trim(var2, var3);
      }
   }

   public static float[] unwrap(FloatIterator var0) {
      return unwrap(var0, 2147483647);
   }

   public static int unwrap(FloatIterator var0, FloatCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextFloat());
         }

         return var2 - var3 - 1;
      }
   }

   public static long unwrap(FloatIterator var0, FloatCollection var1) {
      long var2;
      for(var2 = 0L; var0.hasNext(); ++var2) {
         var1.add(var0.nextFloat());
      }

      return var2;
   }

   public static int pour(FloatIterator var0, FloatCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextFloat());
         }

         return var2 - var3 - 1;
      }
   }

   public static int pour(FloatIterator var0, FloatCollection var1) {
      return pour(var0, var1, 2147483647);
   }

   public static FloatList pour(FloatIterator var0, int var1) {
      FloatArrayList var2 = new FloatArrayList();
      pour(var0, var2, var1);
      var2.trim();
      return var2;
   }

   public static FloatList pour(FloatIterator var0) {
      return pour(var0, 2147483647);
   }

   public static FloatIterator asFloatIterator(Iterator var0) {
      return (FloatIterator)(var0 instanceof FloatIterator ? (FloatIterator)var0 : new FloatIterators.IteratorWrapper(var0));
   }

   public static FloatListIterator asFloatIterator(ListIterator var0) {
      return (FloatListIterator)(var0 instanceof FloatListIterator ? (FloatListIterator)var0 : new FloatIterators.ListIteratorWrapper(var0));
   }

   public static boolean any(FloatIterator var0, DoublePredicate var1) {
      return indexOf(var0, var1) != -1;
   }

   public static boolean all(FloatIterator var0, DoublePredicate var1) {
      Objects.requireNonNull(var1);

      while(var0.hasNext()) {
         if (!var1.test((double)var0.nextFloat())) {
            return false;
         }
      }

      return true;
   }

   public static int indexOf(FloatIterator var0, DoublePredicate var1) {
      Objects.requireNonNull(var1);

      for(int var2 = 0; var0.hasNext(); ++var2) {
         if (var1.test((double)var0.nextFloat())) {
            return var2;
         }
      }

      return -1;
   }

   public static FloatIterator concat(FloatIterator[] var0) {
      return concat(var0, 0, var0.length);
   }

   public static FloatIterator concat(FloatIterator[] var0, int var1, int var2) {
      return new FloatIterators.IteratorConcatenator(var0, var1, var2);
   }

   public static FloatIterator unmodifiable(FloatIterator var0) {
      return new FloatIterators.UnmodifiableIterator(var0);
   }

   public static FloatBidirectionalIterator unmodifiable(FloatBidirectionalIterator var0) {
      return new FloatIterators.UnmodifiableBidirectionalIterator(var0);
   }

   public static FloatListIterator unmodifiable(FloatListIterator var0) {
      return new FloatIterators.UnmodifiableListIterator(var0);
   }

   public static FloatIterator wrap(ByteIterator var0) {
      return new FloatIterators.ByteIteratorWrapper(var0);
   }

   public static FloatIterator wrap(ShortIterator var0) {
      return new FloatIterators.ShortIteratorWrapper(var0);
   }

   protected static class ShortIteratorWrapper implements FloatIterator {
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
      public Float next() {
         return (float)this.iterator.nextShort();
      }

      public float nextFloat() {
         return (float)this.iterator.nextShort();
      }

      public void remove() {
         this.iterator.remove();
      }

      public int skip(int var1) {
         return this.iterator.skip(var1);
      }
   }

   protected static class ByteIteratorWrapper implements FloatIterator {
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
      public Float next() {
         return (float)this.iterator.nextByte();
      }

      public float nextFloat() {
         return (float)this.iterator.nextByte();
      }

      public void remove() {
         this.iterator.remove();
      }

      public int skip(int var1) {
         return this.iterator.skip(var1);
      }
   }

   public static class UnmodifiableListIterator implements FloatListIterator {
      protected final FloatListIterator i;

      public UnmodifiableListIterator(FloatListIterator var1) {
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

      public int nextIndex() {
         return this.i.nextIndex();
      }

      public int previousIndex() {
         return this.i.previousIndex();
      }
   }

   public static class UnmodifiableBidirectionalIterator implements FloatBidirectionalIterator {
      protected final FloatBidirectionalIterator i;

      public UnmodifiableBidirectionalIterator(FloatBidirectionalIterator var1) {
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
   }

   public static class UnmodifiableIterator implements FloatIterator {
      protected final FloatIterator i;

      public UnmodifiableIterator(FloatIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public float nextFloat() {
         return this.i.nextFloat();
      }
   }

   private static class IteratorConcatenator implements FloatIterator {
      final FloatIterator[] a;
      int offset;
      int length;
      int lastOffset = -1;

      public IteratorConcatenator(FloatIterator[] var1, int var2, int var3) {
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

      public float nextFloat() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            float var1 = this.a[this.lastOffset = this.offset].nextFloat();
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

   private static class ListIteratorWrapper implements FloatListIterator {
      final ListIterator<Float> i;

      public ListIteratorWrapper(ListIterator<Float> var1) {
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

      public void set(float var1) {
         this.i.set(var1);
      }

      public void add(float var1) {
         this.i.add(var1);
      }

      public void remove() {
         this.i.remove();
      }

      public float nextFloat() {
         return (Float)this.i.next();
      }

      public float previousFloat() {
         return (Float)this.i.previous();
      }
   }

   private static class IteratorWrapper implements FloatIterator {
      final Iterator<Float> i;

      public IteratorWrapper(Iterator<Float> var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public void remove() {
         this.i.remove();
      }

      public float nextFloat() {
         return (Float)this.i.next();
      }
   }

   private static class ArrayIterator implements FloatListIterator {
      private final float[] array;
      private final int offset;
      private final int length;
      private int curr;

      public ArrayIterator(float[] var1, int var2, int var3) {
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

      public float nextFloat() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + this.curr++];
         }
      }

      public float previousFloat() {
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

   private static class SingletonIterator implements FloatListIterator {
      private final float element;
      private int curr;

      public SingletonIterator(float var1) {
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

      public int nextIndex() {
         return this.curr;
      }

      public int previousIndex() {
         return this.curr - 1;
      }
   }

   public static class EmptyIterator implements FloatListIterator, Serializable, Cloneable {
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

      public float nextFloat() {
         throw new NoSuchElementException();
      }

      public float previousFloat() {
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
         return FloatIterators.EMPTY_ITERATOR;
      }

      private Object readResolve() {
         return FloatIterators.EMPTY_ITERATOR;
      }
   }
}
