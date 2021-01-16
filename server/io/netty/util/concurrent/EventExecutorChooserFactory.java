package io.netty.util.concurrent;

public interface EventExecutorChooserFactory {
   EventExecutorChooserFactory.EventExecutorChooser newChooser(EventExecutor[] var1);

   public interface EventExecutorChooser {
      EventExecutor next();
   }
}
