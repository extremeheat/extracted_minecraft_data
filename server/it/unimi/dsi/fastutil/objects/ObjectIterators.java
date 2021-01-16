package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

public final class ObjectIterators {
   public static final ObjectIterators.EmptyIterator EMPTY_ITERATOR = new ObjectIterators.EmptyIterator();

   private ObjectIterators() {
      super();
   }

   public static <K> ObjectIterator<K> emptyIterator() {
      return EMPTY_ITERATOR;
   }

   public static <K> ObjectListIterator<K> singleton(K var0) {
      return new ObjectIterators.SingletonIterator(var0);
   }

   public static <K> ObjectListIterator<K> wrap(K[] var0, int var1, int var2) {
      ObjectArrays.ensureOffsetLength(var0, var1, var2);
      return new ObjectIterators.ArrayIterator(var0, var1, var2);
   }

   public static <K> ObjectListIterator<K> wrap(K[] var0) {
      return new ObjectIterators.ArrayIterator(var0, 0, var0.length);
   }

   public static <K> int unwrap(Iterator<? extends K> var0, K[] var1, int var2, int var3) {
      if (var3 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var3 + ") is negative");
      } else if (var2 >= 0 && var2 + var3 <= var1.length) {
         int var4;
         for(var4 = var3; var4-- != 0 && var0.hasNext(); var1[var2++] = var0.next()) {
         }

         return var3 - var4 - 1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static <K> int unwrap(Iterator<? extends K> var0, K[] var1) {
      return unwrap(var0, var1, 0, var1.length);
   }

   public static <K> K[] unwrap(Iterator<? extends K> var0, int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var1 + ") is negative");
      } else {
         Object[] var2 = new Object[16];

         int var3;
         for(var3 = 0; var1-- != 0 && var0.hasNext(); var2[var3++] = var0.next()) {
            if (var3 == var2.length) {
               var2 = ObjectArrays.grow(var2, var3 + 1);
            }
         }

         return ObjectArrays.trim(var2, var3);
      }
   }

   public static <K> K[] unwrap(Iterator<? extends K> var0) {
      return unwrap(var0, 2147483647);
   }

   public static <K> int unwrap(Iterator<K> var0, ObjectCollection<? super K> var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.next());
         }

         return var2 - var3 - 1;
      }
   }

   public static <K> long unwrap(Iterator<K> var0, ObjectCollection<? super K> var1) {
      long var2;
      for(var2 = 0L; var0.hasNext(); ++var2) {
         var1.add(var0.next());
      }

      return var2;
   }

   public static <K> int pour(Iterator<K> var0, ObjectCollection<? super K> var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + var2 + ") is negative");
      } else {
         int var3 = var2;

         while(var3-- != 0 && var0.hasNext()) {
            var1.add(var0.next());
         }

         return var2 - var3 - 1;
      }
   }

   public static <K> int pour(Iterator<K> var0, ObjectCollection<? super K> var1) {
      return pour(var0, var1, 2147483647);
   }

   public static <K> ObjectList<K> pour(Iterator<K> var0, int var1) {
      ObjectArrayList var2 = new ObjectArrayList();
      pour(var0, var2, var1);
      var2.trim();
      return var2;
   }

   public static <K> ObjectList<K> pour(Iterator<K> var0) {
      return pour(var0, 2147483647);
   }

   public static <K> ObjectIterator<K> asObjectIterator(Iterator<K> var0) {
      return (ObjectIterator)(var0 instanceof ObjectIterator ? (ObjectIterator)var0 : new ObjectIterators.IteratorWrapper(var0));
   }

   public static <K> ObjectListIterator<K> asObjectIterator(ListIterator<K> var0) {
      return (ObjectListIterator)(var0 instanceof ObjectListIterator ? (ObjectListIterator)var0 : new ObjectIterators.ListIteratorWrapper(var0));
   }

   public static <K> boolean any(ObjectIterator<K> var0, Predicate<? super K> var1) {
      return indexOf(var0, var1) != -1;
   }

   public static <K> boolean all(ObjectIterator<K> var0, Predicate<? super K> var1) {
      Objects.requireNonNull(var1);

      while(var0.hasNext()) {
         if (!var1.test(var0.next())) {
            return false;
         }
      }

      return true;
   }

   public static <K> int indexOf(ObjectIterator<K> var0, Predicate<? super K> var1) {
      Objects.requireNonNull(var1);

      for(int var2 = 0; var0.hasNext(); ++var2) {
         if (var1.test(var0.next())) {
            return var2;
         }
      }

      return -1;
   }

   public static <K> ObjectIterator<K> concat(ObjectIterator<? extends K>[] var0) {
      return concat(var0, 0, var0.length);
   }

   public static <K> ObjectIterator<K> concat(ObjectIterator<? extends K>[] var0, int var1, int var2) {
      return new ObjectIterators.IteratorConcatenator(var0, var1, var2);
   }

   public static <K> ObjectIterator<K> unmodifiable(ObjectIterator<K> var0) {
      return new ObjectIterators.UnmodifiableIterator(var0);
   }

   public static <K> ObjectBidirectionalIterator<K> unmodifiable(ObjectBidirectionalIterator<K> var0) {
      return new ObjectIterators.UnmodifiableBidirectionalIterator(var0);
   }

   public static <K> ObjectListIterator<K> unmodifiable(ObjectListIterator<K> var0) {
      return new ObjectIterators.UnmodifiableListIterator(var0);
   }

   public static class UnmodifiableListIterator<K> implements ObjectListIterator<K> {
      protected final ObjectListIterator<K> i;

      public UnmodifiableListIterator(ObjectListIterator<K> var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public K next() {
         return this.i.next();
      }

      public K previous() {
         return this.i.previous();
      }

      public int nextIndex() {
         return this.i.nextIndex();
      }

      public int previousIndex() {
         return this.i.previousIndex();
      }
   }

   public static class UnmodifiableBidirectionalIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<K> i;

      public UnmodifiableBidirectionalIterator(ObjectBidirectionalIterator<K> var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public K next() {
         return this.i.next();
      }

      public K previous() {
         return this.i.previous();
      }
   }

   public static class UnmodifiableIterator<K> implements ObjectIterator<K> {
      protected final ObjectIterator<K> i;

      public UnmodifiableIterator(ObjectIterator<K> var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public K next() {
         return this.i.next();
      }
   }

   private static class IteratorConcatenator<K> implements ObjectIterator<K> {
      final ObjectIterator<? extends K>[] a;
      int offset;
      int length;
      int lastOffset = -1;

      public IteratorConcatenator(ObjectIterator<? extends K>[] var1, int var2, int var3) {
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

      public K next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            Object var1 = this.a[this.lastOffset = this.offset].next();
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

   private static class ListIteratorWrapper<K> implements ObjectListIterator<K> {
      final ListIterator<K> i;

      public ListIteratorWrapper(ListIterator<K> var1) {
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

      public void set(K var1) {
         this.i.set(var1);
      }

      public void add(K var1) {
         this.i.add(var1);
      }

      public void remove() {
         this.i.remove();
      }

      public K next() {
         return this.i.next();
      }

      public K previous() {
         return this.i.previous();
      }
   }

   private static class IteratorWrapper<K> implements ObjectIterator<K> {
      final Iterator<K> i;

      public IteratorWrapper(Iterator<K> var1) {
         super();
         this.i = var1;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public void remove() {
         this.i.remove();
      }

      public K next() {
         return this.i.next();
      }
   }

   private static class ArrayIterator<K> implements ObjectListIterator<K> {
      private final K[] array;
      private final int offset;
      private final int length;
      private int curr;

      public ArrayIterator(K[] var1, int var2, int var3) {
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

      public K next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + this.curr++];
         }
      }

      public K previous() {
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

   private static class SingletonIterator<K> implements ObjectListIterator<K> {
      private final K element;
      private int curr;

      public SingletonIterator(K var1) {
         super();
         this.element = var1;
      }

      public boolean hasNext() {
         return this.curr == 0;
      }

      public boolean hasPrevious() {
         return this.curr == 1;
      }

      public K next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      public K previous() {
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

   public static class EmptyIterator<K> implements ObjectListIterator<K>, Serializable, Cloneable {
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

      public K next() {
         throw new NoSuchElementException();
      }

      public K previous() {
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
         return ObjectIterators.EMPTY_ITERATOR;
      }

      private Object readResolve() {
         return ObjectIterators.EMPTY_ITERATOR;
      }
   }
}
