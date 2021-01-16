package org.apache.logging.log4j.spi;

import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ProviderUtil;

public final class ThreadContextMapFactory {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String THREAD_CONTEXT_KEY = "log4j2.threadContextMap";
   private static final String GC_FREE_THREAD_CONTEXT_KEY = "log4j2.garbagefree.threadContextMap";

   private ThreadContextMapFactory() {
      super();
   }

   public static ThreadContextMap createThreadContextMap() {
      PropertiesUtil var0 = PropertiesUtil.getProperties();
      String var1 = var0.getStringProperty("log4j2.threadContextMap");
      ClassLoader var2 = ProviderUtil.findClassLoader();
      ThreadContextMap var3 = null;
      if (var1 != null) {
         try {
            Class var4 = var2.loadClass(var1);
            if (ThreadContextMap.class.isAssignableFrom(var4)) {
               var3 = (ThreadContextMap)var4.newInstance();
            }
         } catch (ClassNotFoundException var9) {
            LOGGER.error((String)"Unable to locate configured ThreadContextMap {}", (Object)var1);
         } catch (Exception var10) {
            LOGGER.error((String)"Unable to create configured ThreadContextMap {}", (Object)var1, (Object)var10);
         }
      }

      if (var3 == null && ProviderUtil.hasProviders() && LogManager.getFactory() != null) {
         String var12 = LogManager.getFactory().getClass().getName();
         Iterator var5 = ProviderUtil.getProviders().iterator();

         label46:
         while(true) {
            Provider var6;
            Class var7;
            do {
               do {
                  if (!var5.hasNext()) {
                     break label46;
                  }

                  var6 = (Provider)var5.next();
               } while(!var12.equals(var6.getClassName()));

               var7 = var6.loadThreadContextMap();
            } while(var7 == null);

            try {
               var3 = (ThreadContextMap)var7.newInstance();
               break;
            } catch (Exception var11) {
               LOGGER.error((String)"Unable to locate or load configured ThreadContextMap {}", (Object)var6.getThreadContextMap(), (Object)var11);
               var3 = createDefaultThreadContextMap();
            }
         }
      }

      if (var3 == null) {
         var3 = createDefaultThreadContextMap();
      }

      return var3;
   }

   private static ThreadContextMap createDefaultThreadContextMap() {
      if (Constants.ENABLE_THREADLOCALS) {
         return (ThreadContextMap)(PropertiesUtil.getProperties().getBooleanProperty("log4j2.garbagefree.threadContextMap") ? new GarbageFreeSortedArrayThreadContextMap() : new CopyOnWriteSortedArrayThreadContextMap());
      } else {
         return new DefaultThreadContextMap(true);
      }
   }
}
