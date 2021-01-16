package org.apache.logging.log4j.core.config;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.util.CronExpression;
import org.apache.logging.log4j.core.util.Log4jThreadFactory;
import org.apache.logging.log4j.status.StatusLogger;

public class ConfigurationScheduler extends AbstractLifeCycle {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String SIMPLE_NAME = "Log4j2 " + ConfigurationScheduler.class.getSimpleName();
   private static final int MAX_SCHEDULED_ITEMS = 5;
   private ScheduledExecutorService executorService;
   private int scheduledItems = 0;

   public ConfigurationScheduler() {
      super();
   }

   public void start() {
      super.start();
   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      if (this.isExecutorServiceSet()) {
         LOGGER.debug((String)"{} shutting down threads in {}", (Object)SIMPLE_NAME, (Object)this.getExecutorService());
         this.executorService.shutdown();

         try {
            this.executorService.awaitTermination(var1, var3);
         } catch (InterruptedException var7) {
            this.executorService.shutdownNow();

            try {
               this.executorService.awaitTermination(var1, var3);
            } catch (InterruptedException var6) {
               LOGGER.warn("ConfigurationScheduler stopped but some scheduled services may not have completed.");
            }

            Thread.currentThread().interrupt();
         }
      }

      this.setStopped();
      return true;
   }

   public boolean isExecutorServiceSet() {
      return this.executorService != null;
   }

   public void incrementScheduledItems() {
      if (this.isExecutorServiceSet()) {
         LOGGER.error((String)"{} attempted to increment scheduled items after start", (Object)SIMPLE_NAME);
      } else {
         ++this.scheduledItems;
      }

   }

   public void decrementScheduledItems() {
      if (!this.isStarted() && this.scheduledItems > 0) {
         --this.scheduledItems;
      }

   }

   public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
      return this.getExecutorService().schedule(var1, var2, var4);
   }

   public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
      return this.getExecutorService().schedule(var1, var2, var4);
   }

   public CronScheduledFuture<?> scheduleWithCron(CronExpression var1, Runnable var2) {
      return this.scheduleWithCron(var1, new Date(), var2);
   }

   public CronScheduledFuture<?> scheduleWithCron(CronExpression var1, Date var2, Runnable var3) {
      Date var4 = var1.getNextValidTimeAfter(var2 == null ? new Date() : var2);
      ConfigurationScheduler.CronRunnable var5 = new ConfigurationScheduler.CronRunnable(var3, var1);
      ScheduledFuture var6 = this.schedule((Runnable)var5, this.nextFireInterval(var4), TimeUnit.MILLISECONDS);
      CronScheduledFuture var7 = new CronScheduledFuture(var6, var4);
      var5.setScheduledFuture(var7);
      LOGGER.debug((String)"Scheduled cron expression {} to fire at {}", (Object)var1.getCronExpression(), (Object)var4);
      return var7;
   }

   public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
      return this.getExecutorService().scheduleAtFixedRate(var1, var2, var4, var6);
   }

   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
      return this.getExecutorService().scheduleWithFixedDelay(var1, var2, var4, var6);
   }

   public long nextFireInterval(Date var1) {
      return var1.getTime() - (new Date()).getTime();
   }

   private ScheduledExecutorService getExecutorService() {
      if (this.executorService == null) {
         if (this.scheduledItems > 0) {
            LOGGER.debug((String)"{} starting {} threads", (Object)SIMPLE_NAME, (Object)this.scheduledItems);
            this.scheduledItems = Math.min(this.scheduledItems, 5);
            ScheduledThreadPoolExecutor var1 = new ScheduledThreadPoolExecutor(this.scheduledItems, Log4jThreadFactory.createDaemonThreadFactory("Scheduled"));
            var1.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
            var1.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            this.executorService = var1;
         } else {
            LOGGER.debug((String)"{}: No scheduled items", (Object)SIMPLE_NAME);
         }
      }

      return this.executorService;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("ConfigurationScheduler {");
      BlockingQueue var2 = ((ScheduledThreadPoolExecutor)this.executorService).getQueue();
      boolean var3 = true;

      for(Iterator var4 = var2.iterator(); var4.hasNext(); var3 = false) {
         Runnable var5 = (Runnable)var4.next();
         if (!var3) {
            var1.append(", ");
         }

         var1.append(var5.toString());
      }

      var1.append("}");
      return var1.toString();
   }

   public class CronRunnable implements Runnable {
      private final CronExpression cronExpression;
      private final Runnable runnable;
      private CronScheduledFuture<?> scheduledFuture;

      public CronRunnable(Runnable var2, CronExpression var3) {
         super();
         this.cronExpression = var3;
         this.runnable = var2;
      }

      public void setScheduledFuture(CronScheduledFuture<?> var1) {
         this.scheduledFuture = var1;
      }

      public void run() {
         boolean var10 = false;

         ScheduledFuture var2;
         Date var14;
         label73: {
            try {
               var10 = true;
               long var1 = this.scheduledFuture.getFireTime().getTime() - System.currentTimeMillis();
               if (var1 > 0L) {
                  ConfigurationScheduler.LOGGER.debug((String)"Cron thread woke up {} millis early. Sleeping", (Object)var1);

                  try {
                     Thread.sleep(var1);
                  } catch (InterruptedException var11) {
                  }
               }

               this.runnable.run();
               var10 = false;
               break label73;
            } catch (Throwable var12) {
               ConfigurationScheduler.LOGGER.error((String)"{} caught error running command", (Object)ConfigurationScheduler.SIMPLE_NAME, (Object)var12);
               var10 = false;
            } finally {
               if (var10) {
                  Date var5 = this.cronExpression.getNextValidTimeAfter(new Date());
                  ScheduledFuture var6 = ConfigurationScheduler.this.schedule((Runnable)this, ConfigurationScheduler.this.nextFireInterval(var5), TimeUnit.MILLISECONDS);
                  ConfigurationScheduler.LOGGER.debug((String)"Cron expression {} scheduled to fire again at {}", (Object)this.cronExpression.getCronExpression(), (Object)var5);
                  this.scheduledFuture.reset(var6, var5);
               }
            }

            var14 = this.cronExpression.getNextValidTimeAfter(new Date());
            var2 = ConfigurationScheduler.this.schedule((Runnable)this, ConfigurationScheduler.this.nextFireInterval(var14), TimeUnit.MILLISECONDS);
            ConfigurationScheduler.LOGGER.debug((String)"Cron expression {} scheduled to fire again at {}", (Object)this.cronExpression.getCronExpression(), (Object)var14);
            this.scheduledFuture.reset(var2, var14);
            return;
         }

         var14 = this.cronExpression.getNextValidTimeAfter(new Date());
         var2 = ConfigurationScheduler.this.schedule((Runnable)this, ConfigurationScheduler.this.nextFireInterval(var14), TimeUnit.MILLISECONDS);
         ConfigurationScheduler.LOGGER.debug((String)"Cron expression {} scheduled to fire again at {}", (Object)this.cronExpression.getCronExpression(), (Object)var14);
         this.scheduledFuture.reset(var2, var14);
      }

      public String toString() {
         return "CronRunnable{" + this.cronExpression.getCronExpression() + " - " + this.scheduledFuture.getFireTime();
      }
   }
}
