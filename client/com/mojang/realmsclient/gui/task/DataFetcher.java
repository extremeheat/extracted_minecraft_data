package com.mojang.realmsclient.gui.task;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.ArrayList;
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

   public <T> DataFetcher.Task<T> createTask(String var1, Callable<T> var2, Duration var3, RepeatedDelayStrategy var4) {
      long var5 = this.resolution.convert(var3);
      if (var5 == 0L) {
         throw new IllegalArgumentException("Period of " + var3 + " too short for selected resolution of " + this.resolution);
      } else {
         return new DataFetcher.Task<>(var1, var2, var5, var4);
      }
   }

   public DataFetcher.Subscription createSubscription() {
      return new DataFetcher.Subscription();
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   class SubscribedTask<T> {
      private final DataFetcher.Task<T> task;
      private final Consumer<T> output;
      private long lastCheckTime = -1L;

      SubscribedTask(final DataFetcher.Task<T> nullx, final Consumer<T> nullxx) {
         super();
         this.task = nullx;
         this.output = nullxx;
      }

      void update(long var1) {
         this.task.updateIfNeeded(var1);
         this.runCallbackIfNeeded();
      }

      void runCallbackIfNeeded() {
         DataFetcher.SuccessfulComputationResult var1 = this.task.lastResult;
         if (var1 != null && this.lastCheckTime < var1.time) {
            this.output.accept(var1.value);
            this.lastCheckTime = var1.time;
         }
      }

      void runCallback() {
         DataFetcher.SuccessfulComputationResult var1 = this.task.lastResult;
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

   public class Subscription {
      private final List<DataFetcher.SubscribedTask<?>> subscriptions = new ArrayList<>();

      public Subscription() {
         super();
      }

      public <T> void subscribe(DataFetcher.Task<T> var1, Consumer<T> var2) {
         DataFetcher.SubscribedTask var3 = DataFetcher.this.new SubscribedTask(var1, var2);
         this.subscriptions.add(var3);
         var3.runCallbackIfNeeded();
      }

      public void forceUpdate() {
         for (DataFetcher.SubscribedTask var2 : this.subscriptions) {
            var2.runCallback();
         }
      }

      public void tick() {
         for (DataFetcher.SubscribedTask var2 : this.subscriptions) {
            var2.update(DataFetcher.this.timeSource.get(DataFetcher.this.resolution));
         }
      }

      public void reset() {
         for (DataFetcher.SubscribedTask var2 : this.subscriptions) {
            var2.reset();
         }
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   public class Task<T> {
      private final String id;
      private final Callable<T> updater;
      private final long period;
      private final RepeatedDelayStrategy repeatStrategy;
      @Nullable
      private CompletableFuture<DataFetcher.ComputationResult<T>> pendingTask;
      @Nullable
      DataFetcher.SuccessfulComputationResult<T> lastResult;
      private long nextUpdate = -1L;

      Task(final String nullx, final Callable<T> nullxx, final long nullxxx, final RepeatedDelayStrategy nullxxxx) {
         super();
         this.id = nullx;
         this.updater = nullxx;
         this.period = nullxxx;
         this.repeatStrategy = nullxxxx;
      }

      void updateIfNeeded(long var1) {
         if (this.pendingTask != null) {
            DataFetcher.ComputationResult var3 = this.pendingTask.getNow(null);
            if (var3 == null) {
               return;
            }

            this.pendingTask = null;
            long var4 = var3.time;
            var3.value().ifLeft(var3x -> {
               this.lastResult = new DataFetcher.SuccessfulComputationResult<>((T)var3x, var4);
               this.nextUpdate = var4 + this.period * this.repeatStrategy.delayCyclesAfterSuccess();
            }).ifRight(var3x -> {
               long var4x = this.repeatStrategy.delayCyclesAfterFailure();
               DataFetcher.LOGGER.warn("Failed to process task {}, will repeat after {} cycles", new Object[]{this.id, var4x, var3x});
               this.nextUpdate = var4 + this.period * var4x;
            });
         }

         if (this.nextUpdate <= var1) {
            this.pendingTask = CompletableFuture.supplyAsync(() -> {
               try {
                  Object var1x = this.updater.call();
                  long var5 = DataFetcher.this.timeSource.get(DataFetcher.this.resolution);
                  return new DataFetcher.ComputationResult<>(Either.left(var1x), var5);
               } catch (Exception var4x) {
                  long var2 = DataFetcher.this.timeSource.get(DataFetcher.this.resolution);
                  return new DataFetcher.ComputationResult<>(Either.right(var4x), var2);
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
}
