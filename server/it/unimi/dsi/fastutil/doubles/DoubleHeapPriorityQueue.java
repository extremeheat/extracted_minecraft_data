package it.unimi.dsi.fastutil.doubles;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoubleHeapPriorityQueue implements DoublePriorityQueue, Serializable {
   private static final long serialVersionUID = 1L;
   protected transient double[] heap;
   protected int size;
   protected DoubleComparator c;

   public DoubleHeapPriorityQueue(int var1, DoubleComparator var2) {
      super();
      this.heap = DoubleArrays.EMPTY_ARRAY;
      if (var1 > 0) {
         this.heap = new double[var1];
      }

      this.c = var2;
   }

   public DoubleHeapPriorityQueue(int var1) {
      this(var1, (DoubleComparator)null);
   }

   public DoubleHeapPriorityQueue(DoubleComparator var1) {
      this(0, var1);
   }

   public DoubleHeapPriorityQueue() {
      this(0, (DoubleComparator)null);
   }

   public DoubleHeapPriorityQueue(double[] var1, int var2, DoubleComparator var3) {
      this(var3);
      this.heap = var1;
      this.size = var2;
      DoubleHeaps.makeHeap(var1, var2, var3);
   }

   public DoubleHeapPriorityQueue(double[] var1, DoubleComparator var2) {
      this(var1, var1.length, var2);
   }

   public DoubleHeapPriorityQueue(double[] var1, int var2) {
      this(var1, var2, (DoubleComparator)null);
   }

   public DoubleHeapPriorityQueue(double[] var1) {
      this(var1, var1.length);
   }

   public DoubleHeapPriorityQueue(DoubleCollection var1, DoubleComparator var2) {
      this(var1.toDoubleArray(), var2);
   }

   public DoubleHeapPriorityQueue(DoubleCollection var1) {
      this((DoubleCollection)var1, (DoubleComparator)null);
   }

   public DoubleHeapPriorityQueue(Collection<? extends Double> var1, DoubleComparator var2) {
      this(var1.size(), var2);
      Iterator var3 = var1.iterator();
      int var4 = var1.size();

      for(int var5 = 0; var5 < var4; ++var5) {
         this.heap[var5] = (Double)var3.next();
      }

   }

   public DoubleHeapPriorityQueue(Collection<? extends Double> var1) {
      this((Collection)var1, (DoubleComparator)null);
   }

   public void enqueue(double var1) {
      if (this.size == this.heap.length) {
         this.heap = DoubleArrays.grow(this.heap, this.size + 1);
      }

      this.heap[this.size++] = var1;
      DoubleHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
   }

   public double dequeueDouble() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         double var1 = this.heap[0];
         this.heap[0] = this.heap[--this.size];
         if (this.size != 0) {
            DoubleHeaps.downHeap(this.heap, this.size, 0, this.c);
         }

         return var1;
      }
   }

   public double firstDouble() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.heap[0];
      }
   }

   public void changed() {
      DoubleHeaps.downHeap(this.heap, this.size, 0, this.c);
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      this.size = 0;
   }

   public void trim() {
      this.heap = DoubleArrays.trim(this.heap, this.size);
   }

   public DoubleComparator comparator() {
      return this.c;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.heap.length);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeDouble(this.heap[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.heap = new double[var1.readInt()];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.heap[var2] = var1.readDouble();
      }

   }
}
