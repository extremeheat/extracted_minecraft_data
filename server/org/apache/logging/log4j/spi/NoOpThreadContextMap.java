package org.apache.logging.log4j.spi;

import java.util.HashMap;
import java.util.Map;

public class NoOpThreadContextMap implements ThreadContextMap {
   public NoOpThreadContextMap() {
      super();
   }

   public void clear() {
   }

   public boolean containsKey(String var1) {
      return false;
   }

   public String get(String var1) {
      return null;
   }

   public Map<String, String> getCopy() {
      return new HashMap();
   }

   public Map<String, String> getImmutableMapOrNull() {
      return null;
   }

   public boolean isEmpty() {
      return true;
   }

   public void put(String var1, String var2) {
   }

   public void remove(String var1) {
   }
}
