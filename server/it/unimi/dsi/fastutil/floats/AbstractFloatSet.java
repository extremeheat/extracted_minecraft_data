package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import java.util.Set;

public abstract class AbstractFloatSet extends AbstractFloatCollection implements Cloneable, FloatSet {
   protected AbstractFloatSet() {
      super();
   }

   public abstract FloatIterator iterator();

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

      float var4;
      for(FloatIterator var3 = this.iterator(); var2-- != 0; var1 += HashCommon.float2int(var4)) {
         var4 = var3.nextFloat();
      }

      return var1;
   }

   public boolean remove(float var1) {
      return super.rem(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean rem(float var1) {
      return this.remove(var1);
   }
}
