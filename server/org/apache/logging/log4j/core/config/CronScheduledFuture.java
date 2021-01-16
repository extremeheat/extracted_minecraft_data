package org.apache.logging.log4j.core.config;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CronScheduledFuture<V> implements ScheduledFuture<V> {
   private volatile CronScheduledFuture<V>.FutureData futureData;

   public CronScheduledFuture(ScheduledFuture<V> var1, Date var2) {
      super();
      this.futureData = new CronScheduledFuture.FutureData(var1, var2);
   }

   public Date getFireTime() {
      return this.futureData.runDate;
   }

   void reset(ScheduledFuture<?> var1, Date var2) {
      this.futureData = new CronScheduledFuture.FutureData(var1, var2);
   }

   public long getDelay(TimeUnit var1) {
      return this.futureData.scheduledFuture.getDelay(var1);
   }

   public int compareTo(Delayed var1) {
      return this.futureData.scheduledFuture.compareTo(var1);
   }

   public boolean cancel(boolean var1) {
      return this.futureData.scheduledFuture.cancel(var1);
   }

   public boolean isCancelled() {
      return this.futureData.scheduledFuture.isCancelled();
   }

   public boolean isDone() {
      return this.futureData.scheduledFuture.isDone();
   }

   public V get() throws InterruptedException, ExecutionException {
      return this.futureData.scheduledFuture.get();
   }

   public V get(long var1, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException {
      return this.futureData.scheduledFuture.get(var1, var3);
   }

   private class FutureData {
      private final ScheduledFuture<?> scheduledFuture;
      private final Date runDate;

      FutureData(ScheduledFuture<?> var2, Date var3) {
         super();
         this.scheduledFuture = var2;
         this.runDate = var3;
      }
   }
}
