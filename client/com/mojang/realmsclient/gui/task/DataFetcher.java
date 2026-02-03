package com.mojang.realmsclient.gui.task;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.minecraft.util.TimeSource;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class DataFetcher {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Executor executor;
   private final TimeUnit resolution;
   private final TimeSource timeSource;

   public DataFetcher(final Executor executor, final TimeUnit resolution, final TimeSource timeSource) {
      super();
      this.executor = executor;
      this.resolution = resolution;
      this.timeSource = timeSource;
   }

   public <T> Task<T> createTask(final String id, final Callable<T> updater, final Duration period, final RepeatedDelayStrategy repeatStrategy) {
      long periodInUnit = this.resolution.convert(period);
      if (periodInUnit == 0L) {
         String var10002 = String.valueOf(period);
         throw new IllegalArgumentException("Period of " + var10002 + " too short for selected resolution of " + String.valueOf(this.resolution));
      } else {
         return new Task<T>(id, updater, periodInUnit, repeatStrategy);
      }
   }

   public Subscription createSubscription() {
      return new Subscription();
   }

   private static record ComputationResult<T>(Either<T, Exception> value, long time) {
      private ComputationResult {
         super();
      }
   }

   private static record SuccessfulComputationResult<T>(T value, long time) {
      private SuccessfulComputationResult {
         super();
      }
   }

   public class Task<T> {
      private final String id;
      private final Callable<T> updater;
      private final long period;
      private final RepeatedDelayStrategy repeatStrategy;
      private @Nullable CompletableFuture<ComputationResult<T>> pendingTask;
      private @Nullable SuccessfulComputationResult<T> lastResult;
      private long nextUpdate;

      private Task(final String id, final Callable<T> updater, final long period, final RepeatedDelayStrategy repeatStrategy) {
         Objects.requireNonNull(DataFetcher.this);
         super();
         this.nextUpdate = -1L;
         this.id = id;
         this.updater = updater;
         this.period = period;
         this.repeatStrategy = repeatStrategy;
      }

      private void updateIfNeeded(final long currentTime) {
         if (this.pendingTask != null) {
            ComputationResult<T> result = (ComputationResult)this.pendingTask.getNow((Object)null);
            if (result == null) {
               return;
            }

            this.pendingTask = null;
            long completionTime = result.time;
            result.value().ifLeft((value) -> {
               this.lastResult = new SuccessfulComputationResult<T>(value, completionTime);
               this.nextUpdate = completionTime + this.period * this.repeatStrategy.delayCyclesAfterSuccess();
            }).ifRight((e) -> {
               long cycles = this.repeatStrategy.delayCyclesAfterFailure();
               DataFetcher.LOGGER.warn("Failed to process task {}, will repeat after {} cycles", new Object[]{this.id, cycles, e});
               this.nextUpdate = completionTime + this.period * cycles;
            });
         }

         if (this.nextUpdate <= currentTime) {
            this.pendingTask = CompletableFuture.supplyAsync(() -> {
               try {
                  T result = (T)this.updater.call();
                  long completionTime = DataFetcher.this.timeSource.get(DataFetcher.this.resolution);
                  return new ComputationResult(Either.left(result), completionTime);
               } catch (Exception e) {
                  long completionTime = DataFetcher.this.timeSource.get(DataFetcher.this.resolution);
                  return new ComputationResult(Either.right(e), completionTime);
               }
            }, DataFetcher.this.executor);
         }

      }

      public void reset() {
         this.pendingTask = null;
         this.lastResult = null;
         this.nextUpdate = -1L;
      }
   }

   private class SubscribedTask<T> {
      private final Task<T> task;
      private final Consumer<T> output;
      private long lastCheckTime;

      private SubscribedTask(final Task<T> task, final Consumer<T> output) {
         Objects.requireNonNull(DataFetcher.this);
         super();
         this.lastCheckTime = -1L;
         this.task = task;
         this.output = output;
      }

      private void update(final long currentTime) {
         this.task.updateIfNeeded(currentTime);
         this.runCallbackIfNeeded();
      }

      private void runCallbackIfNeeded() {
         SuccessfulComputationResult<T> lastResult = this.task.lastResult;
         if (lastResult != null && this.lastCheckTime < lastResult.time) {
            this.output.accept(lastResult.value);
            this.lastCheckTime = lastResult.time;
         }

      }

      private void runCallback() {
         SuccessfulComputationResult<T> lastResult = this.task.lastResult;
         if (lastResult != null) {
            this.output.accept(lastResult.value);
            this.lastCheckTime = lastResult.time;
         }

      }

      private void reset() {
         this.task.reset();
         this.lastCheckTime = -1L;
      }
   }

   public class Subscription {
      private final List<SubscribedTask<?>> subscriptions;

      public Subscription() {
         Objects.requireNonNull(DataFetcher.this);
         super();
         this.subscriptions = new ArrayList();
      }

      public <T> void subscribe(final Task<T> task, final Consumer<T> output) {
         SubscribedTask<T> subscription = DataFetcher.this.new SubscribedTask<T>(task, output);
         this.subscriptions.add(subscription);
         subscription.runCallbackIfNeeded();
      }

      public void forceUpdate() {
         for(SubscribedTask<?> subscription : this.subscriptions) {
            subscription.runCallback();
         }

      }

      public void tick() {
         for(SubscribedTask<?> subscription : this.subscriptions) {
            subscription.update(DataFetcher.this.timeSource.get(DataFetcher.this.resolution));
         }

      }

      public void reset() {
         for(SubscribedTask<?> subscription : this.subscriptions) {
            subscription.reset();
         }

      }
   }
}
