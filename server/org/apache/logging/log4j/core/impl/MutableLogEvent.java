package org.apache.logging.log4j.core.impl;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.ReusableMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StringMap;

public class MutableLogEvent implements LogEvent, ReusableMessage {
   private static final Message EMPTY = new SimpleMessage("");
   private int threadPriority;
   private long threadId;
   private long timeMillis;
   private long nanoTime;
   private short parameterCount;
   private boolean includeLocation;
   private boolean endOfBatch;
   private Level level;
   private String threadName;
   private String loggerName;
   private Message message;
   private StringBuilder messageText;
   private Object[] parameters;
   private Throwable thrown;
   private ThrowableProxy thrownProxy;
   private StringMap contextData;
   private Marker marker;
   private String loggerFqcn;
   private StackTraceElement source;
   private ThreadContext.ContextStack contextStack;
   transient boolean reserved;

   public MutableLogEvent() {
      this(new StringBuilder(Constants.INITIAL_REUSABLE_MESSAGE_SIZE), new Object[10]);
   }

   public MutableLogEvent(StringBuilder var1, Object[] var2) {
      super();
      this.endOfBatch = false;
      this.contextData = ContextDataFactory.createContextData();
      this.reserved = false;
      this.messageText = var1;
      this.parameters = var2;
   }

   public Log4jLogEvent toImmutable() {
      return this.createMemento();
   }

   public void initFrom(LogEvent var1) {
      this.loggerFqcn = var1.getLoggerFqcn();
      this.marker = var1.getMarker();
      this.level = var1.getLevel();
      this.loggerName = var1.getLoggerName();
      this.timeMillis = var1.getTimeMillis();
      this.thrown = var1.getThrown();
      this.thrownProxy = var1.getThrownProxy();
      this.contextData.putAll(var1.getContextData());
      this.contextStack = var1.getContextStack();
      this.source = var1.isIncludeLocation() ? var1.getSource() : null;
      this.threadId = var1.getThreadId();
      this.threadName = var1.getThreadName();
      this.threadPriority = var1.getThreadPriority();
      this.endOfBatch = var1.isEndOfBatch();
      this.includeLocation = var1.isIncludeLocation();
      this.nanoTime = var1.getNanoTime();
      this.setMessage(var1.getMessage());
   }

   public void clear() {
      this.loggerFqcn = null;
      this.marker = null;
      this.level = null;
      this.loggerName = null;
      this.message = null;
      this.thrown = null;
      this.thrownProxy = null;
      this.source = null;
      if (this.contextData != null) {
         if (this.contextData.isFrozen()) {
            this.contextData = null;
         } else {
            this.contextData.clear();
         }
      }

      this.contextStack = null;
      this.trimMessageText();
      if (this.parameters != null) {
         for(int var1 = 0; var1 < this.parameters.length; ++var1) {
            this.parameters[var1] = null;
         }
      }

   }

   private void trimMessageText() {
      if (this.messageText != null && this.messageText.length() > Constants.MAX_REUSABLE_MESSAGE_SIZE) {
         this.messageText.setLength(Constants.MAX_REUSABLE_MESSAGE_SIZE);
         this.messageText.trimToSize();
      }

   }

   public String getLoggerFqcn() {
      return this.loggerFqcn;
   }

   public void setLoggerFqcn(String var1) {
      this.loggerFqcn = var1;
   }

   public Marker getMarker() {
      return this.marker;
   }

   public void setMarker(Marker var1) {
      this.marker = var1;
   }

   public Level getLevel() {
      if (this.level == null) {
         this.level = Level.OFF;
      }

      return this.level;
   }

   public void setLevel(Level var1) {
      this.level = var1;
   }

   public String getLoggerName() {
      return this.loggerName;
   }

   public void setLoggerName(String var1) {
      this.loggerName = var1;
   }

   public Message getMessage() {
      if (this.message == null) {
         return (Message)(this.messageText == null ? EMPTY : this);
      } else {
         return this.message;
      }
   }

   public void setMessage(Message var1) {
      if (var1 instanceof ReusableMessage) {
         ReusableMessage var2 = (ReusableMessage)var1;
         var2.formatTo(this.getMessageTextForWriting());
         if (this.parameters != null) {
            this.parameters = var2.swapParameters(this.parameters);
            this.parameterCount = var2.getParameterCount();
         }
      } else {
         if (var1 != null && !this.canFormatMessageInBackground(var1)) {
            var1.getFormattedMessage();
         }

         this.message = var1;
      }

   }

   private boolean canFormatMessageInBackground(Message var1) {
      return Constants.FORMAT_MESSAGES_IN_BACKGROUND || var1.getClass().isAnnotationPresent(AsynchronouslyFormattable.class);
   }

   private StringBuilder getMessageTextForWriting() {
      if (this.messageText == null) {
         this.messageText = new StringBuilder(Constants.INITIAL_REUSABLE_MESSAGE_SIZE);
      }

      this.messageText.setLength(0);
      return this.messageText;
   }

   public String getFormattedMessage() {
      return this.messageText.toString();
   }

   public String getFormat() {
      return null;
   }

   public Object[] getParameters() {
      return this.parameters == null ? null : Arrays.copyOf(this.parameters, this.parameterCount);
   }

   public Throwable getThrowable() {
      return this.getThrown();
   }

   public void formatTo(StringBuilder var1) {
      var1.append(this.messageText);
   }

   public Object[] swapParameters(Object[] var1) {
      Object[] var2 = this.parameters;
      this.parameters = var1;
      return var2;
   }

   public short getParameterCount() {
      return this.parameterCount;
   }

   public Message memento() {
      if (this.message != null) {
         return this.message;
      } else {
         Object[] var1 = this.parameters == null ? new Object[0] : Arrays.copyOf(this.parameters, this.parameterCount);
         return new ParameterizedMessage(this.messageText.toString(), var1);
      }
   }

   public Throwable getThrown() {
      return this.thrown;
   }

   public void setThrown(Throwable var1) {
      this.thrown = var1;
   }

   public long getTimeMillis() {
      return this.timeMillis;
   }

   public void setTimeMillis(long var1) {
      this.timeMillis = var1;
   }

   public ThrowableProxy getThrownProxy() {
      if (this.thrownProxy == null && this.thrown != null) {
         this.thrownProxy = new ThrowableProxy(this.thrown);
      }

      return this.thrownProxy;
   }

   public StackTraceElement getSource() {
      if (this.source != null) {
         return this.source;
      } else if (this.loggerFqcn != null && this.includeLocation) {
         this.source = Log4jLogEvent.calcLocation(this.loggerFqcn);
         return this.source;
      } else {
         return null;
      }
   }

   public ReadOnlyStringMap getContextData() {
      return this.contextData;
   }

   public Map<String, String> getContextMap() {
      return this.contextData.toMap();
   }

   public void setContextData(StringMap var1) {
      this.contextData = var1;
   }

   public ThreadContext.ContextStack getContextStack() {
      return this.contextStack;
   }

   public void setContextStack(ThreadContext.ContextStack var1) {
      this.contextStack = var1;
   }

   public long getThreadId() {
      return this.threadId;
   }

   public void setThreadId(long var1) {
      this.threadId = var1;
   }

   public String getThreadName() {
      return this.threadName;
   }

   public void setThreadName(String var1) {
      this.threadName = var1;
   }

   public int getThreadPriority() {
      return this.threadPriority;
   }

   public void setThreadPriority(int var1) {
      this.threadPriority = var1;
   }

   public boolean isIncludeLocation() {
      return this.includeLocation;
   }

   public void setIncludeLocation(boolean var1) {
      this.includeLocation = var1;
   }

   public boolean isEndOfBatch() {
      return this.endOfBatch;
   }

   public void setEndOfBatch(boolean var1) {
      this.endOfBatch = var1;
   }

   public long getNanoTime() {
      return this.nanoTime;
   }

   public void setNanoTime(long var1) {
      this.nanoTime = var1;
   }

   protected Object writeReplace() {
      return new Log4jLogEvent.LogEventProxy(this, this.includeLocation);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   public Log4jLogEvent createMemento() {
      return Log4jLogEvent.deserialize(Log4jLogEvent.serialize((LogEvent)this, this.includeLocation));
   }

   public void initializeBuilder(Log4jLogEvent.Builder var1) {
      var1.setContextData(this.contextData).setContextStack(this.contextStack).setEndOfBatch(this.endOfBatch).setIncludeLocation(this.includeLocation).setLevel(this.getLevel()).setLoggerFqcn(this.loggerFqcn).setLoggerName(this.loggerName).setMarker(this.marker).setMessage(this.getNonNullImmutableMessage()).setNanoTime(this.nanoTime).setSource(this.source).setThreadId(this.threadId).setThreadName(this.threadName).setThreadPriority(this.threadPriority).setThrown(this.getThrown()).setThrownProxy(this.thrownProxy).setTimeMillis(this.timeMillis);
   }

   private Message getNonNullImmutableMessage() {
      return (Message)(this.message != null ? this.message : new SimpleMessage(String.valueOf(this.messageText)));
   }
}
