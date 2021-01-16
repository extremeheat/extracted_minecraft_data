package it.unimi.dsi.fastutil.doubles;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.NoSuchElementException;

public class DoubleArrayPriorityQueue implements DoublePriorityQueue, Serializable {
   private static final long serialVersionUID = 1L;
   protected transient double[] array;
   protected int size;
   protected DoubleComparator c;
   protected transient int firstIndex;
   protected transient boolean firstIndexValid;

   public DoubleArrayPriorityQueue(int var1, DoubleComparator var2) {
      super();
      this.array = DoubleArrays.EMPTY_ARRAY;
      if (var1 > 0) {
         this.array = new double[var1];
      }

      this.c = var2;
   }

   public DoubleArrayPriorityQueue(int var1) {
      this(var1, (DoubleComparator)null);
   }

   public DoubleArrayPriorityQueue(DoubleComparator var1) {
      this(0, var1);
   }

   public DoubleArrayPriorityQueue() {
      this(0, (DoubleComparator)null);
   }

   public DoubleArrayPriorityQueue(double[] var1, int var2, DoubleComparator var3) {
      this(var3);
      this.array = var1;
      this.size = var2;
   }

   public DoubleArrayPriorityQueue(double[] var1, DoubleComparator var2) {
      this(var1, var1.length, var2);
   }

   public DoubleArrayPriorityQueue(double[] var1, int var2) {
      this(var1, var2, (DoubleComparator)null);
   }

   public DoubleArrayPriorityQueue(double[] var1) {
      this(var1, var1.length);
   }

   private int findFirst() {
      if (this.firstIndexValid) {
         return this.firstIndex;
      } else {
         this.firstIndexValid = true;
         int var1 = this.size;
         --var1;
         int var2 = var1;
         double var3 = this.array[var1];
         if (this.c == null) {
            while(var1-- != 0) {
               if (Double.compare(this.array[var1], var3) < 0) {
                  var2 = var1;
                  var3 = this.array[var1];
               }
            }
         } else {
            while(var1-- != 0) {
               if (this.c.compare(this.array[var1], var3) < 0) {
                  var2 = var1;
                  var3 = this.array[var1];
               }
            }
         }

         return this.firstIndex = var2;
      }
   }

   private void ensureNonEmpty() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      }
   }

   public void enqueue(double var1) {
      if (this.size == this.array.length) {
         this.array = DoubleArrays.grow(this.array, this.size + 1);
      }

      if (this.firstIndexValid) {
         if (this.c == null) {
            if (Double.compare(var1, this.array[this.firstIndex]) < 0) {
               this.firstIndex = this.size;
            }
         } else if (this.c.compare(var1, this.array[this.firstIndex]) < 0) {
            this.firstIndex = this.size;
         }
      } else {
         this.firstIndexValid = false;
      }

      this.array[this.size++] = var1;
   }

   public double dequeueDouble() {
      this.ensureNonEmpty();
      int var1 = this.findFirst();
      double var2 = this.array[var1];
      System.arraycopy(this.array, var1 + 1, this.array, var1, --this.size - var1);
      this.firstIndexValid = false;
      return var2;
   }

   public double firstDouble() {
      this.ensureNonEmpty();
      return this.array[this.findFirst()];
   }

   public void changed() {
      this.ensureNonEmpty();
      this.firstIndexValid = false;
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      this.size = 0;
      this.firstIndexValid = false;
   }

   public void trim() {
      this.array = DoubleArrays.trim(this.array, this.size);
   }

   public DoubleComparator comparator() {
      return this.c;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.array.length);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeDouble(this.array[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.array = new double[var1.readInt()];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.array[var2] = var1.readDouble();
      }

   }
}
