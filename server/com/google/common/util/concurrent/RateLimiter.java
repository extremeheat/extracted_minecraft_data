package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
@Beta
@GwtIncompatible
public abstract class RateLimiter {
   private final RateLimiter.SleepingStopwatch stopwatch;
   private volatile Object mutexDoNotUseDirectly;

   public static RateLimiter create(double var0) {
      return create(RateLimiter.SleepingStopwatch.createFromSystemTimer(), var0);
   }

   @VisibleForTesting
   static RateLimiter create(RateLimiter.SleepingStopwatch var0, double var1) {
      SmoothRateLimiter.SmoothBursty var3 = new SmoothRateLimiter.SmoothBursty(var0, 1.0D);
      var3.setRate(var1);
      return var3;
   }

   public static RateLimiter create(double var0, long var2, TimeUnit var4) {
      Preconditions.checkArgument(var2 >= 0L, "warmupPeriod must not be negative: %s", var2);
      return create(RateLimiter.SleepingStopwatch.createFromSystemTimer(), var0, var2, var4, 3.0D);
   }

   @VisibleForTesting
   static RateLimiter create(RateLimiter.SleepingStopwatch var0, double var1, long var3, TimeUnit var5, double var6) {
      SmoothRateLimiter.SmoothWarmingUp var8 = new SmoothRateLimiter.SmoothWarmingUp(var0, var3, var5, var6);
      var8.setRate(var1);
      return var8;
   }

   private Object mutex() {
      Object var1 = this.mutexDoNotUseDirectly;
      if (var1 == null) {
         synchronized(this) {
            var1 = this.mutexDoNotUseDirectly;
            if (var1 == null) {
               this.mutexDoNotUseDirectly = var1 = new Object();
            }
         }
      }

      return var1;
   }

   RateLimiter(RateLimiter.SleepingStopwatch var1) {
      super();
      this.stopwatch = (RateLimiter.SleepingStopwatch)Preconditions.checkNotNull(var1);
   }

   public final void setRate(double var1) {
      Preconditions.checkArgument(var1 > 0.0D && !Double.isNaN(var1), "rate must be positive");
      synchronized(this.mutex()) {
         this.doSetRate(var1, this.stopwatch.readMicros());
      }
   }

   abstract void doSetRate(double var1, long var3);

   public final double getRate() {
      synchronized(this.mutex()) {
         return this.doGetRate();
      }
   }

   abstract double doGetRate();

   @CanIgnoreReturnValue
   public double acquire() {
      return this.acquire(1);
   }

   @CanIgnoreReturnValue
   public double acquire(int var1) {
      long var2 = this.reserve(var1);
      this.stopwatch.sleepMicrosUninterruptibly(var2);
      return 1.0D * (double)var2 / (double)TimeUnit.SECONDS.toMicros(1L);
   }

   final long reserve(int var1) {
      checkPermits(var1);
      synchronized(this.mutex()) {
         return this.reserveAndGetWaitLength(var1, this.stopwatch.readMicros());
      }
   }

   public boolean tryAcquire(long var1, TimeUnit var3) {
      return this.tryAcquire(1, var1, var3);
   }

   public boolean tryAcquire(int var1) {
      return this.tryAcquire(var1, 0L, TimeUnit.MICROSECONDS);
   }

   public boolean tryAcquire() {
      return this.tryAcquire(1, 0L, TimeUnit.MICROSECONDS);
   }

   public boolean tryAcquire(int var1, long var2, TimeUnit var4) {
      long var5 = Math.max(var4.toMicros(var2), 0L);
      checkPermits(var1);
      long var7;
      synchronized(this.mutex()) {
         long var10 = this.stopwatch.readMicros();
         if (!this.canAcquire(var10, var5)) {
            return false;
         }

         var7 = this.reserveAndGetWaitLength(var1, var10);
      }

      this.stopwatch.sleepMicrosUninterruptibly(var7);
      return true;
   }

   private boolean canAcquire(long var1, long var3) {
      return this.queryEarliestAvailable(var1) - var3 <= var1;
   }

   final long reserveAndGetWaitLength(int var1, long var2) {
      long var4 = this.reserveEarliestAvailable(var1, var2);
      return Math.max(var4 - var2, 0L);
   }

   abstract long queryEarliestAvailable(long var1);

   abstract long reserveEarliestAvailable(int var1, long var2);

   public String toString() {
      return String.format(Locale.ROOT, "RateLimiter[stableRate=%3.1fqps]", this.getRate());
   }

   private static void checkPermits(int var0) {
      Preconditions.checkArgument(var0 > 0, "Requested permits (%s) must be positive", var0);
   }

   abstract static class SleepingStopwatch {
      protected SleepingStopwatch() {
         super();
      }

      protected abstract long readMicros();

      protected abstract void sleepMicrosUninterruptibly(long var1);

      public static final RateLimiter.SleepingStopwatch createFromSystemTimer() {
         return new RateLimiter.SleepingStopwatch() {
            final Stopwatch stopwatch = Stopwatch.createStarted();

            protected long readMicros() {
               return this.stopwatch.elapsed(TimeUnit.MICROSECONDS);
            }

            protected void sleepMicrosUninterruptibly(long var1) {
               if (var1 > 0L) {
                  Uninterruptibles.sleepUninterruptibly(var1, TimeUnit.MICROSECONDS);
               }

            }
         };
      }
   }
}
