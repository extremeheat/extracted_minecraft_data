package it.unimi.dsi.fastutil.booleans;

import java.util.Comparator;

@FunctionalInterface
public interface BooleanComparator extends Comparator<Boolean> {
   int compare(boolean var1, boolean var2);

   /** @deprecated */
   @Deprecated
   default int compare(Boolean var1, Boolean var2) {
      return this.compare(var1, var2);
   }
}
