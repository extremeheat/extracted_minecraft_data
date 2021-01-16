package it.unimi.dsi.fastutil.chars;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CharHeapPriorityQueue implements CharPriorityQueue, Serializable {
   private static final long serialVersionUID = 1L;
   protected transient char[] heap;
   protected int size;
   protected CharComparator c;

   public CharHeapPriorityQueue(int var1, CharComparator var2) {
      super();
      this.heap = CharArrays.EMPTY_ARRAY;
      if (var1 > 0) {
         this.heap = new char[var1];
      }

      this.c = var2;
   }

   public CharHeapPriorityQueue(int var1) {
      this(var1, (CharComparator)null);
   }

   public CharHeapPriorityQueue(CharComparator var1) {
      this(0, var1);
   }

   public CharHeapPriorityQueue() {
      this(0, (CharComparator)null);
   }

   public CharHeapPriorityQueue(char[] var1, int var2, CharComparator var3) {
      this(var3);
      this.heap = var1;
      this.size = var2;
      CharHeaps.makeHeap(var1, var2, var3);
   }

   public CharHeapPriorityQueue(char[] var1, CharComparator var2) {
      this(var1, var1.length, var2);
   }

   public CharHeapPriorityQueue(char[] var1, int var2) {
      this(var1, var2, (CharComparator)null);
   }

   public CharHeapPriorityQueue(char[] var1) {
      this(var1, var1.length);
   }

   public CharHeapPriorityQueue(CharCollection var1, CharComparator var2) {
      this(var1.toCharArray(), var2);
   }

   public CharHeapPriorityQueue(CharCollection var1) {
      this((CharCollection)var1, (CharComparator)null);
   }

   public CharHeapPriorityQueue(Collection<? extends Character> var1, CharComparator var2) {
      this(var1.size(), var2);
      Iterator var3 = var1.iterator();
      int var4 = var1.size();

      for(int var5 = 0; var5 < var4; ++var5) {
         this.heap[var5] = (Character)var3.next();
      }

   }

   public CharHeapPriorityQueue(Collection<? extends Character> var1) {
      this((Collection)var1, (CharComparator)null);
   }

   public void enqueue(char var1) {
      if (this.size == this.heap.length) {
         this.heap = CharArrays.grow(this.heap, this.size + 1);
      }

      this.heap[this.size++] = var1;
      CharHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
   }

   public char dequeueChar() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         char var1 = this.heap[0];
         this.heap[0] = this.heap[--this.size];
         if (this.size != 0) {
            CharHeaps.downHeap(this.heap, this.size, 0, this.c);
         }

         return var1;
      }
   }

   public char firstChar() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.heap[0];
      }
   }

   public void changed() {
      CharHeaps.downHeap(this.heap, this.size, 0, this.c);
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      this.size = 0;
   }

   public void trim() {
      this.heap = CharArrays.trim(this.heap, this.size);
   }

   public CharComparator comparator() {
      return this.c;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.heap.length);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeChar(this.heap[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.heap = new char[var1.readInt()];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.heap[var2] = var1.readChar();
      }

   }
}
