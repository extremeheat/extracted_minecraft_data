package it.unimi.dsi.fastutil.doubles;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.NoSuchElementException;

public class DoubleArraySet extends AbstractDoubleSet implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient double[] a;
   private int size;

   public DoubleArraySet(double[] var1) {
      super();
      this.a = var1;
      this.size = var1.length;
   }

   public DoubleArraySet() {
      super();
      this.a = DoubleArrays.EMPTY_ARRAY;
   }

   public DoubleArraySet(int var1) {
      super();
      this.a = new double[var1];
   }

   public DoubleArraySet(DoubleCollection var1) {
      this(var1.size());
      this.addAll(var1);
   }

   public DoubleArraySet(Collection<? extends Double> var1) {
      this(var1.size());
      this.addAll(var1);
   }

   public DoubleArraySet(double[] var1, int var2) {
      super();
      this.a = var1;
      this.size = var2;
      if (var2 > var1.length) {
         throw new IllegalArgumentException("The provided size (" + var2 + ") is larger than or equal to the array size (" + var1.length + ")");
      }
   }

   private int findKey(double var1) {
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return -1;
         }
      } while(Double.doubleToLongBits(this.a[var3]) != Double.doubleToLongBits(var1));

      return var3;
   }

   public DoubleIterator iterator() {
      return new DoubleIterator() {
         int next = 0;

         public boolean hasNext() {
            return this.next < DoubleArraySet.this.size;
         }

         public double nextDouble() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return DoubleArraySet.this.a[this.next++];
            }
         }

         public void remove() {
            int var1 = DoubleArraySet.this.size-- - this.next--;
            System.arraycopy(DoubleArraySet.this.a, this.next + 1, DoubleArraySet.this.a, this.next, var1);
         }
      };
   }

   public boolean contains(double var1) {
      return this.findKey(var1) != -1;
   }

   public int size() {
      return this.size;
   }

   public boolean remove(double var1) {
      int var3 = this.findKey(var1);
      if (var3 == -1) {
         return false;
      } else {
         int var4 = this.size - var3 - 1;

         for(int var5 = 0; var5 < var4; ++var5) {
            this.a[var3 + var5] = this.a[var3 + var5 + 1];
         }

         --this.size;
         return true;
      }
   }

   public boolean add(double var1) {
      int var3 = this.findKey(var1);
      if (var3 != -1) {
         return false;
      } else {
         if (this.size == this.a.length) {
            double[] var4 = new double[this.size == 0 ? 2 : this.size * 2];

            for(int var5 = this.size; var5-- != 0; var4[var5] = this.a[var5]) {
            }

            this.a = var4;
         }

         this.a[this.size++] = var1;
         return true;
      }
   }

   public void clear() {
      this.size = 0;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public DoubleArraySet clone() {
      DoubleArraySet var1;
      try {
         var1 = (DoubleArraySet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.a = (double[])this.a.clone();
      return var1;
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
