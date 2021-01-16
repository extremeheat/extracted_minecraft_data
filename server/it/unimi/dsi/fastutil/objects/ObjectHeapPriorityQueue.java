package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.PriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class ObjectHeapPriorityQueue<K> implements PriorityQueue<K>, Serializable {
   private static final long serialVersionUID = 1L;
   protected transient K[] heap;
   protected int size;
   protected Comparator<? super K> c;

   public ObjectHeapPriorityQueue(int var1, Comparator<? super K> var2) {
      super();
      this.heap = ObjectArrays.EMPTY_ARRAY;
      if (var1 > 0) {
         this.heap = new Object[var1];
      }

      this.c = var2;
   }

   public ObjectHeapPriorityQueue(int var1) {
      this(var1, (Comparator)null);
   }

   public ObjectHeapPriorityQueue(Comparator<? super K> var1) {
      this(0, var1);
   }

   public ObjectHeapPriorityQueue() {
      this(0, (Comparator)null);
   }

   public ObjectHeapPriorityQueue(K[] var1, int var2, Comparator<? super K> var3) {
      this(var3);
      this.heap = var1;
      this.size = var2;
      ObjectHeaps.makeHeap(var1, var2, var3);
   }

   public ObjectHeapPriorityQueue(K[] var1, Comparator<? super K> var2) {
      this(var1, var1.length, var2);
   }

   public ObjectHeapPriorityQueue(K[] var1, int var2) {
      this(var1, var2, (Comparator)null);
   }

   public ObjectHeapPriorityQueue(K[] var1) {
      this(var1, var1.length);
   }

   public ObjectHeapPriorityQueue(Collection<? extends K> var1, Comparator<? super K> var2) {
      this(var1.toArray(), var2);
   }

   public ObjectHeapPriorityQueue(Collection<? extends K> var1) {
      this((Collection)var1, (Comparator)null);
   }

   public void enqueue(K var1) {
      if (this.size == this.heap.length) {
         this.heap = ObjectArrays.grow(this.heap, this.size + 1);
      }

      this.heap[this.size++] = var1;
      ObjectHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
   }

   public K dequeue() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         Object var1 = this.heap[0];
         this.heap[0] = this.heap[--this.size];
         this.heap[this.size] = null;
         if (this.size != 0) {
            ObjectHeaps.downHeap(this.heap, this.size, 0, this.c);
         }

         return var1;
      }
   }

   public K first() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.heap[0];
      }
   }

   public void changed() {
      ObjectHeaps.downHeap(this.heap, this.size, 0, this.c);
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      Arrays.fill(this.heap, 0, this.size, (Object)null);
      this.size = 0;
   }

   public void trim() {
      this.heap = ObjectArrays.trim(this.heap, this.size);
   }

   public Comparator<? super K> comparator() {
      return this.c;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.heap.length);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeObject(this.heap[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.heap = new Object[var1.readInt()];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.heap[var2] = var1.readObject();
      }

   }
}
