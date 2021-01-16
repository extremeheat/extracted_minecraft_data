package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.NoSuchElementException;

public class LongArrayFIFOQueue implements LongPriorityQueue, Serializable {
   private static final long serialVersionUID = 0L;
   public static final int INITIAL_CAPACITY = 4;
   protected transient long[] array;
   protected transient int length;
   protected transient int start;
   protected transient int end;

   public LongArrayFIFOQueue(int var1) {
      super();
      if (var1 < 0) {
         throw new IllegalArgumentException("Initial capacity (" + var1 + ") is negative");
      } else {
         this.array = new long[Math.max(1, var1)];
         this.length = this.array.length;
      }
   }

   public LongArrayFIFOQueue() {
      this(4);
   }

   public LongComparator comparator() {
      return null;
   }

   public long dequeueLong() {
      if (this.start == this.end) {
         throw new NoSuchElementException();
      } else {
         long var1 = this.array[this.start];
         if (++this.start == this.length) {
            this.start = 0;
         }

         this.reduce();
         return var1;
      }
   }

   public long dequeueLastLong() {
      if (this.start == this.end) {
         throw new NoSuchElementException();
      } else {
         if (this.end == 0) {
            this.end = this.length;
         }

         long var1 = this.array[--this.end];
         this.reduce();
         return var1;
      }
   }

   private final void resize(int var1, int var2) {
      long[] var3 = new long[var2];
      if (this.start >= this.end) {
         if (var1 != 0) {
            System.arraycopy(this.array, this.start, var3, 0, this.length - this.start);
            System.arraycopy(this.array, 0, var3, this.length - this.start, this.end);
         }
      } else {
         System.arraycopy(this.array, this.start, var3, 0, this.end - this.start);
      }

      this.start = 0;
      this.end = var1;
      this.array = var3;
      this.length = var2;
   }

   private final void expand() {
      this.resize(this.length, (int)Math.min(2147483639L, 2L * (long)this.length));
   }

   private final void reduce() {
      int var1 = this.size();
      if (this.length > 4 && var1 <= this.length / 4) {
         this.resize(var1, this.length / 2);
      }

   }

   public void enqueue(long var1) {
      this.array[this.end++] = var1;
      if (this.end == this.length) {
         this.end = 0;
      }

      if (this.end == this.start) {
         this.expand();
      }

   }

   public void enqueueFirst(long var1) {
      if (this.start == 0) {
         this.start = this.length;
      }

      this.array[--this.start] = var1;
      if (this.end == this.start) {
         this.expand();
      }

   }

   public long firstLong() {
      if (this.start == this.end) {
         throw new NoSuchElementException();
      } else {
         return this.array[this.start];
      }
   }

   public long lastLong() {
      if (this.start == this.end) {
         throw new NoSuchElementException();
      } else {
         return this.array[(this.end == 0 ? this.length : this.end) - 1];
      }
   }

   public void clear() {
      this.start = this.end = 0;
   }

   public void trim() {
      int var1 = this.size();
      long[] var2 = new long[var1 + 1];
      if (this.start <= this.end) {
         System.arraycopy(this.array, this.start, var2, 0, this.end - this.start);
      } else {
         System.arraycopy(this.array, this.start, var2, 0, this.length - this.start);
         System.arraycopy(this.array, 0, var2, this.length - this.start, this.end);
      }

      this.start = 0;
      this.length = (this.end = var1) + 1;
      this.array = var2;
   }

   public int size() {
      int var1 = this.end - this.start;
      return var1 >= 0 ? var1 : this.length + var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      int var2 = this.size();
      var1.writeInt(var2);
      int var3 = this.start;

      while(var2-- != 0) {
         var1.writeLong(this.array[var3++]);
         if (var3 == this.length) {
            var3 = 0;
         }
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.end = var1.readInt();
      this.array = new long[this.length = HashCommon.nextPowerOfTwo(this.end + 1)];

      for(int var2 = 0; var2 < this.end; ++var2) {
         this.array[var2] = var1.readLong();
      }

   }
}
