package it.unimi.dsi.fastutil.shorts;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ShortArrayList extends AbstractShortList implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353130L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected transient short[] a;
   protected int size;

   protected ShortArrayList(short[] var1, boolean var2) {
      super();
      this.a = var1;
   }

   public ShortArrayList(int var1) {
      super();
      if (var1 < 0) {
         throw new IllegalArgumentException("Initial capacity (" + var1 + ") is negative");
      } else {
         if (var1 == 0) {
            this.a = ShortArrays.EMPTY_ARRAY;
         } else {
            this.a = new short[var1];
         }

      }
   }

   public ShortArrayList() {
      super();
      this.a = ShortArrays.DEFAULT_EMPTY_ARRAY;
   }

   public ShortArrayList(Collection<? extends Short> var1) {
      this(var1.size());
      this.size = ShortIterators.unwrap(ShortIterators.asShortIterator(var1.iterator()), this.a);
   }

   public ShortArrayList(ShortCollection var1) {
      this(var1.size());
      this.size = ShortIterators.unwrap(var1.iterator(), this.a);
   }

   public ShortArrayList(ShortList var1) {
      this(var1.size());
      var1.getElements(0, this.a, 0, this.size = var1.size());
   }

   public ShortArrayList(short[] var1) {
      this(var1, 0, var1.length);
   }

   public ShortArrayList(short[] var1, int var2, int var3) {
      this(var3);
      System.arraycopy(var1, var2, this.a, 0, var3);
      this.size = var3;
   }

   public ShortArrayList(Iterator<? extends Short> var1) {
      this();

      while(var1.hasNext()) {
         this.add((Short)var1.next());
      }

   }

   public ShortArrayList(ShortIterator var1) {
      this();

      while(var1.hasNext()) {
         this.add(var1.nextShort());
      }

   }

   public short[] elements() {
      return this.a;
   }

   public static ShortArrayList wrap(short[] var0, int var1) {
      if (var1 > var0.length) {
         throw new IllegalArgumentException("The specified length (" + var1 + ") is greater than the array size (" + var0.length + ")");
      } else {
         ShortArrayList var2 = new ShortArrayList(var0, false);
         var2.size = var1;
         return var2;
      }
   }

   public static ShortArrayList wrap(short[] var0) {
      return wrap(var0, var0.length);
   }

   public void ensureCapacity(int var1) {
      if (var1 > this.a.length && this.a != ShortArrays.DEFAULT_EMPTY_ARRAY) {
         this.a = ShortArrays.ensureCapacity(this.a, var1, this.size);

         assert this.size <= this.a.length;

      }
   }

   private void grow(int var1) {
      if (var1 > this.a.length) {
         if (this.a != ShortArrays.DEFAULT_EMPTY_ARRAY) {
            var1 = (int)Math.max(Math.min((long)this.a.length + (long)(this.a.length >> 1), 2147483639L), (long)var1);
         } else if (var1 < 10) {
            var1 = 10;
         }

         this.a = ShortArrays.forceCapacity(this.a, var1, this.size);

         assert this.size <= this.a.length;

      }
   }

   public void add(int var1, short var2) {
      this.ensureIndex(var1);
      this.grow(this.size + 1);
      if (var1 != this.size) {
         System.arraycopy(this.a, var1, this.a, var1 + 1, this.size - var1);
      }

      this.a[var1] = var2;
      ++this.size;

      assert this.size <= this.a.length;

   }

   public boolean add(short var1) {
      this.grow(this.size + 1);
      this.a[this.size++] = var1;

      assert this.size <= this.a.length;

      return true;
   }

   public short getShort(int var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return this.a[var1];
      }
   }

   public int indexOf(short var1) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         if (var1 == this.a[var2]) {
            return var2;
         }
      }

      return -1;
   }

   public int lastIndexOf(short var1) {
      int var2 = this.size;

      do {
         if (var2-- == 0) {
            return -1;
         }
      } while(var1 != this.a[var2]);

      return var2;
   }

   public short removeShort(int var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         short var2 = this.a[var1];
         --this.size;
         if (var1 != this.size) {
            System.arraycopy(this.a, var1 + 1, this.a, var1, this.size - var1);
         }

         assert this.size <= this.a.length;

         return var2;
      }
   }

   public boolean rem(short var1) {
      int var2 = this.indexOf(var1);
      if (var2 == -1) {
         return false;
      } else {
         this.removeShort(var2);

         assert this.size <= this.a.length;

         return true;
      }
   }

   public short set(int var1, short var2) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         short var3 = this.a[var1];
         this.a[var1] = var2;
         return var3;
      }
   }

   public void clear() {
      this.size = 0;

      assert this.size <= this.a.length;

   }

   public int size() {
      return this.size;
   }

   public void size(int var1) {
      if (var1 > this.a.length) {
         this.ensureCapacity(var1);
      }

      if (var1 > this.size) {
         Arrays.fill(this.a, this.size, var1, (short)0);
      }

      this.size = var1;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public void trim() {
      this.trim(0);
   }

   public void trim(int var1) {
      if (var1 < this.a.length && this.size != this.a.length) {
         short[] var2 = new short[Math.max(var1, this.size)];
         System.arraycopy(this.a, 0, var2, 0, this.size);
         this.a = var2;

         assert this.size <= this.a.length;

      }
   }

   public void getElements(int var1, short[] var2, int var3, int var4) {
      ShortArrays.ensureOffsetLength(var2, var3, var4);
      System.arraycopy(this.a, var1, var2, var3, var4);
   }

   public void removeElements(int var1, int var2) {
      it.unimi.dsi.fastutil.Arrays.ensureFromTo(this.size, var1, var2);
      System.arraycopy(this.a, var2, this.a, var1, this.size - var2);
      this.size -= var2 - var1;
   }

   public void addElements(int var1, short[] var2, int var3, int var4) {
      this.ensureIndex(var1);
      ShortArrays.ensureOffsetLength(var2, var3, var4);
      this.grow(this.size + var4);
      System.arraycopy(this.a, var1, this.a, var1 + var4, this.size - var1);
      System.arraycopy(var2, var3, this.a, var1, var4);
      this.size += var4;
   }

   public short[] toArray(short[] var1) {
      if (var1 == null || var1.length < this.size) {
         var1 = new short[this.size];
      }

      System.arraycopy(this.a, 0, var1, 0, this.size);
      return var1;
   }

   public boolean addAll(int var1, ShortCollection var2) {
      this.ensureIndex(var1);
      int var3 = var2.size();
      if (var3 == 0) {
         return false;
      } else {
         this.grow(this.size + var3);
         if (var1 != this.size) {
            System.arraycopy(this.a, var1, this.a, var1 + var3, this.size - var1);
         }

         ShortIterator var4 = var2.iterator();

         for(this.size += var3; var3-- != 0; this.a[var1++] = var4.nextShort()) {
         }

         assert this.size <= this.a.length;

         return true;
      }
   }

   public boolean addAll(int var1, ShortList var2) {
      this.ensureIndex(var1);
      int var3 = var2.size();
      if (var3 == 0) {
         return false;
      } else {
         this.grow(this.size + var3);
         if (var1 != this.size) {
            System.arraycopy(this.a, var1, this.a, var1 + var3, this.size - var1);
         }

         var2.getElements(0, this.a, var1, var3);
         this.size += var3;

         assert this.size <= this.a.length;

         return true;
      }
   }

   public boolean removeAll(ShortCollection var1) {
      short[] var2 = this.a;
      int var3 = 0;

      for(int var4 = 0; var4 < this.size; ++var4) {
         if (!var1.contains(var2[var4])) {
            var2[var3++] = var2[var4];
         }
      }

      boolean var5 = this.size != var3;
      this.size = var3;
      return var5;
   }

   public boolean removeAll(Collection<?> var1) {
      short[] var2 = this.a;
      int var3 = 0;

      for(int var4 = 0; var4 < this.size; ++var4) {
         if (!var1.contains(var2[var4])) {
            var2[var3++] = var2[var4];
         }
      }

      boolean var5 = this.size != var3;
      this.size = var3;
      return var5;
   }

   public ShortListIterator listIterator(final int var1) {
      this.ensureIndex(var1);
      return new ShortListIterator() {
         int pos = var1;
         int last = -1;

         public boolean hasNext() {
            return this.pos < ShortArrayList.this.size;
         }

         public boolean hasPrevious() {
            return this.pos > 0;
         }

         public short nextShort() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return ShortArrayList.this.a[this.last = this.pos++];
            }
         }

         public short previousShort() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return ShortArrayList.this.a[this.last = --this.pos];
            }
         }

         public int nextIndex() {
            return this.pos;
         }

         public int previousIndex() {
            return this.pos - 1;
         }

         public void add(short var1x) {
            ShortArrayList.this.add(this.pos++, var1x);
            this.last = -1;
         }

         public void set(short var1x) {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               ShortArrayList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               ShortArrayList.this.removeShort(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1;
            }
         }
      };
   }

   public ShortArrayList clone() {
      ShortArrayList var1 = new ShortArrayList(this.size);
      System.arraycopy(this.a, 0, var1.a, 0, this.size);
      var1.size = this.size;
      return var1;
   }

   public boolean equals(ShortArrayList var1) {
      if (var1 == this) {
         return true;
      } else {
         int var2 = this.size();
         if (var2 != var1.size()) {
            return false;
         } else {
            short[] var3 = this.a;
            short[] var4 = var1.a;

            do {
               if (var2-- == 0) {
                  return true;
               }
            } while(var3[var2] == var4[var2]);

            return false;
         }
      }
   }

   public int compareTo(ShortArrayList var1) {
      int var2 = this.size();
      int var3 = var1.size();
      short[] var4 = this.a;
      short[] var5 = var1.a;

      int var9;
      for(var9 = 0; var9 < var2 && var9 < var3; ++var9) {
         short var6 = var4[var9];
         short var7 = var5[var9];
         int var8;
         if ((var8 = Short.compare(var6, var7)) != 0) {
            return var8;
         }
      }

      return var9 < var3 ? -1 : (var9 < var2 ? 1 : 0);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeShort(this.a[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.a = new short[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.a[var2] = var1.readShort();
      }

   }
}
