package org.apache.logging.log4j.core.util;

public final class Booleans {
   private Booleans() {
      super();
   }

   public static boolean parseBoolean(String var0, boolean var1) {
      return "true".equalsIgnoreCase(var0) || var1 && !"false".equalsIgnoreCase(var0);
   }
}
