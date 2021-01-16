package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.BigArrays;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class CharBigArrayBigList extends AbstractCharBigList implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353130L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected transient char[][] a;
   protected long size;

   protected CharBigArrayBigList(char[][] var1, boolean var2) {
      super();
      this.a = var1;
   }

   public CharBigArrayBigList(long var1) {
      super();
      if (var1 < 0L) {
         throw new IllegalArgumentException("Initial capacity (" + var1 + ") is negative");
      } else {
         if (var1 == 0L) {
            this.a = CharBigArrays.EMPTY_BIG_ARRAY;
         } else {
            this.a = CharBigArrays.newBigArray(var1);
         }

      }
   }

   public CharBigArrayBigList() {
      super();
      this.a = CharBigArrays.DEFAULT_EMPTY_BIG_ARRAY;
   }

   public CharBigArrayBigList(CharCollection var1) {
      this((long)var1.size());
      CharIterator var2 = var1.iterator();

      while(var2.hasNext()) {
         this.add(var2.nextChar());
      }

   }

   public CharBigArrayBigList(CharBigList var1) {
      this(var1.size64());
      var1.getElements(0L, this.a, 0L, this.size = var1.size64());
   }

   public CharBigArrayBigList(char[][] var1) {
      this(var1, 0L, CharBigArrays.length(var1));
   }

   public CharBigArrayBigList(char[][] var1, long var2, long var4) {
      this(var4);
      CharBigArrays.copy(var1, var2, this.a, 0L, var4);
      this.size = var4;
   }

   public CharBigArrayBigList(Iterator<? extends Character> var1) {
      this();

      while(var1.hasNext()) {
         this.add((Character)var1.next());
      }

   }

   public CharBigArrayBigList(CharIterator var1) {
      this();

      while(var1.hasNext()) {
         this.add(var1.nextChar());
      }

   }

   public char[][] elements() {
      return this.a;
   }

   public static CharBigArrayBigList wrap(char[][] var0, long var1) {
      if (var1 > CharBigArrays.length(var0)) {
         throw new IllegalArgumentException("The specified length (" + var1 + ") is greater than the array size (" + CharBigArrays.length(var0) + ")");
      } else {
         CharBigArrayBigList var3 = new CharBigArrayBigList(var0, false);
         var3.size = var1;
         return var3;
      }
   }

   public static CharBigArrayBigList wrap(char[][] var0) {
      return wrap(var0, CharBigArrays.length(var0));
   }

   public void ensureCapacity(long var1) {
      if (var1 > (long)this.a.length && this.a != CharBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
         this.a = CharBigArrays.forceCapacity(this.a, var1, this.size);

         assert this.size <= CharBigArrays.length(this.a);

      }
   }

   private void grow(long var1) {
      long var3 = CharBigArrays.length(this.a);
      if (var1 > var3) {
         if (this.a != CharBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
            var1 = Math.max(var3 + (var3 >> 1), var1);
         } else if (var1 < 10L) {
            var1 = 10L;
         }

         this.a = CharBigArrays.forceCapacity(this.a, var1, this.size);

         assert this.size <= CharBigArrays.length(this.a);

      }
   }

   public void add(long var1, char var3) {
      this.ensureIndex(var1);
      this.grow(this.size + 1L);
      if (var1 != this.size) {
         CharBigArrays.copy(this.a, var1, this.a, var1 + 1L, this.size - var1);
      }

      CharBigArrays.set(this.a, var1, var3);
      ++this.size;

      assert this.size <= CharBigArrays.length(this.a);

   }

   public boolean add(char var1) {
      this.grow(this.size + 1L);
      CharBigArrays.set(this.a, (long)(this.size++), var1);

      assert this.size <= CharBigArrays.length(this.a);

      return true;
   }

   public char getChar(long var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return CharBigArrays.get(this.a, var1);
      }
   }

   public long indexOf(char var1) {
      for(long var2 = 0L; var2 < this.size; ++var2) {
         if (var1 == CharBigArrays.get(this.a, var2)) {
            return var2;
         }
      }

      return -1L;
   }

   public long lastIndexOf(char var1) {
      long var2 = this.size;

      do {
         if (var2-- == 0L) {
            return -1L;
         }
      } while(var1 != CharBigArrays.get(this.a, var2));

      return var2;
   }

   public char removeChar(long var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         char var3 = CharBigArrays.get(this.a, var1);
         --this.size;
         if (var1 != this.size) {
            CharBigArrays.copy(this.a, var1 + 1L, this.a, var1, this.size - var1);
         }

         assert this.size <= CharBigArrays.length(this.a);

         return var3;
      }
   }

   public boolean rem(char var1) {
      long var2 = this.indexOf(var1);
      if (var2 == -1L) {
         return false;
      } else {
         this.removeChar(var2);

         assert this.size <= CharBigArrays.length(this.a);

         return true;
      }
   }

   public char set(long var1, char var3) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         char var4 = CharBigArrays.get(this.a, var1);
         CharBigArrays.set(this.a, var1, var3);
         return var4;
      }
   }

   public boolean removeAll(CharCollection var1) {
      char[] var2 = null;
      char[] var3 = null;
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
      char[] var2 = null;
      char[] var3 = null;
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

      assert this.size <= CharBigArrays.length(this.a);

   }

   public long size64() {
      return this.size;
   }

   public void size(long var1) {
      if (var1 > CharBigArrays.length(this.a)) {
         this.ensureCapacity(var1);
      }

      if (var1 > this.size) {
         CharBigArrays.fill(this.a, this.size, var1, '\u0000');
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
      long var3 = CharBigArrays.length(this.a);
      if (var1 < var3 && this.size != var3) {
         this.a = CharBigArrays.trim(this.a, Math.max(var1, this.size));

         assert this.size <= CharBigArrays.length(this.a);

      }
   }

   public void getElements(long var1, char[][] var3, long var4, long var6) {
      CharBigArrays.copy(this.a, var1, var3, var4, var6);
   }

   public void removeElements(long var1, long var3) {
      BigArrays.ensureFromTo(this.size, var1, var3);
      CharBigArrays.copy(this.a, var3, this.a, var1, this.size - var3);
      this.size -= var3 - var1;
   }

   public void addElements(long var1, char[][] var3, long var4, long var6) {
      this.ensureIndex(var1);
      CharBigArrays.ensureOffsetLength(var3, var4, var6);
      this.grow(this.size + var6);
      CharBigArrays.copy(this.a, var1, this.a, var1 + var6, this.size - var1);
      CharBigArrays.copy(var3, var4, this.a, var1, var6);
      this.size += var6;
   }

   public CharBigListIterator listIterator(final long var1) {
      this.ensureIndex(var1);
      return new CharBigListIterator() {
         long pos = var1;
         long last = -1L;

         public boolean hasNext() {
            return this.pos < CharBigArrayBigList.this.size;
         }

         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         public char nextChar() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return CharBigArrays.get(CharBigArrayBigList.this.a, this.last = (long)(this.pos++));
            }
         }

         public char previousChar() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return CharBigArrays.get(CharBigArrayBigList.this.a, this.last = --this.pos);
            }
         }

         public long nextIndex() {
            return this.pos;
         }

         public long previousIndex() {
            return this.pos - 1L;
         }

         public void add(char var1x) {
            CharBigArrayBigList.this.add((long)(this.pos++), var1x);
            this.last = -1L;
         }

         public void set(char var1x) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               CharBigArrayBigList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               CharBigArrayBigList.this.removeChar(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1L;
            }
         }
      };
   }

   public CharBigArrayBigList clone() {
      CharBigArrayBigList var1 = new CharBigArrayBigList(this.size);
      CharBigArrays.copy(this.a, 0L, var1.a, 0L, this.size);
      var1.size = this.size;
      return var1;
   }

   public boolean equals(CharBigArrayBigList var1) {
      if (var1 == this) {
         return true;
      } else {
         long var2 = this.size64();
         if (var2 != var1.size64()) {
            return false;
         } else {
            char[][] var4 = this.a;
            char[][] var5 = var1.a;

            do {
               if (var2-- == 0L) {
                  return true;
               }
            } while(CharBigArrays.get(var4, var2) == CharBigArrays.get(var5, var2));

            return false;
         }
      }
   }

   public int compareTo(CharBigArrayBigList var1) {
      long var2 = this.size64();
      long var4 = var1.size64();
      char[][] var6 = this.a;
      char[][] var7 = var1.a;

      int var11;
      for(var11 = 0; (long)var11 < var2 && (long)var11 < var4; ++var11) {
         char var8 = CharBigArrays.get(var6, (long)var11);
         char var9 = CharBigArrays.get(var7, (long)var11);
         int var10;
         if ((var10 = Character.compare(var8, var9)) != 0) {
            return var10;
         }
      }

      return (long)var11 < var4 ? -1 : ((long)var11 < var2 ? 1 : 0);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; (long)var2 < this.size; ++var2) {
         var1.writeChar(CharBigArrays.get(this.a, (long)var2));
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.a = CharBigArrays.newBigArray(this.size);

      for(int var2 = 0; (long)var2 < this.size; ++var2) {
         CharBigArrays.set(this.a, (long)var2, var1.readChar());
      }

   }
}
