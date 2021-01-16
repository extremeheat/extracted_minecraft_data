package it.unimi.dsi.fastutil.bytes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ByteHeapPriorityQueue implements BytePriorityQueue, Serializable {
   private static final long serialVersionUID = 1L;
   protected transient byte[] heap;
   protected int size;
   protected ByteComparator c;

   public ByteHeapPriorityQueue(int var1, ByteComparator var2) {
      super();
      this.heap = ByteArrays.EMPTY_ARRAY;
      if (var1 > 0) {
         this.heap = new byte[var1];
      }

      this.c = var2;
   }

   public ByteHeapPriorityQueue(int var1) {
      this(var1, (ByteComparator)null);
   }

   public ByteHeapPriorityQueue(ByteComparator var1) {
      this(0, var1);
   }

   public ByteHeapPriorityQueue() {
      this(0, (ByteComparator)null);
   }

   public ByteHeapPriorityQueue(byte[] var1, int var2, ByteComparator var3) {
      this(var3);
      this.heap = var1;
      this.size = var2;
      ByteHeaps.makeHeap(var1, var2, var3);
   }

   public ByteHeapPriorityQueue(byte[] var1, ByteComparator var2) {
      this(var1, var1.length, var2);
   }

   public ByteHeapPriorityQueue(byte[] var1, int var2) {
      this(var1, var2, (ByteComparator)null);
   }

   public ByteHeapPriorityQueue(byte[] var1) {
      this(var1, var1.length);
   }

   public ByteHeapPriorityQueue(ByteCollection var1, ByteComparator var2) {
      this(var1.toByteArray(), var2);
   }

   public ByteHeapPriorityQueue(ByteCollection var1) {
      this((ByteCollection)var1, (ByteComparator)null);
   }

   public ByteHeapPriorityQueue(Collection<? extends Byte> var1, ByteComparator var2) {
      this(var1.size(), var2);
      Iterator var3 = var1.iterator();
      int var4 = var1.size();

      for(int var5 = 0; var5 < var4; ++var5) {
         this.heap[var5] = (Byte)var3.next();
      }

   }

   public ByteHeapPriorityQueue(Collection<? extends Byte> var1) {
      this((Collection)var1, (ByteComparator)null);
   }

   public void enqueue(byte var1) {
      if (this.size == this.heap.length) {
         this.heap = ByteArrays.grow(this.heap, this.size + 1);
      }

      this.heap[this.size++] = var1;
      ByteHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
   }

   public byte dequeueByte() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         byte var1 = this.heap[0];
         this.heap[0] = this.heap[--this.size];
         if (this.size != 0) {
            ByteHeaps.downHeap(this.heap, this.size, 0, this.c);
         }

         return var1;
      }
   }

   public byte firstByte() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.heap[0];
      }
   }

   public void changed() {
      ByteHeaps.downHeap(this.heap, this.size, 0, this.c);
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      this.size = 0;
   }

   public void trim() {
      this.heap = ByteArrays.trim(this.heap, this.size);
   }

   public ByteComparator comparator() {
      return this.c;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.heap.length);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeByte(this.heap[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.heap = new byte[var1.readInt()];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.heap[var2] = var1.readByte();
      }

   }
}
