package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.TimeoutBlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.Integers;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

final class DisruptorUtil {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final int RINGBUFFER_MIN_SIZE = 128;
   private static final int RINGBUFFER_DEFAULT_SIZE = 262144;
   private static final int RINGBUFFER_NO_GC_DEFAULT_SIZE = 4096;

   private DisruptorUtil() {
      super();
   }

   static long getTimeout(String var0, long var1) {
      return PropertiesUtil.getProperties().getLongProperty(var0, var1);
   }

   static WaitStrategy createWaitStrategy(String var0) {
      String var1 = var0.startsWith("AsyncLogger.") ? "AsyncLogger.Timeout" : "AsyncLoggerConfig.Timeout";
      long var2 = getTimeout(var1, 10L);
      return createWaitStrategy(var0, var2);
   }

   static WaitStrategy createWaitStrategy(String var0, long var1) {
      String var3 = PropertiesUtil.getProperties().getStringProperty(var0, "TIMEOUT");
      LOGGER.trace((String)"property {}={}", (Object)var0, (Object)var3);
      String var4 = var3.toUpperCase(Locale.ROOT);
      byte var6 = -1;
      switch(var4.hashCode()) {
      case -595928767:
         if (var4.equals("TIMEOUT")) {
            var6 = 4;
         }
         break;
      case -349268549:
         if (var4.equals("BUSYSPIN")) {
            var6 = 3;
         }
         break;
      case 63294573:
         if (var4.equals("BLOCK")) {
            var6 = 2;
         }
         break;
      case 78984887:
         if (var4.equals("SLEEP")) {
            var6 = 0;
         }
         break;
      case 84436845:
         if (var4.equals("YIELD")) {
            var6 = 1;
         }
      }

      switch(var6) {
      case 0:
         return new SleepingWaitStrategy();
      case 1:
         return new YieldingWaitStrategy();
      case 2:
         return new BlockingWaitStrategy();
      case 3:
         return new BusySpinWaitStrategy();
      case 4:
         return new TimeoutBlockingWaitStrategy(var1, TimeUnit.MILLISECONDS);
      default:
         return new TimeoutBlockingWaitStrategy(var1, TimeUnit.MILLISECONDS);
      }
   }

   static int calculateRingBufferSize(String var0) {
      int var1 = Constants.ENABLE_THREADLOCALS ? 4096 : 262144;
      String var2 = PropertiesUtil.getProperties().getStringProperty(var0, String.valueOf(var1));

      try {
         int var3 = Integer.parseInt(var2);
         if (var3 < 128) {
            var3 = 128;
            LOGGER.warn((String)"Invalid RingBufferSize {}, using minimum size {}.", (Object)var2, (int)128);
         }

         var1 = var3;
      } catch (Exception var4) {
         LOGGER.warn((String)"Invalid RingBufferSize {}, using default size {}.", (Object)var2, (Object)var1);
      }

      return Integers.ceilingNextPowerOfTwo(var1);
   }

   static ExceptionHandler<RingBufferLogEvent> getAsyncLoggerExceptionHandler() {
      String var0 = PropertiesUtil.getProperties().getStringProperty("AsyncLogger.ExceptionHandler");
      if (var0 == null) {
         return new AsyncLoggerDefaultExceptionHandler();
      } else {
         try {
            Class var1 = LoaderUtil.loadClass(var0);
            return (ExceptionHandler)var1.newInstance();
         } catch (Exception var2) {
            LOGGER.debug((String)"Invalid AsyncLogger.ExceptionHandler value: error creating {}: ", (Object)var0, (Object)var2);
            return new AsyncLoggerDefaultExceptionHandler();
         }
      }
   }

   static ExceptionHandler<AsyncLoggerConfigDisruptor.Log4jEventWrapper> getAsyncLoggerConfigExceptionHandler() {
      String var0 = PropertiesUtil.getProperties().getStringProperty("AsyncLoggerConfig.ExceptionHandler");
      if (var0 == null) {
         return new AsyncLoggerConfigDefaultExceptionHandler();
      } else {
         try {
            Class var1 = LoaderUtil.loadClass(var0);
            return (ExceptionHandler)var1.newInstance();
         } catch (Exception var2) {
            LOGGER.debug((String)"Invalid AsyncLoggerConfig.ExceptionHandler value: error creating {}: ", (Object)var0, (Object)var2);
            return new AsyncLoggerConfigDefaultExceptionHandler();
         }
      }
   }

   public static long getExecutorThreadId(ExecutorService var0) {
      Future var1 = var0.submit(new Callable<Long>() {
         public Long call() {
            return Thread.currentThread().getId();
         }
      });

      try {
         return (Long)var1.get();
      } catch (Exception var4) {
         String var3 = "Could not obtain executor thread Id. Giving up to avoid the risk of application deadlock.";
         throw new IllegalStateException("Could not obtain executor thread Id. Giving up to avoid the risk of application deadlock.", var4);
      }
   }
}
