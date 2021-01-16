package org.apache.logging.log4j.util;

public final class Constants {
   public static final boolean IS_WEB_APP = PropertiesUtil.getProperties().getBooleanProperty("log4j2.is.webapp", isClassAvailable("javax.servlet.Servlet"));
   public static final boolean ENABLE_THREADLOCALS;
   public static final int JAVA_MAJOR_VERSION;

   private static boolean isClassAvailable(String var0) {
      try {
         return LoaderUtil.loadClass(var0) != null;
      } catch (Throwable var2) {
         return false;
      }
   }

   private Constants() {
      super();
   }

   private static int getMajorVersion() {
      String var0 = System.getProperty("java.version");
      String[] var1 = var0.split("-|\\.");

      try {
         int var3 = Integer.parseInt(var1[0]);
         boolean var2 = var3 != 1;
         return var2 ? var3 : Integer.parseInt(var1[1]);
      } catch (Exception var4) {
         return 0;
      }
   }

   static {
      ENABLE_THREADLOCALS = !IS_WEB_APP && PropertiesUtil.getProperties().getBooleanProperty("log4j2.enable.threadlocals", true);
      JAVA_MAJOR_VERSION = getMajorVersion();
   }
}
