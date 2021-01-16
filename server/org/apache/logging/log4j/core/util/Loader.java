package org.apache.logging.log4j.core.util;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

public final class Loader {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";

   private Loader() {
      super();
   }

   public static ClassLoader getClassLoader() {
      return getClassLoader(Loader.class, (Class)null);
   }

   public static ClassLoader getThreadContextClassLoader() {
      return LoaderUtil.getThreadContextClassLoader();
   }

   public static ClassLoader getClassLoader(Class<?> var0, Class<?> var1) {
      ClassLoader var2 = getThreadContextClassLoader();
      ClassLoader var3 = var0 == null ? null : var0.getClassLoader();
      ClassLoader var4 = var1 == null ? null : var1.getClassLoader();
      if (isChild(var2, var3)) {
         return isChild(var2, var4) ? var2 : var4;
      } else {
         return isChild(var3, var4) ? var3 : var4;
      }
   }

   public static URL getResource(String var0, ClassLoader var1) {
      try {
         ClassLoader var2 = getThreadContextClassLoader();
         URL var3;
         if (var2 != null) {
            LOGGER.trace((String)"Trying to find [{}] using context class loader {}.", (Object)var0, (Object)var2);
            var3 = var2.getResource(var0);
            if (var3 != null) {
               return var3;
            }
         }

         var2 = Loader.class.getClassLoader();
         if (var2 != null) {
            LOGGER.trace((String)"Trying to find [{}] using {} class loader.", (Object)var0, (Object)var2);
            var3 = var2.getResource(var0);
            if (var3 != null) {
               return var3;
            }
         }

         if (var1 != null) {
            LOGGER.trace((String)"Trying to find [{}] using {} class loader.", (Object)var0, (Object)var1);
            var3 = var1.getResource(var0);
            if (var3 != null) {
               return var3;
            }
         }
      } catch (Throwable var4) {
         LOGGER.warn("Caught Exception while in Loader.getResource. This may be innocuous.", var4);
      }

      LOGGER.trace((String)"Trying to find [{}] using ClassLoader.getSystemResource().", (Object)var0);
      return ClassLoader.getSystemResource(var0);
   }

   public static InputStream getResourceAsStream(String var0, ClassLoader var1) {
      try {
         ClassLoader var2 = getThreadContextClassLoader();
         InputStream var3;
         if (var2 != null) {
            LOGGER.trace((String)"Trying to find [{}] using context class loader {}.", (Object)var0, (Object)var2);
            var3 = var2.getResourceAsStream(var0);
            if (var3 != null) {
               return var3;
            }
         }

         var2 = Loader.class.getClassLoader();
         if (var2 != null) {
            LOGGER.trace((String)"Trying to find [{}] using {} class loader.", (Object)var0, (Object)var2);
            var3 = var2.getResourceAsStream(var0);
            if (var3 != null) {
               return var3;
            }
         }

         if (var1 != null) {
            LOGGER.trace((String)"Trying to find [{}] using {} class loader.", (Object)var0, (Object)var1);
            var3 = var1.getResourceAsStream(var0);
            if (var3 != null) {
               return var3;
            }
         }
      } catch (Throwable var4) {
         LOGGER.warn("Caught Exception while in Loader.getResource. This may be innocuous.", var4);
      }

      LOGGER.trace((String)"Trying to find [{}] using ClassLoader.getSystemResource().", (Object)var0);
      return ClassLoader.getSystemResourceAsStream(var0);
   }

   private static boolean isChild(ClassLoader var0, ClassLoader var1) {
      if (var0 != null && var1 != null) {
         ClassLoader var2;
         for(var2 = var0.getParent(); var2 != null && var2 != var1; var2 = var2.getParent()) {
         }

         return var2 != null;
      } else {
         return var0 != null;
      }
   }

   public static Class<?> initializeClass(String var0, ClassLoader var1) throws ClassNotFoundException {
      return Class.forName(var0, true, var1);
   }

   public static Class<?> loadClass(String var0, ClassLoader var1) throws ClassNotFoundException {
      return var1 != null ? var1.loadClass(var0) : null;
   }

   public static Class<?> loadSystemClass(String var0) throws ClassNotFoundException {
      try {
         return Class.forName(var0, true, ClassLoader.getSystemClassLoader());
      } catch (Throwable var2) {
         LOGGER.trace((String)"Couldn't use SystemClassLoader. Trying Class.forName({}).", (Object)var0, (Object)var2);
         return Class.forName(var0);
      }
   }

   public static Object newInstanceOf(String var0) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
      return LoaderUtil.newInstanceOf(var0);
   }

   public static <T> T newCheckedInstanceOf(String var0, Class<T> var1) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      return LoaderUtil.newCheckedInstanceOf(var0, var1);
   }

   public static boolean isClassAvailable(String var0) {
      return LoaderUtil.isClassAvailable(var0);
   }

   public static boolean isJansiAvailable() {
      return isClassAvailable("org.fusesource.jansi.AnsiRenderer");
   }
}
