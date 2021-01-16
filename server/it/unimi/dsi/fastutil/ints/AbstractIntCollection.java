package it.unimi.dsi.fastutil.ints;

import java.util.AbstractCollection;

public abstract class AbstractIntCollection extends AbstractCollection<Integer> implements IntCollection {
   protected AbstractIntCollection() {
      super();
   }

   public abstract IntIterator iterator();

   public boolean add(int var1) {
      throw new UnsupportedOperationException();
   }

   public boolean contains(int var1) {
      IntIterator var2 = this.iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(var1 != var2.nextInt());

      return true;
   }

   public boolean rem(int var1) {
      IntIterator var2 = this.iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(var1 != var2.nextInt());

      var2.remove();
      return true;
   }

   /** @deprecated */
   @Deprecated
   public boolean add(Integer var1) {
      return IntCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean contains(Object var1) {
      return IntCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean remove(Object var1) {
      return IntCollection.super.remove(var1);
   }

   public int[] toArray(int[] var1) {
      if (var1 == null || var1.length < this.size()) {
         var1 = new int[this.size()];
      }

      IntIterators.unwrap(this.iterator(), var1);
      return var1;
   }

   public int[] toIntArray() {
      return this.toArray((int[])null);
   }

   /** @deprecated */
   @Deprecated
   public int[] toIntArray(int[] var1) {
      return this.toArray(var1);
   }

   public boolean addAll(IntCollection var1) {
      boolean var2 = false;
      IntIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.add(var3.nextInt())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean containsAll(IntCollection var1) {
      IntIterator var2 = var1.iterator();

      do {
         if (!var2.hasNext()) {
            return true;
         }
      } while(this.contains(var2.nextInt()));

      return false;
   }

   public boolean removeAll(IntCollection var1) {
      boolean var2 = false;
      IntIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.rem(var3.nextInt())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(IntCollection var1) {
      boolean var2 = false;
      IntIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.nextInt())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      IntIterator var2 = this.iterator();
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         int var4 = var2.nextInt();
         var1.append(String.valueOf(var4));
      }

      var1.append("}");
      return var1.toString();
   }
}
