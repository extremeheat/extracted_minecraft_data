package org.apache.logging.log4j.core;

import java.io.Serializable;

public interface Appender extends LifeCycle {
   String ELEMENT_TYPE = "appender";

   void append(LogEvent var1);

   String getName();

   Layout<? extends Serializable> getLayout();

   boolean ignoreExceptions();

   ErrorHandler getHandler();

   void setHandler(ErrorHandler var1);
}
