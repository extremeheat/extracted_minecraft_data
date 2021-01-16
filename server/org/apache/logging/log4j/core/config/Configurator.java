package org.apache.logging.log4j.core.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public final class Configurator {
   private static final String FQCN = Configurator.class.getName();
   private static final Logger LOGGER = StatusLogger.getLogger();

   private static Log4jContextFactory getFactory() {
      LoggerContextFactory var0 = LogManager.getFactory();
      if (var0 instanceof Log4jContextFactory) {
         return (Log4jContextFactory)var0;
      } else if (var0 != null) {
         LOGGER.error((String)"LogManager returned an instance of {} which does not implement {}. Unable to initialize Log4j.", (Object)var0.getClass().getName(), (Object)Log4jContextFactory.class.getName());
         return null;
      } else {
         LOGGER.fatal("LogManager did not return a LoggerContextFactory. This indicates something has gone terribly wrong!");
         return null;
      }
   }

   public static LoggerContext initialize(ClassLoader var0, ConfigurationSource var1) {
      return initialize((ClassLoader)var0, (ConfigurationSource)var1, (Object)null);
   }

   public static LoggerContext initialize(ClassLoader var0, ConfigurationSource var1, Object var2) {
      try {
         Log4jContextFactory var3 = getFactory();
         return var3 == null ? null : var3.getContext(FQCN, var0, var2, false, var1);
      } catch (Exception var4) {
         LOGGER.error((String)"There was a problem obtaining a LoggerContext using the configuration source [{}]", (Object)var1, (Object)var4);
         return null;
      }
   }

   public static LoggerContext initialize(String var0, ClassLoader var1, String var2) {
      return initialize(var0, var1, (String)var2, (Object)null);
   }

   public static LoggerContext initialize(String var0, ClassLoader var1, String var2, Object var3) {
      if (Strings.isBlank(var2)) {
         return initialize(var0, var1, (URI)null, var3);
      } else if (var2.contains(",")) {
         String[] var4 = var2.split(",");
         String var5 = null;
         ArrayList var6 = new ArrayList(var4.length);
         String[] var7 = var4;
         int var8 = var4.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String var10 = var7[var9];
            URI var11 = NetUtils.toURI(var5 != null ? var5 + ":" + var10.trim() : var10.trim());
            if (var5 == null && var11.getScheme() != null) {
               var5 = var11.getScheme();
            }

            var6.add(var11);
         }

         return initialize(var0, var1, (List)var6, var3);
      } else {
         return initialize(var0, var1, NetUtils.toURI(var2), var3);
      }
   }

   public static LoggerContext initialize(String var0, ClassLoader var1, URI var2) {
      return initialize(var0, var1, (URI)var2, (Object)null);
   }

   public static LoggerContext initialize(String var0, ClassLoader var1, URI var2, Object var3) {
      try {
         Log4jContextFactory var4 = getFactory();
         return var4 == null ? null : var4.getContext(FQCN, var1, var3, false, var2, var0);
      } catch (Exception var5) {
         LOGGER.error((String)"There was a problem initializing the LoggerContext [{}] using configuration at [{}].", (Object)var0, var2, var5);
         return null;
      }
   }

   public static LoggerContext initialize(String var0, ClassLoader var1, List<URI> var2, Object var3) {
      try {
         Log4jContextFactory var4 = getFactory();
         return var4 == null ? null : var4.getContext(FQCN, var1, var3, false, var2, var0);
      } catch (Exception var5) {
         LOGGER.error((String)"There was a problem initializing the LoggerContext [{}] using configurations at [{}].", (Object)var0, var2, var5);
         return null;
      }
   }

   public static LoggerContext initialize(String var0, String var1) {
      return initialize((String)var0, (ClassLoader)null, (String)var1);
   }

   public static LoggerContext initialize(Configuration var0) {
      return initialize((ClassLoader)null, (Configuration)var0, (Object)null);
   }

   public static LoggerContext initialize(ClassLoader var0, Configuration var1) {
      return initialize((ClassLoader)var0, (Configuration)var1, (Object)null);
   }

   public static LoggerContext initialize(ClassLoader var0, Configuration var1, Object var2) {
      try {
         Log4jContextFactory var3 = getFactory();
         return var3 == null ? null : var3.getContext(FQCN, var0, var2, false, var1);
      } catch (Exception var4) {
         LOGGER.error((String)"There was a problem initializing the LoggerContext using configuration {}", (Object)var1.getName(), (Object)var4);
         return null;
      }
   }

   public static void setAllLevels(String var0, Level var1) {
      LoggerContext var2 = LoggerContext.getContext(false);
      Configuration var3 = var2.getConfiguration();
      boolean var4 = setLevel(var0, var1, var3);
      Iterator var5 = var3.getLoggers().entrySet().iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         if (((String)var6.getKey()).startsWith(var0)) {
            var4 |= setLevel((LoggerConfig)var6.getValue(), var1);
         }
      }

      if (var4) {
         var2.updateLoggers();
      }

   }

   private static boolean setLevel(LoggerConfig var0, Level var1) {
      boolean var2 = !var0.getLevel().equals(var1);
      if (var2) {
         var0.setLevel(var1);
      }

      return var2;
   }

   public static void setLevel(Map<String, Level> var0) {
      LoggerContext var1 = LoggerContext.getContext(false);
      Configuration var2 = var1.getConfiguration();
      boolean var3 = false;

      String var6;
      Level var7;
      for(Iterator var4 = var0.entrySet().iterator(); var4.hasNext(); var3 |= setLevel(var6, var7, var2)) {
         Entry var5 = (Entry)var4.next();
         var6 = (String)var5.getKey();
         var7 = (Level)var5.getValue();
      }

      if (var3) {
         var1.updateLoggers();
      }

   }

   public static void setLevel(String var0, Level var1) {
      LoggerContext var2 = LoggerContext.getContext(false);
      if (Strings.isEmpty(var0)) {
         setRootLevel(var1);
      } else if (setLevel(var0, var1, var2.getConfiguration())) {
         var2.updateLoggers();
      }

   }

   private static boolean setLevel(String var0, Level var1, Configuration var2) {
      LoggerConfig var4 = var2.getLoggerConfig(var0);
      boolean var3;
      if (!var0.equals(var4.getName())) {
         var4 = new LoggerConfig(var0, var1, true);
         var2.addLogger(var0, var4);
         var4.setLevel(var1);
         var3 = true;
      } else {
         var3 = setLevel(var4, var1);
      }

      return var3;
   }

   public static void setRootLevel(Level var0) {
      LoggerContext var1 = LoggerContext.getContext(false);
      LoggerConfig var2 = var1.getConfiguration().getRootLogger();
      if (!var2.getLevel().equals(var0)) {
         var2.setLevel(var0);
         var1.updateLoggers();
      }

   }

   public static void shutdown(LoggerContext var0) {
      if (var0 != null) {
         var0.stop();
      }

   }

   public static boolean shutdown(LoggerContext var0, long var1, TimeUnit var3) {
      return var0 != null ? var0.stop(var1, var3) : true;
   }

   private Configurator() {
      super();
   }
}
