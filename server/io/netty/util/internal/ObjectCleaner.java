package io.netty.util.internal;

import io.netty.util.concurrent.FastThreadLocalThread;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ObjectCleaner {
   private static final int REFERENCE_QUEUE_POLL_TIMEOUT_MS = Math.max(500, SystemPropertyUtil.getInt("io.netty.util.internal.ObjectCleaner.refQueuePollTimeout", 10000));
   static final String CLEANER_THREAD_NAME = ObjectCleaner.class.getSimpleName() + "Thread";
   private static final Set<ObjectCleaner.AutomaticCleanerReference> LIVE_SET = new ConcurrentSet();
   private static final ReferenceQueue<Object> REFERENCE_QUEUE = new ReferenceQueue();
   private static final AtomicBoolean CLEANER_RUNNING = new AtomicBoolean(false);
   private static final Runnable CLEANER_TASK = new Runnable() {
      public void run() {
         boolean var1 = false;

         do {
            while(!ObjectCleaner.LIVE_SET.isEmpty()) {
               ObjectCleaner.AutomaticCleanerReference var2;
               try {
                  var2 = (ObjectCleaner.AutomaticCleanerReference)ObjectCleaner.REFERENCE_QUEUE.remove((long)ObjectCleaner.REFERENCE_QUEUE_POLL_TIMEOUT_MS);
               } catch (InterruptedException var5) {
                  var1 = true;
                  continue;
               }

               if (var2 != null) {
                  try {
                     var2.cleanup();
                  } catch (Throwable var4) {
                  }

                  ObjectCleaner.LIVE_SET.remove(var2);
               }
            }

            ObjectCleaner.CLEANER_RUNNING.set(false);
         } while(!ObjectCleaner.LIVE_SET.isEmpty() && ObjectCleaner.CLEANER_RUNNING.compareAndSet(false, true));

         if (var1) {
            Thread.currentThread().interrupt();
         }

      }
   };

   public static void register(Object var0, Runnable var1) {
      ObjectCleaner.AutomaticCleanerReference var2 = new ObjectCleaner.AutomaticCleanerReference(var0, (Runnable)ObjectUtil.checkNotNull(var1, "cleanupTask"));
      LIVE_SET.add(var2);
      if (CLEANER_RUNNING.compareAndSet(false, true)) {
         final FastThreadLocalThread var3 = new FastThreadLocalThread(CLEANER_TASK);
         var3.setPriority(1);
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               var3.setContextClassLoader((ClassLoader)null);
               return null;
            }
         });
         var3.setName(CLEANER_THREAD_NAME);
         var3.setDaemon(true);
         var3.start();
      }

   }

   public static int getLiveSetCount() {
      return LIVE_SET.size();
   }

   private ObjectCleaner() {
      super();
   }

   private static final class AutomaticCleanerReference extends WeakReference<Object> {
      private final Runnable cleanupTask;

      AutomaticCleanerReference(Object var1, Runnable var2) {
         super(var1, ObjectCleaner.REFERENCE_QUEUE);
         this.cleanupTask = var2;
      }

      void cleanup() {
         this.cleanupTask.run();
      }

      public Thread get() {
         return null;
      }

      public void clear() {
         ObjectCleaner.LIVE_SET.remove(this);
         super.clear();
      }
   }
}
