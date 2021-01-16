package org.apache.logging.log4j.core.async;

import java.util.concurrent.BlockingQueue;

public interface BlockingQueueFactory<E> {
   String ELEMENT_TYPE = "BlockingQueueFactory";

   BlockingQueue<E> create(int var1);
}
