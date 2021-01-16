package it.unimi.dsi.fastutil.shorts;

import java.util.AbstractCollection;

public abstract class AbstractShortCollection extends AbstractCollection<Short> implements ShortCollection {
   protected AbstractShortCollection() {
      super();
   }

   public abstract ShortIterator iterator();

   public boolean add(short var1) {
      throw new UnsupportedOperationException();
   }

   public boolean contains(short var1) {
      ShortIterator var2 = this.iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(var1 != var2.nextShort());

      return true;
   }

   public boolean rem(short var1) {
      ShortIterator var2 = this.iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(var1 != var2.nextShort());

      var2.remove();
      return true;
   }

   /** @deprecated */
   @Deprecated
   public boolean add(Short var1) {
      return ShortCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean contains(Object var1) {
      return ShortCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean remove(Object var1) {
      return ShortCollection.super.remove(var1);
   }

   public short[] toArray(short[] var1) {
      if (var1 == null || var1.length < this.size()) {
         var1 = new short[this.size()];
      }

      ShortIterators.unwrap(this.iterator(), var1);
      return var1;
   }

   public short[] toShortArray() {
      return this.toArray((short[])null);
   }

   /** @deprecated */
   @Deprecated
   public short[] toShortArray(short[] var1) {
      return this.toArray(var1);
   }

   public boolean addAll(ShortCollection var1) {
      boolean var2 = false;
      ShortIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.add(var3.nextShort())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean containsAll(ShortCollection var1) {
      ShortIterator var2 = var1.iterator();

      do {
         if (!var2.hasNext()) {
            return true;
         }
      } while(this.contains(var2.nextShort()));

      return false;
   }

   public boolean removeAll(ShortCollection var1) {
      boolean var2 = false;
      ShortIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.rem(var3.nextShort())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(ShortCollection var1) {
      boolean var2 = false;
      ShortIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.nextShort())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ShortIterator var2 = this.iterator();
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         short var4 = var2.nextShort();
         var1.append(String.valueOf(var4));
      }

      var1.append("}");
      return var1.toString();
   }
}
