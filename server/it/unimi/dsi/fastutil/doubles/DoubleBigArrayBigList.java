package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.BigArrays;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class DoubleBigArrayBigList extends AbstractDoubleBigList implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353130L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected transient double[][] a;
   protected long size;

   protected DoubleBigArrayBigList(double[][] var1, boolean var2) {
      super();
      this.a = var1;
   }

   public DoubleBigArrayBigList(long var1) {
      super();
      if (var1 < 0L) {
         throw new IllegalArgumentException("Initial capacity (" + var1 + ") is negative");
      } else {
         if (var1 == 0L) {
            this.a = DoubleBigArrays.EMPTY_BIG_ARRAY;
         } else {
            this.a = DoubleBigArrays.newBigArray(var1);
         }

      }
   }

   public DoubleBigArrayBigList() {
      super();
      this.a = DoubleBigArrays.DEFAULT_EMPTY_BIG_ARRAY;
   }

   public DoubleBigArrayBigList(DoubleCollection var1) {
      this((long)var1.size());
      DoubleIterator var2 = var1.iterator();

      while(var2.hasNext()) {
         this.add(var2.nextDouble());
      }

   }

   public DoubleBigArrayBigList(DoubleBigList var1) {
      this(var1.size64());
      var1.getElements(0L, this.a, 0L, this.size = var1.size64());
   }

   public DoubleBigArrayBigList(double[][] var1) {
      this(var1, 0L, DoubleBigArrays.length(var1));
   }

   public DoubleBigArrayBigList(double[][] var1, long var2, long var4) {
      this(var4);
      DoubleBigArrays.copy(var1, var2, this.a, 0L, var4);
      this.size = var4;
   }

   public DoubleBigArrayBigList(Iterator<? extends Double> var1) {
      this();

      while(var1.hasNext()) {
         this.add((Double)var1.next());
      }

   }

   public DoubleBigArrayBigList(DoubleIterator var1) {
      this();

      while(var1.hasNext()) {
         this.add(var1.nextDouble());
      }

   }

   public double[][] elements() {
      return this.a;
   }

   public static DoubleBigArrayBigList wrap(double[][] var0, long var1) {
      if (var1 > DoubleBigArrays.length(var0)) {
         throw new IllegalArgumentException("The specified length (" + var1 + ") is greater than the array size (" + DoubleBigArrays.length(var0) + ")");
      } else {
         DoubleBigArrayBigList var3 = new DoubleBigArrayBigList(var0, false);
         var3.size = var1;
         return var3;
      }
   }

   public static DoubleBigArrayBigList wrap(double[][] var0) {
      return wrap(var0, DoubleBigArrays.length(var0));
   }

   public void ensureCapacity(long var1) {
      if (var1 > (long)this.a.length && this.a != DoubleBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
         this.a = DoubleBigArrays.forceCapacity(this.a, var1, this.size);

         assert this.size <= DoubleBigArrays.length(this.a);

      }
   }

   private void grow(long var1) {
      long var3 = DoubleBigArrays.length(this.a);
      if (var1 > var3) {
         if (this.a != DoubleBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
            var1 = Math.max(var3 + (var3 >> 1), var1);
         } else if (var1 < 10L) {
            var1 = 10L;
         }

         this.a = DoubleBigArrays.forceCapacity(this.a, var1, this.size);

         assert this.size <= DoubleBigArrays.length(this.a);

      }
   }

   public void add(long var1, double var3) {
      this.ensureIndex(var1);
      this.grow(this.size + 1L);
      if (var1 != this.size) {
         DoubleBigArrays.copy(this.a, var1, this.a, var1 + 1L, this.size - var1);
      }

      DoubleBigArrays.set(this.a, var1, var3);
      ++this.size;

      assert this.size <= DoubleBigArrays.length(this.a);

   }

   public boolean add(double var1) {
      this.grow(this.size + 1L);
      DoubleBigArrays.set(this.a, (long)(this.size++), var1);

      assert this.size <= DoubleBigArrays.length(this.a);

      return true;
   }

   public double getDouble(long var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return DoubleBigArrays.get(this.a, var1);
      }
   }

   public long indexOf(double var1) {
      for(long var3 = 0L; var3 < this.size; ++var3) {
         if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(DoubleBigArrays.get(this.a, var3))) {
            return var3;
         }
      }

      return -1L;
   }

   public long lastIndexOf(double var1) {
      long var3 = this.size;

      do {
         if (var3-- == 0L) {
            return -1L;
         }
      } while(Double.doubleToLongBits(var1) != Double.doubleToLongBits(DoubleBigArrays.get(this.a, var3)));

      return var3;
   }

   public double removeDouble(long var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         double var3 = DoubleBigArrays.get(this.a, var1);
         --this.size;
         if (var1 != this.size) {
            DoubleBigArrays.copy(this.a, var1 + 1L, this.a, var1, this.size - var1);
         }

         assert this.size <= DoubleBigArrays.length(this.a);

         return var3;
      }
   }

   public boolean rem(double var1) {
      long var3 = this.indexOf(var1);
      if (var3 == -1L) {
         return false;
      } else {
         this.removeDouble(var3);

         assert this.size <= DoubleBigArrays.length(this.a);

         return true;
      }
   }

   public double set(long var1, double var3) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         double var5 = DoubleBigArrays.get(this.a, var1);
         DoubleBigArrays.set(this.a, var1, var3);
         return var5;
      }
   }

   public boolean removeAll(DoubleCollection var1) {
      double[] var2 = null;
      double[] var3 = null;
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
      double[] var2 = null;
      double[] var3 = null;
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

      assert this.size <= DoubleBigArrays.length(this.a);

   }

   public long size64() {
      return this.size;
   }

   public void size(long var1) {
      if (var1 > DoubleBigArrays.length(this.a)) {
         this.ensureCapacity(var1);
      }

      if (var1 > this.size) {
         DoubleBigArrays.fill(this.a, this.size, var1, 0.0D);
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
      long var3 = DoubleBigArrays.length(this.a);
      if (var1 < var3 && this.size != var3) {
         this.a = DoubleBigArrays.trim(this.a, Math.max(var1, this.size));

         assert this.size <= DoubleBigArrays.length(this.a);

      }
   }

   public void getElements(long var1, double[][] var3, long var4, long var6) {
      DoubleBigArrays.copy(this.a, var1, var3, var4, var6);
   }

   public void removeElements(long var1, long var3) {
      BigArrays.ensureFromTo(this.size, var1, var3);
      DoubleBigArrays.copy(this.a, var3, this.a, var1, this.size - var3);
      this.size -= var3 - var1;
   }

   public void addElements(long var1, double[][] var3, long var4, long var6) {
      this.ensureIndex(var1);
      DoubleBigArrays.ensureOffsetLength(var3, var4, var6);
      this.grow(this.size + var6);
      DoubleBigArrays.copy(this.a, var1, this.a, var1 + var6, this.size - var1);
      DoubleBigArrays.copy(var3, var4, this.a, var1, var6);
      this.size += var6;
   }

   public DoubleBigListIterator listIterator(final long var1) {
      this.ensureIndex(var1);
      return new DoubleBigListIterator() {
         long pos = var1;
         long last = -1L;

         public boolean hasNext() {
            return this.pos < DoubleBigArrayBigList.this.size;
         }

         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         public double nextDouble() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return DoubleBigArrays.get(DoubleBigArrayBigList.this.a, this.last = (long)(this.pos++));
            }
         }

         public double previousDouble() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return DoubleBigArrays.get(DoubleBigArrayBigList.this.a, this.last = --this.pos);
            }
         }

         public long nextIndex() {
            return this.pos;
         }

         public long previousIndex() {
            return this.pos - 1L;
         }

         public void add(double var1x) {
            DoubleBigArrayBigList.this.add((long)(this.pos++), var1x);
            this.last = -1L;
         }

         public void set(double var1x) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               DoubleBigArrayBigList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               DoubleBigArrayBigList.this.removeDouble(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1L;
            }
         }
      };
   }

   public DoubleBigArrayBigList clone() {
      DoubleBigArrayBigList var1 = new DoubleBigArrayBigList(this.size);
      DoubleBigArrays.copy(this.a, 0L, var1.a, 0L, this.size);
      var1.size = this.size;
      return var1;
   }

   public boolean equals(DoubleBigArrayBigList var1) {
      if (var1 == this) {
         return true;
      } else {
         long var2 = this.size64();
         if (var2 != var1.size64()) {
            return false;
         } else {
            double[][] var4 = this.a;
            double[][] var5 = var1.a;

            do {
               if (var2-- == 0L) {
                  return true;
               }
            } while(DoubleBigArrays.get(var4, var2) == DoubleBigArrays.get(var5, var2));

            return false;
         }
      }
   }

   public int compareTo(DoubleBigArrayBigList var1) {
      long var2 = this.size64();
      long var4 = var1.size64();
      double[][] var6 = this.a;
      double[][] var7 = var1.a;

      int var13;
      for(var13 = 0; (long)var13 < var2 && (long)var13 < var4; ++var13) {
         double var8 = DoubleBigArrays.get(var6, (long)var13);
         double var10 = DoubleBigArrays.get(var7, (long)var13);
         int var12;
         if ((var12 = Double.compare(var8, var10)) != 0) {
            return var12;
         }
      }

      return (long)var13 < var4 ? -1 : ((long)var13 < var2 ? 1 : 0);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; (long)var2 < this.size; ++var2) {
         var1.writeDouble(DoubleBigArrays.get(this.a, (long)var2));
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.a = DoubleBigArrays.newBigArray(this.size);

      for(int var2 = 0; (long)var2 < this.size; ++var2) {
         DoubleBigArrays.set(this.a, (long)var2, var1.readDouble());
      }

   }
}
