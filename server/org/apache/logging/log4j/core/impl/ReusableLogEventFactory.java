package org.apache.logging.log4j.core.impl;

import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.async.ThreadNameCachingStrategy;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.util.Clock;
import org.apache.logging.log4j.core.util.ClockFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.TimestampMessage;
import org.apache.logging.log4j.util.StringMap;

public class ReusableLogEventFactory implements LogEventFactory {
   private static final ThreadNameCachingStrategy THREAD_NAME_CACHING_STRATEGY = ThreadNameCachingStrategy.create();
   private static final Clock CLOCK = ClockFactory.getClock();
   private static ThreadLocal<MutableLogEvent> mutableLogEventThreadLocal = new ThreadLocal();
   private final ContextDataInjector injector = ContextDataInjectorFactory.createInjector();

   public ReusableLogEventFactory() {
      super();
   }

   public LogEvent createEvent(String var1, Marker var2, String var3, Level var4, Message var5, List<Property> var6, Throwable var7) {
      MutableLogEvent var8 = (MutableLogEvent)mutableLogEventThreadLocal.get();
      if (var8 == null || var8.reserved) {
         boolean var9 = var8 == null;
         var8 = new MutableLogEvent();
         var8.setThreadId(Thread.currentThread().getId());
         var8.setThreadName(Thread.currentThread().getName());
         var8.setThreadPriority(Thread.currentThread().getPriority());
         if (var9) {
            mutableLogEventThreadLocal.set(var8);
         }
      }

      var8.reserved = true;
      var8.clear();
      var8.setLoggerName(var1);
      var8.setMarker(var2);
      var8.setLoggerFqcn(var3);
      var8.setLevel(var4 == null ? Level.OFF : var4);
      var8.setMessage(var5);
      var8.setThrown(var7);
      var8.setContextData(this.injector.injectContextData(var6, (StringMap)var8.getContextData()));
      var8.setContextStack((ThreadContext.ContextStack)(ThreadContext.getDepth() == 0 ? ThreadContext.EMPTY_STACK : ThreadContext.cloneStack()));
      var8.setTimeMillis(var5 instanceof TimestampMessage ? ((TimestampMessage)var5).getTimestamp() : CLOCK.currentTimeMillis());
      var8.setNanoTime(Log4jLogEvent.getNanoClock().nanoTime());
      if (THREAD_NAME_CACHING_STRATEGY == ThreadNameCachingStrategy.UNCACHED) {
         var8.setThreadName(Thread.currentThread().getName());
         var8.setThreadPriority(Thread.currentThread().getPriority());
      }

      return var8;
   }

   public static void release(LogEvent var0) {
      if (var0 instanceof MutableLogEvent) {
         ((MutableLogEvent)var0).reserved = false;
      }

   }
}
