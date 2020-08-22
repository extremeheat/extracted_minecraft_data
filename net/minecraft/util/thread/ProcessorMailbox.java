package net.minecraft.util.thread;

import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProcessorMailbox implements ProcessorHandle, AutoCloseable, Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final AtomicInteger status = new AtomicInteger(0);
   public final StrictQueue queue;
   private final Executor dispatcher;
   private final String name;

   public static ProcessorMailbox create(Executor var0, String var1) {
      return new ProcessorMailbox(new StrictQueue.QueueStrictQueue(new ConcurrentLinkedQueue()), var0, var1);
   }

   public ProcessorMailbox(StrictQueue var1, Executor var2, String var3) {
      this.dispatcher = var2;
      this.queue = var1;
      this.name = var3;
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
            var1.run();
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

   public void tell(Object var1) {
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

   public String toString() {
      return this.name + " " + this.status.get() + " " + this.queue.isEmpty();
   }

   public String name() {
      return this.name;
   }
}
