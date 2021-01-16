package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class SystemPropertyUtil {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SystemPropertyUtil.class);

   public static boolean contains(String var0) {
      return get(var0) != null;
   }

   public static String get(String var0) {
      return get(var0, (String)null);
   }

   public static String get(final String var0, String var1) {
      if (var0 == null) {
         throw new NullPointerException("key");
      } else if (var0.isEmpty()) {
         throw new IllegalArgumentException("key must not be empty.");
      } else {
         String var2 = null;

         try {
            if (System.getSecurityManager() == null) {
               var2 = System.getProperty(var0);
            } else {
               var2 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
                  public String run() {
                     return System.getProperty(var0);
                  }
               });
            }
         } catch (SecurityException var4) {
            logger.warn("Unable to retrieve a system property '{}'; default values will be used.", var0, var4);
         }

         return var2 == null ? var1 : var2;
      }
   }

   public static boolean getBoolean(String var0, boolean var1) {
      String var2 = get(var0);
      if (var2 == null) {
         return var1;
      } else {
         var2 = var2.trim().toLowerCase();
         if (var2.isEmpty()) {
            return var1;
         } else if (!"true".equals(var2) && !"yes".equals(var2) && !"1".equals(var2)) {
            if (!"false".equals(var2) && !"no".equals(var2) && !"0".equals(var2)) {
               logger.warn("Unable to parse the boolean system property '{}':{} - using the default value: {}", var0, var2, var1);
               return var1;
            } else {
               return false;
            }
         } else {
            return true;
         }
      }
   }

   public static int getInt(String var0, int var1) {
      String var2 = get(var0);
      if (var2 == null) {
         return var1;
      } else {
         var2 = var2.trim();

         try {
            return Integer.parseInt(var2);
         } catch (Exception var4) {
            logger.warn("Unable to parse the integer system property '{}':{} - using the default value: {}", var0, var2, var1);
            return var1;
         }
      }
   }

   public static long getLong(String var0, long var1) {
      String var3 = get(var0);
      if (var3 == null) {
         return var1;
      } else {
         var3 = var3.trim();

         try {
            return Long.parseLong(var3);
         } catch (Exception var5) {
            logger.warn("Unable to parse the long integer system property '{}':{} - using the default value: {}", var0, var3, var1);
            return var1;
         }
      }
   }

   private SystemPropertyUtil() {
      super();
   }
}
