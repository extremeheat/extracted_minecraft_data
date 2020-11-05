package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SortedArraySet<T> extends AbstractSet<T> {
   private final Comparator<T> comparator;
   private T[] contents;
   private int size;

   private SortedArraySet(int var1, Comparator<T> var2) {
      super();
      this.comparator = var2;
      if (var1 < 0) {
         throw new IllegalArgumentException("Initial capacity (" + var1 + ") is negative");
      } else {
         this.contents = castRawArray(new Object[var1]);
      }
   }

   public static <T extends Comparable<T>> SortedArraySet<T> create(int var0) {
      return new SortedArraySet(var0, Comparator.naturalOrder());
   }

   private static <T> T[] castRawArray(Object[] var0) {
      return (Object[])var0;
   }

   private int findIndex(T var1) {
      return Arrays.binarySearch(this.contents, 0, this.size, var1, this.comparator);
   }

   private static int getInsertionPosition(int var0) {
      return -var0 - 1;
   }

   public boolean add(T var1) {
      int var2 = this.findIndex(var1);
      if (var2 >= 0) {
         return false;
      } else {
         int var3 = getInsertionPosition(var2);
         this.addInternal(var1, var3);
         return true;
      }
   }

   private void grow(int var1) {
      if (var1 > this.contents.length) {
         if (this.contents != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
            var1 = (int)Math.max(Math.min((long)this.contents.length + (long)(this.contents.length >> 1), 2147483639L), (long)var1);
         } else if (var1 < 10) {
            var1 = 10;
         }

         Object[] var2 = new Object[var1];
         System.arraycopy(this.contents, 0, var2, 0, this.size);
         this.contents = castRawArray(var2);
      }
   }

   private void addInternal(T var1, int var2) {
      this.grow(this.size + 1);
      if (var2 != this.size) {
         System.arraycopy(this.contents, var2, this.contents, var2 + 1, this.size - var2);
      }

      this.contents[var2] = var1;
      ++this.size;
   }

   private void removeInternal(int var1) {
      --this.size;
      if (var1 != this.size) {
         System.arraycopy(this.contents, var1 + 1, this.contents, var1, this.size - var1);
      }

      this.contents[this.size] = null;
   }

   private T getInternal(int var1) {
      return this.contents[var1];
   }

   public T addOrGet(T var1) {
      int var2 = this.findIndex(var1);
      if (var2 >= 0) {
         return this.getInternal(var2);
      } else {
         this.addInternal(var1, getInsertionPosition(var2));
         return var1;
      }
   }

   public boolean remove(Object var1) {
      int var2 = this.findIndex(var1);
      if (var2 >= 0) {
         this.removeInternal(var2);
         return true;
      } else {
         return false;
      }
   }

   public T first() {
      return this.getInternal(0);
   }

   public boolean contains(Object var1) {
      int var2 = this.findIndex(var1);
      return var2 >= 0;
   }

   public Iterator<T> iterator() {
      return new SortedArraySet.ArrayIterator();
   }

   public int size() {
      return this.size;
   }

   public Object[] toArray() {
      return (Object[])this.contents.clone();
   }

   public <U> U[] toArray(U[] var1) {
      if (var1.length < this.size) {
         return (Object[])Arrays.copyOf(this.contents, this.size, var1.getClass());
      } else {
         System.arraycopy(this.contents, 0, var1, 0, this.size);
         if (var1.length > this.size) {
            var1[this.size] = null;
         }

         return var1;
      }
   }

   public void clear() {
      Arrays.fill(this.contents, 0, this.size, (Object)null);
      this.size = 0;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof SortedArraySet) {
            SortedArraySet var2 = (SortedArraySet)var1;
            if (this.comparator.equals(var2.comparator)) {
               return this.size == var2.size && Arrays.equals(this.contents, var2.contents);
            }
         }

         return super.equals(var1);
      }
   }

   class ArrayIterator implements Iterator<T> {
      private int index;
      private int last;

      private ArrayIterator() {
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
            return SortedArraySet.this.contents[this.last];
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

      // $FF: synthetic method
      ArrayIterator(Object var2) {
         this();
      }
   }
}
