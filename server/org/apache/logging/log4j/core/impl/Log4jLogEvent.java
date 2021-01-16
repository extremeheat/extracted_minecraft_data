package org.apache.logging.log4j.core.impl;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.async.RingBufferLogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.util.Clock;
import org.apache.logging.log4j.core.util.ClockFactory;
import org.apache.logging.log4j.core.util.DummyNanoClock;
import org.apache.logging.log4j.core.util.NanoClock;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ReusableMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.message.TimestampMessage;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StringMap;

public class Log4jLogEvent implements LogEvent {
   private static final long serialVersionUID = -8393305700508709443L;
   private static final Clock CLOCK = ClockFactory.getClock();
   private static volatile NanoClock nanoClock = new DummyNanoClock();
   private static final ContextDataInjector CONTEXT_DATA_INJECTOR = ContextDataInjectorFactory.createInjector();
   private final String loggerFqcn;
   private final Marker marker;
   private final Level level;
   private final String loggerName;
   private Message message;
   private final long timeMillis;
   private final transient Throwable thrown;
   private ThrowableProxy thrownProxy;
   private final StringMap contextData;
   private final ThreadContext.ContextStack contextStack;
   private long threadId;
   private String threadName;
   private int threadPriority;
   private StackTraceElement source;
   private boolean includeLocation;
   private boolean endOfBatch;
   private final transient long nanoTime;

   public static Log4jLogEvent.Builder newBuilder() {
      return new Log4jLogEvent.Builder();
   }

   public Log4jLogEvent() {
      this("", (Marker)null, "", (Level)null, (Message)null, (Throwable)null, (ThrowableProxy)null, (StringMap)null, (ThreadContext.ContextStack)null, 0L, (String)null, 0, (StackTraceElement)null, CLOCK.currentTimeMillis(), nanoClock.nanoTime());
   }

   /** @deprecated */
   @Deprecated
   public Log4jLogEvent(long var1) {
      this("", (Marker)null, "", (Level)null, (Message)null, (Throwable)null, (ThrowableProxy)null, (StringMap)null, (ThreadContext.ContextStack)null, 0L, (String)null, 0, (StackTraceElement)null, var1, nanoClock.nanoTime());
   }

   /** @deprecated */
   @Deprecated
   public Log4jLogEvent(String var1, Marker var2, String var3, Level var4, Message var5, Throwable var6) {
      this(var1, var2, var3, var4, var5, (List)null, var6);
   }

   public Log4jLogEvent(String var1, Marker var2, String var3, Level var4, Message var5, List<Property> var6, Throwable var7) {
      this(var1, var2, var3, var4, var5, var7, (ThrowableProxy)null, createContextData(var6), ThreadContext.getDepth() == 0 ? null : ThreadContext.cloneStack(), 0L, (String)null, 0, (StackTraceElement)null, var5 instanceof TimestampMessage ? ((TimestampMessage)var5).getTimestamp() : CLOCK.currentTimeMillis(), nanoClock.nanoTime());
   }

   /** @deprecated */
   @Deprecated
   public Log4jLogEvent(String var1, Marker var2, String var3, Level var4, Message var5, Throwable var6, Map<String, String> var7, ThreadContext.ContextStack var8, String var9, StackTraceElement var10, long var11) {
      this(var1, var2, var3, var4, var5, var6, (ThrowableProxy)null, createContextData(var7), var8, 0L, var9, 0, var10, var11, nanoClock.nanoTime());
   }

   /** @deprecated */
   @Deprecated
   public static Log4jLogEvent createEvent(String var0, Marker var1, String var2, Level var3, Message var4, Throwable var5, ThrowableProxy var6, Map<String, String> var7, ThreadContext.ContextStack var8, String var9, StackTraceElement var10, long var11) {
      Log4jLogEvent var13 = new Log4jLogEvent(var0, var1, var2, var3, var4, var5, var6, createContextData(var7), var8, 0L, var9, 0, var10, var11, nanoClock.nanoTime());
      return var13;
   }

   private Log4jLogEvent(String var1, Marker var2, String var3, Level var4, Message var5, Throwable var6, ThrowableProxy var7, StringMap var8, ThreadContext.ContextStack var9, long var10, String var12, int var13, StackTraceElement var14, long var15, long var17) {
      super();
      this.endOfBatch = false;
      this.loggerName = var1;
      this.marker = var2;
      this.loggerFqcn = var3;
      this.level = var4 == null ? Level.OFF : var4;
      this.message = var5;
      this.thrown = var6;
      this.thrownProxy = var7;
      this.contextData = var8 == null ? ContextDataFactory.createContextData() : var8;
      this.contextStack = (ThreadContext.ContextStack)(var9 == null ? ThreadContext.EMPTY_STACK : var9);
      this.timeMillis = var5 instanceof TimestampMessage ? ((TimestampMessage)var5).getTimestamp() : var15;
      this.threadId = var10;
      this.threadName = var12;
      this.threadPriority = var13;
      this.source = var14;
      if (var5 != null && var5 instanceof LoggerNameAwareMessage) {
         ((LoggerNameAwareMessage)var5).setLoggerName(var1);
      }

      this.nanoTime = var17;
   }

   private static StringMap createContextData(Map<String, String> var0) {
      StringMap var1 = ContextDataFactory.createContextData();
      if (var0 != null) {
         Iterator var2 = var0.entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            var1.putValue((String)var3.getKey(), var3.getValue());
         }
      }

      return var1;
   }

   private static StringMap createContextData(List<Property> var0) {
      StringMap var1 = ContextDataFactory.createContextData();
      return CONTEXT_DATA_INJECTOR.injectContextData(var0, var1);
   }

   public static NanoClock getNanoClock() {
      return nanoClock;
   }

   public static void setNanoClock(NanoClock var0) {
      nanoClock = (NanoClock)Objects.requireNonNull(var0, "NanoClock must be non-null");
      StatusLogger.getLogger().trace("Using {} for nanosecond timestamps.", var0.getClass().getSimpleName());
   }

   public Log4jLogEvent.Builder asBuilder() {
      return new Log4jLogEvent.Builder(this);
   }

   public Log4jLogEvent toImmutable() {
      if (this.getMessage() instanceof ReusableMessage) {
         this.makeMessageImmutable();
      }

      return this;
   }

   public Level getLevel() {
      return this.level;
   }

   public String getLoggerName() {
      return this.loggerName;
   }

   public Message getMessage() {
      return this.message;
   }

   public void makeMessageImmutable() {
      this.message = new SimpleMessage(this.message.getFormattedMessage());
   }

   public long getThreadId() {
      if (this.threadId == 0L) {
         this.threadId = Thread.currentThread().getId();
      }

      return this.threadId;
   }

   public String getThreadName() {
      if (this.threadName == null) {
         this.threadName = Thread.currentThread().getName();
      }

      return this.threadName;
   }

   public int getThreadPriority() {
      if (this.threadPriority == 0) {
         this.threadPriority = Thread.currentThread().getPriority();
      }

      return this.threadPriority;
   }

   public long getTimeMillis() {
      return this.timeMillis;
   }

   public Throwable getThrown() {
      return this.thrown;
   }

   public ThrowableProxy getThrownProxy() {
      if (this.thrownProxy == null && this.thrown != null) {
         this.thrownProxy = new ThrowableProxy(this.thrown);
      }

      return this.thrownProxy;
   }

   public Marker getMarker() {
      return this.marker;
   }

   public String getLoggerFqcn() {
      return this.loggerFqcn;
   }

   public ReadOnlyStringMap getContextData() {
      return this.contextData;
   }

   public Map<String, String> getContextMap() {
      return this.contextData.toMap();
   }

   public ThreadContext.ContextStack getContextStack() {
      return this.contextStack;
   }

   public StackTraceElement getSource() {
      if (this.source != null) {
         return this.source;
      } else if (this.loggerFqcn != null && this.includeLocation) {
         this.source = calcLocation(this.loggerFqcn);
         return this.source;
      } else {
         return null;
      }
   }

   public static StackTraceElement calcLocation(String var0) {
      if (var0 == null) {
         return null;
      } else {
         StackTraceElement[] var1 = (new Throwable()).getStackTrace();
         StackTraceElement var2 = null;

         for(int var3 = var1.length - 1; var3 > 0; --var3) {
            String var4 = var1[var3].getClassName();
            if (var0.equals(var4)) {
               return var2;
            }

            var2 = var1[var3];
         }

         return null;
      }
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

   protected Object writeReplace() {
      this.getThrownProxy();
      return new Log4jLogEvent.LogEventProxy(this, this.includeLocation);
   }

   public static Serializable serialize(LogEvent var0, boolean var1) {
      if (var0 instanceof Log4jLogEvent) {
         var0.getThrownProxy();
         return new Log4jLogEvent.LogEventProxy((Log4jLogEvent)var0, var1);
      } else {
         return new Log4jLogEvent.LogEventProxy(var0, var1);
      }
   }

   public static Serializable serialize(Log4jLogEvent var0, boolean var1) {
      var0.getThrownProxy();
      return new Log4jLogEvent.LogEventProxy(var0, var1);
   }

   public static boolean canDeserialize(Serializable var0) {
      return var0 instanceof Log4jLogEvent.LogEventProxy;
   }

   public static Log4jLogEvent deserialize(Serializable var0) {
      Objects.requireNonNull(var0, "Event cannot be null");
      if (var0 instanceof Log4jLogEvent.LogEventProxy) {
         Log4jLogEvent.LogEventProxy var1 = (Log4jLogEvent.LogEventProxy)var0;
         Log4jLogEvent var2 = new Log4jLogEvent(var1.loggerName, var1.marker, var1.loggerFQCN, var1.level, var1.message, var1.thrown, var1.thrownProxy, var1.contextData, var1.contextStack, var1.threadId, var1.threadName, var1.threadPriority, var1.source, var1.timeMillis, var1.nanoTime);
         var2.setEndOfBatch(var1.isEndOfBatch);
         var2.setIncludeLocation(var1.isLocationRequired);
         return var2;
      } else {
         throw new IllegalArgumentException("Event is not a serialized LogEvent: " + var0.toString());
      }
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   public LogEvent createMemento() {
      return createMemento(this);
   }

   public static LogEvent createMemento(LogEvent var0) {
      return (new Log4jLogEvent.Builder(var0)).build();
   }

   public static Log4jLogEvent createMemento(LogEvent var0, boolean var1) {
      return deserialize(serialize(var0, var1));
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = this.loggerName.isEmpty() ? "root" : this.loggerName;
      var1.append("Logger=").append(var2);
      var1.append(" Level=").append(this.level.name());
      var1.append(" Message=").append(this.message == null ? null : this.message.getFormattedMessage());
      return var1.toString();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Log4jLogEvent var2 = (Log4jLogEvent)var1;
         if (this.endOfBatch != var2.endOfBatch) {
            return false;
         } else if (this.includeLocation != var2.includeLocation) {
            return false;
         } else if (this.timeMillis != var2.timeMillis) {
            return false;
         } else if (this.nanoTime != var2.nanoTime) {
            return false;
         } else {
            if (this.loggerFqcn != null) {
               if (!this.loggerFqcn.equals(var2.loggerFqcn)) {
                  return false;
               }
            } else if (var2.loggerFqcn != null) {
               return false;
            }

            label136: {
               if (this.level != null) {
                  if (this.level.equals(var2.level)) {
                     break label136;
                  }
               } else if (var2.level == null) {
                  break label136;
               }

               return false;
            }

            label129: {
               if (this.source != null) {
                  if (this.source.equals(var2.source)) {
                     break label129;
                  }
               } else if (var2.source == null) {
                  break label129;
               }

               return false;
            }

            if (this.marker != null) {
               if (!this.marker.equals(var2.marker)) {
                  return false;
               }
            } else if (var2.marker != null) {
               return false;
            }

            if (this.contextData != null) {
               if (!this.contextData.equals(var2.contextData)) {
                  return false;
               }
            } else if (var2.contextData != null) {
               return false;
            }

            if (!this.message.equals(var2.message)) {
               return false;
            } else if (!this.loggerName.equals(var2.loggerName)) {
               return false;
            } else {
               label105: {
                  if (this.contextStack != null) {
                     if (this.contextStack.equals(var2.contextStack)) {
                        break label105;
                     }
                  } else if (var2.contextStack == null) {
                     break label105;
                  }

                  return false;
               }

               if (this.threadId != var2.threadId) {
                  return false;
               } else {
                  if (this.threadName != null) {
                     if (!this.threadName.equals(var2.threadName)) {
                        return false;
                     }
                  } else if (var2.threadName != null) {
                     return false;
                  }

                  if (this.threadPriority != var2.threadPriority) {
                     return false;
                  } else {
                     label89: {
                        if (this.thrown != null) {
                           if (this.thrown.equals(var2.thrown)) {
                              break label89;
                           }
                        } else if (var2.thrown == null) {
                           break label89;
                        }

                        return false;
                     }

                     if (this.thrownProxy != null) {
                        if (!this.thrownProxy.equals(var2.thrownProxy)) {
                           return false;
                        }
                     } else if (var2.thrownProxy != null) {
                        return false;
                     }

                     return true;
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.loggerFqcn != null ? this.loggerFqcn.hashCode() : 0;
      var1 = 31 * var1 + (this.marker != null ? this.marker.hashCode() : 0);
      var1 = 31 * var1 + (this.level != null ? this.level.hashCode() : 0);
      var1 = 31 * var1 + this.loggerName.hashCode();
      var1 = 31 * var1 + this.message.hashCode();
      var1 = 31 * var1 + (int)(this.timeMillis ^ this.timeMillis >>> 32);
      var1 = 31 * var1 + (int)(this.nanoTime ^ this.nanoTime >>> 32);
      var1 = 31 * var1 + (this.thrown != null ? this.thrown.hashCode() : 0);
      var1 = 31 * var1 + (this.thrownProxy != null ? this.thrownProxy.hashCode() : 0);
      var1 = 31 * var1 + (this.contextData != null ? this.contextData.hashCode() : 0);
      var1 = 31 * var1 + (this.contextStack != null ? this.contextStack.hashCode() : 0);
      var1 = 31 * var1 + (int)(this.threadId ^ this.threadId >>> 32);
      var1 = 31 * var1 + (this.threadName != null ? this.threadName.hashCode() : 0);
      var1 = 31 * var1 + (this.threadPriority ^ this.threadPriority >>> 32);
      var1 = 31 * var1 + (this.source != null ? this.source.hashCode() : 0);
      var1 = 31 * var1 + (this.includeLocation ? 1 : 0);
      var1 = 31 * var1 + (this.endOfBatch ? 1 : 0);
      return var1;
   }

   // $FF: synthetic method
   Log4jLogEvent(String var1, Marker var2, String var3, Level var4, Message var5, Throwable var6, ThrowableProxy var7, StringMap var8, ThreadContext.ContextStack var9, long var10, String var12, int var13, StackTraceElement var14, long var15, long var17, Object var19) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var12, var13, var14, var15, var17);
   }

   static class LogEventProxy implements Serializable {
      private static final long serialVersionUID = -8634075037355293699L;
      private final String loggerFQCN;
      private final Marker marker;
      private final Level level;
      private final String loggerName;
      private final transient Message message;
      private MarshalledObject<Message> marshalledMessage;
      private String messageString;
      private final long timeMillis;
      private final transient Throwable thrown;
      private final ThrowableProxy thrownProxy;
      private final StringMap contextData;
      private final ThreadContext.ContextStack contextStack;
      private final long threadId;
      private final String threadName;
      private final int threadPriority;
      private final StackTraceElement source;
      private final boolean isLocationRequired;
      private final boolean isEndOfBatch;
      private final transient long nanoTime;

      public LogEventProxy(Log4jLogEvent var1, boolean var2) {
         super();
         this.loggerFQCN = var1.loggerFqcn;
         this.marker = var1.marker;
         this.level = var1.level;
         this.loggerName = var1.loggerName;
         this.message = var1.message instanceof ReusableMessage ? memento((ReusableMessage)var1.message) : var1.message;
         this.timeMillis = var1.timeMillis;
         this.thrown = var1.thrown;
         this.thrownProxy = var1.thrownProxy;
         this.contextData = var1.contextData;
         this.contextStack = var1.contextStack;
         this.source = var2 ? var1.getSource() : null;
         this.threadId = var1.getThreadId();
         this.threadName = var1.getThreadName();
         this.threadPriority = var1.getThreadPriority();
         this.isLocationRequired = var2;
         this.isEndOfBatch = var1.endOfBatch;
         this.nanoTime = var1.nanoTime;
      }

      public LogEventProxy(LogEvent var1, boolean var2) {
         super();
         this.loggerFQCN = var1.getLoggerFqcn();
         this.marker = var1.getMarker();
         this.level = var1.getLevel();
         this.loggerName = var1.getLoggerName();
         Message var3 = var1.getMessage();
         this.message = var3 instanceof ReusableMessage ? memento((ReusableMessage)var3) : var3;
         this.timeMillis = var1.getTimeMillis();
         this.thrown = var1.getThrown();
         this.thrownProxy = var1.getThrownProxy();
         this.contextData = memento(var1.getContextData());
         this.contextStack = var1.getContextStack();
         this.source = var2 ? var1.getSource() : null;
         this.threadId = var1.getThreadId();
         this.threadName = var1.getThreadName();
         this.threadPriority = var1.getThreadPriority();
         this.isLocationRequired = var2;
         this.isEndOfBatch = var1.isEndOfBatch();
         this.nanoTime = var1.getNanoTime();
      }

      private static Message memento(ReusableMessage var0) {
         return var0.memento();
      }

      private static StringMap memento(ReadOnlyStringMap var0) {
         StringMap var1 = ContextDataFactory.createContextData();
         var1.putAll(var0);
         return var1;
      }

      private static MarshalledObject<Message> marshall(Message var0) {
         try {
            return new MarshalledObject(var0);
         } catch (Exception var2) {
            return null;
         }
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         this.messageString = this.message.getFormattedMessage();
         this.marshalledMessage = marshall(this.message);
         var1.defaultWriteObject();
      }

      protected Object readResolve() {
         Log4jLogEvent var1 = new Log4jLogEvent(this.loggerName, this.marker, this.loggerFQCN, this.level, this.message(), this.thrown, this.thrownProxy, this.contextData, this.contextStack, this.threadId, this.threadName, this.threadPriority, this.source, this.timeMillis, this.nanoTime);
         var1.setEndOfBatch(this.isEndOfBatch);
         var1.setIncludeLocation(this.isLocationRequired);
         return var1;
      }

      private Message message() {
         if (this.marshalledMessage != null) {
            try {
               return (Message)this.marshalledMessage.get();
            } catch (Exception var2) {
            }
         }

         return new SimpleMessage(this.messageString);
      }
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<LogEvent> {
      private String loggerFqcn;
      private Marker marker;
      private Level level;
      private String loggerName;
      private Message message;
      private Throwable thrown;
      private long timeMillis;
      private ThrowableProxy thrownProxy;
      private StringMap contextData;
      private ThreadContext.ContextStack contextStack;
      private long threadId;
      private String threadName;
      private int threadPriority;
      private StackTraceElement source;
      private boolean includeLocation;
      private boolean endOfBatch;
      private long nanoTime;

      public Builder() {
         super();
         this.timeMillis = Log4jLogEvent.CLOCK.currentTimeMillis();
         this.contextData = Log4jLogEvent.createContextData((List)null);
         this.contextStack = ThreadContext.getImmutableStack();
         this.endOfBatch = false;
      }

      public Builder(LogEvent var1) {
         super();
         this.timeMillis = Log4jLogEvent.CLOCK.currentTimeMillis();
         this.contextData = Log4jLogEvent.createContextData((List)null);
         this.contextStack = ThreadContext.getImmutableStack();
         this.endOfBatch = false;
         Objects.requireNonNull(var1);
         if (var1 instanceof RingBufferLogEvent) {
            ((RingBufferLogEvent)var1).initializeBuilder(this);
         } else if (var1 instanceof MutableLogEvent) {
            ((MutableLogEvent)var1).initializeBuilder(this);
         } else {
            this.loggerFqcn = var1.getLoggerFqcn();
            this.marker = var1.getMarker();
            this.level = var1.getLevel();
            this.loggerName = var1.getLoggerName();
            this.message = var1.getMessage();
            this.timeMillis = var1.getTimeMillis();
            this.thrown = var1.getThrown();
            this.contextStack = var1.getContextStack();
            this.includeLocation = var1.isIncludeLocation();
            this.endOfBatch = var1.isEndOfBatch();
            this.nanoTime = var1.getNanoTime();
            if (var1 instanceof Log4jLogEvent) {
               Log4jLogEvent var2 = (Log4jLogEvent)var1;
               this.contextData = var2.contextData;
               this.thrownProxy = var2.thrownProxy;
               this.source = var2.source;
               this.threadId = var2.threadId;
               this.threadName = var2.threadName;
               this.threadPriority = var2.threadPriority;
            } else {
               if (var1.getContextData() instanceof StringMap) {
                  this.contextData = (StringMap)var1.getContextData();
               } else {
                  if (this.contextData.isFrozen()) {
                     this.contextData = ContextDataFactory.createContextData();
                  } else {
                     this.contextData.clear();
                  }

                  this.contextData.putAll(var1.getContextData());
               }

               this.thrownProxy = var1.getThrownProxy();
               this.source = var1.getSource();
               this.threadId = var1.getThreadId();
               this.threadName = var1.getThreadName();
               this.threadPriority = var1.getThreadPriority();
            }

         }
      }

      public Log4jLogEvent.Builder setLevel(Level var1) {
         this.level = var1;
         return this;
      }

      public Log4jLogEvent.Builder setLoggerFqcn(String var1) {
         this.loggerFqcn = var1;
         return this;
      }

      public Log4jLogEvent.Builder setLoggerName(String var1) {
         this.loggerName = var1;
         return this;
      }

      public Log4jLogEvent.Builder setMarker(Marker var1) {
         this.marker = var1;
         return this;
      }

      public Log4jLogEvent.Builder setMessage(Message var1) {
         this.message = var1;
         return this;
      }

      public Log4jLogEvent.Builder setThrown(Throwable var1) {
         this.thrown = var1;
         return this;
      }

      public Log4jLogEvent.Builder setTimeMillis(long var1) {
         this.timeMillis = var1;
         return this;
      }

      public Log4jLogEvent.Builder setThrownProxy(ThrowableProxy var1) {
         this.thrownProxy = var1;
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Log4jLogEvent.Builder setContextMap(Map<String, String> var1) {
         this.contextData = ContextDataFactory.createContextData();
         if (var1 != null) {
            Iterator var2 = var1.entrySet().iterator();

            while(var2.hasNext()) {
               Entry var3 = (Entry)var2.next();
               this.contextData.putValue((String)var3.getKey(), var3.getValue());
            }
         }

         return this;
      }

      public Log4jLogEvent.Builder setContextData(StringMap var1) {
         this.contextData = var1;
         return this;
      }

      public Log4jLogEvent.Builder setContextStack(ThreadContext.ContextStack var1) {
         this.contextStack = var1;
         return this;
      }

      public Log4jLogEvent.Builder setThreadId(long var1) {
         this.threadId = var1;
         return this;
      }

      public Log4jLogEvent.Builder setThreadName(String var1) {
         this.threadName = var1;
         return this;
      }

      public Log4jLogEvent.Builder setThreadPriority(int var1) {
         this.threadPriority = var1;
         return this;
      }

      public Log4jLogEvent.Builder setSource(StackTraceElement var1) {
         this.source = var1;
         return this;
      }

      public Log4jLogEvent.Builder setIncludeLocation(boolean var1) {
         this.includeLocation = var1;
         return this;
      }

      public Log4jLogEvent.Builder setEndOfBatch(boolean var1) {
         this.endOfBatch = var1;
         return this;
      }

      public Log4jLogEvent.Builder setNanoTime(long var1) {
         this.nanoTime = var1;
         return this;
      }

      public Log4jLogEvent build() {
         Log4jLogEvent var1 = new Log4jLogEvent(this.loggerName, this.marker, this.loggerFqcn, this.level, this.message, this.thrown, this.thrownProxy, this.contextData, this.contextStack, this.threadId, this.threadName, this.threadPriority, this.source, this.timeMillis, this.nanoTime);
         var1.setIncludeLocation(this.includeLocation);
         var1.setEndOfBatch(this.endOfBatch);
         return var1;
      }
   }
}
