package io.netty.util.internal.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

class Log4J2Logger extends ExtendedLoggerWrapper implements InternalLogger {
   private static final long serialVersionUID = 5485418394879791397L;
   private static final String EXCEPTION_MESSAGE = "Unexpected exception:";

   Log4J2Logger(Logger var1) {
      super((ExtendedLogger)var1, var1.getName(), var1.getMessageFactory());
   }

   public String name() {
      return this.getName();
   }

   public void trace(Throwable var1) {
      this.log((Level)Level.TRACE, "Unexpected exception:", (Throwable)var1);
   }

   public void debug(Throwable var1) {
      this.log((Level)Level.DEBUG, "Unexpected exception:", (Throwable)var1);
   }

   public void info(Throwable var1) {
      this.log((Level)Level.INFO, "Unexpected exception:", (Throwable)var1);
   }

   public void warn(Throwable var1) {
      this.log((Level)Level.WARN, "Unexpected exception:", (Throwable)var1);
   }

   public void error(Throwable var1) {
      this.log((Level)Level.ERROR, "Unexpected exception:", (Throwable)var1);
   }

   public boolean isEnabled(InternalLogLevel var1) {
      return this.isEnabled(this.toLevel(var1));
   }

   public void log(InternalLogLevel var1, String var2) {
      this.log((Level)this.toLevel(var1), (String)var2);
   }

   public void log(InternalLogLevel var1, String var2, Object var3) {
      this.log((Level)this.toLevel(var1), var2, (Object)var3);
   }

   public void log(InternalLogLevel var1, String var2, Object var3, Object var4) {
      this.log(this.toLevel(var1), var2, var3, var4);
   }

   public void log(InternalLogLevel var1, String var2, Object... var3) {
      this.log((Level)this.toLevel(var1), var2, (Object[])var3);
   }

   public void log(InternalLogLevel var1, String var2, Throwable var3) {
      this.log((Level)this.toLevel(var1), var2, (Throwable)var3);
   }

   public void log(InternalLogLevel var1, Throwable var2) {
      this.log((Level)this.toLevel(var1), "Unexpected exception:", (Throwable)var2);
   }

   protected Level toLevel(InternalLogLevel var1) {
      switch(var1) {
      case INFO:
         return Level.INFO;
      case DEBUG:
         return Level.DEBUG;
      case WARN:
         return Level.WARN;
      case ERROR:
         return Level.ERROR;
      case TRACE:
         return Level.TRACE;
      default:
         throw new Error();
      }
   }
}
