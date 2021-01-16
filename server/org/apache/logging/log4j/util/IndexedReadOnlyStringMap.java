package org.apache.logging.log4j.util;

public interface IndexedReadOnlyStringMap extends ReadOnlyStringMap {
   String getKeyAt(int var1);

   <V> V getValueAt(int var1);

   int indexOfKey(String var1);
}
