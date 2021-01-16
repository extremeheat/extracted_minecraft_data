package it.unimi.dsi.fastutil.chars;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.NoSuchElementException;

public class CharArrayPriorityQueue implements CharPriorityQueue, Serializable {
   private static final long serialVersionUID = 1L;
   protected transient char[] array;
   protected int size;
   protected CharComparator c;
   protected transient int firstIndex;
   protected transient boolean firstIndexValid;

   public CharArrayPriorityQueue(int var1, CharComparator var2) {
      super();
      this.array = CharArrays.EMPTY_ARRAY;
      if (var1 > 0) {
         this.array = new char[var1];
      }

      this.c = var2;
   }

   public CharArrayPriorityQueue(int var1) {
      this(var1, (CharComparator)null);
   }

   public CharArrayPriorityQueue(CharComparator var1) {
      this(0, var1);
   }

   public CharArrayPriorityQueue() {
      this(0, (CharComparator)null);
   }

   public CharArrayPriorityQueue(char[] var1, int var2, CharComparator var3) {
      this(var3);
      this.array = var1;
      this.size = var2;
   }

   public CharArrayPriorityQueue(char[] var1, CharComparator var2) {
      this(var1, var1.length, var2);
   }

   public CharArrayPriorityQueue(char[] var1, int var2) {
      this(var1, var2, (CharComparator)null);
   }

   public CharArrayPriorityQueue(char[] var1) {
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
         char var3 = this.array[var1];
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

   public void enqueue(char var1) {
      if (this.size == this.array.length) {
         this.array = CharArrays.grow(this.array, this.size + 1);
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

   public char dequeueChar() {
      this.ensureNonEmpty();
      int var1 = this.findFirst();
      char var2 = this.array[var1];
      System.arraycopy(this.array, var1 + 1, this.array, var1, --this.size - var1);
      this.firstIndexValid = false;
      return var2;
   }

   public char firstChar() {
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
      this.array = CharArrays.trim(this.array, this.size);
   }

   public CharComparator comparator() {
      return this.c;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.array.length);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeChar(this.array[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.array = new char[var1.readInt()];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.array[var2] = var1.readChar();
      }

   }
}
