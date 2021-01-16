package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigArrays;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ShortBigArrayBigList extends AbstractShortBigList implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353130L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected transient short[][] a;
   protected long size;

   protected ShortBigArrayBigList(short[][] var1, boolean var2) {
      super();
      this.a = var1;
   }

   public ShortBigArrayBigList(long var1) {
      super();
      if (var1 < 0L) {
         throw new IllegalArgumentException("Initial capacity (" + var1 + ") is negative");
      } else {
         if (var1 == 0L) {
            this.a = ShortBigArrays.EMPTY_BIG_ARRAY;
         } else {
            this.a = ShortBigArrays.newBigArray(var1);
         }

      }
   }

   public ShortBigArrayBigList() {
      super();
      this.a = ShortBigArrays.DEFAULT_EMPTY_BIG_ARRAY;
   }

   public ShortBigArrayBigList(ShortCollection var1) {
      this((long)var1.size());
      ShortIterator var2 = var1.iterator();

      while(var2.hasNext()) {
         this.add(var2.nextShort());
      }

   }

   public ShortBigArrayBigList(ShortBigList var1) {
      this(var1.size64());
      var1.getElements(0L, this.a, 0L, this.size = var1.size64());
   }

   public ShortBigArrayBigList(short[][] var1) {
      this(var1, 0L, ShortBigArrays.length(var1));
   }

   public ShortBigArrayBigList(short[][] var1, long var2, long var4) {
      this(var4);
      ShortBigArrays.copy(var1, var2, this.a, 0L, var4);
      this.size = var4;
   }

   public ShortBigArrayBigList(Iterator<? extends Short> var1) {
      this();

      while(var1.hasNext()) {
         this.add((Short)var1.next());
      }

   }

   public ShortBigArrayBigList(ShortIterator var1) {
      this();

      while(var1.hasNext()) {
         this.add(var1.nextShort());
      }

   }

   public short[][] elements() {
      return this.a;
   }

   public static ShortBigArrayBigList wrap(short[][] var0, long var1) {
      if (var1 > ShortBigArrays.length(var0)) {
         throw new IllegalArgumentException("The specified length (" + var1 + ") is greater than the array size (" + ShortBigArrays.length(var0) + ")");
      } else {
         ShortBigArrayBigList var3 = new ShortBigArrayBigList(var0, false);
         var3.size = var1;
         return var3;
      }
   }

   public static ShortBigArrayBigList wrap(short[][] var0) {
      return wrap(var0, ShortBigArrays.length(var0));
   }

   public void ensureCapacity(long var1) {
      if (var1 > (long)this.a.length && this.a != ShortBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
         this.a = ShortBigArrays.forceCapacity(this.a, var1, this.size);

         assert this.size <= ShortBigArrays.length(this.a);

      }
   }

   private void grow(long var1) {
      long var3 = ShortBigArrays.length(this.a);
      if (var1 > var3) {
         if (this.a != ShortBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
            var1 = Math.max(var3 + (var3 >> 1), var1);
         } else if (var1 < 10L) {
            var1 = 10L;
         }

         this.a = ShortBigArrays.forceCapacity(this.a, var1, this.size);

         assert this.size <= ShortBigArrays.length(this.a);

      }
   }

   public void add(long var1, short var3) {
      this.ensureIndex(var1);
      this.grow(this.size + 1L);
      if (var1 != this.size) {
         ShortBigArrays.copy(this.a, var1, this.a, var1 + 1L, this.size - var1);
      }

      ShortBigArrays.set(this.a, var1, var3);
      ++this.size;

      assert this.size <= ShortBigArrays.length(this.a);

   }

   public boolean add(short var1) {
      this.grow(this.size + 1L);
      ShortBigArrays.set(this.a, (long)(this.size++), var1);

      assert this.size <= ShortBigArrays.length(this.a);

      return true;
   }

   public short getShort(long var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return ShortBigArrays.get(this.a, var1);
      }
   }

   public long indexOf(short var1) {
      for(long var2 = 0L; var2 < this.size; ++var2) {
         if (var1 == ShortBigArrays.get(this.a, var2)) {
            return var2;
         }
      }

      return -1L;
   }

   public long lastIndexOf(short var1) {
      long var2 = this.size;

      do {
         if (var2-- == 0L) {
            return -1L;
         }
      } while(var1 != ShortBigArrays.get(this.a, var2));

      return var2;
   }

   public short removeShort(long var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         short var3 = ShortBigArrays.get(this.a, var1);
         --this.size;
         if (var1 != this.size) {
            ShortBigArrays.copy(this.a, var1 + 1L, this.a, var1, this.size - var1);
         }

         assert this.size <= ShortBigArrays.length(this.a);

         return var3;
      }
   }

   public boolean rem(short var1) {
      long var2 = this.indexOf(var1);
      if (var2 == -1L) {
         return false;
      } else {
         this.removeShort(var2);

         assert this.size <= ShortBigArrays.length(this.a);

         return true;
      }
   }

   public short set(long var1, short var3) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         short var4 = ShortBigArrays.get(this.a, var1);
         ShortBigArrays.set(this.a, var1, var3);
         return var4;
      }
   }

   public boolean removeAll(ShortCollection var1) {
      short[] var2 = null;
      short[] var3 = null;
      int var4 = -1;
      int var5 = 134217728;
      int var6 = -1;
      int var7 = 134217728;

      long var8;
      for(var8 = 0L; var8 < this.size; ++var8) {
         if (var5 == 134217728) {
            var5 = 0;
            ++var4;
            var2 = this.a[var4];
         }

         if (!var1.contains(var2[var5])) {
            if (var7 == 134217728) {
               ++var6;
               var3 = this.a[var6];
               var7 = 0;
            }

            var3[var7++] = var2[var5];
         }

         ++var5;
      }

      var8 = BigArrays.index(var6, var7);
      boolean var10 = this.size != var8;
      this.size = var8;
      return var10;
   }

   public boolean removeAll(Collection<?> var1) {
      short[] var2 = null;
      short[] var3 = null;
      int var4 = -1;
      int var5 = 134217728;
      int var6 = -1;
      int var7 = 134217728;

      long var8;
      for(var8 = 0L; var8 < this.size; ++var8) {
         if (var5 == 134217728) {
            var5 = 0;
            ++var4;
            var2 = this.a[var4];
         }

         if (!var1.contains(var2[var5])) {
            if (var7 == 134217728) {
               ++var6;
               var3 = this.a[var6];
               var7 = 0;
            }

            var3[var7++] = var2[var5];
         }

         ++var5;
      }

      var8 = BigArrays.index(var6, var7);
      boolean var10 = this.size != var8;
      this.size = var8;
      return var10;
   }

   public void clear() {
      this.size = 0L;

      assert this.size <= ShortBigArrays.length(this.a);

   }

   public long size64() {
      return this.size;
   }

   public void size(long var1) {
      if (var1 > ShortBigArrays.length(this.a)) {
         this.ensureCapacity(var1);
      }

      if (var1 > this.size) {
         ShortBigArrays.fill(this.a, this.size, var1, (short)0);
      }

      this.size = var1;
   }

   public boolean isEmpty() {
      return this.size == 0L;
   }

   public void trim() {
      this.trim(0L);
   }

   public void trim(long var1) {
      long var3 = ShortBigArrays.length(this.a);
      if (var1 < var3 && this.size != var3) {
         this.a = ShortBigArrays.trim(this.a, Math.max(var1, this.size));

         assert this.size <= ShortBigArrays.length(this.a);

      }
   }

   public void getElements(long var1, short[][] var3, long var4, long var6) {
      ShortBigArrays.copy(this.a, var1, var3, var4, var6);
   }

   public void removeElements(long var1, long var3) {
      BigArrays.ensureFromTo(this.size, var1, var3);
      ShortBigArrays.copy(this.a, var3, this.a, var1, this.size - var3);
      this.size -= var3 - var1;
   }

   public void addElements(long var1, short[][] var3, long var4, long var6) {
      this.ensureIndex(var1);
      ShortBigArrays.ensureOffsetLength(var3, var4, var6);
      this.grow(this.size + var6);
      ShortBigArrays.copy(this.a, var1, this.a, var1 + var6, this.size - var1);
      ShortBigArrays.copy(var3, var4, this.a, var1, var6);
      this.size += var6;
   }

   public ShortBigListIterator listIterator(final long var1) {
      this.ensureIndex(var1);
      return new ShortBigListIterator() {
         long pos = var1;
         long last = -1L;

         public boolean hasNext() {
            return this.pos < ShortBigArrayBigList.this.size;
         }

         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         public short nextShort() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return ShortBigArrays.get(ShortBigArrayBigList.this.a, this.last = (long)(this.pos++));
            }
         }

         public short previousShort() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return ShortBigArrays.get(ShortBigArrayBigList.this.a, this.last = --this.pos);
            }
         }

         public long nextIndex() {
            return this.pos;
         }

         public long previousIndex() {
            return this.pos - 1L;
         }

         public void add(short var1x) {
            ShortBigArrayBigList.this.add((long)(this.pos++), var1x);
            this.last = -1L;
         }

         public void set(short var1x) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               ShortBigArrayBigList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               ShortBigArrayBigList.this.removeShort(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1L;
            }
         }
      };
   }

   public ShortBigArrayBigList clone() {
      ShortBigArrayBigList var1 = new ShortBigArrayBigList(this.size);
      ShortBigArrays.copy(this.a, 0L, var1.a, 0L, this.size);
      var1.size = this.size;
      return var1;
   }

   public boolean equals(ShortBigArrayBigList var1) {
      if (var1 == this) {
         return true;
      } else {
         long var2 = this.size64();
         if (var2 != var1.size64()) {
            return false;
         } else {
            short[][] var4 = this.a;
            short[][] var5 = var1.a;

            do {
               if (var2-- == 0L) {
                  return true;
               }
            } while(ShortBigArrays.get(var4, var2) == ShortBigArrays.get(var5, var2));

            return false;
         }
      }
   }

   public int compareTo(ShortBigArrayBigList var1) {
      long var2 = this.size64();
      long var4 = var1.size64();
      short[][] var6 = this.a;
      short[][] var7 = var1.a;

      int var11;
      for(var11 = 0; (long)var11 < var2 && (long)var11 < var4; ++var11) {
         short var8 = ShortBigArrays.get(var6, (long)var11);
         short var9 = ShortBigArrays.get(var7, (long)var11);
         int var10;
         if ((var10 = Short.compare(var8, var9)) != 0) {
            return var10;
         }
      }

      return (long)var11 < var4 ? -1 : ((long)var11 < var2 ? 1 : 0);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; (long)var2 < this.size; ++var2) {
         var1.writeShort(ShortBigArrays.get(this.a, (long)var2));
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.a = ShortBigArrays.newBigArray(this.size);

      for(int var2 = 0; (long)var2 < this.size; ++var2) {
         ShortBigArrays.set(this.a, (long)var2, var1.readShort());
      }

   }
}
