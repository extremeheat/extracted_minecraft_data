package it.unimi.dsi.fastutil.booleans;

import java.util.AbstractCollection;

public abstract class AbstractBooleanCollection extends AbstractCollection<Boolean> implements BooleanCollection {
   protected AbstractBooleanCollection() {
      super();
   }

   public abstract BooleanIterator iterator();

   public boolean add(boolean var1) {
      throw new UnsupportedOperationException();
   }

   public boolean contains(boolean var1) {
      BooleanIterator var2 = this.iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(var1 != var2.nextBoolean());

      return true;
   }

   public boolean rem(boolean var1) {
      BooleanIterator var2 = this.iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(var1 != var2.nextBoolean());

      var2.remove();
      return true;
   }

   /** @deprecated */
   @Deprecated
   public boolean add(Boolean var1) {
      return BooleanCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean contains(Object var1) {
      return BooleanCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean remove(Object var1) {
      return BooleanCollection.super.remove(var1);
   }

   public boolean[] toArray(boolean[] var1) {
      if (var1 == null || var1.length < this.size()) {
         var1 = new boolean[this.size()];
      }

      BooleanIterators.unwrap(this.iterator(), var1);
      return var1;
   }

   public boolean[] toBooleanArray() {
      return this.toArray((boolean[])null);
   }

   /** @deprecated */
   @Deprecated
   public boolean[] toBooleanArray(boolean[] var1) {
      return this.toArray(var1);
   }

   public boolean addAll(BooleanCollection var1) {
      boolean var2 = false;
      BooleanIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.add(var3.nextBoolean())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean containsAll(BooleanCollection var1) {
      BooleanIterator var2 = var1.iterator();

      do {
         if (!var2.hasNext()) {
            return true;
         }
      } while(this.contains(var2.nextBoolean()));

      return false;
   }

   public boolean removeAll(BooleanCollection var1) {
      boolean var2 = false;
      BooleanIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.rem(var3.nextBoolean())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(BooleanCollection var1) {
      boolean var2 = false;
      BooleanIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.nextBoolean())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      BooleanIterator var2 = this.iterator();
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         boolean var4 = var2.nextBoolean();
         var1.append(String.valueOf(var4));
      }

      var1.append("}");
      return var1.toString();
   }
}
