package it.unimi.dsi.fastutil.ints;

import java.util.Set;

public abstract class AbstractIntSet extends AbstractIntCollection implements Cloneable, IntSet {
   protected AbstractIntSet() {
      super();
   }

   public abstract IntIterator iterator();

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Set)) {
         return false;
      } else {
         Set var2 = (Set)var1;
         return var2.size() != this.size() ? false : this.containsAll(var2);
      }
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      int var4;
      for(IntIterator var3 = this.iterator(); var2-- != 0; var1 += var4) {
         var4 = var3.nextInt();
      }

      return var1;
   }

   public boolean remove(int var1) {
      return super.rem(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean rem(int var1) {
      return this.remove(var1);
   }
}
