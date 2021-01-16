package org.apache.logging.log4j.core.util;

public interface Cancellable extends Runnable {
   void cancel();
}
