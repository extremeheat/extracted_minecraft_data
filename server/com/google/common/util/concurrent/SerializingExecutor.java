package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

@GwtIncompatible
final class SerializingExecutor implements Executor {
   private static final Logger log = Logger.getLogger(SerializingExecutor.class.getName());
   private final Executor executor;
   @GuardedBy("internalLock")
   private final Deque<Runnable> queue = new ArrayDeque();
   @GuardedBy("internalLock")
   private boolean isWorkerRunning = false;
   @GuardedBy("internalLock")
   private int suspensions = 0;
   private final Object internalLock = new Object();

   public SerializingExecutor(Executor var1) {
      super();
      this.executor = (Executor)Preconditions.checkNotNull(var1);
   }

   public void execute(Runnable var1) {
      synchronized(this.internalLock) {
         this.queue.add(var1);
      }

      this.startQueueWorker();
   }

   public void executeFirst(Runnable var1) {
      synchronized(this.internalLock) {
         this.queue.addFirst(var1);
      }

      this.startQueueWorker();
   }

   public void suspend() {
      synchronized(this.internalLock) {
         ++this.suspensions;
      }
   }

   public void resume() {
      synchronized(this.internalLock) {
         Preconditions.checkState(this.suspensions > 0);
         --this.suspensions;
      }

      this.startQueueWorker();
   }

   private void startQueueWorker() {
      synchronized(this.internalLock) {
         if (this.queue.peek() == null) {
            return;
         }

         if (this.suspensions > 0) {
            return;
         }

         if (this.isWorkerRunning) {
            return;
         }

         this.isWorkerRunning = true;
      }

      boolean var1 = true;
      boolean var11 = false;

      try {
         var11 = true;
         this.executor.execute(new SerializingExecutor.QueueWorker());
         var1 = false;
         var11 = false;
      } finally {
         if (var11) {
            if (var1) {
               synchronized(this.internalLock) {
                  this.isWorkerRunning = false;
               }
            }

         }
      }

      if (var1) {
         synchronized(this.internalLock) {
            this.isWorkerRunning = false;
         }
      }

   }

   private final class QueueWorker implements Runnable {
      private QueueWorker() {
         super();
      }

      public void run() {
         try {
            this.workOnQueue();
         } catch (Error var5) {
            synchronized(SerializingExecutor.this.internalLock) {
               SerializingExecutor.this.isWorkerRunning = false;
            }

            throw var5;
         }
      }

      private void workOnQueue() {
         while(true) {
            Runnable var1 = null;
            synchronized(SerializingExecutor.this.internalLock) {
               if (SerializingExecutor.this.suspensions == 0) {
                  var1 = (Runnable)SerializingExecutor.this.queue.poll();
               }

               if (var1 == null) {
                  SerializingExecutor.this.isWorkerRunning = false;
                  return;
               }
            }

            try {
               var1.run();
            } catch (RuntimeException var4) {
               SerializingExecutor.log.log(Level.SEVERE, "Exception while executing runnable " + var1, var4);
            }
         }
      }

      // $FF: synthetic method
      QueueWorker(Object var2) {
         this();
      }
   }
}
