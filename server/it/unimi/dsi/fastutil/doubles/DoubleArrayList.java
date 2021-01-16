package it.unimi.dsi.fastutil.doubles;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class DoubleArrayList extends AbstractDoubleList implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353130L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected transient double[] a;
   protected int size;

   protected DoubleArrayList(double[] var1, boolean var2) {
      super();
      this.a = var1;
   }

   public DoubleArrayList(int var1) {
      super();
      if (var1 < 0) {
         throw new IllegalArgumentException("Initial capacity (" + var1 + ") is negative");
      } else {
         if (var1 == 0) {
            this.a = DoubleArrays.EMPTY_ARRAY;
         } else {
            this.a = new double[var1];
         }

      }
   }

   public DoubleArrayList() {
      super();
      this.a = DoubleArrays.DEFAULT_EMPTY_ARRAY;
   }

   public DoubleArrayList(Collection<? extends Double> var1) {
      this(var1.size());
      this.size = DoubleIterators.unwrap(DoubleIterators.asDoubleIterator(var1.iterator()), this.a);
   }

   public DoubleArrayList(DoubleCollection var1) {
      this(var1.size());
      this.size = DoubleIterators.unwrap(var1.iterator(), this.a);
   }

   public DoubleArrayList(DoubleList var1) {
      this(var1.size());
      var1.getElements(0, this.a, 0, this.size = var1.size());
   }

   public DoubleArrayList(double[] var1) {
      this(var1, 0, var1.length);
   }

   public DoubleArrayList(double[] var1, int var2, int var3) {
      this(var3);
      System.arraycopy(var1, var2, this.a, 0, var3);
      this.size = var3;
   }

   public DoubleArrayList(Iterator<? extends Double> var1) {
      this();

      while(var1.hasNext()) {
         this.add((Double)var1.next());
      }

   }

   public DoubleArrayList(DoubleIterator var1) {
      this();

      while(var1.hasNext()) {
         this.add(var1.nextDouble());
      }

   }

   public double[] elements() {
      return this.a;
   }

   public static DoubleArrayList wrap(double[] var0, int var1) {
      if (var1 > var0.length) {
         throw new IllegalArgumentException("The specified length (" + var1 + ") is greater than the array size (" + var0.length + ")");
      } else {
         DoubleArrayList var2 = new DoubleArrayList(var0, false);
         var2.size = var1;
         return var2;
      }
   }

   public static DoubleArrayList wrap(double[] var0) {
      return wrap(var0, var0.length);
   }

   public void ensureCapacity(int var1) {
      if (var1 > this.a.length && this.a != DoubleArrays.DEFAULT_EMPTY_ARRAY) {
         this.a = DoubleArrays.ensureCapacity(this.a, var1, this.size);

         assert this.size <= this.a.length;

      }
   }

   private void grow(int var1) {
      if (var1 > this.a.length) {
         if (this.a != DoubleArrays.DEFAULT_EMPTY_ARRAY) {
            var1 = (int)Math.max(Math.min((long)this.a.length + (long)(this.a.length >> 1), 2147483639L), (long)var1);
         } else if (var1 < 10) {
            var1 = 10;
         }

         this.a = DoubleArrays.forceCapacity(this.a, var1, this.size);

         assert this.size <= this.a.length;

      }
   }

   public void add(int var1, double var2) {
      this.ensureIndex(var1);
      this.grow(this.size + 1);
      if (var1 != this.size) {
         System.arraycopy(this.a, var1, this.a, var1 + 1, this.size - var1);
      }

      this.a[var1] = var2;
      ++this.size;

      assert this.size <= this.a.length;

   }

   public boolean add(double var1) {
      this.grow(this.size + 1);
      this.a[this.size++] = var1;

      assert this.size <= this.a.length;

      return true;
   }

   public double getDouble(int var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return this.a[var1];
      }
   }

   public int indexOf(double var1) {
      for(int var3 = 0; var3 < this.size; ++var3) {
         if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(this.a[var3])) {
            return var3;
         }
      }

      return -1;
   }

   public int lastIndexOf(double var1) {
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return -1;
         }
      } while(Double.doubleToLongBits(var1) != Double.doubleToLongBits(this.a[var3]));

      return var3;
   }

   public double removeDouble(int var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         double var2 = this.a[var1];
         --this.size;
         if (var1 != this.size) {
            System.arraycopy(this.a, var1 + 1, this.a, var1, this.size - var1);
         }

         assert this.size <= this.a.length;

         return var2;
      }
   }

   public boolean rem(double var1) {
      int var3 = this.indexOf(var1);
      if (var3 == -1) {
         return false;
      } else {
         this.removeDouble(var3);

         assert this.size <= this.a.length;

         return true;
      }
   }

   public double set(int var1, double var2) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         double var4 = this.a[var1];
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
         Arrays.fill(this.a, this.size, var1, 0.0D);
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
         double[] var2 = new double[Math.max(var1, this.size)];
         System.arraycopy(this.a, 0, var2, 0, this.size);
         this.a = var2;

         assert this.size <= this.a.length;

      }
   }

   public void getElements(int var1, double[] var2, int var3, int var4) {
      DoubleArrays.ensureOffsetLength(var2, var3, var4);
      System.arraycopy(this.a, var1, var2, var3, var4);
   }

   public void removeElements(int var1, int var2) {
      it.unimi.dsi.fastutil.Arrays.ensureFromTo(this.size, var1, var2);
      System.arraycopy(this.a, var2, this.a, var1, this.size - var2);
      this.size -= var2 - var1;
   }

   public void addElements(int var1, double[] var2, int var3, int var4) {
      this.ensureIndex(var1);
      DoubleArrays.ensureOffsetLength(var2, var3, var4);
      this.grow(this.size + var4);
      System.arraycopy(this.a, var1, this.a, var1 + var4, this.size - var1);
      System.arraycopy(var2, var3, this.a, var1, var4);
      this.size += var4;
   }

   public double[] toArray(double[] var1) {
      if (var1 == null || var1.length < this.size) {
         var1 = new double[this.size];
      }

      System.arraycopy(this.a, 0, var1, 0, this.size);
      return var1;
   }

   public boolean addAll(int var1, DoubleCollection var2) {
      this.ensureIndex(var1);
      int var3 = var2.size();
      if (var3 == 0) {
         return false;
      } else {
         this.grow(this.size + var3);
         if (var1 != this.size) {
            System.arraycopy(this.a, var1, this.a, var1 + var3, this.size - var1);
         }

         DoubleIterator var4 = var2.iterator();

         for(this.size += var3; var3-- != 0; this.a[var1++] = var4.nextDouble()) {
         }

         assert this.size <= this.a.length;

         return true;
      }
   }

   public boolean addAll(int var1, DoubleList var2) {
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

   public boolean removeAll(DoubleCollection var1) {
      double[] var2 = this.a;
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
      double[] var2 = this.a;
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

   public DoubleListIterator listIterator(final int var1) {
      this.ensureIndex(var1);
      return new DoubleListIterator() {
         int pos = var1;
         int last = -1;

         public boolean hasNext() {
            return this.pos < DoubleArrayList.this.size;
         }

         public boolean hasPrevious() {
            return this.pos > 0;
         }

         public double nextDouble() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return DoubleArrayList.this.a[this.last = this.pos++];
            }
         }

         public double previousDouble() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return DoubleArrayList.this.a[this.last = --this.pos];
            }
         }

         public int nextIndex() {
            return this.pos;
         }

         public int previousIndex() {
            return this.pos - 1;
         }

         public void add(double var1x) {
            DoubleArrayList.this.add(this.pos++, var1x);
            this.last = -1;
         }

         public void set(double var1x) {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               DoubleArrayList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               DoubleArrayList.this.removeDouble(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1;
            }
         }
      };
   }

   public DoubleArrayList clone() {
      DoubleArrayList var1 = new DoubleArrayList(this.size);
      System.arraycopy(this.a, 0, var1.a, 0, this.size);
      var1.size = this.size;
      return var1;
   }

   public boolean equals(DoubleArrayList var1) {
      if (var1 == this) {
         return true;
      } else {
         int var2 = this.size();
         if (var2 != var1.size()) {
            return false;
         } else {
            double[] var3 = this.a;
            double[] var4 = var1.a;

            do {
               if (var2-- == 0) {
                  return true;
               }
            } while(var3[var2] == var4[var2]);

            return false;
         }
      }
   }

   public int compareTo(DoubleArrayList var1) {
      int var2 = this.size();
      int var3 = var1.size();
      double[] var4 = this.a;
      double[] var5 = var1.a;

      int var11;
      for(var11 = 0; var11 < var2 && var11 < var3; ++var11) {
         double var6 = var4[var11];
         double var8 = var5[var11];
         int var10;
         if ((var10 = Double.compare(var6, var8)) != 0) {
            return var10;
         }
      }

      return var11 < var3 ? -1 : (var11 < var2 ? 1 : 0);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeDouble(this.a[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.a = new double[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.a[var2] = var1.readDouble();
      }

   }
}
