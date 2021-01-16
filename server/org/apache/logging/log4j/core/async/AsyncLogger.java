package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.EventTranslatorVararg;
import com.lmax.disruptor.dsl.Disruptor;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.ReliabilityStrategy;
import org.apache.logging.log4j.core.impl.ContextDataFactory;
import org.apache.logging.log4j.core.impl.ContextDataInjectorFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.util.Clock;
import org.apache.logging.log4j.core.util.ClockFactory;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.NanoClock;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ReusableMessage;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StringMap;

public class AsyncLogger extends Logger implements EventTranslatorVararg<RingBufferLogEvent> {
   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   private static final Clock CLOCK = ClockFactory.getClock();
   private static final ContextDataInjector CONTEXT_DATA_INJECTOR = ContextDataInjectorFactory.createInjector();
   private static final ThreadNameCachingStrategy THREAD_NAME_CACHING_STRATEGY = ThreadNameCachingStrategy.create();
   private final ThreadLocal<RingBufferLogEventTranslator> threadLocalTranslator = new ThreadLocal();
   private final AsyncLoggerDisruptor loggerDisruptor;
   private volatile boolean includeLocation;
   private volatile NanoClock nanoClock;

   public AsyncLogger(LoggerContext var1, String var2, MessageFactory var3, AsyncLoggerDisruptor var4) {
      super(var1, var2, var3);
      this.loggerDisruptor = var4;
      this.includeLocation = this.privateConfig.loggerConfig.isIncludeLocation();
      this.nanoClock = var1.getConfiguration().getNanoClock();
   }

   protected void updateConfiguration(Configuration var1) {
      this.nanoClock = var1.getNanoClock();
      this.includeLocation = var1.getLoggerConfig(this.name).isIncludeLocation();
      super.updateConfiguration(var1);
   }

   NanoClock getNanoClock() {
      return this.nanoClock;
   }

   private RingBufferLogEventTranslator getCachedTranslator() {
      RingBufferLogEventTranslator var1 = (RingBufferLogEventTranslator)this.threadLocalTranslator.get();
      if (var1 == null) {
         var1 = new RingBufferLogEventTranslator();
         this.threadLocalTranslator.set(var1);
      }

      return var1;
   }

   public void logMessage(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      if (this.loggerDisruptor.isUseThreadLocals()) {
         this.logWithThreadLocalTranslator(var1, var2, var3, var4, var5);
      } else {
         this.logWithVarargTranslator(var1, var2, var3, var4, var5);
      }

   }

   private boolean isReused(Message var1) {
      return var1 instanceof ReusableMessage;
   }

   private void logWithThreadLocalTranslator(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      RingBufferLogEventTranslator var6 = this.getCachedTranslator();
      this.initTranslator(var6, var1, var2, var3, var4, var5);
      this.initTranslatorThreadValues(var6);
      this.publish(var6);
   }

   private void publish(RingBufferLogEventTranslator var1) {
      if (!this.loggerDisruptor.tryPublish(var1)) {
         this.handleRingBufferFull(var1);
      }

   }

   private void handleRingBufferFull(RingBufferLogEventTranslator var1) {
      EventRoute var2 = this.loggerDisruptor.getEventRoute(var1.level);
      switch(var2) {
      case ENQUEUE:
         this.loggerDisruptor.enqueueLogMessageInfo(var1);
         break;
      case SYNCHRONOUS:
         this.logMessageInCurrentThread(var1.fqcn, var1.level, var1.marker, var1.message, var1.thrown);
      case DISCARD:
         break;
      default:
         throw new IllegalStateException("Unknown EventRoute " + var2);
      }

   }

   private void initTranslator(RingBufferLogEventTranslator var1, String var2, Level var3, Marker var4, Message var5, Throwable var6) {
      var1.setBasicValues(this, this.name, var4, var2, var3, var5, var6, ThreadContext.getImmutableStack(), this.calcLocationIfRequested(var2), CLOCK.currentTimeMillis(), this.nanoClock.nanoTime());
   }

   private void initTranslatorThreadValues(RingBufferLogEventTranslator var1) {
      if (THREAD_NAME_CACHING_STRATEGY == ThreadNameCachingStrategy.UNCACHED) {
         var1.updateThreadValues();
      }

   }

   private StackTraceElement calcLocationIfRequested(String var1) {
      return this.includeLocation ? Log4jLogEvent.calcLocation(var1) : null;
   }

   private void logWithVarargTranslator(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      Disruptor var6 = this.loggerDisruptor.getDisruptor();
      if (var6 == null) {
         LOGGER.error("Ignoring log event after Log4j has been shut down.");
      } else {
         if (!this.canFormatMessageInBackground(var4) && !this.isReused(var4)) {
            var4.getFormattedMessage();
         }

         var6.getRingBuffer().publishEvent(this, new Object[]{this, this.calcLocationIfRequested(var1), var1, var2, var3, var4, var5});
      }
   }

   private boolean canFormatMessageInBackground(Message var1) {
      return Constants.FORMAT_MESSAGES_IN_BACKGROUND || var1.getClass().isAnnotationPresent(AsynchronouslyFormattable.class);
   }

   public void translateTo(RingBufferLogEvent var1, long var2, Object... var4) {
      AsyncLogger var5 = (AsyncLogger)var4[0];
      StackTraceElement var6 = (StackTraceElement)var4[1];
      String var7 = (String)var4[2];
      Level var8 = (Level)var4[3];
      Marker var9 = (Marker)var4[4];
      Message var10 = (Message)var4[5];
      Throwable var11 = (Throwable)var4[6];
      ThreadContext.ContextStack var12 = ThreadContext.getImmutableStack();
      Thread var13 = Thread.currentThread();
      String var14 = THREAD_NAME_CACHING_STRATEGY.getThreadName();
      var1.setValues(var5, var5.getName(), var9, var7, var8, var10, var11, CONTEXT_DATA_INJECTOR.injectContextData((List)null, (StringMap)var1.getContextData()), var12, var13.getId(), var14, var13.getPriority(), var6, CLOCK.currentTimeMillis(), this.nanoClock.nanoTime());
   }

   void logMessageInCurrentThread(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      ReliabilityStrategy var6 = this.privateConfig.loggerConfig.getReliabilityStrategy();
      var6.log(this, this.getName(), var1, var3, var2, var4, var5);
   }

   public void actualAsyncLog(RingBufferLogEvent var1) {
      List var2 = this.privateConfig.loggerConfig.getPropertyList();
      if (var2 != null) {
         StringMap var3 = (StringMap)var1.getContextData();
         if (var3.isFrozen()) {
            StringMap var4 = ContextDataFactory.createContextData();
            var4.putAll(var3);
            var3 = var4;
         }

         for(int var8 = 0; var8 < var2.size(); ++var8) {
            Property var5 = (Property)var2.get(var8);
            if (var3.getValue(var5.getName()) == null) {
               String var6 = var5.isValueNeedsLookup() ? this.privateConfig.config.getStrSubstitutor().replace((LogEvent)var1, (String)var5.getValue()) : var5.getValue();
               var3.putValue(var5.getName(), var6);
            }
         }

         var1.setContextData(var3);
      }

      ReliabilityStrategy var7 = this.privateConfig.loggerConfig.getReliabilityStrategy();
      var7.log(this, var1);
   }
}
