package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class ShortHeapIndirectPriorityQueue extends ShortHeapSemiIndirectPriorityQueue {
   protected final int[] inv;

   public ShortHeapIndirectPriorityQueue(short[] var1, int var2, ShortComparator var3) {
      super(var1, var2, var3);
      if (var2 > 0) {
         this.heap = new int[var2];
      }

      this.c = var3;
      this.inv = new int[var1.length];
      Arrays.fill(this.inv, -1);
   }

   public ShortHeapIndirectPriorityQueue(short[] var1, int var2) {
      this(var1, var2, (ShortComparator)null);
   }

   public ShortHeapIndirectPriorityQueue(short[] var1, ShortComparator var2) {
      this(var1, var1.length, var2);
   }

   public ShortHeapIndirectPriorityQueue(short[] var1) {
      this(var1, var1.length, (ShortComparator)null);
   }

   public ShortHeapIndirectPriorityQueue(short[] var1, int[] var2, int var3, ShortComparator var4) {
      this(var1, 0, var4);
      this.heap = var2;
      this.size = var3;

      for(int var5 = var3; var5-- != 0; this.inv[var2[var5]] = var5) {
         if (this.inv[var2[var5]] != -1) {
            throw new IllegalArgumentException("Index " + var2[var5] + " appears twice in the heap");
         }
      }

      ShortIndirectHeaps.makeHeap(var1, var2, this.inv, var3, var4);
   }

   public ShortHeapIndirectPriorityQueue(short[] var1, int[] var2, ShortComparator var3) {
      this(var1, var2, var2.length, var3);
   }

   public ShortHeapIndirectPriorityQueue(short[] var1, int[] var2, int var3) {
      this(var1, var2, var3, (ShortComparator)null);
   }

   public ShortHeapIndirectPriorityQueue(short[] var1, int[] var2) {
      this(var1, var2, var2.length);
   }

   public void enqueue(int var1) {
      if (this.inv[var1] >= 0) {
         throw new IllegalArgumentException("Index " + var1 + " belongs to the queue");
      } else {
         if (this.size == this.heap.length) {
            this.heap = IntArrays.grow(this.heap, this.size + 1);
         }

         this.inv[this.heap[this.size] = var1] = this.size++;
         ShortIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, this.size - 1, this.c);
      }
   }

   public boolean contains(int var1) {
      return this.inv[var1] >= 0;
   }

   public int dequeue() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         int var1 = this.heap[0];
         if (--this.size != 0) {
            this.inv[this.heap[0] = this.heap[this.size]] = 0;
         }

         this.inv[var1] = -1;
         if (this.size != 0) {
            ShortIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, 0, this.c);
         }

         return var1;
      }
   }

   public void changed() {
      ShortIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, 0, this.c);
   }

   public void changed(int var1) {
      int var2 = this.inv[var1];
      if (var2 < 0) {
         throw new IllegalArgumentException("Index " + var1 + " does not belong to the queue");
      } else {
         int var3 = ShortIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, var2, this.c);
         ShortIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, var3, this.c);
      }
   }

   public void allChanged() {
      ShortIndirectHeaps.makeHeap(this.refArray, this.heap, this.inv, this.size, this.c);
   }

   public boolean remove(int var1) {
      int var2 = this.inv[var1];
      if (var2 < 0) {
         return false;
      } else {
         this.inv[var1] = -1;
         if (var2 < --this.size) {
            this.inv[this.heap[var2] = this.heap[this.size]] = var2;
            int var3 = ShortIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, var2, this.c);
            ShortIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, var3, this.c);
         }

         return true;
      }
   }

   public void clear() {
      this.size = 0;
      Arrays.fill(this.inv, -1);
   }
}
