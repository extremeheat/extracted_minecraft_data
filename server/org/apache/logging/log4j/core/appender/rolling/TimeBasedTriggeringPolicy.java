package org.apache.logging.log4j.core.appender.rolling;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(
   name = "TimeBasedTriggeringPolicy",
   category = "Core",
   printObject = true
)
public final class TimeBasedTriggeringPolicy extends AbstractTriggeringPolicy {
   private long nextRolloverMillis;
   private final int interval;
   private final boolean modulate;
   private RollingFileManager manager;

   private TimeBasedTriggeringPolicy(int var1, boolean var2) {
      super();
      this.interval = var1;
      this.modulate = var2;
   }

   public int getInterval() {
      return this.interval;
   }

   public long getNextRolloverMillis() {
      return this.nextRolloverMillis;
   }

   public void initialize(RollingFileManager var1) {
      this.manager = var1;
      var1.getPatternProcessor().getNextTime(var1.getFileTime(), this.interval, this.modulate);
      this.nextRolloverMillis = var1.getPatternProcessor().getNextTime(var1.getFileTime(), this.interval, this.modulate);
   }

   public boolean isTriggeringEvent(LogEvent var1) {
      if (this.manager.getFileSize() == 0L) {
         return false;
      } else {
         long var2 = var1.getTimeMillis();
         if (var2 >= this.nextRolloverMillis) {
            this.nextRolloverMillis = this.manager.getPatternProcessor().getNextTime(var2, this.interval, this.modulate);
            return true;
         } else {
            return false;
         }
      }
   }

   @PluginFactory
   public static TimeBasedTriggeringPolicy createPolicy(@PluginAttribute("interval") String var0, @PluginAttribute("modulate") String var1) {
      int var2 = Integers.parseInt(var0, 1);
      boolean var3 = Boolean.parseBoolean(var1);
      return new TimeBasedTriggeringPolicy(var2, var3);
   }

   public String toString() {
      return "TimeBasedTriggeringPolicy(nextRolloverMillis=" + this.nextRolloverMillis + ", interval=" + this.interval + ", modulate=" + this.modulate + ")";
   }
}
