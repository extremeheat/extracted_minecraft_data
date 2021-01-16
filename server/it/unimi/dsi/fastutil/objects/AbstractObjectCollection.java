package it.unimi.dsi.fastutil.objects;

import java.util.AbstractCollection;

public abstract class AbstractObjectCollection<K> extends AbstractCollection<K> implements ObjectCollection<K> {
   protected AbstractObjectCollection() {
      super();
   }

   public abstract ObjectIterator<K> iterator();

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = this.iterator();
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Object var4 = var2.next();
         if (this == var4) {
            var1.append("(this collection)");
         } else {
            var1.append(String.valueOf(var4));
         }
      }

      var1.append("}");
      return var1.toString();
   }
}
