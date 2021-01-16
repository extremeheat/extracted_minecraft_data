package it.unimi.dsi.fastutil.shorts;

import java.util.Comparator;

@FunctionalInterface
public interface ShortComparator extends Comparator<Short> {
   int compare(short var1, short var2);

   /** @deprecated */
   @Deprecated
   default int compare(Short var1, Short var2) {
      return this.compare(var1, var2);
   }
}
