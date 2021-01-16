package org.apache.logging.log4j.spi;

import java.util.Map;
import org.apache.logging.log4j.util.StringMap;

public interface ThreadContextMap2 extends ThreadContextMap {
   void putAll(Map<String, String> var1);

   StringMap getReadOnlyContextData();
}
