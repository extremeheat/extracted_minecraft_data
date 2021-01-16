package it.unimi.dsi.fastutil.floats;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FloatHeapPriorityQueue implements FloatPriorityQueue, Serializable {
   private static final long serialVersionUID = 1L;
   protected transient float[] heap;
   protected int size;
   protected FloatComparator c;

   public FloatHeapPriorityQueue(int var1, FloatComparator var2) {
      super();
      this.heap = FloatArrays.EMPTY_ARRAY;
      if (var1 > 0) {
         this.heap = new float[var1];
      }

      this.c = var2;
   }

   public FloatHeapPriorityQueue(int var1) {
      this(var1, (FloatComparator)null);
   }

   public FloatHeapPriorityQueue(FloatComparator var1) {
      this(0, var1);
   }

   public FloatHeapPriorityQueue() {
      this(0, (FloatComparator)null);
   }

   public FloatHeapPriorityQueue(float[] var1, int var2, FloatComparator var3) {
      this(var3);
      this.heap = var1;
      this.size = var2;
      FloatHeaps.makeHeap(var1, var2, var3);
   }

   public FloatHeapPriorityQueue(float[] var1, FloatComparator var2) {
      this(var1, var1.length, var2);
   }

   public FloatHeapPriorityQueue(float[] var1, int var2) {
      this(var1, var2, (FloatComparator)null);
   }

   public FloatHeapPriorityQueue(float[] var1) {
      this(var1, var1.length);
   }

   public FloatHeapPriorityQueue(FloatCollection var1, FloatComparator var2) {
      this(var1.toFloatArray(), var2);
   }

   public FloatHeapPriorityQueue(FloatCollection var1) {
      this((FloatCollection)var1, (FloatComparator)null);
   }

   public FloatHeapPriorityQueue(Collection<? extends Float> var1, FloatComparator var2) {
      this(var1.size(), var2);
      Iterator var3 = var1.iterator();
      int var4 = var1.size();

      for(int var5 = 0; var5 < var4; ++var5) {
         this.heap[var5] = (Float)var3.next();
      }

   }

   public FloatHeapPriorityQueue(Collection<? extends Float> var1) {
      this((Collection)var1, (FloatComparator)null);
   }

   public void enqueue(float var1) {
      if (this.size == this.heap.length) {
         this.heap = FloatArrays.grow(this.heap, this.size + 1);
      }

      this.heap[this.size++] = var1;
      FloatHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
   }

   public float dequeueFloat() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         float var1 = this.heap[0];
         this.heap[0] = this.heap[--this.size];
         if (this.size != 0) {
            FloatHeaps.downHeap(this.heap, this.size, 0, this.c);
         }

         return var1;
      }
   }

   public float firstFloat() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.heap[0];
      }
   }

   public void changed() {
      FloatHeaps.downHeap(this.heap, this.size, 0, this.c);
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      this.size = 0;
   }

   public void trim() {
      this.heap = FloatArrays.trim(this.heap, this.size);
   }

   public FloatComparator comparator() {
      return this.c;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.heap.length);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeFloat(this.heap[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.heap = new float[var1.readInt()];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.heap[var2] = var1.readFloat();
      }

   }
}
