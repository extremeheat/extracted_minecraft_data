package org.apache.logging.log4j.core.appender.rolling;

import java.lang.reflect.Method;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "OnStartupTriggeringPolicy",
   category = "Core",
   printObject = true
)
public class OnStartupTriggeringPolicy extends AbstractTriggeringPolicy {
   private static final long JVM_START_TIME = initStartTime();
   private final long minSize;

   private OnStartupTriggeringPolicy(long var1) {
      super();
      this.minSize = var1;
   }

   private static long initStartTime() {
      try {
         Class var0 = Loader.loadSystemClass("java.lang.management.ManagementFactory");
         Method var1 = var0.getMethod("getRuntimeMXBean");
         Object var2 = var1.invoke((Object)null);
         Class var3 = Loader.loadSystemClass("java.lang.management.RuntimeMXBean");
         Method var4 = var3.getMethod("getStartTime");
         Long var5 = (Long)var4.invoke(var2);
         return var5;
      } catch (Throwable var6) {
         StatusLogger.getLogger().error("Unable to call ManagementFactory.getRuntimeMXBean().getStartTime(), using system time for OnStartupTriggeringPolicy", var6);
         return System.currentTimeMillis();
      }
   }

   public void initialize(RollingFileManager var1) {
      if (var1.getFileTime() < JVM_START_TIME && var1.getFileSize() >= this.minSize) {
         if (this.minSize == 0L) {
            var1.setRenameEmptyFiles(true);
         }

         var1.skipFooter(true);
         var1.rollover();
         var1.skipFooter(false);
      }

   }

   public boolean isTriggeringEvent(LogEvent var1) {
      return false;
   }

   public String toString() {
      return "OnStartupTriggeringPolicy";
   }

   @PluginFactory
   public static OnStartupTriggeringPolicy createPolicy(@PluginAttribute(value = "minSize",defaultLong = 1L) long var0) {
      return new OnStartupTriggeringPolicy(var0);
   }
}
