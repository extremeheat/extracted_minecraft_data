package org.apache.logging.log4j.core.appender.rolling;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationScheduler;
import org.apache.logging.log4j.core.config.CronScheduledFuture;
import org.apache.logging.log4j.core.config.Scheduled;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.CronExpression;

@Plugin(
   name = "CronTriggeringPolicy",
   category = "Core",
   printObject = true
)
@Scheduled
public final class CronTriggeringPolicy extends AbstractTriggeringPolicy {
   private static final String defaultSchedule = "0 0 0 * * ?";
   private RollingFileManager manager;
   private final CronExpression cronExpression;
   private final Configuration configuration;
   private final boolean checkOnStartup;
   private volatile Date lastRollDate;
   private CronScheduledFuture<?> future;

   private CronTriggeringPolicy(CronExpression var1, boolean var2, Configuration var3) {
      super();
      this.cronExpression = (CronExpression)Objects.requireNonNull(var1, "schedule");
      this.configuration = (Configuration)Objects.requireNonNull(var3, "configuration");
      this.checkOnStartup = var2;
   }

   public void initialize(RollingFileManager var1) {
      this.manager = var1;
      Date var2 = new Date();
      Date var3 = this.cronExpression.getPrevFireTime(new Date(this.manager.getFileTime()));
      Date var4 = this.cronExpression.getPrevFireTime(new Date());
      var1.getPatternProcessor().setCurrentFileTime(var4.getTime());
      LOGGER.debug((String)"LastRollForFile {}, LastRegularRole {}", (Object)var3, (Object)var4);
      var1.getPatternProcessor().setPrevFileTime(var4.getTime());
      if (this.checkOnStartup && var3 != null && var4 != null && var3.before(var4)) {
         this.lastRollDate = var3;
         this.rollover();
      }

      ConfigurationScheduler var5 = this.configuration.getScheduler();
      if (!var5.isExecutorServiceSet()) {
         var5.incrementScheduledItems();
      }

      if (!var5.isStarted()) {
         var5.start();
      }

      this.lastRollDate = var4;
      this.future = var5.scheduleWithCron(this.cronExpression, var2, new CronTriggeringPolicy.CronTrigger());
      LOGGER.debug(var5.toString());
   }

   public boolean isTriggeringEvent(LogEvent var1) {
      return false;
   }

   public CronExpression getCronExpression() {
      return this.cronExpression;
   }

   @PluginFactory
   public static CronTriggeringPolicy createPolicy(@PluginConfiguration Configuration var0, @PluginAttribute("evaluateOnStartup") String var1, @PluginAttribute("schedule") String var2) {
      boolean var4 = Boolean.parseBoolean(var1);
      CronExpression var3;
      if (var2 == null) {
         LOGGER.info("No schedule specified, defaulting to Daily");
         var3 = getSchedule("0 0 0 * * ?");
      } else {
         var3 = getSchedule(var2);
         if (var3 == null) {
            LOGGER.error("Invalid expression specified. Defaulting to Daily");
            var3 = getSchedule("0 0 0 * * ?");
         }
      }

      return new CronTriggeringPolicy(var3, var4, var0);
   }

   private static CronExpression getSchedule(String var0) {
      try {
         return new CronExpression(var0);
      } catch (ParseException var2) {
         LOGGER.error((String)("Invalid cron expression - " + var0), (Throwable)var2);
         return null;
      }
   }

   private void rollover() {
      this.manager.getPatternProcessor().setPrevFileTime(this.lastRollDate.getTime());
      Date var1 = this.cronExpression.getPrevFireTime(new Date());
      this.manager.getPatternProcessor().setCurrentFileTime(var1.getTime());
      this.manager.rollover();
      if (this.future != null) {
         this.lastRollDate = this.future.getFireTime();
      }

   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = this.stop(this.future);
      this.setStopped();
      return var4;
   }

   public String toString() {
      return "CronTriggeringPolicy(schedule=" + this.cronExpression.getCronExpression() + ")";
   }

   private class CronTrigger implements Runnable {
      private CronTrigger() {
         super();
      }

      public void run() {
         CronTriggeringPolicy.this.rollover();
      }

      // $FF: synthetic method
      CronTrigger(Object var2) {
         this();
      }
   }
}
