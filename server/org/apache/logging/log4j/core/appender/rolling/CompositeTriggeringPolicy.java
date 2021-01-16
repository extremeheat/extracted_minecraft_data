package org.apache.logging.log4j.core.appender.rolling;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "Policies",
   category = "Core",
   printObject = true
)
public final class CompositeTriggeringPolicy extends AbstractTriggeringPolicy {
   private final TriggeringPolicy[] triggeringPolicies;

   private CompositeTriggeringPolicy(TriggeringPolicy... var1) {
      super();
      this.triggeringPolicies = var1;
   }

   public TriggeringPolicy[] getTriggeringPolicies() {
      return this.triggeringPolicies;
   }

   public void initialize(RollingFileManager var1) {
      TriggeringPolicy[] var2 = this.triggeringPolicies;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TriggeringPolicy var5 = var2[var4];
         var5.initialize(var1);
      }

   }

   public boolean isTriggeringEvent(LogEvent var1) {
      TriggeringPolicy[] var2 = this.triggeringPolicies;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TriggeringPolicy var5 = var2[var4];
         if (var5.isTriggeringEvent(var1)) {
            return true;
         }
      }

      return false;
   }

   @PluginFactory
   public static CompositeTriggeringPolicy createPolicy(@PluginElement("Policies") TriggeringPolicy... var0) {
      return new CompositeTriggeringPolicy(var0);
   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = true;
      TriggeringPolicy[] var5 = this.triggeringPolicies;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         TriggeringPolicy var8 = var5[var7];
         if (var8 instanceof LifeCycle2) {
            var4 &= ((LifeCycle2)var8).stop(var1, var3);
         } else if (var8 instanceof LifeCycle) {
            ((LifeCycle)var8).stop();
            var4 &= true;
         }
      }

      this.setStopped();
      return var4;
   }

   public String toString() {
      return "CompositeTriggeringPolicy(policies=" + Arrays.toString(this.triggeringPolicies) + ")";
   }
}
