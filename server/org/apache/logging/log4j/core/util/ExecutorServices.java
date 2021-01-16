package org.apache.logging.log4j.core.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public class ExecutorServices {
   private static final Logger LOGGER = StatusLogger.getLogger();

   public ExecutorServices() {
      super();
   }

   public static boolean shutdown(ExecutorService var0, long var1, TimeUnit var3, String var4) {
      if (var0 != null && !var0.isTerminated()) {
         var0.shutdown();
         if (var1 > 0L && var3 == null) {
            throw new IllegalArgumentException(String.format("%s can't shutdown %s when timeout = %,d and timeUnit = %s.", var4, var0, var1, var3));
         } else {
            if (var1 > 0L) {
               try {
                  if (!var0.awaitTermination(var1, var3)) {
                     var0.shutdownNow();
                     if (!var0.awaitTermination(var1, var3)) {
                        LOGGER.error((String)"{} pool {} did not terminate after {} {}", (Object)var4, var0, var1, var3);
                     }

                     return false;
                  }
               } catch (InterruptedException var6) {
                  var0.shutdownNow();
                  Thread.currentThread().interrupt();
               }
            } else {
               var0.shutdown();
            }

            return true;
         }
      } else {
         return true;
      }
   }
}
