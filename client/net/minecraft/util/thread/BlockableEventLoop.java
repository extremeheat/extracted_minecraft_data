package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.ProfilerMeasured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockableEventLoop<R extends Runnable> implements ProfilerMeasured, ProcessorHandle<R>, Executor {
   private final String name;
   private static final Logger LOGGER = LogManager.getLogger();
   private final Queue<R> pendingRunnables = Queues.newConcurrentLinkedQueue();
   private int blockingCount;

   protected BlockableEventLoop(String var1) {
      super();
      this.name = var1;
      MetricsRegistry.INSTANCE.add(this);
   }

   protected abstract R wrapRunnable(Runnable var1);

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

   public void tell(R var1) {
      this.pendingRunnables.add(var1);
      LockSupport.unpark(this.getRunningThread());
   }

   public void execute(Runnable var1) {
      if (this.scheduleExecutables()) {
         this.tell(this.wrapRunnable(var1));
      } else {
         var1.run();
      }

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
         var1.run();
      } catch (Exception var3) {
         LOGGER.fatal("Error executing task on {}", this.name(), var3);
      }

   }

   public List<MetricSampler> profiledMetrics() {
      return ImmutableList.of(MetricSampler.create(this.name + "-pending-tasks", MetricCategory.EVENT_LOOPS, this::getPendingTasksCount));
   }

   // $FF: synthetic method
   public void tell(Object var1) {
      this.tell((Runnable)var1);
   }
}
