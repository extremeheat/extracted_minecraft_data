package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.PriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class ObjectArrayPriorityQueue<K> implements PriorityQueue<K>, Serializable {
   private static final long serialVersionUID = 1L;
   protected transient K[] array;
   protected int size;
   protected Comparator<? super K> c;
   protected transient int firstIndex;
   protected transient boolean firstIndexValid;

   public ObjectArrayPriorityQueue(int var1, Comparator<? super K> var2) {
      super();
      this.array = ObjectArrays.EMPTY_ARRAY;
      if (var1 > 0) {
         this.array = new Object[var1];
      }

      this.c = var2;
   }

   public ObjectArrayPriorityQueue(int var1) {
      this(var1, (Comparator)null);
   }

   public ObjectArrayPriorityQueue(Comparator<? super K> var1) {
      this(0, var1);
   }

   public ObjectArrayPriorityQueue() {
      this(0, (Comparator)null);
   }

   public ObjectArrayPriorityQueue(K[] var1, int var2, Comparator<? super K> var3) {
      this(var3);
      this.array = var1;
      this.size = var2;
   }

   public ObjectArrayPriorityQueue(K[] var1, Comparator<? super K> var2) {
      this(var1, var1.length, var2);
   }

   public ObjectArrayPriorityQueue(K[] var1, int var2) {
      this(var1, var2, (Comparator)null);
   }

   public ObjectArrayPriorityQueue(K[] var1) {
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
         Object var3 = this.array[var1];
         if (this.c == null) {
            while(var1-- != 0) {
               if (((Comparable)this.array[var1]).compareTo(var3) < 0) {
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

   public void enqueue(K var1) {
      if (this.size == this.array.length) {
         this.array = ObjectArrays.grow(this.array, this.size + 1);
      }

      if (this.firstIndexValid) {
         if (this.c == null) {
            if (((Comparable)var1).compareTo(this.array[this.firstIndex]) < 0) {
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

   public K dequeue() {
      this.ensureNonEmpty();
      int var1 = this.findFirst();
      Object var2 = this.array[var1];
      System.arraycopy(this.array, var1 + 1, this.array, var1, --this.size - var1);
      this.array[this.size] = null;
      this.firstIndexValid = false;
      return var2;
   }

   public K first() {
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
      Arrays.fill(this.array, 0, this.size, (Object)null);
      this.size = 0;
      this.firstIndexValid = false;
   }

   public void trim() {
      this.array = ObjectArrays.trim(this.array, this.size);
   }

   public Comparator<? super K> comparator() {
      return this.c;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.array.length);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeObject(this.array[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.array = new Object[var1.readInt()];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.array[var2] = var1.readObject();
      }

   }
}
