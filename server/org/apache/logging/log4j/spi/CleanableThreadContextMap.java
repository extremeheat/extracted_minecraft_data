package org.apache.logging.log4j.spi;

public interface CleanableThreadContextMap extends ThreadContextMap2 {
   void removeAll(Iterable<String> var1);
}
