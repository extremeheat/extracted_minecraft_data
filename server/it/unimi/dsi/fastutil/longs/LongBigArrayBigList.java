package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigArrays;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class LongBigArrayBigList extends AbstractLongBigList implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353130L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected transient long[][] a;
   protected long size;

   protected LongBigArrayBigList(long[][] var1, boolean var2) {
      super();
      this.a = var1;
   }

   public LongBigArrayBigList(long var1) {
      super();
      if (var1 < 0L) {
         throw new IllegalArgumentException("Initial capacity (" + var1 + ") is negative");
      } else {
         if (var1 == 0L) {
            this.a = LongBigArrays.EMPTY_BIG_ARRAY;
         } else {
            this.a = LongBigArrays.newBigArray(var1);
         }

      }
   }

   public LongBigArrayBigList() {
      super();
      this.a = LongBigArrays.DEFAULT_EMPTY_BIG_ARRAY;
   }

   public LongBigArrayBigList(LongCollection var1) {
      this((long)var1.size());
      LongIterator var2 = var1.iterator();

      while(var2.hasNext()) {
         this.add(var2.nextLong());
      }

   }

   public LongBigArrayBigList(LongBigList var1) {
      this(var1.size64());
      var1.getElements(0L, this.a, 0L, this.size = var1.size64());
   }

   public LongBigArrayBigList(long[][] var1) {
      this(var1, 0L, LongBigArrays.length(var1));
   }

   public LongBigArrayBigList(long[][] var1, long var2, long var4) {
      this(var4);
      LongBigArrays.copy(var1, var2, this.a, 0L, var4);
      this.size = var4;
   }

   public LongBigArrayBigList(Iterator<? extends Long> var1) {
      this();

      while(var1.hasNext()) {
         this.add((Long)var1.next());
      }

   }

   public LongBigArrayBigList(LongIterator var1) {
      this();

      while(var1.hasNext()) {
         this.add(var1.nextLong());
      }

   }

   public long[][] elements() {
      return this.a;
   }

   public static LongBigArrayBigList wrap(long[][] var0, long var1) {
      if (var1 > LongBigArrays.length(var0)) {
         throw new IllegalArgumentException("The specified length (" + var1 + ") is greater than the array size (" + LongBigArrays.length(var0) + ")");
      } else {
         LongBigArrayBigList var3 = new LongBigArrayBigList(var0, false);
         var3.size = var1;
         return var3;
      }
   }

   public static LongBigArrayBigList wrap(long[][] var0) {
      return wrap(var0, LongBigArrays.length(var0));
   }

   public void ensureCapacity(long var1) {
      if (var1 > (long)this.a.length && this.a != LongBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
         this.a = LongBigArrays.forceCapacity(this.a, var1, this.size);

         assert this.size <= LongBigArrays.length(this.a);

      }
   }

   private void grow(long var1) {
      long var3 = LongBigArrays.length(this.a);
      if (var1 > var3) {
         if (this.a != LongBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
            var1 = Math.max(var3 + (var3 >> 1), var1);
         } else if (var1 < 10L) {
            var1 = 10L;
         }

         this.a = LongBigArrays.forceCapacity(this.a, var1, this.size);

         assert this.size <= LongBigArrays.length(this.a);

      }
   }

   public void add(long var1, long var3) {
      this.ensureIndex(var1);
      this.grow(this.size + 1L);
      if (var1 != this.size) {
         LongBigArrays.copy(this.a, var1, this.a, var1 + 1L, this.size - var1);
      }

      LongBigArrays.set(this.a, var1, var3);
      ++this.size;

      assert this.size <= LongBigArrays.length(this.a);

   }

   public boolean add(long var1) {
      this.grow(this.size + 1L);
      LongBigArrays.set(this.a, (long)(this.size++), var1);

      assert this.size <= LongBigArrays.length(this.a);

      return true;
   }

   public long getLong(long var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return LongBigArrays.get(this.a, var1);
      }
   }

   public long indexOf(long var1) {
      for(long var3 = 0L; var3 < this.size; ++var3) {
         if (var1 == LongBigArrays.get(this.a, var3)) {
            return var3;
         }
      }

      return -1L;
   }

   public long lastIndexOf(long var1) {
      long var3 = this.size;

      do {
         if (var3-- == 0L) {
            return -1L;
         }
      } while(var1 != LongBigArrays.get(this.a, var3));

      return var3;
   }

   public long removeLong(long var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         long var3 = LongBigArrays.get(this.a, var1);
         --this.size;
         if (var1 != this.size) {
            LongBigArrays.copy(this.a, var1 + 1L, this.a, var1, this.size - var1);
         }

         assert this.size <= LongBigArrays.length(this.a);

         return var3;
      }
   }

   public boolean rem(long var1) {
      long var3 = this.indexOf(var1);
      if (var3 == -1L) {
         return false;
      } else {
         this.removeLong(var3);

         assert this.size <= LongBigArrays.length(this.a);

         return true;
      }
   }

   public long set(long var1, long var3) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         long var5 = LongBigArrays.get(this.a, var1);
         LongBigArrays.set(this.a, var1, var3);
         return var5;
      }
   }

   public boolean removeAll(LongCollection var1) {
      long[] var2 = null;
      long[] var3 = null;
      int var4 = -1;
      int var5 = 134217728;
      int var6 = -1;
      int var7 = 134217728;

      long var8;
      for(var8 = 0L; var8 < this.size; ++var8) {
         if (var5 == 134217728) {
            var5 = 0;
            ++var4;
            var2 = this.a[var4];
         }

         if (!var1.contains(var2[var5])) {
            if (var7 == 134217728) {
               ++var6;
               var3 = this.a[var6];
               var7 = 0;
            }

            var3[var7++] = var2[var5];
         }

         ++var5;
      }

      var8 = BigArrays.index(var6, var7);
      boolean var10 = this.size != var8;
      this.size = var8;
      return var10;
   }

   public boolean removeAll(Collection<?> var1) {
      long[] var2 = null;
      long[] var3 = null;
      int var4 = -1;
      int var5 = 134217728;
      int var6 = -1;
      int var7 = 134217728;

      long var8;
      for(var8 = 0L; var8 < this.size; ++var8) {
         if (var5 == 134217728) {
            var5 = 0;
            ++var4;
            var2 = this.a[var4];
         }

         if (!var1.contains(var2[var5])) {
            if (var7 == 134217728) {
               ++var6;
               var3 = this.a[var6];
               var7 = 0;
            }

            var3[var7++] = var2[var5];
         }

         ++var5;
      }

      var8 = BigArrays.index(var6, var7);
      boolean var10 = this.size != var8;
      this.size = var8;
      return var10;
   }

   public void clear() {
      this.size = 0L;

      assert this.size <= LongBigArrays.length(this.a);

   }

   public long size64() {
      return this.size;
   }

   public void size(long var1) {
      if (var1 > LongBigArrays.length(this.a)) {
         this.ensureCapacity(var1);
      }

      if (var1 > this.size) {
         LongBigArrays.fill(this.a, this.size, var1, 0L);
      }

      this.size = var1;
   }

   public boolean isEmpty() {
      return this.size == 0L;
   }

   public void trim() {
      this.trim(0L);
   }

   public void trim(long var1) {
      long var3 = LongBigArrays.length(this.a);
      if (var1 < var3 && this.size != var3) {
         this.a = LongBigArrays.trim(this.a, Math.max(var1, this.size));

         assert this.size <= LongBigArrays.length(this.a);

      }
   }

   public void getElements(long var1, long[][] var3, long var4, long var6) {
      LongBigArrays.copy(this.a, var1, var3, var4, var6);
   }

   public void removeElements(long var1, long var3) {
      BigArrays.ensureFromTo(this.size, var1, var3);
      LongBigArrays.copy(this.a, var3, this.a, var1, this.size - var3);
      this.size -= var3 - var1;
   }

   public void addElements(long var1, long[][] var3, long var4, long var6) {
      this.ensureIndex(var1);
      LongBigArrays.ensureOffsetLength(var3, var4, var6);
      this.grow(this.size + var6);
      LongBigArrays.copy(this.a, var1, this.a, var1 + var6, this.size - var1);
      LongBigArrays.copy(var3, var4, this.a, var1, var6);
      this.size += var6;
   }

   public LongBigListIterator listIterator(final long var1) {
      this.ensureIndex(var1);
      return new LongBigListIterator() {
         long pos = var1;
         long last = -1L;

         public boolean hasNext() {
            return this.pos < LongBigArrayBigList.this.size;
         }

         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         public long nextLong() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return LongBigArrays.get(LongBigArrayBigList.this.a, this.last = (long)(this.pos++));
            }
         }

         public long previousLong() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return LongBigArrays.get(LongBigArrayBigList.this.a, this.last = --this.pos);
            }
         }

         public long nextIndex() {
            return this.pos;
         }

         public long previousIndex() {
            return this.pos - 1L;
         }

         public void add(long var1x) {
            LongBigArrayBigList.this.add((long)(this.pos++), var1x);
            this.last = -1L;
         }

         public void set(long var1x) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               LongBigArrayBigList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               LongBigArrayBigList.this.removeLong(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1L;
            }
         }
      };
   }

   public LongBigArrayBigList clone() {
      LongBigArrayBigList var1 = new LongBigArrayBigList(this.size);
      LongBigArrays.copy(this.a, 0L, var1.a, 0L, this.size);
      var1.size = this.size;
      return var1;
   }

   public boolean equals(LongBigArrayBigList var1) {
      if (var1 == this) {
         return true;
      } else {
         long var2 = this.size64();
         if (var2 != var1.size64()) {
            return false;
         } else {
            long[][] var4 = this.a;
            long[][] var5 = var1.a;

            do {
               if (var2-- == 0L) {
                  return true;
               }
            } while(LongBigArrays.get(var4, var2) == LongBigArrays.get(var5, var2));

            return false;
         }
      }
   }

   public int compareTo(LongBigArrayBigList var1) {
      long var2 = this.size64();
      long var4 = var1.size64();
      long[][] var6 = this.a;
      long[][] var7 = var1.a;

      int var13;
      for(var13 = 0; (long)var13 < var2 && (long)var13 < var4; ++var13) {
         long var8 = LongBigArrays.get(var6, (long)var13);
         long var10 = LongBigArrays.get(var7, (long)var13);
         int var12;
         if ((var12 = Long.compare(var8, var10)) != 0) {
            return var12;
         }
      }

      return (long)var13 < var4 ? -1 : ((long)var13 < var2 ? 1 : 0);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; (long)var2 < this.size; ++var2) {
         var1.writeLong(LongBigArrays.get(this.a, (long)var2));
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.a = LongBigArrays.newBigArray(this.size);

      for(int var2 = 0; (long)var2 < this.size; ++var2) {
         LongBigArrays.set(this.a, (long)var2, var1.readLong());
      }

   }
}
