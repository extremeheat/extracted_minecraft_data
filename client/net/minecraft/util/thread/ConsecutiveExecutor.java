package net.minecraft.util.thread;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class ConsecutiveExecutor extends AbstractConsecutiveExecutor<Runnable> {
   public ConsecutiveExecutor(Executor var1, String var2) {
      super(new StrictQueue.QueueStrictQueue(new ConcurrentLinkedQueue<>()), var1, var2);
   }

   @Override
   public Runnable wrapRunnable(Runnable var1) {
      return var1;
   }
}
