package it.unimi.dsi.fastutil.longs;

import java.util.Comparator;

@FunctionalInterface
public interface LongComparator extends Comparator<Long> {
   int compare(long var1, long var3);

   /** @deprecated */
   @Deprecated
   default int compare(Long var1, Long var2) {
      return this.compare(var1, var2);
   }
}
