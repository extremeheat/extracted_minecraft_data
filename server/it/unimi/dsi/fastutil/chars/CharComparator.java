package it.unimi.dsi.fastutil.chars;

import java.util.Comparator;

@FunctionalInterface
public interface CharComparator extends Comparator<Character> {
   int compare(char var1, char var2);

   /** @deprecated */
   @Deprecated
   default int compare(Character var1, Character var2) {
      return this.compare(var1, var2);
   }
}
