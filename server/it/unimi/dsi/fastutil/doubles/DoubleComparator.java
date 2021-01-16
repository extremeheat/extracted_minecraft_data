package it.unimi.dsi.fastutil.doubles;

import java.util.Comparator;

@FunctionalInterface
public interface DoubleComparator extends Comparator<Double> {
   int compare(double var1, double var3);

   /** @deprecated */
   @Deprecated
   default int compare(Double var1, Double var2) {
      return this.compare(var1, var2);
   }
}
