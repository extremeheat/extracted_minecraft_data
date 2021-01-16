package it.unimi.dsi.fastutil.longs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.NoSuchElementException;

public class LongArraySet extends AbstractLongSet implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient long[] a;
   private int size;

   public LongArraySet(long[] var1) {
      super();
      this.a = var1;
      this.size = var1.length;
   }

   public LongArraySet() {
      super();
      this.a = LongArrays.EMPTY_ARRAY;
   }

   public LongArraySet(int var1) {
      super();
      this.a = new long[var1];
   }

   public LongArraySet(LongCollection var1) {
      this(var1.size());
      this.addAll(var1);
   }

   public LongArraySet(Collection<? extends Long> var1) {
      this(var1.size());
      this.addAll(var1);
   }

   public LongArraySet(long[] var1, int var2) {
      super();
      this.a = var1;
      this.size = var2;
      if (var2 > var1.length) {
         throw new IllegalArgumentException("The provided size (" + var2 + ") is larger than or equal to the array size (" + var1.length + ")");
      }
   }

   private int findKey(long var1) {
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return -1;
         }
      } while(this.a[var3] != var1);

      return var3;
   }

   public LongIterator iterator() {
      return new LongIterator() {
         int next = 0;

         public boolean hasNext() {
            return this.next < LongArraySet.this.size;
         }

         public long nextLong() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return LongArraySet.this.a[this.next++];
            }
         }

         public void remove() {
            int var1 = LongArraySet.this.size-- - this.next--;
            System.arraycopy(LongArraySet.this.a, this.next + 1, LongArraySet.this.a, this.next, var1);
         }
      };
   }

   public boolean contains(long var1) {
      return this.findKey(var1) != -1;
   }

   public int size() {
      return this.size;
   }

   public boolean remove(long var1) {
      int var3 = this.findKey(var1);
      if (var3 == -1) {
         return false;
      } else {
         int var4 = this.size - var3 - 1;

         for(int var5 = 0; var5 < var4; ++var5) {
            this.a[var3 + var5] = this.a[var3 + var5 + 1];
         }

         --this.size;
         return true;
      }
   }

   public boolean add(long var1) {
      int var3 = this.findKey(var1);
      if (var3 != -1) {
         return false;
      } else {
         if (this.size == this.a.length) {
            long[] var4 = new long[this.size == 0 ? 2 : this.size * 2];

            for(int var5 = this.size; var5-- != 0; var4[var5] = this.a[var5]) {
            }

            this.a = var4;
         }

         this.a[this.size++] = var1;
         return true;
      }
   }

   public void clear() {
      this.size = 0;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public LongArraySet clone() {
      LongArraySet var1;
      try {
         var1 = (LongArraySet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.a = (long[])this.a.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeLong(this.a[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.a = new long[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.a[var2] = var1.readLong();
      }

   }
}
