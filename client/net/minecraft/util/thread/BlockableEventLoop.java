package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.mojang.jtracy.TracyClient;
import com.mojang.jtracy.Zone;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.CheckReturnValue;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.ProfilerMeasured;
import org.slf4j.Logger;

public abstract class BlockableEventLoop<R extends Runnable> implements ProfilerMeasured, TaskScheduler<R>, Executor {
   public static final long BLOCK_TIME_NANOS = 100000L;
   private final String name;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Queue<R> pendingRunnables = Queues.newConcurrentLinkedQueue();
   private int blockingCount;

   protected BlockableEventLoop(String var1) {
      super();
      this.name = var1;
      MetricsRegistry.INSTANCE.add(this);
   }

   protected abstract boolean shouldRun(R var1);

   public boolean isSameThread() {
      return Thread.currentThread() == this.getRunningThread();
   }

   protected abstract Thread getRunningThread();

   protected boolean scheduleExecutables() {
      return !this.isSameThread();
   }

   public int getPendingTasksCount() {
      return this.pendingRunnables.size();
   }

   public String name() {
      return this.name;
   }

   public <V> CompletableFuture<V> submit(Supplier<V> var1) {
      return this.scheduleExecutables() ? CompletableFuture.supplyAsync(var1, this) : CompletableFuture.completedFuture(var1.get());
   }

   private CompletableFuture<Void> submitAsync(Runnable var1) {
      return CompletableFuture.supplyAsync(() -> {
         var1.run();
         return null;
      }, this);
   }

   @CheckReturnValue
   public CompletableFuture<Void> submit(Runnable var1) {
      if (this.scheduleExecutables()) {
         return this.submitAsync(var1);
      } else {
         var1.run();
         return CompletableFuture.completedFuture((Object)null);
      }
   }

   public void executeBlocking(Runnable var1) {
      if (!this.isSameThread()) {
         this.submitAsync(var1).join();
      } else {
         var1.run();
      }

   }

   public void schedule(R var1) {
      this.pendingRunnables.add(var1);
      LockSupport.unpark(this.getRunningThread());
   }

   public void execute(Runnable var1) {
      if (this.scheduleExecutables()) {
         this.schedule(this.wrapRunnable(var1));
      } else {
         var1.run();
      }

   }

   public void executeIfPossible(Runnable var1) {
      this.execute(var1);
   }

   protected void dropAllTasks() {
      this.pendingRunnables.clear();
   }

   protected void runAllTasks() {
      while(this.pollTask()) {
      }

   }

   public boolean pollTask() {
      Runnable var1 = (Runnable)this.pendingRunnables.peek();
      if (var1 == null) {
         return false;
      } else if (this.blockingCount == 0 && !this.shouldRun(var1)) {
         return false;
      } else {
         this.doRunTask((Runnable)this.pendingRunnables.remove());
         return true;
      }
   }

   public void managedBlock(BooleanSupplier var1) {
      ++this.blockingCount;

      try {
         while(!var1.getAsBoolean()) {
            if (!this.pollTask()) {
               this.waitForTasks();
            }
         }
      } finally {
         --this.blockingCount;
      }

   }

   protected void waitForTasks() {
      Thread.yield();
      LockSupport.parkNanos("waiting for tasks", 100000L);
   }

   protected void doRunTask(R var1) {
      try {
         Zone var2 = TracyClient.beginZone("Task", SharedConstants.IS_RUNNING_IN_IDE);

         try {
            var1.run();
         } catch (Throwable var6) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var2 != null) {
            var2.close();
         }

      } catch (Exception var7) {
         LOGGER.error(LogUtils.FATAL_MARKER, "Error executing task on {}", this.name(), var7);
         throw var7;
      }
   }

   public List<MetricSampler> profiledMetrics() {
      return ImmutableList.of(MetricSampler.create(this.name + "-pending-tasks", MetricCategory.EVENT_LOOPS, this::getPendingTasksCount));
   }

   public static boolean isNonRecoverable(Throwable var0) {
      if (var0 instanceof ReportedException var1) {
         return isNonRecoverable(var1.getCause());
      } else {
         return var0 instanceof OutOfMemoryError || var0 instanceof StackOverflowError;
      }
   }
}
