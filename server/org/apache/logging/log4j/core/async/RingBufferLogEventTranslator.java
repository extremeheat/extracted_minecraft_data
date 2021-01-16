package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.EventTranslator;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.impl.ContextDataInjectorFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.StringMap;

public class RingBufferLogEventTranslator implements EventTranslator<RingBufferLogEvent> {
   private final ContextDataInjector injector = ContextDataInjectorFactory.createInjector();
   private AsyncLogger asyncLogger;
   private String loggerName;
   protected Marker marker;
   protected String fqcn;
   protected Level level;
   protected Message message;
   protected Throwable thrown;
   private ThreadContext.ContextStack contextStack;
   private long threadId = Thread.currentThread().getId();
   private String threadName = Thread.currentThread().getName();
   private int threadPriority = Thread.currentThread().getPriority();
   private StackTraceElement location;
   private long currentTimeMillis;
   private long nanoTime;

   public RingBufferLogEventTranslator() {
      super();
   }

   public void translateTo(RingBufferLogEvent var1, long var2) {
      var1.setValues(this.asyncLogger, this.loggerName, this.marker, this.fqcn, this.level, this.message, this.thrown, this.injector.injectContextData((List)null, (StringMap)var1.getContextData()), this.contextStack, this.threadId, this.threadName, this.threadPriority, this.location, this.currentTimeMillis, this.nanoTime);
      this.clear();
   }

   private void clear() {
      this.setBasicValues((AsyncLogger)null, (String)null, (Marker)null, (String)null, (Level)null, (Message)null, (Throwable)null, (ThreadContext.ContextStack)null, (StackTraceElement)null, 0L, 0L);
   }

   public void setBasicValues(AsyncLogger var1, String var2, Marker var3, String var4, Level var5, Message var6, Throwable var7, ThreadContext.ContextStack var8, StackTraceElement var9, long var10, long var12) {
      this.asyncLogger = var1;
      this.loggerName = var2;
      this.marker = var3;
      this.fqcn = var4;
      this.level = var5;
      this.message = var6;
      this.thrown = var7;
      this.contextStack = var8;
      this.location = var9;
      this.currentTimeMillis = var10;
      this.nanoTime = var12;
   }

   public void updateThreadValues() {
      Thread var1 = Thread.currentThread();
      this.threadId = var1.getId();
      this.threadName = var1.getName();
      this.threadPriority = var1.getPriority();
   }
}
