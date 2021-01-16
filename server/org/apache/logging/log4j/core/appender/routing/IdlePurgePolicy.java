package org.apache.logging.log4j.core.appender.routing;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationScheduler;
import org.apache.logging.log4j.core.config.Scheduled;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "IdlePurgePolicy",
   category = "Core",
   printObject = true
)
@Scheduled
public class IdlePurgePolicy extends AbstractLifeCycle implements PurgePolicy, Runnable {
   private final long timeToLive;
   private final long checkInterval;
   private final ConcurrentMap<String, Long> appendersUsage = new ConcurrentHashMap();
   private RoutingAppender routingAppender;
   private final ConfigurationScheduler scheduler;
   private volatile ScheduledFuture<?> future;

   public IdlePurgePolicy(long var1, long var3, ConfigurationScheduler var5) {
      super();
      this.timeToLive = var1;
      this.checkInterval = var3;
      this.scheduler = var5;
   }

   public void initialize(RoutingAppender var1) {
      this.routingAppender = var1;
   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = this.stop(this.future);
      this.setStopped();
      return var4;
   }

   public void purge() {
      long var1 = System.currentTimeMillis() - this.timeToLive;
      Iterator var3 = this.appendersUsage.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if ((Long)var4.getValue() < var1) {
            LOGGER.debug("Removing appender " + (String)var4.getKey());
            if (this.appendersUsage.remove(var4.getKey(), var4.getValue())) {
               this.routingAppender.deleteAppender((String)var4.getKey());
            }
         }
      }

   }

   public void update(String var1, LogEvent var2) {
      long var3 = System.currentTimeMillis();
      this.appendersUsage.put(var1, var3);
      if (this.future == null) {
         synchronized(this) {
            if (this.future == null) {
               this.scheduleNext();
            }
         }
      }

   }

   public void run() {
      this.purge();
      this.scheduleNext();
   }

   private void scheduleNext() {
      long var1 = 9223372036854775807L;
      Iterator var3 = this.appendersUsage.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if ((Long)var4.getValue() < var1) {
            var1 = (Long)var4.getValue();
         }
      }

      if (var1 < 9223372036854775807L) {
         long var5 = this.timeToLive - (System.currentTimeMillis() - var1);
         this.future = this.scheduler.schedule((Runnable)this, var5, TimeUnit.MILLISECONDS);
      } else {
         this.future = this.scheduler.schedule((Runnable)this, this.checkInterval, TimeUnit.MILLISECONDS);
      }

   }

   @PluginFactory
   public static PurgePolicy createPurgePolicy(@PluginAttribute("timeToLive") String var0, @PluginAttribute("checkInterval") String var1, @PluginAttribute("timeUnit") String var2, @PluginConfiguration Configuration var3) {
      if (var0 == null) {
         LOGGER.error("A timeToLive value is required");
         return null;
      } else {
         TimeUnit var4;
         if (var2 == null) {
            var4 = TimeUnit.MINUTES;
         } else {
            try {
               var4 = TimeUnit.valueOf(var2.toUpperCase());
            } catch (Exception var9) {
               LOGGER.error((String)"Invalid timeUnit value {}. timeUnit set to MINUTES", (Object)var2, (Object)var9);
               var4 = TimeUnit.MINUTES;
            }
         }

         long var5 = var4.toMillis(Long.parseLong(var0));
         if (var5 < 0L) {
            LOGGER.error("timeToLive must be positive. timeToLive set to 0");
            var5 = 0L;
         }

         long var7;
         if (var1 == null) {
            var7 = var5;
         } else {
            var7 = var4.toMillis(Long.parseLong(var1));
            if (var7 < 0L) {
               LOGGER.error((String)"checkInterval must be positive. checkInterval set equal to timeToLive = {}", (Object)var5);
               var7 = var5;
            }
         }

         return new IdlePurgePolicy(var5, var7, var3.getScheduler());
      }
   }

   public String toString() {
      return "timeToLive=" + this.timeToLive;
   }
}
