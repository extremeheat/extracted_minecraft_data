package it.unimi.dsi.fastutil.bytes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.NoSuchElementException;

public class ByteArrayPriorityQueue implements BytePriorityQueue, Serializable {
   private static final long serialVersionUID = 1L;
   protected transient byte[] array;
   protected int size;
   protected ByteComparator c;
   protected transient int firstIndex;
   protected transient boolean firstIndexValid;

   public ByteArrayPriorityQueue(int var1, ByteComparator var2) {
      super();
      this.array = ByteArrays.EMPTY_ARRAY;
      if (var1 > 0) {
         this.array = new byte[var1];
      }

      this.c = var2;
   }

   public ByteArrayPriorityQueue(int var1) {
      this(var1, (ByteComparator)null);
   }

   public ByteArrayPriorityQueue(ByteComparator var1) {
      this(0, var1);
   }

   public ByteArrayPriorityQueue() {
      this(0, (ByteComparator)null);
   }

   public ByteArrayPriorityQueue(byte[] var1, int var2, ByteComparator var3) {
      this(var3);
      this.array = var1;
      this.size = var2;
   }

   public ByteArrayPriorityQueue(byte[] var1, ByteComparator var2) {
      this(var1, var1.length, var2);
   }

   public ByteArrayPriorityQueue(byte[] var1, int var2) {
      this(var1, var2, (ByteComparator)null);
   }

   public ByteArrayPriorityQueue(byte[] var1) {
      this(var1, var1.length);
   }

   private int findFirst() {
      if (this.firstIndexValid) {
         return this.firstIndex;
      } else {
         this.firstIndexValid = true;
         int var1 = this.size;
         --var1;
         int var2 = var1;
         byte var3 = this.array[var1];
         if (this.c == null) {
            while(var1-- != 0) {
               if (this.array[var1] < var3) {
                  var2 = var1;
                  var3 = this.array[var1];
               }
            }
         } else {
            while(var1-- != 0) {
               if (this.c.compare(this.array[var1], var3) < 0) {
                  var2 = var1;
                  var3 = this.array[var1];
               }
            }
         }

         return this.firstIndex = var2;
      }
   }

   private void ensureNonEmpty() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      }
   }

   public void enqueue(byte var1) {
      if (this.size == this.array.length) {
         this.array = ByteArrays.grow(this.array, this.size + 1);
      }

      if (this.firstIndexValid) {
         if (this.c == null) {
            if (var1 < this.array[this.firstIndex]) {
               this.firstIndex = this.size;
            }
         } else if (this.c.compare(var1, this.array[this.firstIndex]) < 0) {
            this.firstIndex = this.size;
         }
      } else {
         this.firstIndexValid = false;
      }

      this.array[this.size++] = var1;
   }

   public byte dequeueByte() {
      this.ensureNonEmpty();
      int var1 = this.findFirst();
      byte var2 = this.array[var1];
      System.arraycopy(this.array, var1 + 1, this.array, var1, --this.size - var1);
      this.firstIndexValid = false;
      return var2;
   }

   public byte firstByte() {
      this.ensureNonEmpty();
      return this.array[this.findFirst()];
   }

   public void changed() {
      this.ensureNonEmpty();
      this.firstIndexValid = false;
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      this.size = 0;
      this.firstIndexValid = false;
   }

   public void trim() {
      this.array = ByteArrays.trim(this.array, this.size);
   }

   public ByteComparator comparator() {
      return this.c;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.array.length);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeByte(this.array[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.array = new byte[var1.readInt()];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.array[var2] = var1.readByte();
      }

   }
}
