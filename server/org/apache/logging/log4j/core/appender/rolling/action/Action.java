package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.IOException;

public interface Action extends Runnable {
   boolean execute() throws IOException;

   void close();

   boolean isComplete();
}
