package org.apache.logging.log4j.core.async;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.jmx.RingBufferAdmin;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.status.StatusLogger;

public class AsyncLoggerContext extends LoggerContext {
   private final AsyncLoggerDisruptor loggerDisruptor;

   public AsyncLoggerContext(String var1) {
      super(var1);
      this.loggerDisruptor = new AsyncLoggerDisruptor(var1);
   }

   public AsyncLoggerContext(String var1, Object var2) {
      super(var1, var2);
      this.loggerDisruptor = new AsyncLoggerDisruptor(var1);
   }

   public AsyncLoggerContext(String var1, Object var2, URI var3) {
      super(var1, var2, var3);
      this.loggerDisruptor = new AsyncLoggerDisruptor(var1);
   }

   public AsyncLoggerContext(String var1, Object var2, String var3) {
      super(var1, var2, var3);
      this.loggerDisruptor = new AsyncLoggerDisruptor(var1);
   }

   protected Logger newInstance(LoggerContext var1, String var2, MessageFactory var3) {
      return new AsyncLogger(var1, var2, var3, this.loggerDisruptor);
   }

   public void setName(String var1) {
      super.setName("AsyncContext[" + var1 + "]");
      this.loggerDisruptor.setContextName(var1);
   }

   public void start() {
      this.loggerDisruptor.start();
      super.start();
   }

   public void start(Configuration var1) {
      this.maybeStartHelper(var1);
      super.start(var1);
   }

   private void maybeStartHelper(Configuration var1) {
      if (var1 instanceof DefaultConfiguration) {
         StatusLogger.getLogger().debug("[{}] Not starting Disruptor for DefaultConfiguration.", this.getName());
      } else {
         this.loggerDisruptor.start();
      }

   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      this.loggerDisruptor.stop(var1, var3);
      super.stop(var1, var3);
      return true;
   }

   public RingBufferAdmin createRingBufferAdmin() {
      return this.loggerDisruptor.createRingBufferAdmin(this.getName());
   }

   public void setUseThreadLocals(boolean var1) {
      this.loggerDisruptor.setUseThreadLocals(var1);
   }
}
