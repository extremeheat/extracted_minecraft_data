package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public class SortedArraySet<T> extends AbstractSet<T> {
   private static final int DEFAULT_INITIAL_CAPACITY = 10;
   private final Comparator<T> comparator;
   private T[] contents;
   private int size;

   private SortedArraySet(final int initialCapacity, final Comparator<T> comparator) {
      super();
      this.comparator = comparator;
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Initial capacity (" + initialCapacity + ") is negative");
      } else {
         this.contents = (T[])castRawArray(new Object[initialCapacity]);
      }
   }

   public static <T extends Comparable<T>> SortedArraySet<T> create() {
      return create(10);
   }

   public static <T extends Comparable<T>> SortedArraySet<T> create(final int initialCapacity) {
      return new SortedArraySet<T>(initialCapacity, Comparator.naturalOrder());
   }

   public static <T> SortedArraySet<T> create(final Comparator<T> comparator) {
      return create(comparator, 10);
   }

   public static <T> SortedArraySet<T> create(final Comparator<T> comparator, final int initialCapacity) {
      return new SortedArraySet<T>(initialCapacity, comparator);
   }

   private static <T> T[] castRawArray(final Object[] array) {
      return (T[])array;
   }

   private int findIndex(final T t) {
      return Arrays.binarySearch(this.contents, 0, this.size, t, this.comparator);
   }

   private static int getInsertionPosition(final int position) {
      return -position - 1;
   }

   public boolean add(final T t) {
      int position = this.findIndex(t);
      if (position >= 0) {
         return false;
      } else {
         int pos = getInsertionPosition(position);
         this.addInternal(t, pos);
         return true;
      }
   }

   private void grow(int capacity) {
      if (capacity > this.contents.length) {
         if (this.contents != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
            capacity = Util.growByHalf(this.contents.length, capacity);
         } else if (capacity < 10) {
            capacity = 10;
         }

         Object[] t = new Object[capacity];
         System.arraycopy(this.contents, 0, t, 0, this.size);
         this.contents = (T[])castRawArray(t);
      }
   }

   private void addInternal(final T t, final int pos) {
      this.grow(this.size + 1);
      if (pos != this.size) {
         System.arraycopy(this.contents, pos, this.contents, pos + 1, this.size - pos);
      }

      this.contents[pos] = t;
      ++this.size;
   }

   private void removeInternal(final int position) {
      --this.size;
      if (position != this.size) {
         System.arraycopy(this.contents, position + 1, this.contents, position, this.size - position);
      }

      this.contents[this.size] = null;
   }

   private T getInternal(final int position) {
      return (T)this.contents[position];
   }

   public T addOrGet(final T t) {
      int position = this.findIndex(t);
      if (position >= 0) {
         return (T)this.getInternal(position);
      } else {
         this.addInternal(t, getInsertionPosition(position));
         return t;
      }
   }

   public boolean remove(final Object o) {
      int position = this.findIndex(o);
      if (position >= 0) {
         this.removeInternal(position);
         return true;
      } else {
         return false;
      }
   }

   public @Nullable T get(final T t) {
      int position = this.findIndex(t);
      return (T)(position >= 0 ? this.getInternal(position) : null);
   }

   public T first() {
      return (T)this.getInternal(0);
   }

   public T last() {
      return (T)this.getInternal(this.size - 1);
   }

   public boolean contains(final Object o) {
      int result = this.findIndex(o);
      return result >= 0;
   }

   public Iterator<T> iterator() {
      return new ArrayIterator();
   }

   public int size() {
      return this.size;
   }

   public Object[] toArray() {
      return Arrays.copyOf(this.contents, this.size, Object[].class);
   }

   public <U> U[] toArray(final U[] a) {
      if (a.length < this.size) {
         return (U[])Arrays.copyOf(this.contents, this.size, a.getClass());
      } else {
         System.arraycopy(this.contents, 0, a, 0, this.size);
         if (a.length > this.size) {
            a[this.size] = null;
         }

         return a;
      }
   }

   public void clear() {
      Arrays.fill(this.contents, 0, this.size, (Object)null);
      this.size = 0;
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else {
         if (o instanceof SortedArraySet) {
            SortedArraySet<?> that = (SortedArraySet)o;
            if (this.comparator.equals(that.comparator)) {
               return this.size == that.size && Arrays.equals(this.contents, that.contents);
            }
         }

         return super.equals(o);
      }
   }

   private class ArrayIterator implements Iterator<T> {
      private int index;
      private int last;

      private ArrayIterator() {
         Objects.requireNonNull(SortedArraySet.this);
         super();
         this.last = -1;
      }

      public boolean hasNext() {
         return this.index < SortedArraySet.this.size;
      }

      public T next() {
         if (this.index >= SortedArraySet.this.size) {
            throw new NoSuchElementException();
         } else {
            this.last = this.index++;
            return (T)SortedArraySet.this.contents[this.last];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            SortedArraySet.this.removeInternal(this.last);
            --this.index;
            this.last = -1;
         }
      }
   }
}
