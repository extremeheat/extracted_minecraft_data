package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceReportingEventHandler;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.LogEventFactory;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.core.impl.ReusableLogEventFactory;
import org.apache.logging.log4j.core.jmx.RingBufferAdmin;
import org.apache.logging.log4j.core.util.ExecutorServices;
import org.apache.logging.log4j.core.util.Log4jThreadFactory;
import org.apache.logging.log4j.message.ReusableMessage;

public class AsyncLoggerConfigDisruptor extends AbstractLifeCycle implements AsyncLoggerConfigDelegate {
   private static final int MAX_DRAIN_ATTEMPTS_BEFORE_SHUTDOWN = 200;
   private static final int SLEEP_MILLIS_BETWEEN_DRAIN_ATTEMPTS = 50;
   private static final EventFactory<AsyncLoggerConfigDisruptor.Log4jEventWrapper> FACTORY = new EventFactory<AsyncLoggerConfigDisruptor.Log4jEventWrapper>() {
      public AsyncLoggerConfigDisruptor.Log4jEventWrapper newInstance() {
         return new AsyncLoggerConfigDisruptor.Log4jEventWrapper();
      }
   };
   private static final EventFactory<AsyncLoggerConfigDisruptor.Log4jEventWrapper> MUTABLE_FACTORY = new EventFactory<AsyncLoggerConfigDisruptor.Log4jEventWrapper>() {
      public AsyncLoggerConfigDisruptor.Log4jEventWrapper newInstance() {
         return new AsyncLoggerConfigDisruptor.Log4jEventWrapper(new MutableLogEvent());
      }
   };
   private static final EventTranslatorTwoArg<AsyncLoggerConfigDisruptor.Log4jEventWrapper, LogEvent, AsyncLoggerConfig> TRANSLATOR = new EventTranslatorTwoArg<AsyncLoggerConfigDisruptor.Log4jEventWrapper, LogEvent, AsyncLoggerConfig>() {
      public void translateTo(AsyncLoggerConfigDisruptor.Log4jEventWrapper var1, long var2, LogEvent var4, AsyncLoggerConfig var5) {
         var1.event = var4;
         var1.loggerConfig = var5;
      }
   };
   private static final EventTranslatorTwoArg<AsyncLoggerConfigDisruptor.Log4jEventWrapper, LogEvent, AsyncLoggerConfig> MUTABLE_TRANSLATOR = new EventTranslatorTwoArg<AsyncLoggerConfigDisruptor.Log4jEventWrapper, LogEvent, AsyncLoggerConfig>() {
      public void translateTo(AsyncLoggerConfigDisruptor.Log4jEventWrapper var1, long var2, LogEvent var4, AsyncLoggerConfig var5) {
         ((MutableLogEvent)var1.event).initFrom(var4);
         var1.loggerConfig = var5;
      }
   };
   private static final ThreadFactory THREAD_FACTORY = Log4jThreadFactory.createDaemonThreadFactory("AsyncLoggerConfig");
   private int ringBufferSize;
   private AsyncQueueFullPolicy asyncQueueFullPolicy;
   private Boolean mutable;
   private volatile Disruptor<AsyncLoggerConfigDisruptor.Log4jEventWrapper> disruptor;
   private ExecutorService executor;
   private long backgroundThreadId;
   private EventFactory<AsyncLoggerConfigDisruptor.Log4jEventWrapper> factory;
   private EventTranslatorTwoArg<AsyncLoggerConfigDisruptor.Log4jEventWrapper, LogEvent, AsyncLoggerConfig> translator;

   public AsyncLoggerConfigDisruptor() {
      super();
      this.mutable = Boolean.FALSE;
   }

   public void setLogEventFactory(LogEventFactory var1) {
      this.mutable = this.mutable || var1 instanceof ReusableLogEventFactory;
   }

   public synchronized void start() {
      if (this.disruptor != null) {
         LOGGER.trace("AsyncLoggerConfigDisruptor not starting new disruptor for this configuration, using existing object.");
      } else {
         LOGGER.trace("AsyncLoggerConfigDisruptor creating new disruptor for this configuration.");
         this.ringBufferSize = DisruptorUtil.calculateRingBufferSize("AsyncLoggerConfig.RingBufferSize");
         WaitStrategy var1 = DisruptorUtil.createWaitStrategy("AsyncLoggerConfig.WaitStrategy");
         this.executor = Executors.newSingleThreadExecutor(THREAD_FACTORY);
         this.backgroundThreadId = DisruptorUtil.getExecutorThreadId(this.executor);
         this.asyncQueueFullPolicy = AsyncQueueFullPolicyFactory.create();
         this.translator = this.mutable ? MUTABLE_TRANSLATOR : TRANSLATOR;
         this.factory = this.mutable ? MUTABLE_FACTORY : FACTORY;
         this.disruptor = new Disruptor(this.factory, this.ringBufferSize, this.executor, ProducerType.MULTI, var1);
         ExceptionHandler var2 = DisruptorUtil.getAsyncLoggerConfigExceptionHandler();
         this.disruptor.handleExceptionsWith(var2);
         AsyncLoggerConfigDisruptor.Log4jEventWrapperHandler[] var3 = new AsyncLoggerConfigDisruptor.Log4jEventWrapperHandler[]{new AsyncLoggerConfigDisruptor.Log4jEventWrapperHandler()};
         this.disruptor.handleEventsWith(var3);
         LOGGER.debug((String)"Starting AsyncLoggerConfig disruptor for this configuration with ringbufferSize={}, waitStrategy={}, exceptionHandler={}...", (Object)this.disruptor.getRingBuffer().getBufferSize(), var1.getClass().getSimpleName(), var2);
         this.disruptor.start();
         super.start();
      }
   }

   public boolean stop(long var1, TimeUnit var3) {
      Disruptor var4 = this.disruptor;
      if (var4 == null) {
         LOGGER.trace("AsyncLoggerConfigDisruptor: disruptor for this configuration already shut down.");
         return true;
      } else {
         this.setStopping();
         LOGGER.trace("AsyncLoggerConfigDisruptor: shutting down disruptor for this configuration.");
         this.disruptor = null;

         for(int var5 = 0; hasBacklog(var4) && var5 < 200; ++var5) {
            try {
               Thread.sleep(50L);
            } catch (InterruptedException var7) {
            }
         }

         var4.shutdown();
         LOGGER.trace("AsyncLoggerConfigDisruptor: shutting down disruptor executor for this configuration.");
         ExecutorServices.shutdown(this.executor, var1, var3, this.toString());
         this.executor = null;
         if (DiscardingAsyncQueueFullPolicy.getDiscardCount(this.asyncQueueFullPolicy) > 0L) {
            LOGGER.trace((String)"AsyncLoggerConfigDisruptor: {} discarded {} events.", (Object)this.asyncQueueFullPolicy, (Object)DiscardingAsyncQueueFullPolicy.getDiscardCount(this.asyncQueueFullPolicy));
         }

         this.setStopped();
         return true;
      }
   }

   private static boolean hasBacklog(Disruptor<?> var0) {
      RingBuffer var1 = var0.getRingBuffer();
      return !var1.hasAvailableCapacity(var1.getBufferSize());
   }

   public EventRoute getEventRoute(Level var1) {
      int var2 = this.remainingDisruptorCapacity();
      return var2 < 0 ? EventRoute.DISCARD : this.asyncQueueFullPolicy.getRoute(this.backgroundThreadId, var1);
   }

   private int remainingDisruptorCapacity() {
      Disruptor var1 = this.disruptor;
      return this.hasLog4jBeenShutDown(var1) ? -1 : (int)var1.getRingBuffer().remainingCapacity();
   }

   private boolean hasLog4jBeenShutDown(Disruptor<AsyncLoggerConfigDisruptor.Log4jEventWrapper> var1) {
      if (var1 == null) {
         LOGGER.warn("Ignoring log event after log4j was shut down");
         return true;
      } else {
         return false;
      }
   }

   public void enqueueEvent(LogEvent var1, AsyncLoggerConfig var2) {
      try {
         LogEvent var3 = this.prepareEvent(var1);
         this.enqueue(var3, var2);
      } catch (NullPointerException var4) {
         LOGGER.warn("Ignoring log event after log4j was shut down.");
      }

   }

   private LogEvent prepareEvent(LogEvent var1) {
      LogEvent var2 = this.ensureImmutable(var1);
      if (var2 instanceof Log4jLogEvent && var2.getMessage() instanceof ReusableMessage) {
         ((Log4jLogEvent)var2).makeMessageImmutable();
      }

      return var2;
   }

   private void enqueue(LogEvent var1, AsyncLoggerConfig var2) {
      this.disruptor.getRingBuffer().publishEvent(this.translator, var1, var2);
   }

   public boolean tryEnqueue(LogEvent var1, AsyncLoggerConfig var2) {
      LogEvent var3 = this.prepareEvent(var1);
      return this.disruptor.getRingBuffer().tryPublishEvent(this.translator, var3, var2);
   }

   private LogEvent ensureImmutable(LogEvent var1) {
      LogEvent var2 = var1;
      if (var1 instanceof RingBufferLogEvent) {
         var2 = ((RingBufferLogEvent)var1).createMemento();
      }

      return var2;
   }

   public RingBufferAdmin createRingBufferAdmin(String var1, String var2) {
      return RingBufferAdmin.forAsyncLoggerConfig(this.disruptor.getRingBuffer(), var1, var2);
   }

   private static class Log4jEventWrapperHandler implements SequenceReportingEventHandler<AsyncLoggerConfigDisruptor.Log4jEventWrapper> {
      private static final int NOTIFY_PROGRESS_THRESHOLD = 50;
      private Sequence sequenceCallback;
      private int counter;

      private Log4jEventWrapperHandler() {
         super();
      }

      public void setSequenceCallback(Sequence var1) {
         this.sequenceCallback = var1;
      }

      public void onEvent(AsyncLoggerConfigDisruptor.Log4jEventWrapper var1, long var2, boolean var4) throws Exception {
         var1.event.setEndOfBatch(var4);
         var1.loggerConfig.asyncCallAppenders(var1.event);
         var1.clear();
         this.notifyIntermediateProgress(var2);
      }

      private void notifyIntermediateProgress(long var1) {
         if (++this.counter > 50) {
            this.sequenceCallback.set(var1);
            this.counter = 0;
         }

      }

      // $FF: synthetic method
      Log4jEventWrapperHandler(Object var1) {
         this();
      }
   }

   public static class Log4jEventWrapper {
      private AsyncLoggerConfig loggerConfig;
      private LogEvent event;

      public Log4jEventWrapper() {
         super();
      }

      public Log4jEventWrapper(MutableLogEvent var1) {
         super();
         this.event = var1;
      }

      public void clear() {
         this.loggerConfig = null;
         if (this.event instanceof MutableLogEvent) {
            ((MutableLogEvent)this.event).clear();
         } else {
            this.event = null;
         }

      }

      public String toString() {
         return String.valueOf(this.event);
      }
   }
}
