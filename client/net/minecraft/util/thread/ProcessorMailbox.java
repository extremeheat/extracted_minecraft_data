package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.Util;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.ProfilerMeasured;
import org.slf4j.Logger;

public class ProcessorMailbox<T> implements ProfilerMeasured, ProcessorHandle<T>, AutoCloseable, Runnable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int CLOSED_BIT = 1;
   private static final int SCHEDULED_BIT = 2;
   private final AtomicInteger status = new AtomicInteger(0);
   private final StrictQueue<? super T, ? extends Runnable> queue;
   private final Executor dispatcher;
   private final String name;

   public static ProcessorMailbox<Runnable> create(Executor var0, String var1) {
      return new ProcessorMailbox(new StrictQueue.QueueStrictQueue(new ConcurrentLinkedQueue()), var0, var1);
   }

   public ProcessorMailbox(StrictQueue<? super T, ? extends Runnable> var1, Executor var2, String var3) {
      super();
      this.dispatcher = var2;
      this.queue = var1;
      this.name = var3;
      MetricsRegistry.INSTANCE.add(this);
   }

   private boolean setAsScheduled() {
      int var1;
      do {
         var1 = this.status.get();
         if ((var1 & 3) != 0) {
            return false;
         }
      } while(!this.status.compareAndSet(var1, var1 | 2));

      return true;
   }

   private void setAsIdle() {
      int var1;
      do {
         var1 = this.status.get();
      } while(!this.status.compareAndSet(var1, var1 & -3));

   }

   private boolean canBeScheduled() {
      if ((this.status.get() & 1) != 0) {
         return false;
      } else {
         return !this.queue.isEmpty();
      }
   }

   public void close() {
      int var1;
      do {
         var1 = this.status.get();
      } while(!this.status.compareAndSet(var1, var1 | 1));

   }

   private boolean shouldProcess() {
      return (this.status.get() & 2) != 0;
   }

   private boolean pollTask() {
      if (!this.shouldProcess()) {
         return false;
      } else {
         Runnable var1 = (Runnable)this.queue.pop();
         if (var1 == null) {
            return false;
         } else {
            Util.wrapThreadWithTaskName(this.name, var1).run();
            return true;
         }
      }
   }

   public void run() {
      try {
         this.pollUntil((var0) -> {
            return var0 == 0;
         });
      } finally {
         this.setAsIdle();
         this.registerForExecution();
      }

   }

   public void runAll() {
      try {
         this.pollUntil((var0) -> {
            return true;
         });
      } finally {
         this.setAsIdle();
         this.registerForExecution();
      }

   }

   public void tell(T var1) {
      this.queue.push(var1);
      this.registerForExecution();
   }

   private void registerForExecution() {
      if (this.canBeScheduled() && this.setAsScheduled()) {
         try {
            this.dispatcher.execute(this);
         } catch (RejectedExecutionException var4) {
            try {
               this.dispatcher.execute(this);
            } catch (RejectedExecutionException var3) {
               LOGGER.error("Cound not schedule mailbox", var3);
            }
         }
      }

   }

   private int pollUntil(Int2BooleanFunction var1) {
      int var2;
      for(var2 = 0; var1.get(var2) && this.pollTask(); ++var2) {
      }

      return var2;
   }

   public int size() {
      return this.queue.size();
   }

   public boolean hasWork() {
      return this.shouldProcess() && !this.queue.isEmpty();
   }

   public String toString() {
      String var10000 = this.name;
      return var10000 + " " + this.status.get() + " " + this.queue.isEmpty();
   }

   public String name() {
      return this.name;
   }

   public List<MetricSampler> profiledMetrics() {
      return ImmutableList.of(MetricSampler.create(this.name + "-queue-size", MetricCategory.MAIL_BOXES, this::size));
   }
}
