package org.apache.logging.log4j.spi;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public class Provider {
   public static final String FACTORY_PRIORITY = "FactoryPriority";
   public static final String THREAD_CONTEXT_MAP = "ThreadContextMap";
   public static final String LOGGER_CONTEXT_FACTORY = "LoggerContextFactory";
   private static final Integer DEFAULT_PRIORITY = -1;
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final Integer priority;
   private final String className;
   private final String threadContextMap;
   private final URL url;
   private final WeakReference<ClassLoader> classLoader;

   public Provider(Properties var1, URL var2, ClassLoader var3) {
      super();
      this.url = var2;
      this.classLoader = new WeakReference(var3);
      String var4 = var1.getProperty("FactoryPriority");
      this.priority = var4 == null ? DEFAULT_PRIORITY : Integer.valueOf(var4);
      this.className = var1.getProperty("LoggerContextFactory");
      this.threadContextMap = var1.getProperty("ThreadContextMap");
   }

   public Integer getPriority() {
      return this.priority;
   }

   public String getClassName() {
      return this.className;
   }

   public Class<? extends LoggerContextFactory> loadLoggerContextFactory() {
      if (this.className == null) {
         return null;
      } else {
         ClassLoader var1 = (ClassLoader)this.classLoader.get();
         if (var1 == null) {
            return null;
         } else {
            try {
               Class var2 = var1.loadClass(this.className);
               if (LoggerContextFactory.class.isAssignableFrom(var2)) {
                  return var2.asSubclass(LoggerContextFactory.class);
               }
            } catch (Exception var3) {
               LOGGER.error((String)"Unable to create class {} specified in {}", (Object)this.className, this.url.toString(), var3);
            }

            return null;
         }
      }
   }

   public String getThreadContextMap() {
      return this.threadContextMap;
   }

   public Class<? extends ThreadContextMap> loadThreadContextMap() {
      if (this.threadContextMap == null) {
         return null;
      } else {
         ClassLoader var1 = (ClassLoader)this.classLoader.get();
         if (var1 == null) {
            return null;
         } else {
            try {
               Class var2 = var1.loadClass(this.threadContextMap);
               if (ThreadContextMap.class.isAssignableFrom(var2)) {
                  return var2.asSubclass(ThreadContextMap.class);
               }
            } catch (Exception var3) {
               LOGGER.error((String)"Unable to create class {} specified in {}", (Object)this.threadContextMap, this.url.toString(), var3);
            }

            return null;
         }
      }
   }

   public URL getUrl() {
      return this.url;
   }

   public String toString() {
      String var1 = "Provider[";
      if (!DEFAULT_PRIORITY.equals(this.priority)) {
         var1 = var1 + "priority=" + this.priority + ", ";
      }

      if (this.threadContextMap != null) {
         var1 = var1 + "threadContextMap=" + this.threadContextMap + ", ";
      }

      if (this.className != null) {
         var1 = var1 + "className=" + this.className + ", ";
      }

      var1 = var1 + "url=" + this.url;
      ClassLoader var2 = (ClassLoader)this.classLoader.get();
      if (var2 == null) {
         var1 = var1 + ", classLoader=null(not reachable)";
      } else {
         var1 = var1 + ", classLoader=" + var2;
      }

      var1 = var1 + "]";
      return var1;
   }
}
