package org.apache.logging.log4j.core.util;

import javax.naming.Context;
import javax.naming.NamingException;

public final class JndiCloser {
   private JndiCloser() {
      super();
   }

   public static void close(Context var0) throws NamingException {
      if (var0 != null) {
         var0.close();
      }

   }

   public static boolean closeSilently(Context var0) {
      try {
         close(var0);
         return true;
      } catch (NamingException var2) {
         return false;
      }
   }
}
