package it.unimi.dsi.fastutil.longs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class LongArrayList extends AbstractLongList implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353130L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected transient long[] a;
   protected int size;

   protected LongArrayList(long[] var1, boolean var2) {
      super();
      this.a = var1;
   }

   public LongArrayList(int var1) {
      super();
      if (var1 < 0) {
         throw new IllegalArgumentException("Initial capacity (" + var1 + ") is negative");
      } else {
         if (var1 == 0) {
            this.a = LongArrays.EMPTY_ARRAY;
         } else {
            this.a = new long[var1];
         }

      }
   }

   public LongArrayList() {
      super();
      this.a = LongArrays.DEFAULT_EMPTY_ARRAY;
   }

   public LongArrayList(Collection<? extends Long> var1) {
      this(var1.size());
      this.size = LongIterators.unwrap(LongIterators.asLongIterator(var1.iterator()), this.a);
   }

   public LongArrayList(LongCollection var1) {
      this(var1.size());
      this.size = LongIterators.unwrap(var1.iterator(), this.a);
   }

   public LongArrayList(LongList var1) {
      this(var1.size());
      var1.getElements(0, this.a, 0, this.size = var1.size());
   }

   public LongArrayList(long[] var1) {
      this(var1, 0, var1.length);
   }

   public LongArrayList(long[] var1, int var2, int var3) {
      this(var3);
      System.arraycopy(var1, var2, this.a, 0, var3);
      this.size = var3;
   }

   public LongArrayList(Iterator<? extends Long> var1) {
      this();

      while(var1.hasNext()) {
         this.add((Long)var1.next());
      }

   }

   public LongArrayList(LongIterator var1) {
      this();

      while(var1.hasNext()) {
         this.add(var1.nextLong());
      }

   }

   public long[] elements() {
      return this.a;
   }

   public static LongArrayList wrap(long[] var0, int var1) {
      if (var1 > var0.length) {
         throw new IllegalArgumentException("The specified length (" + var1 + ") is greater than the array size (" + var0.length + ")");
      } else {
         LongArrayList var2 = new LongArrayList(var0, false);
         var2.size = var1;
         return var2;
      }
   }

   public static LongArrayList wrap(long[] var0) {
      return wrap(var0, var0.length);
   }

   public void ensureCapacity(int var1) {
      if (var1 > this.a.length && this.a != LongArrays.DEFAULT_EMPTY_ARRAY) {
         this.a = LongArrays.ensureCapacity(this.a, var1, this.size);

         assert this.size <= this.a.length;

      }
   }

   private void grow(int var1) {
      if (var1 > this.a.length) {
         if (this.a != LongArrays.DEFAULT_EMPTY_ARRAY) {
            var1 = (int)Math.max(Math.min((long)this.a.length + (long)(this.a.length >> 1), 2147483639L), (long)var1);
         } else if (var1 < 10) {
            var1 = 10;
         }

         this.a = LongArrays.forceCapacity(this.a, var1, this.size);

         assert this.size <= this.a.length;

      }
   }

   public void add(int var1, long var2) {
      this.ensureIndex(var1);
      this.grow(this.size + 1);
      if (var1 != this.size) {
         System.arraycopy(this.a, var1, this.a, var1 + 1, this.size - var1);
      }

      this.a[var1] = var2;
      ++this.size;

      assert this.size <= this.a.length;

   }

   public boolean add(long var1) {
      this.grow(this.size + 1);
      this.a[this.size++] = var1;

      assert this.size <= this.a.length;

      return true;
   }

   public long getLong(int var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return this.a[var1];
      }
   }

   public int indexOf(long var1) {
      for(int var3 = 0; var3 < this.size; ++var3) {
         if (var1 == this.a[var3]) {
            return var3;
         }
      }

      return -1;
   }

   public int lastIndexOf(long var1) {
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return -1;
         }
      } while(var1 != this.a[var3]);

      return var3;
   }

   public long removeLong(int var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         long var2 = this.a[var1];
         --this.size;
         if (var1 != this.size) {
            System.arraycopy(this.a, var1 + 1, this.a, var1, this.size - var1);
         }

         assert this.size <= this.a.length;

         return var2;
      }
   }

   public boolean rem(long var1) {
      int var3 = this.indexOf(var1);
      if (var3 == -1) {
         return false;
      } else {
         this.removeLong(var3);

         assert this.size <= this.a.length;

         return true;
      }
   }

   public long set(int var1, long var2) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         long var4 = this.a[var1];
         this.a[var1] = var2;
         return var4;
      }
   }

   public void clear() {
      this.size = 0;

      assert this.size <= this.a.length;

   }

   public int size() {
      return this.size;
   }

   public void size(int var1) {
      if (var1 > this.a.length) {
         this.ensureCapacity(var1);
      }

      if (var1 > this.size) {
         Arrays.fill(this.a, this.size, var1, 0L);
      }

      this.size = var1;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public void trim() {
      this.trim(0);
   }

   public void trim(int var1) {
      if (var1 < this.a.length && this.size != this.a.length) {
         long[] var2 = new long[Math.max(var1, this.size)];
         System.arraycopy(this.a, 0, var2, 0, this.size);
         this.a = var2;

         assert this.size <= this.a.length;

      }
   }

   public void getElements(int var1, long[] var2, int var3, int var4) {
      LongArrays.ensureOffsetLength(var2, var3, var4);
      System.arraycopy(this.a, var1, var2, var3, var4);
   }

   public void removeElements(int var1, int var2) {
      it.unimi.dsi.fastutil.Arrays.ensureFromTo(this.size, var1, var2);
      System.arraycopy(this.a, var2, this.a, var1, this.size - var2);
      this.size -= var2 - var1;
   }

   public void addElements(int var1, long[] var2, int var3, int var4) {
      this.ensureIndex(var1);
      LongArrays.ensureOffsetLength(var2, var3, var4);
      this.grow(this.size + var4);
      System.arraycopy(this.a, var1, this.a, var1 + var4, this.size - var1);
      System.arraycopy(var2, var3, this.a, var1, var4);
      this.size += var4;
   }

   public long[] toArray(long[] var1) {
      if (var1 == null || var1.length < this.size) {
         var1 = new long[this.size];
      }

      System.arraycopy(this.a, 0, var1, 0, this.size);
      return var1;
   }

   public boolean addAll(int var1, LongCollection var2) {
      this.ensureIndex(var1);
      int var3 = var2.size();
      if (var3 == 0) {
         return false;
      } else {
         this.grow(this.size + var3);
         if (var1 != this.size) {
            System.arraycopy(this.a, var1, this.a, var1 + var3, this.size - var1);
         }

         LongIterator var4 = var2.iterator();

         for(this.size += var3; var3-- != 0; this.a[var1++] = var4.nextLong()) {
         }

         assert this.size <= this.a.length;

         return true;
      }
   }

   public boolean addAll(int var1, LongList var2) {
      this.ensureIndex(var1);
      int var3 = var2.size();
      if (var3 == 0) {
         return false;
      } else {
         this.grow(this.size + var3);
         if (var1 != this.size) {
            System.arraycopy(this.a, var1, this.a, var1 + var3, this.size - var1);
         }

         var2.getElements(0, this.a, var1, var3);
         this.size += var3;

         assert this.size <= this.a.length;

         return true;
      }
   }

   public boolean removeAll(LongCollection var1) {
      long[] var2 = this.a;
      int var3 = 0;

      for(int var4 = 0; var4 < this.size; ++var4) {
         if (!var1.contains(var2[var4])) {
            var2[var3++] = var2[var4];
         }
      }

      boolean var5 = this.size != var3;
      this.size = var3;
      return var5;
   }

   public boolean removeAll(Collection<?> var1) {
      long[] var2 = this.a;
      int var3 = 0;

      for(int var4 = 0; var4 < this.size; ++var4) {
         if (!var1.contains(var2[var4])) {
            var2[var3++] = var2[var4];
         }
      }

      boolean var5 = this.size != var3;
      this.size = var3;
      return var5;
   }

   public LongListIterator listIterator(final int var1) {
      this.ensureIndex(var1);
      return new LongListIterator() {
         int pos = var1;
         int last = -1;

         public boolean hasNext() {
            return this.pos < LongArrayList.this.size;
         }

         public boolean hasPrevious() {
            return this.pos > 0;
         }

         public long nextLong() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return LongArrayList.this.a[this.last = this.pos++];
            }
         }

         public long previousLong() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return LongArrayList.this.a[this.last = --this.pos];
            }
         }

         public int nextIndex() {
            return this.pos;
         }

         public int previousIndex() {
            return this.pos - 1;
         }

         public void add(long var1x) {
            LongArrayList.this.add(this.pos++, var1x);
            this.last = -1;
         }

         public void set(long var1x) {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               LongArrayList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               LongArrayList.this.removeLong(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1;
            }
         }
      };
   }

   public LongArrayList clone() {
      LongArrayList var1 = new LongArrayList(this.size);
      System.arraycopy(this.a, 0, var1.a, 0, this.size);
      var1.size = this.size;
      return var1;
   }

   public boolean equals(LongArrayList var1) {
      if (var1 == this) {
         return true;
      } else {
         int var2 = this.size();
         if (var2 != var1.size()) {
            return false;
         } else {
            long[] var3 = this.a;
            long[] var4 = var1.a;

            do {
               if (var2-- == 0) {
                  return true;
               }
            } while(var3[var2] == var4[var2]);

            return false;
         }
      }
   }

   public int compareTo(LongArrayList var1) {
      int var2 = this.size();
      int var3 = var1.size();
      long[] var4 = this.a;
      long[] var5 = var1.a;

      int var11;
      for(var11 = 0; var11 < var2 && var11 < var3; ++var11) {
         long var6 = var4[var11];
         long var8 = var5[var11];
         int var10;
         if ((var10 = Long.compare(var6, var8)) != 0) {
            return var10;
         }
      }

      return var11 < var3 ? -1 : (var11 < var2 ? 1 : 0);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeLong(this.a[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.a = new long[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.a[var2] = var1.readLong();
      }

   }
}
