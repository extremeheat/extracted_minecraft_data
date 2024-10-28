package com.mojang.realmsclient.gui.task;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.TimeSource;
import org.slf4j.Logger;

public class DataFetcher {
   static final Logger LOGGER = LogUtils.getLogger();
   final Executor executor;
   final TimeUnit resolution;
   final TimeSource timeSource;

   public DataFetcher(Executor var1, TimeUnit var2, TimeSource var3) {
      super();
      this.executor = var1;
      this.resolution = var2;
      this.timeSource = var3;
   }

   public <T> Task<T> createTask(String var1, Callable<T> var2, Duration var3, RepeatedDelayStrategy var4) {
      long var5 = this.resolution.convert(var3);
      if (var5 == 0L) {
         String var10002 = String.valueOf(var3);
         throw new IllegalArgumentException("Period of " + var10002 + " too short for selected resolution of " + String.valueOf(this.resolution));
      } else {
         return new Task(var1, var2, var5, var4);
      }
   }

   public Subscription createSubscription() {
      return new Subscription();
   }

   public class Task<T> {
      private final String id;
      private final Callable<T> updater;
      private final long period;
      private final RepeatedDelayStrategy repeatStrategy;
      @Nullable
      private CompletableFuture<ComputationResult<T>> pendingTask;
      @Nullable
      SuccessfulComputationResult<T> lastResult;
      private long nextUpdate = -1L;

      Task(String var2, Callable<T> var3, long var4, RepeatedDelayStrategy var6) {
         super();
         this.id = var2;
         this.updater = var3;
         this.period = var4;
         this.repeatStrategy = var6;
      }

      void updateIfNeeded(long var1) {
         if (this.pendingTask != null) {
            ComputationResult var3 = (ComputationResult)this.pendingTask.getNow((Object)null);
            if (var3 == null) {
               return;
            }

            this.pendingTask = null;
            long var4 = var3.time;
            var3.value().ifLeft((var3x) -> {
               this.lastResult = new SuccessfulComputationResult(var3x, var4);
               this.nextUpdate = var4 + this.period * this.repeatStrategy.delayCyclesAfterSuccess();
            }).ifRight((var3x) -> {
               long var4x = this.repeatStrategy.delayCyclesAfterFailure();
               DataFetcher.LOGGER.warn("Failed to process task {}, will repeat after {} cycles", new Object[]{this.id, var4x, var3x});
               this.nextUpdate = var4 + this.period * var4x;
            });
         }

         if (this.nextUpdate <= var1) {
            this.pendingTask = CompletableFuture.supplyAsync(() -> {
               long var2;
               try {
                  Object var1 = this.updater.call();
                  var2 = DataFetcher.this.timeSource.get(DataFetcher.this.resolution);
                  return new ComputationResult(Either.left(var1), var2);
               } catch (Exception var4) {
                  var2 = DataFetcher.this.timeSource.get(DataFetcher.this.resolution);
                  return new ComputationResult(Either.right(var4), var2);
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

   public class Subscription {
      private final List<SubscribedTask<?>> subscriptions = new ArrayList();

      public Subscription() {
         super();
      }

      public <T> void subscribe(Task<T> var1, Consumer<T> var2) {
         SubscribedTask var3 = DataFetcher.this.new SubscribedTask(DataFetcher.this, var1, var2);
         this.subscriptions.add(var3);
         var3.runCallbackIfNeeded();
      }

      public void forceUpdate() {
         Iterator var1 = this.subscriptions.iterator();

         while(var1.hasNext()) {
            SubscribedTask var2 = (SubscribedTask)var1.next();
            var2.runCallback();
         }

      }

      public void tick() {
         Iterator var1 = this.subscriptions.iterator();

         while(var1.hasNext()) {
            SubscribedTask var2 = (SubscribedTask)var1.next();
            var2.update(DataFetcher.this.timeSource.get(DataFetcher.this.resolution));
         }

      }

      public void reset() {
         Iterator var1 = this.subscriptions.iterator();

         while(var1.hasNext()) {
            SubscribedTask var2 = (SubscribedTask)var1.next();
            var2.reset();
         }

      }
   }

   private class SubscribedTask<T> {
      private final Task<T> task;
      private final Consumer<T> output;
      private long lastCheckTime = -1L;

      SubscribedTask(DataFetcher var1, Task var2, Consumer var3) {
         super();
         this.task = var2;
         this.output = var3;
      }

      void update(long var1) {
         this.task.updateIfNeeded(var1);
         this.runCallbackIfNeeded();
      }

      void runCallbackIfNeeded() {
         SuccessfulComputationResult var1 = this.task.lastResult;
         if (var1 != null && this.lastCheckTime < var1.time) {
            this.output.accept(var1.value);
            this.lastCheckTime = var1.time;
         }

      }

      void runCallback() {
         SuccessfulComputationResult var1 = this.task.lastResult;
         if (var1 != null) {
            this.output.accept(var1.value);
            this.lastCheckTime = var1.time;
         }

      }

      void reset() {
         this.task.reset();
         this.lastCheckTime = -1L;
      }
   }

   private static record SuccessfulComputationResult<T>(T value, long time) {
      final T value;
      final long time;

      SuccessfulComputationResult(T var1, long var2) {
         super();
         this.value = var1;
         this.time = var2;
      }

      public T value() {
         return this.value;
      }

      public long time() {
         return this.time;
      }
   }

   private static record ComputationResult<T>(Either<T, Exception> value, long time) {
      final long time;

      ComputationResult(Either<T, Exception> var1, long var2) {
         super();
         this.value = var1;
         this.time = var2;
      }

      public Either<T, Exception> value() {
         return this.value;
      }

      public long time() {
         return this.time;
      }
   }
}
