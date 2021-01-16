package org.apache.logging.log4j.core.util;

public final class Closer {
   private Closer() {
      super();
   }

   public static void close(AutoCloseable var0) throws Exception {
      if (var0 != null) {
         var0.close();
      }

   }

   public static boolean closeSilently(AutoCloseable var0) {
      try {
         close(var0);
         return true;
      } catch (Exception var2) {
         return false;
      }
   }
}
