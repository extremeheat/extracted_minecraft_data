package it.unimi.dsi.fastutil.objects;

import java.util.Set;

public abstract class AbstractReferenceSet<K> extends AbstractReferenceCollection<K> implements Cloneable, ReferenceSet<K> {
   protected AbstractReferenceSet() {
      super();
   }

   public abstract ObjectIterator<K> iterator();

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

      Object var4;
      for(ObjectIterator var3 = this.iterator(); var2-- != 0; var1 += System.identityHashCode(var4)) {
         var4 = var3.next();
      }

      return var1;
   }
}
