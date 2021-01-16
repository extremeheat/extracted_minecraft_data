package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.NoSuchElementException;

public class FloatHeapSemiIndirectPriorityQueue implements FloatIndirectPriorityQueue {
   protected final float[] refArray;
   protected int[] heap;
   protected int size;
   protected FloatComparator c;

   public FloatHeapSemiIndirectPriorityQueue(float[] var1, int var2, FloatComparator var3) {
      super();
      this.heap = IntArrays.EMPTY_ARRAY;
      if (var2 > 0) {
         this.heap = new int[var2];
      }

      this.refArray = var1;
      this.c = var3;
   }

   public FloatHeapSemiIndirectPriorityQueue(float[] var1, int var2) {
      this(var1, var2, (FloatComparator)null);
   }

   public FloatHeapSemiIndirectPriorityQueue(float[] var1, FloatComparator var2) {
      this(var1, var1.length, var2);
   }

   public FloatHeapSemiIndirectPriorityQueue(float[] var1) {
      this(var1, var1.length, (FloatComparator)null);
   }

   public FloatHeapSemiIndirectPriorityQueue(float[] var1, int[] var2, int var3, FloatComparator var4) {
      this(var1, 0, var4);
      this.heap = var2;
      this.size = var3;
      FloatSemiIndirectHeaps.makeHeap(var1, var2, var3, var4);
   }

   public FloatHeapSemiIndirectPriorityQueue(float[] var1, int[] var2, FloatComparator var3) {
      this(var1, var2, var2.length, var3);
   }

   public FloatHeapSemiIndirectPriorityQueue(float[] var1, int[] var2, int var3) {
      this(var1, var2, var3, (FloatComparator)null);
   }

   public FloatHeapSemiIndirectPriorityQueue(float[] var1, int[] var2) {
      this(var1, var2, var2.length);
   }

   protected void ensureElement(int var1) {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is negative");
      } else if (var1 >= this.refArray.length) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is larger than or equal to reference array size (" + this.refArray.length + ")");
      }
   }

   public void enqueue(int var1) {
      this.ensureElement(var1);
      if (this.size == this.heap.length) {
         this.heap = IntArrays.grow(this.heap, this.size + 1);
      }

      this.heap[this.size++] = var1;
      FloatSemiIndirectHeaps.upHeap(this.refArray, this.heap, this.size, this.size - 1, this.c);
   }

   public int dequeue() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         int var1 = this.heap[0];
         this.heap[0] = this.heap[--this.size];
         if (this.size != 0) {
            FloatSemiIndirectHeaps.downHeap(this.refArray, this.heap, this.size, 0, this.c);
         }

         return var1;
      }
   }

   public int first() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.heap[0];
      }
   }

   public void changed() {
      FloatSemiIndirectHeaps.downHeap(this.refArray, this.heap, this.size, 0, this.c);
   }

   public void allChanged() {
      FloatSemiIndirectHeaps.makeHeap(this.refArray, this.heap, this.size, this.c);
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      this.size = 0;
   }

   public void trim() {
      this.heap = IntArrays.trim(this.heap, this.size);
   }

   public FloatComparator comparator() {
      return this.c;
   }

   public int front(int[] var1) {
      return this.c == null ? FloatSemiIndirectHeaps.front(this.refArray, this.heap, this.size, var1) : FloatSemiIndirectHeaps.front(this.refArray, this.heap, this.size, var1, this.c);
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("[");

      for(int var2 = 0; var2 < this.size; ++var2) {
         if (var2 != 0) {
            var1.append(", ");
         }

         var1.append(this.refArray[this.heap[var2]]);
      }

      var1.append("]");
      return var1.toString();
   }
}
