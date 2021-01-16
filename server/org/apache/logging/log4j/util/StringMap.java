package org.apache.logging.log4j.util;

public interface StringMap extends ReadOnlyStringMap {
   void clear();

   boolean equals(Object var1);

   void freeze();

   int hashCode();

   boolean isFrozen();

   void putAll(ReadOnlyStringMap var1);

   void putValue(String var1, Object var2);

   void remove(String var1);
}
