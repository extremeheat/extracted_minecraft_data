package org.apache.logging.log4j.core.appender.rolling;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "SizeBasedTriggeringPolicy",
   category = "Core",
   printObject = true
)
public class SizeBasedTriggeringPolicy extends AbstractTriggeringPolicy {
   private static final long MAX_FILE_SIZE = 10485760L;
   private final long maxFileSize;
   private RollingFileManager manager;

   protected SizeBasedTriggeringPolicy() {
      super();
      this.maxFileSize = 10485760L;
   }

   protected SizeBasedTriggeringPolicy(long var1) {
      super();
      this.maxFileSize = var1;
   }

   public long getMaxFileSize() {
      return this.maxFileSize;
   }

   public void initialize(RollingFileManager var1) {
      this.manager = var1;
   }

   public boolean isTriggeringEvent(LogEvent var1) {
      boolean var2 = this.manager.getFileSize() > this.maxFileSize;
      if (var2) {
         this.manager.getPatternProcessor().updateTime();
      }

      return var2;
   }

   public String toString() {
      return "SizeBasedTriggeringPolicy(size=" + this.maxFileSize + ')';
   }

   @PluginFactory
   public static SizeBasedTriggeringPolicy createPolicy(@PluginAttribute("size") String var0) {
      long var1 = var0 == null ? 10485760L : FileSize.parse(var0, 10485760L);
      return new SizeBasedTriggeringPolicy(var1);
   }
}
