package it.unimi.dsi.fastutil.floats;

import java.util.Comparator;

@FunctionalInterface
public interface FloatComparator extends Comparator<Float> {
   int compare(float var1, float var2);

   /** @deprecated */
   @Deprecated
   default int compare(Float var1, Float var2) {
      return this.compare(var1, var2);
   }
}
