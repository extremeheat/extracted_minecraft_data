package it.unimi.dsi.fastutil.booleans;

import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

public final class BooleanIterators {
   public static final BooleanIterators.EmptyIterator EMPTY_ITERATOR = new BooleanIterators.EmptyIterator();

   private BooleanIterators() {
      super();
   }

   public static BooleanListIterator singleton(boolean var0) {
      return new BooleanIterators.SingletonIterator(var0);
   }

   public static BooleanListIterator wrap(boolean[] var0, int var1, int var2) {
      BooleanArrays.ensureOffsetLength(var0, var1, var2);
      return new BooleanIterators.ArrayIterator(var0, var1, var2);
   }

   public static BooleanListIterator wrap(boolean[] var0) {
      return new BooleanIterators.ArrayIterator(var0, 0, var0.length);
   }

   public static int unwrap(BooleanIterator var0, boolean[] var1, int var2, int var3) {
      if (var3 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var3 + ") is negative");
      } else if (var2 >= 0 && var2 + var3 <= var1.length) {
         int var4;
         for(var4 = var3; var4-- != 0 && var0.hasNext(); var1[var2++] = var0.nextBoolean()) {
         }

         return var3 - var4 - 1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int unwrap(BooleanIterator var0, boolean[] var1) {
      return unwrap(var0, var1, 0, var1.length);
   }

   public static boolean[] unwrap(BooleanIterator var0, int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var1 + ") is negative");
      } else {
         boolean[] var2 = new boolean[16];

         int var3;
         for(var3 = 0; var1-- != 0 && var0.hasNext(); var2[var3++] = var0.nextBoolean()) {
            if (var3 == var2.length) {
               var2 = BooleanArrays.grow(var2, var3 + 1);
            }
         }

         return BooleanArrays.trim(var2, var3);
      }
   }

   public static boolean[] unwrap(BooleanIterator var0) {
      return unwrap(var0, 2147483647);
   }

   public static int unwrap(BooleanIterator var0, BooleanCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextBoolean());
         }

         return var2 - var3 - 1;
      }
   }

   public static long unwrap(BooleanIterator var0, BooleanCollection var1) {
      long var2;
      for(var2 = 0L; var0.hasNext(); ++var2) {
         var1.add(var0.nextBoolean());
      }

      return var2;
   }

   public static int pour(BooleanIterator var0, BooleanCollection var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.nextBoolean());
         }

         return var2 - var3 - 1;
      }
   }

   public static int pour(BooleanIterator var0, BooleanCollection var1) {
      return pour(var0, var1, 2147483647);
   }

   public static BooleanList pour(BooleanIterator var0, int var1) {
      BooleanArrayList var2 = new BooleanArrayList();
      pour(var0, var2, var1);
      var2.trim();
      return var2;
   }

   public static BooleanList pour(BooleanIterator var0) {
      return pour(var0, 2147483647);
   }

   public static BooleanIterator asBooleanIterator(Iterator var0) {
      return (BooleanIterator)(var0 instanceof BooleanIterator ? (BooleanIterator)var0 : new BooleanIterators.IteratorWrapper(var0));
   }

   public static BooleanListIterator asBooleanIterator(ListIterator var0) {
      return (BooleanListIterator)(var0 instanceof BooleanListIterator ? (BooleanListIterator)var0 : new BooleanIterators.ListIteratorWrapper(var0));
   }

   public static boolean any(BooleanIterator var0, Predicate<? super Boolean> var1) {
      return indexOf(var0, var1) != -1;
   }

   public static boolean all(BooleanIterator var0, Predicate<? super Boolean> var1) {
      Objects.requireNonNull(var1);

      while(var0.hasNext()) {
         if (!var1.test(var0.nextBoolean())) {
            return false;
         }
      }

      return true;
   }

   public static int indexOf(BooleanIterator var0, Predicate<? super Boolean> var1) {
      Objects.requireNonNull(var1);

      for(int var2 = 0; var0.hasNext(); ++var2) {
         if (var1.test(var0.nextBoolean())) {
            return var2;
         }
      }

      return -1;
   }

   public static BooleanIterator concat(BooleanIterator[] var0) {
      return concat(var0, 0, var0.length);
   }

   public static BooleanIterator concat(BooleanIterator[] var0, int var1, int var2) {
      return new BooleanIterators.IteratorConcatenator(var0, var1, var2);
   }

   public static BooleanIterator unmodifiable(BooleanIterator var0) {
      return new BooleanIterators.UnmodifiableIterator(var0);
   }

   public static BooleanBidirectionalIterator unmodifiable(BooleanBidirectionalIterator var0) {
      return new BooleanIterators.UnmodifiableBidirectionalIterator(var0);
   }

   public static BooleanListIterator unmodifiable(BooleanListIterator var0) {
      return new BooleanIterators.UnmodifiableListIterator(var0);
   }

   public static class UnmodifiableListIterator implements BooleanListIterator {
      protected final BooleanListIterator i;

      public UnmodifiableListIterator(BooleanListIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public boolean nextBoolean() {
         return this.i.nextBoolean();
      }

      public boolean previousBoolean() {
         return this.i.previousBoolean();
      }

      public int nextIndex() {
         return this.i.nextIndex();
      }

      public int previousIndex() {
         return this.i.previousIndex();
      }
   }

   public static class UnmodifiableBidirectionalIterator implements BooleanBidirectionalIterator {
      protected final BooleanBidirectionalIterator i;

      public UnmodifiableBidirectionalIterator(BooleanBidirectionalIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public boolean nextBoolean() {
         return this.i.nextBoolean();
      }

      public boolean previousBoolean() {
         return this.i.previousBoolean();
      }
   }

   public static class UnmodifiableIterator implements BooleanIterator {
      protected final BooleanIterator i;

      public UnmodifiableIterator(BooleanIterator var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean nextBoolean() {
         return this.i.nextBoolean();
      }
   }

   private static class IteratorConcatenator implements BooleanIterator {
      final BooleanIterator[] a;
      int offset;
      int length;
      int lastOffset = -1;

      public IteratorConcatenator(BooleanIterator[] var1, int var2, int var3) {
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

      public boolean nextBoolean() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            boolean var1 = this.a[this.lastOffset = this.offset].nextBoolean();
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

   private static class ListIteratorWrapper implements BooleanListIterator {
      final ListIterator<Boolean> i;

      public ListIteratorWrapper(ListIterator<Boolean> var1) {
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

      public void set(boolean var1) {
         this.i.set(var1);
      }

      public void add(boolean var1) {
         this.i.add(var1);
      }

      public void remove() {
         this.i.remove();
      }

      public boolean nextBoolean() {
         return (Boolean)this.i.next();
      }

      public boolean previousBoolean() {
         return (Boolean)this.i.previous();
      }
   }

   private static class IteratorWrapper implements BooleanIterator {
      final Iterator<Boolean> i;

      public IteratorWrapper(Iterator<Boolean> var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public void remove() {
         this.i.remove();
      }

      public boolean nextBoolean() {
         return (Boolean)this.i.next();
      }
   }

   private static class ArrayIterator implements BooleanListIterator {
      private final boolean[] array;
      private final int offset;
      private final int length;
      private int curr;

      public ArrayIterator(boolean[] var1, int var2, int var3) {
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

      public boolean nextBoolean() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + this.curr++];
         }
      }

      public boolean previousBoolean() {
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

   private static class SingletonIterator implements BooleanListIterator {
      private final boolean element;
      private int curr;

      public SingletonIterator(boolean var1) {
         super();
         this.element = var1;
      }

      public boolean hasNext() {
         return this.curr == 0;
      }

      public boolean hasPrevious() {
         return this.curr == 1;
      }

      public boolean nextBoolean() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      public boolean previousBoolean() {
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

   public static class EmptyIterator implements BooleanListIterator, Serializable, Cloneable {
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

      public boolean nextBoolean() {
         throw new NoSuchElementException();
      }

      public boolean previousBoolean() {
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
         return BooleanIterators.EMPTY_ITERATOR;
      }

      private Object readResolve() {
         return BooleanIterators.EMPTY_ITERATOR;
      }
   }
}
