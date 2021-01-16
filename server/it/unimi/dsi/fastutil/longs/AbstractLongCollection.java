package it.unimi.dsi.fastutil.longs;

import java.util.AbstractCollection;

public abstract class AbstractLongCollection extends AbstractCollection<Long> implements LongCollection {
   protected AbstractLongCollection() {
      super();
   }

   public abstract LongIterator iterator();

   public boolean add(long var1) {
      throw new UnsupportedOperationException();
   }

   public boolean contains(long var1) {
      LongIterator var3 = this.iterator();

      do {
         if (!var3.hasNext()) {
            return false;
         }
      } while(var1 != var3.nextLong());

      return true;
   }

   public boolean rem(long var1) {
      LongIterator var3 = this.iterator();

      do {
         if (!var3.hasNext()) {
            return false;
         }
      } while(var1 != var3.nextLong());

      var3.remove();
      return true;
   }

   /** @deprecated */
   @Deprecated
   public boolean add(Long var1) {
      return LongCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean contains(Object var1) {
      return LongCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean remove(Object var1) {
      return LongCollection.super.remove(var1);
   }

   public long[] toArray(long[] var1) {
      if (var1 == null || var1.length < this.size()) {
         var1 = new long[this.size()];
      }

      LongIterators.unwrap(this.iterator(), var1);
      return var1;
   }

   public long[] toLongArray() {
      return this.toArray((long[])null);
   }

   /** @deprecated */
   @Deprecated
   public long[] toLongArray(long[] var1) {
      return this.toArray(var1);
   }

   public boolean addAll(LongCollection var1) {
      boolean var2 = false;
      LongIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.add(var3.nextLong())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean containsAll(LongCollection var1) {
      LongIterator var2 = var1.iterator();

      do {
         if (!var2.hasNext()) {
            return true;
         }
      } while(this.contains(var2.nextLong()));

      return false;
   }

   public boolean removeAll(LongCollection var1) {
      boolean var2 = false;
      LongIterator var3 = var1.iterator();

      while(var3.hasNext()) {
         if (this.rem(var3.nextLong())) {
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(LongCollection var1) {
      boolean var2 = false;
      LongIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.nextLong())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      LongIterator var2 = this.iterator();
      int var3 = this.size();
      boolean var6 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var6) {
            var6 = false;
         } else {
            var1.append(", ");
         }

         long var4 = var2.nextLong();
         var1.append(String.valueOf(var4));
      }

      var1.append("}");
      return var1.toString();
   }
}
