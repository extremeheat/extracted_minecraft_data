package it.unimi.dsi.fastutil.bytes;

import java.util.Comparator;

@FunctionalInterface
public interface ByteComparator extends Comparator<Byte> {
   int compare(byte var1, byte var2);

   /** @deprecated */
   @Deprecated
   default int compare(Byte var1, Byte var2) {
      return this.compare(var1, var2);
   }
}
