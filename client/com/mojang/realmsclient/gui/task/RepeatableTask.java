package com.mojang.realmsclient.gui.task;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class RepeatableTask implements Runnable {
   private final BooleanSupplier isActive;
   private final RestartDelayCalculator restartDelayCalculator;
   private final Duration interval;
   private final Runnable runnable;

   private RepeatableTask(Runnable var1, Duration var2, BooleanSupplier var3, RestartDelayCalculator var4) {
      super();
      this.runnable = var1;
      this.interval = var2;
      this.isActive = var3;
      this.restartDelayCalculator = var4;
   }

   @Override
   public void run() {
      if (this.isActive.getAsBoolean()) {
         this.restartDelayCalculator.markExecutionStart();
         this.runnable.run();
      }
   }

   public ScheduledFuture<?> schedule(ScheduledExecutorService var1) {
      return var1.scheduleAtFixedRate(this, this.restartDelayCalculator.getNextDelayMs(), this.interval.toMillis(), TimeUnit.MILLISECONDS);
   }

   public static RepeatableTask withRestartDelayAccountingForInterval(Runnable var0, Duration var1, BooleanSupplier var2) {
      return new RepeatableTask(var0, var1, var2, new IntervalBasedStartupDelay(var1));
   }

   public static RepeatableTask withImmediateRestart(Runnable var0, Duration var1, BooleanSupplier var2) {
      return new RepeatableTask(var0, var1, var2, new NoStartupDelay());
   }
}
