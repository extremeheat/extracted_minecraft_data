package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import java.util.Set;

public abstract class AbstractDoubleSet extends AbstractDoubleCollection implements Cloneable, DoubleSet {
   protected AbstractDoubleSet() {
      super();
   }

   public abstract DoubleIterator iterator();

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

      double var4;
      for(DoubleIterator var3 = this.iterator(); var2-- != 0; var1 += HashCommon.double2int(var4)) {
         var4 = var3.nextDouble();
      }

      return var1;
   }

   public boolean remove(double var1) {
      return super.rem(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean rem(double var1) {
      return this.remove(var1);
   }
}
