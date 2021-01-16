package org.apache.logging.log4j.core;

import java.util.Collections;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

public abstract class AbstractLogEvent implements LogEvent {
   private static final long serialVersionUID = 1L;

   public AbstractLogEvent() {
      super();
   }

   public LogEvent toImmutable() {
      return this;
   }

   public ReadOnlyStringMap getContextData() {
      return null;
   }

   public Map<String, String> getContextMap() {
      return Collections.emptyMap();
   }

   public ThreadContext.ContextStack getContextStack() {
      return ThreadContext.EMPTY_STACK;
   }

   public Level getLevel() {
      return null;
   }

   public String getLoggerFqcn() {
      return null;
   }

   public String getLoggerName() {
      return null;
   }

   public Marker getMarker() {
      return null;
   }

   public Message getMessage() {
      return null;
   }

   public StackTraceElement getSource() {
      return null;
   }

   public long getThreadId() {
      return 0L;
   }

   public String getThreadName() {
      return null;
   }

   public int getThreadPriority() {
      return 0;
   }

   public Throwable getThrown() {
      return null;
   }

   public ThrowableProxy getThrownProxy() {
      return null;
   }

   public long getTimeMillis() {
      return 0L;
   }

   public boolean isEndOfBatch() {
      return false;
   }

   public boolean isIncludeLocation() {
      return false;
   }

   public void setEndOfBatch(boolean var1) {
   }

   public void setIncludeLocation(boolean var1) {
   }

   public long getNanoTime() {
      return 0L;
   }
}
