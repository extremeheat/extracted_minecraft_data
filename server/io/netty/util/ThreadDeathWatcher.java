package io.netty.util;

import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** @deprecated */
@Deprecated
public final class ThreadDeathWatcher {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ThreadDeathWatcher.class);
   static final ThreadFactory threadFactory;
   private static final Queue<ThreadDeathWatcher.Entry> pendingEntries = new ConcurrentLinkedQueue();
   private static final ThreadDeathWatcher.Watcher watcher = new ThreadDeathWatcher.Watcher();
   private static final AtomicBoolean started = new AtomicBoolean();
   private static volatile Thread watcherThread;

   public static void watch(Thread var0, Runnable var1) {
      if (var0 == null) {
         throw new NullPointerException("thread");
      } else if (var1 == null) {
         throw new NullPointerException("task");
      } else if (!var0.isAlive()) {
         throw new IllegalArgumentException("thread must be alive.");
      } else {
         schedule(var0, var1, true);
      }
   }

   public static void unwatch(Thread var0, Runnable var1) {
      if (var0 == null) {
         throw new NullPointerException("thread");
      } else if (var1 == null) {
         throw new NullPointerException("task");
      } else {
         schedule(var0, var1, false);
      }
   }

   private static void schedule(Thread var0, Runnable var1, boolean var2) {
      pendingEntries.add(new ThreadDeathWatcher.Entry(var0, var1, var2));
      if (started.compareAndSet(false, true)) {
         final Thread var3 = threadFactory.newThread(watcher);
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               var3.setContextClassLoader((ClassLoader)null);
               return null;
            }
         });
         var3.start();
         watcherThread = var3;
      }

   }

   public static boolean awaitInactivity(long var0, TimeUnit var2) throws InterruptedException {
      if (var2 == null) {
         throw new NullPointerException("unit");
      } else {
         Thread var3 = watcherThread;
         if (var3 != null) {
            var3.join(var2.toMillis(var0));
            return !var3.isAlive();
         } else {
            return true;
         }
      }
   }

   private ThreadDeathWatcher() {
      super();
   }

   static {
      String var0 = "threadDeathWatcher";
      String var1 = SystemPropertyUtil.get("io.netty.serviceThreadPrefix");
      if (!StringUtil.isNullOrEmpty(var1)) {
         var0 = var1 + var0;
      }

      threadFactory = new DefaultThreadFactory(var0, true, 1, (ThreadGroup)null);
   }

   private static final class Entry {
      final Thread thread;
      final Runnable task;
      final boolean isWatch;

      Entry(Thread var1, Runnable var2, boolean var3) {
         super();
         this.thread = var1;
         this.task = var2;
         this.isWatch = var3;
      }

      public int hashCode() {
         return this.thread.hashCode() ^ this.task.hashCode();
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ThreadDeathWatcher.Entry)) {
            return false;
         } else {
            ThreadDeathWatcher.Entry var2 = (ThreadDeathWatcher.Entry)var1;
            return this.thread == var2.thread && this.task == var2.task;
         }
      }
   }

   private static final class Watcher implements Runnable {
      private final List<ThreadDeathWatcher.Entry> watchees;

      private Watcher() {
         super();
         this.watchees = new ArrayList();
      }

      public void run() {
         while(true) {
            this.fetchWatchees();
            this.notifyWatchees();
            this.fetchWatchees();
            this.notifyWatchees();

            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var2) {
            }

            if (this.watchees.isEmpty() && ThreadDeathWatcher.pendingEntries.isEmpty()) {
               boolean var1 = ThreadDeathWatcher.started.compareAndSet(true, false);

               assert var1;

               if (ThreadDeathWatcher.pendingEntries.isEmpty() || !ThreadDeathWatcher.started.compareAndSet(false, true)) {
                  return;
               }
            }
         }
      }

      private void fetchWatchees() {
         while(true) {
            ThreadDeathWatcher.Entry var1 = (ThreadDeathWatcher.Entry)ThreadDeathWatcher.pendingEntries.poll();
            if (var1 == null) {
               return;
            }

            if (var1.isWatch) {
               this.watchees.add(var1);
            } else {
               this.watchees.remove(var1);
            }
         }
      }

      private void notifyWatchees() {
         List var1 = this.watchees;
         int var2 = 0;

         while(var2 < var1.size()) {
            ThreadDeathWatcher.Entry var3 = (ThreadDeathWatcher.Entry)var1.get(var2);
            if (!var3.thread.isAlive()) {
               var1.remove(var2);

               try {
                  var3.task.run();
               } catch (Throwable var5) {
                  ThreadDeathWatcher.logger.warn("Thread death watcher task raised an exception:", var5);
               }
            } else {
               ++var2;
            }
         }

      }

      // $FF: synthetic method
      Watcher(Object var1) {
         this();
      }
   }
}
