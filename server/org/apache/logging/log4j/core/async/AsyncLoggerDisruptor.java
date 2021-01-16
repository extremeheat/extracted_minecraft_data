package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.jmx.RingBufferAdmin;
import org.apache.logging.log4j.core.util.ExecutorServices;
import org.apache.logging.log4j.core.util.Log4jThreadFactory;

class AsyncLoggerDisruptor extends AbstractLifeCycle {
   private static final int SLEEP_MILLIS_BETWEEN_DRAIN_ATTEMPTS = 50;
   private static final int MAX_DRAIN_ATTEMPTS_BEFORE_SHUTDOWN = 200;
   private volatile Disruptor<RingBufferLogEvent> disruptor;
   private ExecutorService executor;
   private String contextName;
   private boolean useThreadLocalTranslator = true;
   private long backgroundThreadId;
   private AsyncQueueFullPolicy asyncQueueFullPolicy;
   private int ringBufferSize;

   AsyncLoggerDisruptor(String var1) {
      super();
      this.contextName = var1;
   }

   public String getContextName() {
      return this.contextName;
   }

   public void setContextName(String var1) {
      this.contextName = var1;
   }

   Disruptor<RingBufferLogEvent> getDisruptor() {
      return this.disruptor;
   }

   public synchronized void start() {
      if (this.disruptor != null) {
         LOGGER.trace((String)"[{}] AsyncLoggerDisruptor not starting new disruptor for this context, using existing object.", (Object)this.contextName);
      } else {
         LOGGER.trace((String)"[{}] AsyncLoggerDisruptor creating new disruptor for this context.", (Object)this.contextName);
         this.ringBufferSize = DisruptorUtil.calculateRingBufferSize("AsyncLogger.RingBufferSize");
         WaitStrategy var1 = DisruptorUtil.createWaitStrategy("AsyncLogger.WaitStrategy");
         this.executor = Executors.newSingleThreadExecutor(Log4jThreadFactory.createDaemonThreadFactory("AsyncLogger[" + this.contextName + "]"));
         this.backgroundThreadId = DisruptorUtil.getExecutorThreadId(this.executor);
         this.asyncQueueFullPolicy = AsyncQueueFullPolicyFactory.create();
         this.disruptor = new Disruptor(RingBufferLogEvent.FACTORY, this.ringBufferSize, this.executor, ProducerType.MULTI, var1);
         ExceptionHandler var2 = DisruptorUtil.getAsyncLoggerExceptionHandler();
         this.disruptor.handleExceptionsWith(var2);
         RingBufferLogEventHandler[] var3 = new RingBufferLogEventHandler[]{new RingBufferLogEventHandler()};
         this.disruptor.handleEventsWith(var3);
         LOGGER.debug((String)"[{}] Starting AsyncLogger disruptor for this context with ringbufferSize={}, waitStrategy={}, exceptionHandler={}...", (Object)this.contextName, this.disruptor.getRingBuffer().getBufferSize(), var1.getClass().getSimpleName(), var2);
         this.disruptor.start();
         LOGGER.trace((String)"[{}] AsyncLoggers use a {} translator", (Object)this.contextName, (Object)(this.useThreadLocalTranslator ? "threadlocal" : "vararg"));
         super.start();
      }
   }

   public boolean stop(long var1, TimeUnit var3) {
      Disruptor var4 = this.getDisruptor();
      if (var4 == null) {
         LOGGER.trace((String)"[{}] AsyncLoggerDisruptor: disruptor for this context already shut down.", (Object)this.contextName);
         return true;
      } else {
         this.setStopping();
         LOGGER.debug((String)"[{}] AsyncLoggerDisruptor: shutting down disruptor for this context.", (Object)this.contextName);
         this.disruptor = null;

         for(int var5 = 0; hasBacklog(var4) && var5 < 200; ++var5) {
            try {
               Thread.sleep(50L);
            } catch (InterruptedException var8) {
            }
         }

         try {
            var4.shutdown(var1, var3);
         } catch (TimeoutException var7) {
            var4.shutdown();
         }

         LOGGER.trace((String)"[{}] AsyncLoggerDisruptor: shutting down disruptor executor.", (Object)this.contextName);
         ExecutorServices.shutdown(this.executor, var1, var3, this.toString());
         this.executor = null;
         if (DiscardingAsyncQueueFullPolicy.getDiscardCount(this.asyncQueueFullPolicy) > 0L) {
            LOGGER.trace((String)"AsyncLoggerDisruptor: {} discarded {} events.", (Object)this.asyncQueueFullPolicy, (Object)DiscardingAsyncQueueFullPolicy.getDiscardCount(this.asyncQueueFullPolicy));
         }

         this.setStopped();
         return true;
      }
   }

   private static boolean hasBacklog(Disruptor<?> var0) {
      RingBuffer var1 = var0.getRingBuffer();
      return !var1.hasAvailableCapacity(var1.getBufferSize());
   }

   public RingBufferAdmin createRingBufferAdmin(String var1) {
      RingBuffer var2 = this.disruptor == null ? null : this.disruptor.getRingBuffer();
      return RingBufferAdmin.forAsyncLogger(var2, var1);
   }

   EventRoute getEventRoute(Level var1) {
      int var2 = this.remainingDisruptorCapacity();
      return var2 < 0 ? EventRoute.DISCARD : this.asyncQueueFullPolicy.getRoute(this.backgroundThreadId, var1);
   }

   private int remainingDisruptorCapacity() {
      Disruptor var1 = this.disruptor;
      return this.hasLog4jBeenShutDown(var1) ? -1 : (int)var1.getRingBuffer().remainingCapacity();
   }

   private boolean hasLog4jBeenShutDown(Disruptor<RingBufferLogEvent> var1) {
      if (var1 == null) {
         LOGGER.warn("Ignoring log event after log4j was shut down");
         return true;
      } else {
         return false;
      }
   }

   public boolean tryPublish(RingBufferLogEventTranslator var1) {
      try {
         return this.disruptor.getRingBuffer().tryPublishEvent(var1);
      } catch (NullPointerException var3) {
         LOGGER.warn((String)"[{}] Ignoring log event after log4j was shut down.", (Object)this.contextName);
         return false;
      }
   }

   void enqueueLogMessageInfo(RingBufferLogEventTranslator var1) {
      try {
         this.disruptor.publishEvent(var1);
      } catch (NullPointerException var3) {
         LOGGER.warn((String)"[{}] Ignoring log event after log4j was shut down.", (Object)this.contextName);
      }

   }

   public boolean isUseThreadLocals() {
      return this.useThreadLocalTranslator;
   }

   public void setUseThreadLocals(boolean var1) {
      this.useThreadLocalTranslator = var1;
      LOGGER.trace((String)"[{}] AsyncLoggers have been modified to use a {} translator", (Object)this.contextName, (Object)(this.useThreadLocalTranslator ? "threadlocal" : "vararg"));
   }
}
