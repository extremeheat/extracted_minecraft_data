package it.unimi.dsi.fastutil.ints;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntHeapPriorityQueue implements IntPriorityQueue, Serializable {
   private static final long serialVersionUID = 1L;
   protected transient int[] heap;
   protected int size;
   protected IntComparator c;

   public IntHeapPriorityQueue(int var1, IntComparator var2) {
      super();
      this.heap = IntArrays.EMPTY_ARRAY;
      if (var1 > 0) {
         this.heap = new int[var1];
      }

      this.c = var2;
   }

   public IntHeapPriorityQueue(int var1) {
      this(var1, (IntComparator)null);
   }

   public IntHeapPriorityQueue(IntComparator var1) {
      this(0, var1);
   }

   public IntHeapPriorityQueue() {
      this(0, (IntComparator)null);
   }

   public IntHeapPriorityQueue(int[] var1, int var2, IntComparator var3) {
      this(var3);
      this.heap = var1;
      this.size = var2;
      IntHeaps.makeHeap(var1, var2, var3);
   }

   public IntHeapPriorityQueue(int[] var1, IntComparator var2) {
      this(var1, var1.length, var2);
   }

   public IntHeapPriorityQueue(int[] var1, int var2) {
      this(var1, var2, (IntComparator)null);
   }

   public IntHeapPriorityQueue(int[] var1) {
      this(var1, var1.length);
   }

   public IntHeapPriorityQueue(IntCollection var1, IntComparator var2) {
      this(var1.toIntArray(), var2);
   }

   public IntHeapPriorityQueue(IntCollection var1) {
      this((IntCollection)var1, (IntComparator)null);
   }

   public IntHeapPriorityQueue(Collection<? extends Integer> var1, IntComparator var2) {
      this(var1.size(), var2);
      Iterator var3 = var1.iterator();
      int var4 = var1.size();

      for(int var5 = 0; var5 < var4; ++var5) {
         this.heap[var5] = (Integer)var3.next();
      }

   }

   public IntHeapPriorityQueue(Collection<? extends Integer> var1) {
      this((Collection)var1, (IntComparator)null);
   }

   public void enqueue(int var1) {
      if (this.size == this.heap.length) {
         this.heap = IntArrays.grow(this.heap, this.size + 1);
      }

      this.heap[this.size++] = var1;
      IntHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
   }

   public int dequeueInt() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         int var1 = this.heap[0];
         this.heap[0] = this.heap[--this.size];
         if (this.size != 0) {
            IntHeaps.downHeap(this.heap, this.size, 0, this.c);
         }

         return var1;
      }
   }

   public int firstInt() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.heap[0];
      }
   }

   public void changed() {
      IntHeaps.downHeap(this.heap, this.size, 0, this.c);
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

   public IntComparator comparator() {
      return this.c;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.heap.length);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeInt(this.heap[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.heap = new int[var1.readInt()];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.heap[var2] = var1.readInt();
      }

   }
}
