package org.apache.logging.log4j.core.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Log4jThreadFactory implements ThreadFactory {
   private static final String PREFIX = "TF-";
   private static final AtomicInteger FACTORY_NUMBER = new AtomicInteger(1);
   private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
   private final boolean daemon;
   private final ThreadGroup group;
   private final int priority;
   private final String threadNamePrefix;

   public static Log4jThreadFactory createDaemonThreadFactory(String var0) {
      return new Log4jThreadFactory(var0, true, 5);
   }

   public static Log4jThreadFactory createThreadFactory(String var0) {
      return new Log4jThreadFactory(var0, false, 5);
   }

   public Log4jThreadFactory(String var1, boolean var2, int var3) {
      super();
      this.threadNamePrefix = "TF-" + FACTORY_NUMBER.getAndIncrement() + "-" + var1 + "-";
      this.daemon = var2;
      this.priority = var3;
      SecurityManager var4 = System.getSecurityManager();
      this.group = var4 != null ? var4.getThreadGroup() : Thread.currentThread().getThreadGroup();
   }

   public Thread newThread(Runnable var1) {
      Log4jThread var2 = new Log4jThread(this.group, var1, this.threadNamePrefix + THREAD_NUMBER.getAndIncrement(), 0L);
      if (var2.isDaemon() != this.daemon) {
         var2.setDaemon(this.daemon);
      }

      if (var2.getPriority() != this.priority) {
         var2.setPriority(this.priority);
      }

      return var2;
   }
}
