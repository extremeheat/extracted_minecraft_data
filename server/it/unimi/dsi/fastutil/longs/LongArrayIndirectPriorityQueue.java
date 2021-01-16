package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.NoSuchElementException;

public class LongArrayIndirectPriorityQueue implements LongIndirectPriorityQueue {
   protected long[] refArray;
   protected int[] array;
   protected int size;
   protected LongComparator c;
   protected int firstIndex;
   protected boolean firstIndexValid;

   public LongArrayIndirectPriorityQueue(long[] var1, int var2, LongComparator var3) {
      super();
      this.array = IntArrays.EMPTY_ARRAY;
      if (var2 > 0) {
         this.array = new int[var2];
      }

      this.refArray = var1;
      this.c = var3;
   }

   public LongArrayIndirectPriorityQueue(long[] var1, int var2) {
      this(var1, var2, (LongComparator)null);
   }

   public LongArrayIndirectPriorityQueue(long[] var1, LongComparator var2) {
      this(var1, var1.length, var2);
   }

   public LongArrayIndirectPriorityQueue(long[] var1) {
      this(var1, var1.length, (LongComparator)null);
   }

   public LongArrayIndirectPriorityQueue(long[] var1, int[] var2, int var3, LongComparator var4) {
      this(var1, 0, var4);
      this.array = var2;
      this.size = var3;
   }

   public LongArrayIndirectPriorityQueue(long[] var1, int[] var2, LongComparator var3) {
      this(var1, var2, var2.length, var3);
   }

   public LongArrayIndirectPriorityQueue(long[] var1, int[] var2, int var3) {
      this(var1, var2, var3, (LongComparator)null);
   }

   public LongArrayIndirectPriorityQueue(long[] var1, int[] var2) {
      this(var1, var2, var2.length);
   }

   private int findFirst() {
      if (this.firstIndexValid) {
         return this.firstIndex;
      } else {
         this.firstIndexValid = true;
         int var1 = this.size;
         --var1;
         int var2 = var1;
         long var3 = this.refArray[this.array[var1]];
         if (this.c == null) {
            while(var1-- != 0) {
               if (this.refArray[this.array[var1]] < var3) {
                  var2 = var1;
                  var3 = this.refArray[this.array[var1]];
               }
            }
         } else {
            while(var1-- != 0) {
               if (this.c.compare(this.refArray[this.array[var1]], var3) < 0) {
                  var2 = var1;
                  var3 = this.refArray[this.array[var1]];
               }
            }
         }

         return this.firstIndex = var2;
      }
   }

   private int findLast() {
      int var1 = this.size;
      --var1;
      int var2 = var1;
      long var3 = this.refArray[this.array[var1]];
      if (this.c == null) {
         while(var1-- != 0) {
            if (var3 < this.refArray[this.array[var1]]) {
               var2 = var1;
               var3 = this.refArray[this.array[var1]];
            }
         }
      } else {
         while(var1-- != 0) {
            if (this.c.compare(var3, this.refArray[this.array[var1]]) < 0) {
               var2 = var1;
               var3 = this.refArray[this.array[var1]];
            }
         }
      }

      return var2;
   }

   protected final void ensureNonEmpty() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      }
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
      if (this.size == this.array.length) {
         this.array = IntArrays.grow(this.array, this.size + 1);
      }

      if (this.firstIndexValid) {
         if (this.c == null) {
            if (this.refArray[var1] < this.refArray[this.array[this.firstIndex]]) {
               this.firstIndex = this.size;
            }
         } else if (this.c.compare(this.refArray[var1], this.refArray[this.array[this.firstIndex]]) < 0) {
            this.firstIndex = this.size;
         }
      } else {
         this.firstIndexValid = false;
      }

      this.array[this.size++] = var1;
   }

   public int dequeue() {
      this.ensureNonEmpty();
      int var1 = this.findFirst();
      int var2 = this.array[var1];
      if (--this.size != 0) {
         System.arraycopy(this.array, var1 + 1, this.array, var1, this.size - var1);
      }

      this.firstIndexValid = false;
      return var2;
   }

   public int first() {
      this.ensureNonEmpty();
      return this.array[this.findFirst()];
   }

   public int last() {
      this.ensureNonEmpty();
      return this.array[this.findLast()];
   }

   public void changed() {
      this.ensureNonEmpty();
      this.firstIndexValid = false;
   }

   public void changed(int var1) {
      this.ensureElement(var1);
      if (var1 == this.firstIndex) {
         this.firstIndexValid = false;
      }

   }

   public void allChanged() {
      this.firstIndexValid = false;
   }

   public boolean remove(int var1) {
      this.ensureElement(var1);
      int[] var2 = this.array;
      int var3 = this.size;

      while(var3-- != 0 && var2[var3] != var1) {
      }

      if (var3 < 0) {
         return false;
      } else {
         this.firstIndexValid = false;
         if (--this.size != 0) {
            System.arraycopy(var2, var3 + 1, var2, var3, this.size - var3);
         }

         return true;
      }
   }

   public int front(int[] var1) {
      long var2 = this.refArray[this.array[this.findFirst()]];
      int var4 = this.size;
      int var5 = 0;

      while(var4-- != 0) {
         if (var2 == this.refArray[this.array[var4]]) {
            var1[var5++] = this.array[var4];
         }
      }

      return var5;
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      this.size = 0;
      this.firstIndexValid = false;
   }

   public void trim() {
      this.array = IntArrays.trim(this.array, this.size);
   }

   public LongComparator comparator() {
      return this.c;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("[");

      for(int var2 = 0; var2 < this.size; ++var2) {
         if (var2 != 0) {
            var1.append(", ");
         }

         var1.append(this.refArray[this.array[var2]]);
      }

      var1.append("]");
      return var1.toString();
   }
}
