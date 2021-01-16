package it.unimi.dsi.fastutil.chars;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.NoSuchElementException;

public class CharArraySet extends AbstractCharSet implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient char[] a;
   private int size;

   public CharArraySet(char[] var1) {
      super();
      this.a = var1;
      this.size = var1.length;
   }

   public CharArraySet() {
      super();
      this.a = CharArrays.EMPTY_ARRAY;
   }

   public CharArraySet(int var1) {
      super();
      this.a = new char[var1];
   }

   public CharArraySet(CharCollection var1) {
      this(var1.size());
      this.addAll(var1);
   }

   public CharArraySet(Collection<? extends Character> var1) {
      this(var1.size());
      this.addAll(var1);
   }

   public CharArraySet(char[] var1, int var2) {
      super();
      this.a = var1;
      this.size = var2;
      if (var2 > var1.length) {
         throw new IllegalArgumentException("The provided size (" + var2 + ") is larger than or equal to the array size (" + var1.length + ")");
      }
   }

   private int findKey(char var1) {
      int var2 = this.size;

      do {
         if (var2-- == 0) {
            return -1;
         }
      } while(this.a[var2] != var1);

      return var2;
   }

   public CharIterator iterator() {
      return new CharIterator() {
         int next = 0;

         public boolean hasNext() {
            return this.next < CharArraySet.this.size;
         }

         public char nextChar() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return CharArraySet.this.a[this.next++];
            }
         }

         public void remove() {
            int var1 = CharArraySet.this.size-- - this.next--;
            System.arraycopy(CharArraySet.this.a, this.next + 1, CharArraySet.this.a, this.next, var1);
         }
      };
   }

   public boolean contains(char var1) {
      return this.findKey(var1) != -1;
   }

   public int size() {
      return this.size;
   }

   public boolean remove(char var1) {
      int var2 = this.findKey(var1);
      if (var2 == -1) {
         return false;
      } else {
         int var3 = this.size - var2 - 1;

         for(int var4 = 0; var4 < var3; ++var4) {
            this.a[var2 + var4] = this.a[var2 + var4 + 1];
         }

         --this.size;
         return true;
      }
   }

   public boolean add(char var1) {
      int var2 = this.findKey(var1);
      if (var2 != -1) {
         return false;
      } else {
         if (this.size == this.a.length) {
            char[] var3 = new char[this.size == 0 ? 2 : this.size * 2];

            for(int var4 = this.size; var4-- != 0; var3[var4] = this.a[var4]) {
            }

            this.a = var3;
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

   public CharArraySet clone() {
      CharArraySet var1;
      try {
         var1 = (CharArraySet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.a = (char[])this.a.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeChar(this.a[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.a = new char[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.a[var2] = var1.readChar();
      }

   }
}
