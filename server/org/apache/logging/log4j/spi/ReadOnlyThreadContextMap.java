package org.apache.logging.log4j.spi;

import java.util.Map;
import org.apache.logging.log4j.util.StringMap;

public interface ReadOnlyThreadContextMap {
   void clear();

   boolean containsKey(String var1);

   String get(String var1);

   Map<String, String> getCopy();

   Map<String, String> getImmutableMapOrNull();

   StringMap getReadOnlyContextData();

   boolean isEmpty();
}
