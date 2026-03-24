package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.ProfilerMeasured;
import org.slf4j.Logger;

public abstract class AbstractConsecutiveExecutor<T extends Runnable> implements Runnable, TaskScheduler<T>, ProfilerMeasured {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final AtomicReference<Status> status;
   private final StrictQueue<T> queue;
   private final Executor executor;
   private final String name;

   public AbstractConsecutiveExecutor(final StrictQueue<T> queue, final Executor executor, final String name) {
      super();
      this.status = new AtomicReference(AbstractConsecutiveExecutor.Status.SLEEPING);
      this.executor = executor;
      this.queue = queue;
      this.name = name;
      MetricsRegistry.INSTANCE.add(this);
   }

   private boolean canBeScheduled() {
      return !this.isClosed() && !this.queue.isEmpty();
   }

   public void close() {
      this.status.set(AbstractConsecutiveExecutor.Status.CLOSED);
   }

   private boolean pollTask() {
      if (!this.isRunning()) {
         return false;
      } else {
         Runnable runnable = this.queue.pop();
         if (runnable == null) {
            return false;
         } else {
            Util.runNamed(runnable, this.name);
            return true;
         }
      }
   }

   public void run() {
      try {
         this.pollTask();
      } finally {
         this.setSleeping();
         this.registerForExecution();
      }

   }

   public void runAll() {
      try {
         while(this.pollTask()) {
         }
      } finally {
         this.setSleeping();
         this.registerForExecution();
      }

   }

   public void schedule(final T task) {
      this.queue.push(task);
      this.registerForExecution();
   }

   private void registerForExecution() {
      if (this.canBeScheduled() && this.setRunning()) {
         try {
            this.executor.execute(this);
         } catch (RejectedExecutionException var4) {
            try {
               this.executor.execute(this);
            } catch (RejectedExecutionException e2) {
               LOGGER.error("Could not schedule ConsecutiveExecutor", e2);
            }
         }
      }

   }

   public int size() {
      return this.queue.size();
   }

   public boolean hasWork() {
      return this.isRunning() && !this.queue.isEmpty();
   }

   public String toString() {
      String var10000 = this.name;
      return var10000 + " " + String.valueOf(this.status.get()) + " " + this.queue.isEmpty();
   }

   public String name() {
      return this.name;
   }

   public List<MetricSampler> profiledMetrics() {
      return ImmutableList.of(MetricSampler.create(this.name + "-queue-size", MetricCategory.CONSECUTIVE_EXECUTORS, this::size));
   }

   private boolean setRunning() {
      return this.status.compareAndSet(AbstractConsecutiveExecutor.Status.SLEEPING, AbstractConsecutiveExecutor.Status.RUNNING);
   }

   private void setSleeping() {
      this.status.compareAndSet(AbstractConsecutiveExecutor.Status.RUNNING, AbstractConsecutiveExecutor.Status.SLEEPING);
   }

   private boolean isRunning() {
      return this.status.get() == AbstractConsecutiveExecutor.Status.RUNNING;
   }

   private boolean isClosed() {
      return this.status.get() == AbstractConsecutiveExecutor.Status.CLOSED;
   }

   private static enum Status {
      SLEEPING,
      RUNNING,
      CLOSED;

      private Status() {
      }

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{SLEEPING, RUNNING, CLOSED};
      }
   }
}
