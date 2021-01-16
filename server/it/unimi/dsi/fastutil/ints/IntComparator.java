package it.unimi.dsi.fastutil.ints;

import java.util.Comparator;

@FunctionalInterface
public interface IntComparator extends Comparator<Integer> {
   int compare(int var1, int var2);

   /** @deprecated */
   @Deprecated
   default int compare(Integer var1, Integer var2) {
      return this.compare(var1, var2);
   }
}
