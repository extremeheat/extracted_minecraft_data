package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AsyncAppender;
import org.apache.logging.log4j.message.Message;

public enum EventRoute {
   ENQUEUE {
      public void logMessage(AsyncLogger var1, String var2, Level var3, Marker var4, Message var5, Throwable var6) {
      }

      public void logMessage(AsyncLoggerConfig var1, LogEvent var2) {
         var1.callAppendersInBackgroundThread(var2);
      }

      public void logMessage(AsyncAppender var1, LogEvent var2) {
         var1.logMessageInBackgroundThread(var2);
      }
   },
   SYNCHRONOUS {
      public void logMessage(AsyncLogger var1, String var2, Level var3, Marker var4, Message var5, Throwable var6) {
      }

      public void logMessage(AsyncLoggerConfig var1, LogEvent var2) {
         var1.callAppendersInCurrentThread(var2);
      }

      public void logMessage(AsyncAppender var1, LogEvent var2) {
         var1.logMessageInCurrentThread(var2);
      }
   },
   DISCARD {
      public void logMessage(AsyncLogger var1, String var2, Level var3, Marker var4, Message var5, Throwable var6) {
      }

      public void logMessage(AsyncLoggerConfig var1, LogEvent var2) {
      }

      public void logMessage(AsyncAppender var1, LogEvent var2) {
      }
   };

   private EventRoute() {
   }

   public abstract void logMessage(AsyncLogger var1, String var2, Level var3, Marker var4, Message var5, Throwable var6);

   public abstract void logMessage(AsyncLoggerConfig var1, LogEvent var2);

   public abstract void logMessage(AsyncAppender var1, LogEvent var2);

   // $FF: synthetic method
   EventRoute(Object var3) {
      this();
   }
}
