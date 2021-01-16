package org.apache.logging.log4j.core.impl;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.spi.CopyOnWrite;
import org.apache.logging.log4j.spi.DefaultThreadContextMap;
import org.apache.logging.log4j.spi.ReadOnlyThreadContextMap;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public class ContextDataInjectorFactory {
   public ContextDataInjectorFactory() {
      super();
   }

   public static ContextDataInjector createInjector() {
      String var0 = PropertiesUtil.getProperties().getStringProperty("log4j2.ContextDataInjector");
      if (var0 == null) {
         return createDefaultInjector();
      } else {
         try {
            Class var1 = LoaderUtil.loadClass(var0).asSubclass(ContextDataInjector.class);
            return (ContextDataInjector)var1.newInstance();
         } catch (Exception var3) {
            ContextDataInjector var2 = createDefaultInjector();
            StatusLogger.getLogger().warn("Could not create ContextDataInjector for '{}', using default {}: {}", var0, var2.getClass().getName(), var3);
            return var2;
         }
      }
   }

   private static ContextDataInjector createDefaultInjector() {
      ReadOnlyThreadContextMap var0 = ThreadContext.getThreadContextMap();
      if (!(var0 instanceof DefaultThreadContextMap) && var0 != null) {
         return (ContextDataInjector)(var0 instanceof CopyOnWrite ? new ThreadContextDataInjector.ForCopyOnWriteThreadContextMap() : new ThreadContextDataInjector.ForGarbageFreeThreadContextMap());
      } else {
         return new ThreadContextDataInjector.ForDefaultThreadContextMap();
      }
   }
}
