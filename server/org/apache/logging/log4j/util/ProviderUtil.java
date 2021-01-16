package org.apache.logging.log4j.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.status.StatusLogger;

public final class ProviderUtil {
   protected static final String PROVIDER_RESOURCE = "META-INF/log4j-provider.properties";
   protected static final Collection<Provider> PROVIDERS = new HashSet();
   protected static final Lock STARTUP_LOCK = new ReentrantLock();
   private static final String API_VERSION = "Log4jAPIVersion";
   private static final String[] COMPATIBLE_API_VERSIONS = new String[]{"2.0.0", "2.1.0", "2.2.0", "2.3.0", "2.4.0", "2.5.0", "2.6.0"};
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static volatile ProviderUtil instance;

   private ProviderUtil() {
      super();
      Iterator var1 = LoaderUtil.findUrlResources("META-INF/log4j-provider.properties").iterator();

      while(var1.hasNext()) {
         LoaderUtil.UrlResource var2 = (LoaderUtil.UrlResource)var1.next();
         loadProvider(var2.getUrl(), var2.getClassLoader());
      }

   }

   protected static void loadProvider(URL var0, ClassLoader var1) {
      try {
         Properties var2 = PropertiesUtil.loadClose(var0.openStream(), var0);
         if (validVersion(var2.getProperty("Log4jAPIVersion"))) {
            Provider var3 = new Provider(var2, var0, var1);
            PROVIDERS.add(var3);
            LOGGER.debug((String)"Loaded Provider {}", (Object)var3);
         }
      } catch (IOException var4) {
         LOGGER.error((String)"Unable to open {}", (Object)var0, (Object)var4);
      }

   }

   /** @deprecated */
   @Deprecated
   protected static void loadProviders(Enumeration<URL> var0, ClassLoader var1) {
      if (var0 != null) {
         while(var0.hasMoreElements()) {
            loadProvider((URL)var0.nextElement(), var1);
         }
      }

   }

   public static Iterable<Provider> getProviders() {
      lazyInit();
      return PROVIDERS;
   }

   public static boolean hasProviders() {
      lazyInit();
      return !PROVIDERS.isEmpty();
   }

   protected static void lazyInit() {
      if (instance == null) {
         try {
            STARTUP_LOCK.lockInterruptibly();

            try {
               if (instance == null) {
                  instance = new ProviderUtil();
               }
            } finally {
               STARTUP_LOCK.unlock();
            }
         } catch (InterruptedException var4) {
            LOGGER.fatal((String)"Interrupted before Log4j Providers could be loaded.", (Throwable)var4);
            Thread.currentThread().interrupt();
         }
      }

   }

   public static ClassLoader findClassLoader() {
      return LoaderUtil.getThreadContextClassLoader();
   }

   private static boolean validVersion(String var0) {
      String[] var1 = COMPATIBLE_API_VERSIONS;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         if (var0.startsWith(var4)) {
            return true;
         }
      }

      return false;
   }
}
