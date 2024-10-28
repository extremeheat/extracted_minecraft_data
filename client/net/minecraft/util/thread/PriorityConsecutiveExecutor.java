package net.minecraft.util.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.minecraft.util.profiling.metrics.MetricsRegistry;

public class PriorityConsecutiveExecutor extends AbstractConsecutiveExecutor<StrictQueue.RunnableWithPriority> {
   public PriorityConsecutiveExecutor(int var1, Executor var2, String var3) {
      super(new StrictQueue.FixedPriorityQueue(var1), var2, var3);
      MetricsRegistry.INSTANCE.add(this);
   }

   public StrictQueue.RunnableWithPriority wrapRunnable(Runnable var1) {
      return new StrictQueue.RunnableWithPriority(0, var1);
   }

   public <Source> CompletableFuture<Source> scheduleWithResult(int var1, Consumer<CompletableFuture<Source>> var2) {
      CompletableFuture var3 = new CompletableFuture();
      this.schedule(new StrictQueue.RunnableWithPriority(var1, () -> {
         var2.accept(var3);
      }));
      return var3;
   }

   // $FF: synthetic method
   public Runnable wrapRunnable(final Runnable var1) {
      return this.wrapRunnable(var1);
   }
}
