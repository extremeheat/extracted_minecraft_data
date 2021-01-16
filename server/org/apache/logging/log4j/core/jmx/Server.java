package org.apache.logging.log4j.core.jmx;

import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.QueryExp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AsyncAppender;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.async.AsyncLoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.selector.ContextSelector;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.Log4jThreadFactory;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class Server {
   public static final String DOMAIN = "org.apache.logging.log4j2";
   private static final String PROPERTY_DISABLE_JMX = "log4j2.disable.jmx";
   private static final String PROPERTY_ASYNC_NOTIF = "log4j2.jmx.notify.async";
   private static final String THREAD_NAME_PREFIX = "jmx.notif";
   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   static final Executor executor = isJmxDisabled() ? null : createExecutor();

   private Server() {
      super();
   }

   private static ExecutorService createExecutor() {
      boolean var0 = !Constants.IS_WEB_APP;
      boolean var1 = PropertiesUtil.getProperties().getBooleanProperty("log4j2.jmx.notify.async", var0);
      return var1 ? Executors.newFixedThreadPool(1, Log4jThreadFactory.createDaemonThreadFactory("jmx.notif")) : null;
   }

   public static String escape(String var0) {
      StringBuilder var1 = new StringBuilder(var0.length() * 2);
      boolean var2 = false;

      for(int var3 = 0; var3 < var0.length(); ++var3) {
         char var4 = var0.charAt(var3);
         switch(var4) {
         case '\n':
            var1.append("\\n");
            var2 = true;
         case '\r':
            continue;
         case '"':
         case '*':
         case '?':
         case '\\':
            var1.append('\\');
            var2 = true;
            break;
         case ',':
         case ':':
         case '=':
            var2 = true;
         }

         var1.append(var4);
      }

      if (var2) {
         var1.insert(0, '"');
         var1.append('"');
      }

      return var1.toString();
   }

   private static boolean isJmxDisabled() {
      return PropertiesUtil.getProperties().getBooleanProperty("log4j2.disable.jmx");
   }

   public static void reregisterMBeansAfterReconfigure() {
      if (isJmxDisabled()) {
         LOGGER.debug("JMX disabled for Log4j2. Not registering MBeans.");
      } else {
         MBeanServer var0 = ManagementFactory.getPlatformMBeanServer();
         reregisterMBeansAfterReconfigure(var0);
      }
   }

   public static void reregisterMBeansAfterReconfigure(MBeanServer var0) {
      if (isJmxDisabled()) {
         LOGGER.debug("JMX disabled for Log4j2. Not registering MBeans.");
      } else {
         try {
            ContextSelector var1 = getContextSelector();
            if (var1 == null) {
               LOGGER.debug("Could not register MBeans: no ContextSelector found.");
               return;
            }

            LOGGER.trace("Reregistering MBeans after reconfigure. Selector={}", var1);
            List var2 = var1.getLoggerContexts();
            int var3 = 0;
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               LoggerContext var5 = (LoggerContext)var4.next();
               ++var3;
               LOGGER.trace("Reregistering context ({}/{}): '{}' {}", var3, var2.size(), var5.getName(), var5);
               unregisterLoggerContext(var5.getName(), var0);
               LoggerContextAdmin var6 = new LoggerContextAdmin(var5, executor);
               register(var0, var6, var6.getObjectName());
               if (var5 instanceof AsyncLoggerContext) {
                  RingBufferAdmin var7 = ((AsyncLoggerContext)var5).createRingBufferAdmin();
                  if (var7.getBufferSize() > 0L) {
                     register(var0, var7, var7.getObjectName());
                  }
               }

               registerStatusLogger(var5.getName(), var0, executor);
               registerContextSelector(var5.getName(), var1, var0, executor);
               registerLoggerConfigs(var5, var0, executor);
               registerAppenders(var5, var0, executor);
            }
         } catch (Exception var8) {
            LOGGER.error("Could not register mbeans", var8);
         }

      }
   }

   public static void unregisterMBeans() {
      if (isJmxDisabled()) {
         LOGGER.debug("JMX disabled for Log4j2. Not unregistering MBeans.");
      } else {
         MBeanServer var0 = ManagementFactory.getPlatformMBeanServer();
         unregisterMBeans(var0);
      }
   }

   public static void unregisterMBeans(MBeanServer var0) {
      unregisterStatusLogger("*", var0);
      unregisterContextSelector("*", var0);
      unregisterContexts(var0);
      unregisterLoggerConfigs("*", var0);
      unregisterAsyncLoggerRingBufferAdmins("*", var0);
      unregisterAsyncLoggerConfigRingBufferAdmins("*", var0);
      unregisterAppenders("*", var0);
      unregisterAsyncAppenders("*", var0);
   }

   private static ContextSelector getContextSelector() {
      LoggerContextFactory var0 = LogManager.getFactory();
      if (var0 instanceof Log4jContextFactory) {
         ContextSelector var1 = ((Log4jContextFactory)var0).getSelector();
         return var1;
      } else {
         return null;
      }
   }

   public static void unregisterLoggerContext(String var0) {
      if (isJmxDisabled()) {
         LOGGER.debug("JMX disabled for Log4j2. Not unregistering MBeans.");
      } else {
         MBeanServer var1 = ManagementFactory.getPlatformMBeanServer();
         unregisterLoggerContext(var0, var1);
      }
   }

   public static void unregisterLoggerContext(String var0, MBeanServer var1) {
      String var2 = String.format("org.apache.logging.log4j2:type=%s", escape(var0), "*");
      unregisterAllMatching(var2, var1);
      unregisterStatusLogger(var0, var1);
      unregisterContextSelector(var0, var1);
      unregisterLoggerConfigs(var0, var1);
      unregisterAppenders(var0, var1);
      unregisterAsyncAppenders(var0, var1);
      unregisterAsyncLoggerRingBufferAdmins(var0, var1);
      unregisterAsyncLoggerConfigRingBufferAdmins(var0, var1);
   }

   private static void registerStatusLogger(String var0, MBeanServer var1, Executor var2) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      StatusLoggerAdmin var3 = new StatusLoggerAdmin(var0, var2);
      register(var1, var3, var3.getObjectName());
   }

   private static void registerContextSelector(String var0, ContextSelector var1, MBeanServer var2, Executor var3) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      ContextSelectorAdmin var4 = new ContextSelectorAdmin(var0, var1);
      register(var2, var4, var4.getObjectName());
   }

   private static void unregisterStatusLogger(String var0, MBeanServer var1) {
      String var2 = String.format("org.apache.logging.log4j2:type=%s,component=StatusLogger", escape(var0), "*");
      unregisterAllMatching(var2, var1);
   }

   private static void unregisterContextSelector(String var0, MBeanServer var1) {
      String var2 = String.format("org.apache.logging.log4j2:type=%s,component=ContextSelector", escape(var0), "*");
      unregisterAllMatching(var2, var1);
   }

   private static void unregisterLoggerConfigs(String var0, MBeanServer var1) {
      String var2 = "org.apache.logging.log4j2:type=%s,component=Loggers,name=%s";
      String var3 = String.format("org.apache.logging.log4j2:type=%s,component=Loggers,name=%s", escape(var0), "*");
      unregisterAllMatching(var3, var1);
   }

   private static void unregisterContexts(MBeanServer var0) {
      String var1 = "org.apache.logging.log4j2:type=%s";
      String var2 = String.format("org.apache.logging.log4j2:type=%s", "*");
      unregisterAllMatching(var2, var0);
   }

   private static void unregisterAppenders(String var0, MBeanServer var1) {
      String var2 = "org.apache.logging.log4j2:type=%s,component=Appenders,name=%s";
      String var3 = String.format("org.apache.logging.log4j2:type=%s,component=Appenders,name=%s", escape(var0), "*");
      unregisterAllMatching(var3, var1);
   }

   private static void unregisterAsyncAppenders(String var0, MBeanServer var1) {
      String var2 = "org.apache.logging.log4j2:type=%s,component=AsyncAppenders,name=%s";
      String var3 = String.format("org.apache.logging.log4j2:type=%s,component=AsyncAppenders,name=%s", escape(var0), "*");
      unregisterAllMatching(var3, var1);
   }

   private static void unregisterAsyncLoggerRingBufferAdmins(String var0, MBeanServer var1) {
      String var2 = "org.apache.logging.log4j2:type=%s,component=AsyncLoggerRingBuffer";
      String var3 = String.format("org.apache.logging.log4j2:type=%s,component=AsyncLoggerRingBuffer", escape(var0));
      unregisterAllMatching(var3, var1);
   }

   private static void unregisterAsyncLoggerConfigRingBufferAdmins(String var0, MBeanServer var1) {
      String var2 = "org.apache.logging.log4j2:type=%s,component=Loggers,name=%s,subtype=RingBuffer";
      String var3 = String.format("org.apache.logging.log4j2:type=%s,component=Loggers,name=%s,subtype=RingBuffer", escape(var0), "*");
      unregisterAllMatching(var3, var1);
   }

   private static void unregisterAllMatching(String var0, MBeanServer var1) {
      try {
         ObjectName var2 = new ObjectName(var0);
         Set var3 = var1.queryNames(var2, (QueryExp)null);
         if (var3.isEmpty()) {
            LOGGER.trace("Unregistering but no MBeans found matching '{}'", var0);
         } else {
            LOGGER.trace("Unregistering {} MBeans: {}", var3.size(), var3);
         }

         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            ObjectName var5 = (ObjectName)var4.next();
            var1.unregisterMBean(var5);
         }
      } catch (InstanceNotFoundException var6) {
         LOGGER.debug("Could not unregister MBeans for " + var0 + ". Ignoring " + var6);
      } catch (Exception var7) {
         LOGGER.error("Could not unregister MBeans for " + var0, var7);
      }

   }

   private static void registerLoggerConfigs(LoggerContext var0, MBeanServer var1, Executor var2) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      Map var3 = var0.getConfiguration().getLoggers();
      Iterator var4 = var3.keySet().iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         LoggerConfig var6 = (LoggerConfig)var3.get(var5);
         LoggerConfigAdmin var7 = new LoggerConfigAdmin(var0, var6);
         register(var1, var7, var7.getObjectName());
         if (var6 instanceof AsyncLoggerConfig) {
            AsyncLoggerConfig var8 = (AsyncLoggerConfig)var6;
            RingBufferAdmin var9 = var8.createRingBufferAdmin(var0.getName());
            register(var1, var9, var9.getObjectName());
         }
      }

   }

   private static void registerAppenders(LoggerContext var0, MBeanServer var1, Executor var2) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      Map var3 = var0.getConfiguration().getAppenders();
      Iterator var4 = var3.keySet().iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         Appender var6 = (Appender)var3.get(var5);
         if (var6 instanceof AsyncAppender) {
            AsyncAppender var7 = (AsyncAppender)var6;
            AsyncAppenderAdmin var8 = new AsyncAppenderAdmin(var0.getName(), var7);
            register(var1, var8, var8.getObjectName());
         } else {
            AppenderAdmin var9 = new AppenderAdmin(var0.getName(), var6);
            register(var1, var9, var9.getObjectName());
         }
      }

   }

   private static void register(MBeanServer var0, Object var1, ObjectName var2) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      LOGGER.debug("Registering MBean {}", var2);
      var0.registerMBean(var1, var2);
   }
}
