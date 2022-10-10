package net.minecraft.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NamedThreadFactory implements ThreadFactory {
   private static final Logger field_202908_a = LogManager.getLogger();
   private final ThreadGroup field_202909_b;
   private final AtomicInteger field_202910_c = new AtomicInteger(1);
   private final String field_202911_d;

   public NamedThreadFactory(String var1) {
      super();
      SecurityManager var2 = System.getSecurityManager();
      this.field_202909_b = var2 != null ? var2.getThreadGroup() : Thread.currentThread().getThreadGroup();
      this.field_202911_d = var1 + "-";
   }

   public Thread newThread(Runnable var1) {
      Thread var2 = new Thread(this.field_202909_b, var1, this.field_202911_d + this.field_202910_c.getAndIncrement(), 0L);
      var2.setUncaughtExceptionHandler((var1x, var2x) -> {
         field_202908_a.error("Caught exception in thread {} from {}", var1x, var1);
         field_202908_a.error("", var2x);
      });
      if (var2.getPriority() != 5) {
         var2.setPriority(5);
      }

      return var2;
   }
}
