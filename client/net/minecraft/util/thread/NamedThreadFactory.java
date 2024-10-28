package net.minecraft.util.thread;

import com.mojang.logging.LogUtils;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;

public class NamedThreadFactory implements ThreadFactory {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ThreadGroup group;
   private final AtomicInteger threadNumber = new AtomicInteger(1);
   private final String namePrefix;

   public NamedThreadFactory(String var1) {
      super();
      SecurityManager var2 = System.getSecurityManager();
      this.group = var2 != null ? var2.getThreadGroup() : Thread.currentThread().getThreadGroup();
      this.namePrefix = var1 + "-";
   }

   public Thread newThread(Runnable var1) {
      Thread var2 = new Thread(this.group, var1, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
      var2.setUncaughtExceptionHandler((var1x, var2x) -> {
         LOGGER.error("Caught exception in thread {} from {}", var1x, var1);
         LOGGER.error("", var2x);
      });
      if (var2.getPriority() != 5) {
         var2.setPriority(5);
      }

      return var2;
   }
}
