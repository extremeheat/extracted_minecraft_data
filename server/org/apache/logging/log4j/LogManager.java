package org.apache.logging.log4j;

import java.net.URI;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;
import org.apache.logging.log4j.simple.SimpleLoggerContextFactory;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.spi.Terminable;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ProviderUtil;
import org.apache.logging.log4j.util.ReflectionUtil;

public class LogManager {
   public static final String FACTORY_PROPERTY_NAME = "log4j2.loggerContextFactory";
   public static final String ROOT_LOGGER_NAME = "";
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String FQCN = LogManager.class.getName();
   private static volatile LoggerContextFactory factory;

   protected LogManager() {
      super();
   }

   public static boolean exists(String var0) {
      return getContext().hasLogger(var0);
   }

   public static LoggerContext getContext() {
      try {
         return factory.getContext(FQCN, (ClassLoader)null, (Object)null, true);
      } catch (IllegalStateException var1) {
         LOGGER.warn(var1.getMessage() + " Using SimpleLogger");
         return (new SimpleLoggerContextFactory()).getContext(FQCN, (ClassLoader)null, (Object)null, true);
      }
   }

   public static LoggerContext getContext(boolean var0) {
      try {
         return factory.getContext(FQCN, (ClassLoader)null, (Object)null, var0, (URI)null, (String)null);
      } catch (IllegalStateException var2) {
         LOGGER.warn(var2.getMessage() + " Using SimpleLogger");
         return (new SimpleLoggerContextFactory()).getContext(FQCN, (ClassLoader)null, (Object)null, var0, (URI)null, (String)null);
      }
   }

   public static LoggerContext getContext(ClassLoader var0, boolean var1) {
      try {
         return factory.getContext(FQCN, var0, (Object)null, var1);
      } catch (IllegalStateException var3) {
         LOGGER.warn(var3.getMessage() + " Using SimpleLogger");
         return (new SimpleLoggerContextFactory()).getContext(FQCN, var0, (Object)null, var1);
      }
   }

   public static LoggerContext getContext(ClassLoader var0, boolean var1, Object var2) {
      try {
         return factory.getContext(FQCN, var0, var2, var1);
      } catch (IllegalStateException var4) {
         LOGGER.warn(var4.getMessage() + " Using SimpleLogger");
         return (new SimpleLoggerContextFactory()).getContext(FQCN, var0, var2, var1);
      }
   }

   public static LoggerContext getContext(ClassLoader var0, boolean var1, URI var2) {
      try {
         return factory.getContext(FQCN, var0, (Object)null, var1, var2, (String)null);
      } catch (IllegalStateException var4) {
         LOGGER.warn(var4.getMessage() + " Using SimpleLogger");
         return (new SimpleLoggerContextFactory()).getContext(FQCN, var0, (Object)null, var1, var2, (String)null);
      }
   }

   public static LoggerContext getContext(ClassLoader var0, boolean var1, Object var2, URI var3) {
      try {
         return factory.getContext(FQCN, var0, var2, var1, var3, (String)null);
      } catch (IllegalStateException var5) {
         LOGGER.warn(var5.getMessage() + " Using SimpleLogger");
         return (new SimpleLoggerContextFactory()).getContext(FQCN, var0, var2, var1, var3, (String)null);
      }
   }

   public static LoggerContext getContext(ClassLoader var0, boolean var1, Object var2, URI var3, String var4) {
      try {
         return factory.getContext(FQCN, var0, var2, var1, var3, var4);
      } catch (IllegalStateException var6) {
         LOGGER.warn(var6.getMessage() + " Using SimpleLogger");
         return (new SimpleLoggerContextFactory()).getContext(FQCN, var0, var2, var1, var3, var4);
      }
   }

   protected static LoggerContext getContext(String var0, boolean var1) {
      try {
         return factory.getContext(var0, (ClassLoader)null, (Object)null, var1);
      } catch (IllegalStateException var3) {
         LOGGER.warn(var3.getMessage() + " Using SimpleLogger");
         return (new SimpleLoggerContextFactory()).getContext(var0, (ClassLoader)null, (Object)null, var1);
      }
   }

   protected static LoggerContext getContext(String var0, ClassLoader var1, boolean var2) {
      try {
         return factory.getContext(var0, var1, (Object)null, var2);
      } catch (IllegalStateException var4) {
         LOGGER.warn(var4.getMessage() + " Using SimpleLogger");
         return (new SimpleLoggerContextFactory()).getContext(var0, var1, (Object)null, var2);
      }
   }

   public static void shutdown() {
      shutdown(false);
   }

   public static void shutdown(boolean var0) {
      shutdown(getContext(var0));
   }

   public static void shutdown(LoggerContext var0) {
      if (var0 != null && var0 instanceof Terminable) {
         ((Terminable)var0).terminate();
      }

   }

   public static LoggerContextFactory getFactory() {
      return factory;
   }

   public static void setFactory(LoggerContextFactory var0) {
      factory = var0;
   }

   public static Logger getFormatterLogger() {
      return getFormatterLogger(ReflectionUtil.getCallerClass(2));
   }

   public static Logger getFormatterLogger(Class<?> var0) {
      return getLogger((Class)(var0 != null ? var0 : ReflectionUtil.getCallerClass(2)), (MessageFactory)StringFormatterMessageFactory.INSTANCE);
   }

   public static Logger getFormatterLogger(Object var0) {
      return getLogger((Class)(var0 != null ? var0.getClass() : ReflectionUtil.getCallerClass(2)), (MessageFactory)StringFormatterMessageFactory.INSTANCE);
   }

   public static Logger getFormatterLogger(String var0) {
      return var0 == null ? getFormatterLogger(ReflectionUtil.getCallerClass(2)) : getLogger((String)var0, (MessageFactory)StringFormatterMessageFactory.INSTANCE);
   }

   private static Class<?> callerClass(Class<?> var0) {
      if (var0 != null) {
         return var0;
      } else {
         Class var1 = ReflectionUtil.getCallerClass(3);
         if (var1 == null) {
            throw new UnsupportedOperationException("No class provided, and an appropriate one cannot be found.");
         } else {
            return var1;
         }
      }
   }

   public static Logger getLogger() {
      return getLogger(ReflectionUtil.getCallerClass(2));
   }

   public static Logger getLogger(Class<?> var0) {
      Class var1 = callerClass(var0);
      return getContext(var1.getClassLoader(), false).getLogger(var1.getName());
   }

   public static Logger getLogger(Class<?> var0, MessageFactory var1) {
      Class var2 = callerClass(var0);
      return getContext(var2.getClassLoader(), false).getLogger(var2.getName(), var1);
   }

   public static Logger getLogger(MessageFactory var0) {
      return getLogger(ReflectionUtil.getCallerClass(2), var0);
   }

   public static Logger getLogger(Object var0) {
      return getLogger(var0 != null ? var0.getClass() : ReflectionUtil.getCallerClass(2));
   }

   public static Logger getLogger(Object var0, MessageFactory var1) {
      return getLogger(var0 != null ? var0.getClass() : ReflectionUtil.getCallerClass(2), var1);
   }

   public static Logger getLogger(String var0) {
      return (Logger)(var0 != null ? getContext(false).getLogger(var0) : getLogger(ReflectionUtil.getCallerClass(2)));
   }

   public static Logger getLogger(String var0, MessageFactory var1) {
      return (Logger)(var0 != null ? getContext(false).getLogger(var0, var1) : getLogger(ReflectionUtil.getCallerClass(2), var1));
   }

   protected static Logger getLogger(String var0, String var1) {
      return factory.getContext(var0, (ClassLoader)null, (Object)null, false).getLogger(var1);
   }

   public static Logger getRootLogger() {
      return getLogger("");
   }

   static {
      PropertiesUtil var0 = PropertiesUtil.getProperties();
      String var1 = var0.getStringProperty("log4j2.loggerContextFactory");
      if (var1 != null) {
         try {
            factory = (LoggerContextFactory)LoaderUtil.newCheckedInstanceOf(var1, LoggerContextFactory.class);
         } catch (ClassNotFoundException var8) {
            LOGGER.error((String)"Unable to locate configured LoggerContextFactory {}", (Object)var1);
         } catch (Exception var9) {
            LOGGER.error((String)"Unable to create configured LoggerContextFactory {}", (Object)var1, (Object)var9);
         }
      }

      if (factory == null) {
         TreeMap var2 = new TreeMap();
         if (ProviderUtil.hasProviders()) {
            Iterator var3 = ProviderUtil.getProviders().iterator();

            while(var3.hasNext()) {
               Provider var4 = (Provider)var3.next();
               Class var5 = var4.loadLoggerContextFactory();
               if (var5 != null) {
                  try {
                     var2.put(var4.getPriority(), var5.newInstance());
                  } catch (Exception var7) {
                     LOGGER.error((String)"Unable to create class {} specified in {}", (Object)var5.getName(), var4.getUrl().toString(), var7);
                  }
               }
            }

            if (var2.isEmpty()) {
               LOGGER.error("Log4j2 could not find a logging implementation. Please add log4j-core to the classpath. Using SimpleLogger to log to the console...");
               factory = new SimpleLoggerContextFactory();
            } else if (var2.size() == 1) {
               factory = (LoggerContextFactory)var2.get(var2.lastKey());
            } else {
               StringBuilder var10 = new StringBuilder("Multiple logging implementations found: \n");
               Iterator var11 = var2.entrySet().iterator();

               while(var11.hasNext()) {
                  Entry var12 = (Entry)var11.next();
                  var10.append("Factory: ").append(((LoggerContextFactory)var12.getValue()).getClass().getName());
                  var10.append(", Weighting: ").append(var12.getKey()).append('\n');
               }

               factory = (LoggerContextFactory)var2.get(var2.lastKey());
               var10.append("Using factory: ").append(factory.getClass().getName());
               LOGGER.warn(var10.toString());
            }
         } else {
            LOGGER.error("Log4j2 could not find a logging implementation. Please add log4j-core to the classpath. Using SimpleLogger to log to the console...");
            factory = new SimpleLoggerContextFactory();
         }
      }

   }
}
