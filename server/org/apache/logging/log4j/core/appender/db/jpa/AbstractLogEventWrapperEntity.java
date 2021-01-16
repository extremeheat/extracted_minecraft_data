package org.apache.logging.log4j.core.appender.db.jpa;

import java.util.Map;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.AbstractLogEvent;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

@MappedSuperclass
@Inheritance(
   strategy = InheritanceType.SINGLE_TABLE
)
public abstract class AbstractLogEventWrapperEntity implements LogEvent {
   private static final long serialVersionUID = 1L;
   private final LogEvent wrappedEvent;

   protected AbstractLogEventWrapperEntity() {
      this(new AbstractLogEventWrapperEntity.NullLogEvent());
   }

   protected AbstractLogEventWrapperEntity(LogEvent var1) {
      super();
      if (var1 == null) {
         throw new IllegalArgumentException("The wrapped event cannot be null.");
      } else {
         this.wrappedEvent = var1;
      }
   }

   public LogEvent toImmutable() {
      return Log4jLogEvent.createMemento(this);
   }

   @Transient
   protected final LogEvent getWrappedEvent() {
      return this.wrappedEvent;
   }

   public void setLevel(Level var1) {
   }

   public void setLoggerName(String var1) {
   }

   public void setSource(StackTraceElement var1) {
   }

   public void setMessage(Message var1) {
   }

   public void setMarker(Marker var1) {
   }

   public void setThreadId(long var1) {
   }

   public void setThreadName(String var1) {
   }

   public void setThreadPriority(int var1) {
   }

   public void setNanoTime(long var1) {
   }

   public void setTimeMillis(long var1) {
   }

   public void setThrown(Throwable var1) {
   }

   public void setContextData(ReadOnlyStringMap var1) {
   }

   public void setContextMap(Map<String, String> var1) {
   }

   public void setContextStack(ThreadContext.ContextStack var1) {
   }

   public void setLoggerFqcn(String var1) {
   }

   @Transient
   public final boolean isIncludeLocation() {
      return this.getWrappedEvent().isIncludeLocation();
   }

   public final void setIncludeLocation(boolean var1) {
      this.getWrappedEvent().setIncludeLocation(var1);
   }

   @Transient
   public final boolean isEndOfBatch() {
      return this.getWrappedEvent().isEndOfBatch();
   }

   public final void setEndOfBatch(boolean var1) {
      this.getWrappedEvent().setEndOfBatch(var1);
   }

   @Transient
   public ReadOnlyStringMap getContextData() {
      return this.getWrappedEvent().getContextData();
   }

   private static class NullLogEvent extends AbstractLogEvent {
      private static final long serialVersionUID = 1L;

      private NullLogEvent() {
         super();
      }

      // $FF: synthetic method
      NullLogEvent(Object var1) {
         this();
      }
   }
}
