package org.apache.logging.log4j.core.async;

import com.conversantmedia.util.concurrent.DisruptorBlockingQueue;
import com.conversantmedia.util.concurrent.SpinPolicy;
import java.util.concurrent.BlockingQueue;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "DisruptorBlockingQueue",
   category = "Core",
   elementType = "BlockingQueueFactory"
)
public class DisruptorBlockingQueueFactory<E> implements BlockingQueueFactory<E> {
   private final SpinPolicy spinPolicy;

   private DisruptorBlockingQueueFactory(SpinPolicy var1) {
      super();
      this.spinPolicy = var1;
   }

   public BlockingQueue<E> create(int var1) {
      return new DisruptorBlockingQueue(var1, this.spinPolicy);
   }

   @PluginFactory
   public static <E> DisruptorBlockingQueueFactory<E> createFactory(@PluginAttribute(value = "SpinPolicy",defaultString = "WAITING") SpinPolicy var0) {
      return new DisruptorBlockingQueueFactory(var0);
   }
}
