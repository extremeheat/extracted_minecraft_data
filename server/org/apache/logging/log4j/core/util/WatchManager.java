package org.apache.logging.log4j.core.util;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.config.ConfigurationScheduler;
import org.apache.logging.log4j.status.StatusLogger;

public class WatchManager extends AbstractLifeCycle {
   private static Logger logger = StatusLogger.getLogger();
   private final ConcurrentMap<File, WatchManager.FileMonitor> watchers = new ConcurrentHashMap();
   private int intervalSeconds = 0;
   private ScheduledFuture<?> future;
   private final ConfigurationScheduler scheduler;

   public WatchManager(ConfigurationScheduler var1) {
      super();
      this.scheduler = var1;
   }

   public void setIntervalSeconds(int var1) {
      if (!this.isStarted()) {
         if (this.intervalSeconds > 0 && var1 == 0) {
            this.scheduler.decrementScheduledItems();
         } else if (this.intervalSeconds == 0 && var1 > 0) {
            this.scheduler.incrementScheduledItems();
         }

         this.intervalSeconds = var1;
      }

   }

   public int getIntervalSeconds() {
      return this.intervalSeconds;
   }

   public void start() {
      super.start();
      if (this.intervalSeconds > 0) {
         this.future = this.scheduler.scheduleWithFixedDelay(new WatchManager.WatchRunnable(), (long)this.intervalSeconds, (long)this.intervalSeconds, TimeUnit.SECONDS);
      }

   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = this.stop(this.future);
      this.setStopped();
      return var4;
   }

   public void watchFile(File var1, FileWatcher var2) {
      this.watchers.put(var1, new WatchManager.FileMonitor(var1.lastModified(), var2));
   }

   public Map<File, FileWatcher> getWatchers() {
      HashMap var1 = new HashMap();
      Iterator var2 = this.watchers.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.put(var3.getKey(), ((WatchManager.FileMonitor)var3.getValue()).fileWatcher);
      }

      return var1;
   }

   private class FileMonitor {
      private final FileWatcher fileWatcher;
      private long lastModified;

      public FileMonitor(long var2, FileWatcher var4) {
         super();
         this.fileWatcher = var4;
         this.lastModified = var2;
      }
   }

   private class WatchRunnable implements Runnable {
      private WatchRunnable() {
         super();
      }

      public void run() {
         Iterator var1 = WatchManager.this.watchers.entrySet().iterator();

         while(var1.hasNext()) {
            Entry var2 = (Entry)var1.next();
            File var3 = (File)var2.getKey();
            WatchManager.FileMonitor var4 = (WatchManager.FileMonitor)var2.getValue();
            long var5 = var3.lastModified();
            if (this.fileModified(var4, var5)) {
               WatchManager.logger.info((String)"File {} was modified on {}, previous modification was {}", (Object)var3, var5, var4.lastModified);
               var4.lastModified = var5;
               var4.fileWatcher.fileModified(var3);
            }
         }

      }

      private boolean fileModified(WatchManager.FileMonitor var1, long var2) {
         return var2 != var1.lastModified;
      }

      // $FF: synthetic method
      WatchRunnable(Object var2) {
         this();
      }
   }
}
