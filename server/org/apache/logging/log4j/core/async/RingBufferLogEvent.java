package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.EventFactory;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.ContextDataFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.ReusableMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.message.TimestampMessage;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StringMap;

public class RingBufferLogEvent implements LogEvent, ReusableMessage, CharSequence {
   public static final RingBufferLogEvent.Factory FACTORY = new RingBufferLogEvent.Factory();
   private static final long serialVersionUID = 8462119088943934758L;
   private static final Message EMPTY = new SimpleMessage("");
   private int threadPriority;
   private long threadId;
   private long currentTimeMillis;
   private long nanoTime;
   private short parameterCount;
   private boolean includeLocation;
   private boolean endOfBatch = false;
   private Level level;
   private String threadName;
   private String loggerName;
   private Message message;
   private StringBuilder messageText;
   private Object[] parameters;
   private transient Throwable thrown;
   private ThrowableProxy thrownProxy;
   private StringMap contextData = ContextDataFactory.createContextData();
   private Marker marker;
   private String fqcn;
   private StackTraceElement location;
   private ThreadContext.ContextStack contextStack;
   private transient AsyncLogger asyncLogger;

   public RingBufferLogEvent() {
      super();
   }

   public void setValues(AsyncLogger var1, String var2, Marker var3, String var4, Level var5, Message var6, Throwable var7, StringMap var8, ThreadContext.ContextStack var9, long var10, String var12, int var13, StackTraceElement var14, long var15, long var17) {
      this.threadPriority = var13;
      this.threadId = var10;
      this.currentTimeMillis = var15;
      this.nanoTime = var17;
      this.level = var5;
      this.threadName = var12;
      this.loggerName = var2;
      this.setMessage(var6);
      this.thrown = var7;
      this.thrownProxy = null;
      this.marker = var3;
      this.fqcn = var4;
      this.location = var14;
      this.contextData = var8;
      this.contextStack = var9;
      this.asyncLogger = var1;
   }

   public LogEvent toImmutable() {
      return this.createMemento();
   }

   private void setMessage(Message var1) {
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

   public void execute(boolean var1) {
      this.endOfBatch = var1;
      this.asyncLogger.actualAsyncLog(this);
   }

   public boolean isEndOfBatch() {
      return this.endOfBatch;
   }

   public void setEndOfBatch(boolean var1) {
      this.endOfBatch = var1;
   }

   public boolean isIncludeLocation() {
      return this.includeLocation;
   }

   public void setIncludeLocation(boolean var1) {
      this.includeLocation = var1;
   }

   public String getLoggerName() {
      return this.loggerName;
   }

   public Marker getMarker() {
      return this.marker;
   }

   public String getLoggerFqcn() {
      return this.fqcn;
   }

   public Level getLevel() {
      if (this.level == null) {
         this.level = Level.OFF;
      }

      return this.level;
   }

   public Message getMessage() {
      if (this.message == null) {
         return (Message)(this.messageText == null ? EMPTY : this);
      } else {
         return this.message;
      }
   }

   public String getFormattedMessage() {
      return this.messageText != null ? this.messageText.toString() : (this.message == null ? null : this.message.getFormattedMessage());
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

   public int length() {
      return this.messageText.length();
   }

   public char charAt(int var1) {
      return this.messageText.charAt(var1);
   }

   public CharSequence subSequence(int var1, int var2) {
      return this.messageText.subSequence(var1, var2);
   }

   private Message getNonNullImmutableMessage() {
      return (Message)(this.message != null ? this.message : new SimpleMessage(String.valueOf(this.messageText)));
   }

   public Throwable getThrown() {
      if (this.thrown == null && this.thrownProxy != null) {
         this.thrown = this.thrownProxy.getThrowable();
      }

      return this.thrown;
   }

   public ThrowableProxy getThrownProxy() {
      if (this.thrownProxy == null && this.thrown != null) {
         this.thrownProxy = new ThrowableProxy(this.thrown);
      }

      return this.thrownProxy;
   }

   public ReadOnlyStringMap getContextData() {
      return this.contextData;
   }

   void setContextData(StringMap var1) {
      this.contextData = var1;
   }

   public Map<String, String> getContextMap() {
      return this.contextData.toMap();
   }

   public ThreadContext.ContextStack getContextStack() {
      return this.contextStack;
   }

   public long getThreadId() {
      return this.threadId;
   }

   public String getThreadName() {
      return this.threadName;
   }

   public int getThreadPriority() {
      return this.threadPriority;
   }

   public StackTraceElement getSource() {
      return this.location;
   }

   public long getTimeMillis() {
      return this.message instanceof TimestampMessage ? ((TimestampMessage)this.message).getTimestamp() : this.currentTimeMillis;
   }

   public long getNanoTime() {
      return this.nanoTime;
   }

   public void clear() {
      this.asyncLogger = null;
      this.loggerName = null;
      this.marker = null;
      this.fqcn = null;
      this.level = null;
      this.message = null;
      this.thrown = null;
      this.thrownProxy = null;
      this.contextStack = null;
      this.location = null;
      if (this.contextData != null) {
         if (this.contextData.isFrozen()) {
            this.contextData = null;
         } else {
            this.contextData.clear();
         }
      }

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

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.getThrownProxy();
      var1.defaultWriteObject();
   }

   public LogEvent createMemento() {
      return (new Log4jLogEvent.Builder(this)).build();
   }

   public void initializeBuilder(Log4jLogEvent.Builder var1) {
      var1.setContextData(this.contextData).setContextStack(this.contextStack).setEndOfBatch(this.endOfBatch).setIncludeLocation(this.includeLocation).setLevel(this.getLevel()).setLoggerFqcn(this.fqcn).setLoggerName(this.loggerName).setMarker(this.marker).setMessage(this.getNonNullImmutableMessage()).setNanoTime(this.nanoTime).setSource(this.location).setThreadId(this.threadId).setThreadName(this.threadName).setThreadPriority(this.threadPriority).setThrown(this.getThrown()).setThrownProxy(this.thrownProxy).setTimeMillis(this.currentTimeMillis);
   }

   private static class Factory implements EventFactory<RingBufferLogEvent> {
      private Factory() {
         super();
      }

      public RingBufferLogEvent newInstance() {
         RingBufferLogEvent var1 = new RingBufferLogEvent();
         if (Constants.ENABLE_THREADLOCALS) {
            var1.messageText = new StringBuilder(Constants.INITIAL_REUSABLE_MESSAGE_SIZE);
            var1.parameters = new Object[10];
         }

         return var1;
      }

      // $FF: synthetic method
      Factory(Object var1) {
         this();
      }
   }
}
