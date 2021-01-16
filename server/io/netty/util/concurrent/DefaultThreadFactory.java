package io.netty.util.concurrent;

import io.netty.util.internal.StringUtil;
import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {
   private static final AtomicInteger poolId = new AtomicInteger();
   private final AtomicInteger nextId;
   private final String prefix;
   private final boolean daemon;
   private final int priority;
   protected final ThreadGroup threadGroup;

   public DefaultThreadFactory(Class<?> var1) {
      this((Class)var1, false, 5);
   }

   public DefaultThreadFactory(String var1) {
      this((String)var1, false, 5);
   }

   public DefaultThreadFactory(Class<?> var1, boolean var2) {
      this((Class)var1, var2, 5);
   }

   public DefaultThreadFactory(String var1, boolean var2) {
      this((String)var1, var2, 5);
   }

   public DefaultThreadFactory(Class<?> var1, int var2) {
      this(var1, false, var2);
   }

   public DefaultThreadFactory(String var1, int var2) {
      this(var1, false, var2);
   }

   public DefaultThreadFactory(Class<?> var1, boolean var2, int var3) {
      this(toPoolName(var1), var2, var3);
   }

   public static String toPoolName(Class<?> var0) {
      if (var0 == null) {
         throw new NullPointerException("poolType");
      } else {
         String var1 = StringUtil.simpleClassName(var0);
         switch(var1.length()) {
         case 0:
            return "unknown";
         case 1:
            return var1.toLowerCase(Locale.US);
         default:
            return Character.isUpperCase(var1.charAt(0)) && Character.isLowerCase(var1.charAt(1)) ? Character.toLowerCase(var1.charAt(0)) + var1.substring(1) : var1;
         }
      }
   }

   public DefaultThreadFactory(String var1, boolean var2, int var3, ThreadGroup var4) {
      super();
      this.nextId = new AtomicInteger();
      if (var1 == null) {
         throw new NullPointerException("poolName");
      } else if (var3 >= 1 && var3 <= 10) {
         this.prefix = var1 + '-' + poolId.incrementAndGet() + '-';
         this.daemon = var2;
         this.priority = var3;
         this.threadGroup = var4;
      } else {
         throw new IllegalArgumentException("priority: " + var3 + " (expected: Thread.MIN_PRIORITY <= priority <= Thread.MAX_PRIORITY)");
      }
   }

   public DefaultThreadFactory(String var1, boolean var2, int var3) {
      this(var1, var2, var3, System.getSecurityManager() == null ? Thread.currentThread().getThreadGroup() : System.getSecurityManager().getThreadGroup());
   }

   public Thread newThread(Runnable var1) {
      Thread var2 = this.newThread(FastThreadLocalRunnable.wrap(var1), this.prefix + this.nextId.incrementAndGet());

      try {
         if (var2.isDaemon() != this.daemon) {
            var2.setDaemon(this.daemon);
         }

         if (var2.getPriority() != this.priority) {
            var2.setPriority(this.priority);
         }
      } catch (Exception var4) {
      }

      return var2;
   }

   protected Thread newThread(Runnable var1, String var2) {
      return new FastThreadLocalThread(this.threadGroup, var1, var2);
   }
}
