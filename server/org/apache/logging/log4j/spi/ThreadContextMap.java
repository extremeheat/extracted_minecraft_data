package org.apache.logging.log4j.spi;

import java.util.Map;

public interface ThreadContextMap {
   void clear();

   boolean containsKey(String var1);

   String get(String var1);

   Map<String, String> getCopy();

   Map<String, String> getImmutableMapOrNull();

   boolean isEmpty();

   void put(String var1, String var2);

   void remove(String var1);
}
