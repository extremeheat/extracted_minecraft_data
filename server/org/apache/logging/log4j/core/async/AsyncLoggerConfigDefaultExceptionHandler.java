package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.ExceptionHandler;

public class AsyncLoggerConfigDefaultExceptionHandler implements ExceptionHandler<AsyncLoggerConfigDisruptor.Log4jEventWrapper> {
   public AsyncLoggerConfigDefaultExceptionHandler() {
      super();
   }

   public void handleEventException(Throwable var1, long var2, AsyncLoggerConfigDisruptor.Log4jEventWrapper var4) {
      StringBuilder var5 = new StringBuilder(512);
      var5.append("AsyncLogger error handling event seq=").append(var2).append(", value='");

      try {
         var5.append(var4);
      } catch (Exception var7) {
         var5.append("[ERROR calling ").append(var4.getClass()).append(".toString(): ");
         var5.append(var7).append("]");
      }

      var5.append("':");
      System.err.println(var5);
      var1.printStackTrace();
   }

   public void handleOnStartException(Throwable var1) {
      System.err.println("AsyncLogger error starting:");
      var1.printStackTrace();
   }

   public void handleOnShutdownException(Throwable var1) {
      System.err.println("AsyncLogger error shutting down:");
      var1.printStackTrace();
   }
}
