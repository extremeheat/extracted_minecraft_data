package it.unimi.dsi.fastutil.booleans;

import java.util.Set;

public abstract class AbstractBooleanSet extends AbstractBooleanCollection implements Cloneable, BooleanSet {
   protected AbstractBooleanSet() {
      super();
   }

   public abstract BooleanIterator iterator();

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

      boolean var4;
      for(BooleanIterator var3 = this.iterator(); var2-- != 0; var1 += var4 ? 1231 : 1237) {
         var4 = var3.nextBoolean();
      }

      return var1;
   }

   public boolean remove(boolean var1) {
      return super.rem(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean rem(boolean var1) {
      return this.remove(var1);
   }
}
