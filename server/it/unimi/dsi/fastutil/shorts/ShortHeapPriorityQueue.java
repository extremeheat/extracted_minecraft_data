package it.unimi.dsi.fastutil.shorts;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ShortHeapPriorityQueue implements ShortPriorityQueue, Serializable {
   private static final long serialVersionUID = 1L;
   protected transient short[] heap;
   protected int size;
   protected ShortComparator c;

   public ShortHeapPriorityQueue(int var1, ShortComparator var2) {
      super();
      this.heap = ShortArrays.EMPTY_ARRAY;
      if (var1 > 0) {
         this.heap = new short[var1];
      }

      this.c = var2;
   }

   public ShortHeapPriorityQueue(int var1) {
      this(var1, (ShortComparator)null);
   }

   public ShortHeapPriorityQueue(ShortComparator var1) {
      this(0, var1);
   }

   public ShortHeapPriorityQueue() {
      this(0, (ShortComparator)null);
   }

   public ShortHeapPriorityQueue(short[] var1, int var2, ShortComparator var3) {
      this(var3);
      this.heap = var1;
      this.size = var2;
      ShortHeaps.makeHeap(var1, var2, var3);
   }

   public ShortHeapPriorityQueue(short[] var1, ShortComparator var2) {
      this(var1, var1.length, var2);
   }

   public ShortHeapPriorityQueue(short[] var1, int var2) {
      this(var1, var2, (ShortComparator)null);
   }

   public ShortHeapPriorityQueue(short[] var1) {
      this(var1, var1.length);
   }

   public ShortHeapPriorityQueue(ShortCollection var1, ShortComparator var2) {
      this(var1.toShortArray(), var2);
   }

   public ShortHeapPriorityQueue(ShortCollection var1) {
      this((ShortCollection)var1, (ShortComparator)null);
   }

   public ShortHeapPriorityQueue(Collection<? extends Short> var1, ShortComparator var2) {
      this(var1.size(), var2);
      Iterator var3 = var1.iterator();
      int var4 = var1.size();

      for(int var5 = 0; var5 < var4; ++var5) {
         this.heap[var5] = (Short)var3.next();
      }

   }

   public ShortHeapPriorityQueue(Collection<? extends Short> var1) {
      this((Collection)var1, (ShortComparator)null);
   }

   public void enqueue(short var1) {
      if (this.size == this.heap.length) {
         this.heap = ShortArrays.grow(this.heap, this.size + 1);
      }

      this.heap[this.size++] = var1;
      ShortHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
   }

   public short dequeueShort() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         short var1 = this.heap[0];
         this.heap[0] = this.heap[--this.size];
         if (this.size != 0) {
            ShortHeaps.downHeap(this.heap, this.size, 0, this.c);
         }

         return var1;
      }
   }

   public short firstShort() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.heap[0];
      }
   }

   public void changed() {
      ShortHeaps.downHeap(this.heap, this.size, 0, this.c);
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      this.size = 0;
   }

   public void trim() {
      this.heap = ShortArrays.trim(this.heap, this.size);
   }

   public ShortComparator comparator() {
      return this.c;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.heap.length);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeShort(this.heap[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.heap = new short[var1.readInt()];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.heap[var2] = var1.readShort();
      }

   }
}
