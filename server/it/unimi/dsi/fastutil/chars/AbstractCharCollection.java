package it.unimi.dsi.fastutil.chars;

import java.util.AbstractCollection;

public abstract class AbstractCharCollection extends AbstractCollection<Character> implements CharCollection {
   protected AbstractCharCollection() {
      super();
   }

   public abstract CharIterator iterator();

   public boolean add(char var1) {
      throw new UnsupportedOperationException();
   }

   public boolean contains(char var1) {
      CharIterator var2 = this.iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(var1 != var2.nextChar());

      return true;
   }

   public boolean rem(char var1) {
      CharIterator var2 = this.iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(var1 != var2.nextChar());

      var2.remove();
      return true;
   }

   /** @deprecated */
   @Deprecated
   public boolean add(Character var1) {
      return CharCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean contains(Object var1) {
      return CharCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean remove(Object var1) {
      return CharCollection.super.remove(var1);
   }

   public char[] toArray(char[] var1) {
      if (var1 == null || var1.length < this.size()) {
         var1 = new char[this.size()];
      }

      CharIterators.unwrap(this.iterator(), var1);
      return var1;
   }

   public char[] toCharArray() {
      return this.toArray((char[])null);
   }

   /** @deprecated */
   @Deprecated
   public char[] toCharArray(char[] var1) {
      return this.toArray(var1);
   }

   public boolean addAll(CharCollection var1) {
      boolean var2 = false;
      CharIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.add(var3.nextChar())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean containsAll(CharCollection var1) {
      CharIterator var2 = var1.iterator();

      do {
         if (!var2.hasNext()) {
            return true;
         }
      } while(this.contains(var2.nextChar()));

      return false;
   }

   public boolean removeAll(CharCollection var1) {
      boolean var2 = false;
      CharIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.rem(var3.nextChar())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(CharCollection var1) {
      boolean var2 = false;
      CharIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.nextChar())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      CharIterator var2 = this.iterator();
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         char var4 = var2.nextChar();
         var1.append(String.valueOf(var4));
      }

      var1.append("}");
      return var1.toString();
   }
}
