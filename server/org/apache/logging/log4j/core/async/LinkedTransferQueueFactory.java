package org.apache.logging.log4j.core.async;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "LinkedTransferQueue",
   category = "Core",
   elementType = "BlockingQueueFactory"
)
public class LinkedTransferQueueFactory<E> implements BlockingQueueFactory<E> {
   public LinkedTransferQueueFactory() {
      super();
   }

   public BlockingQueue<E> create(int var1) {
      return new LinkedTransferQueue();
   }

   @PluginFactory
   public static <E> LinkedTransferQueueFactory<E> createFactory() {
      return new LinkedTransferQueueFactory();
   }
}
