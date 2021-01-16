package it.unimi.dsi.fastutil.chars;

import java.util.Set;

public abstract class AbstractCharSet extends AbstractCharCollection implements Cloneable, CharSet {
   protected AbstractCharSet() {
      super();
   }

   public abstract CharIterator iterator();

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

      char var4;
      for(CharIterator var3 = this.iterator(); var2-- != 0; var1 += var4) {
         var4 = var3.nextChar();
      }

      return var1;
   }

   public boolean remove(char var1) {
      return super.rem(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean rem(char var1) {
      return this.remove(var1);
   }
}
