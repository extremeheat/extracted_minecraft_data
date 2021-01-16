package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import java.util.Set;

public abstract class AbstractLongSet extends AbstractLongCollection implements Cloneable, LongSet {
   protected AbstractLongSet() {
      super();
   }

   public abstract LongIterator iterator();

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

      long var4;
      for(LongIterator var3 = this.iterator(); var2-- != 0; var1 += HashCommon.long2int(var4)) {
         var4 = var3.nextLong();
      }

      return var1;
   }

   public boolean remove(long var1) {
      return super.rem(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean rem(long var1) {
      return this.remove(var1);
   }
}
