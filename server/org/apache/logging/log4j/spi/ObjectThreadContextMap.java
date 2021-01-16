package org.apache.logging.log4j.spi;

import java.util.Map;

public interface ObjectThreadContextMap extends CleanableThreadContextMap {
   <V> V getValue(String var1);

   <V> void putValue(String var1, V var2);

   <V> void putAllValues(Map<String, V> var1);
}
