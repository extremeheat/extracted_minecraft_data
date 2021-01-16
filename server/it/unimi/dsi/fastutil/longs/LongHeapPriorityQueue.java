package it.unimi.dsi.fastutil.longs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LongHeapPriorityQueue implements LongPriorityQueue, Serializable {
   private static final long serialVersionUID = 1L;
   protected transient long[] heap;
   protected int size;
   protected LongComparator c;

   public LongHeapPriorityQueue(int var1, LongComparator var2) {
      super();
      this.heap = LongArrays.EMPTY_ARRAY;
      if (var1 > 0) {
         this.heap = new long[var1];
      }

      this.c = var2;
   }

   public LongHeapPriorityQueue(int var1) {
      this(var1, (LongComparator)null);
   }

   public LongHeapPriorityQueue(LongComparator var1) {
      this(0, var1);
   }

   public LongHeapPriorityQueue() {
      this(0, (LongComparator)null);
   }

   public LongHeapPriorityQueue(long[] var1, int var2, LongComparator var3) {
      this(var3);
      this.heap = var1;
      this.size = var2;
      LongHeaps.makeHeap(var1, var2, var3);
   }

   public LongHeapPriorityQueue(long[] var1, LongComparator var2) {
      this(var1, var1.length, var2);
   }

   public LongHeapPriorityQueue(long[] var1, int var2) {
      this(var1, var2, (LongComparator)null);
   }

   public LongHeapPriorityQueue(long[] var1) {
      this(var1, var1.length);
   }

   public LongHeapPriorityQueue(LongCollection var1, LongComparator var2) {
      this(var1.toLongArray(), var2);
   }

   public LongHeapPriorityQueue(LongCollection var1) {
      this((LongCollection)var1, (LongComparator)null);
   }

   public LongHeapPriorityQueue(Collection<? extends Long> var1, LongComparator var2) {
      this(var1.size(), var2);
      Iterator var3 = var1.iterator();
      int var4 = var1.size();

      for(int var5 = 0; var5 < var4; ++var5) {
         this.heap[var5] = (Long)var3.next();
      }

   }

   public LongHeapPriorityQueue(Collection<? extends Long> var1) {
      this((Collection)var1, (LongComparator)null);
   }

   public void enqueue(long var1) {
      if (this.size == this.heap.length) {
         this.heap = LongArrays.grow(this.heap, this.size + 1);
      }

      this.heap[this.size++] = var1;
      LongHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
   }

   public long dequeueLong() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         long var1 = this.heap[0];
         this.heap[0] = this.heap[--this.size];
         if (this.size != 0) {
            LongHeaps.downHeap(this.heap, this.size, 0, this.c);
         }

         return var1;
      }
   }

   public long firstLong() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.heap[0];
      }
   }

   public void changed() {
      LongHeaps.downHeap(this.heap, this.size, 0, this.c);
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      this.size = 0;
   }

   public void trim() {
      this.heap = LongArrays.trim(this.heap, this.size);
   }

   public LongComparator comparator() {
      return this.c;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.heap.length);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeLong(this.heap[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.heap = new long[var1.readInt()];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.heap[var2] = var1.readLong();
      }

   }
}
