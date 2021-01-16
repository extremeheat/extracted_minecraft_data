package org.apache.logging.log4j.core;

import java.io.Serializable;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

public interface LogEvent extends Serializable {
   LogEvent toImmutable();

   /** @deprecated */
   @Deprecated
   Map<String, String> getContextMap();

   ReadOnlyStringMap getContextData();

   ThreadContext.ContextStack getContextStack();

   String getLoggerFqcn();

   Level getLevel();

   String getLoggerName();

   Marker getMarker();

   Message getMessage();

   long getTimeMillis();

   StackTraceElement getSource();

   String getThreadName();

   long getThreadId();

   int getThreadPriority();

   Throwable getThrown();

   ThrowableProxy getThrownProxy();

   boolean isEndOfBatch();

   boolean isIncludeLocation();

   void setEndOfBatch(boolean var1);

   void setIncludeLocation(boolean var1);

   long getNanoTime();
}
