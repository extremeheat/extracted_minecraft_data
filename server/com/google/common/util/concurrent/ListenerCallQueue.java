package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

@GwtIncompatible
final class ListenerCallQueue<L> implements Runnable {
   private static final Logger logger = Logger.getLogger(ListenerCallQueue.class.getName());
   private final L listener;
   private final Executor executor;
   @GuardedBy("this")
   private final Queue<ListenerCallQueue.Callback<L>> waitQueue = Queues.newArrayDeque();
   @GuardedBy("this")
   private boolean isThreadScheduled;

   ListenerCallQueue(L var1, Executor var2) {
      super();
      this.listener = Preconditions.checkNotNull(var1);
      this.executor = (Executor)Preconditions.checkNotNull(var2);
   }

   synchronized void add(ListenerCallQueue.Callback<L> var1) {
      this.waitQueue.add(var1);
   }

   void execute() {
      boolean var1 = false;
      synchronized(this) {
         if (!this.isThreadScheduled) {
            this.isThreadScheduled = true;
            var1 = true;
         }
      }

      if (var1) {
         try {
            this.executor.execute(this);
         } catch (RuntimeException var6) {
            synchronized(this) {
               this.isThreadScheduled = false;
            }

            logger.log(Level.SEVERE, "Exception while running callbacks for " + this.listener + " on " + this.executor, var6);
            throw var6;
         }
      }

   }

   public void run() {
      boolean var1 = true;

      while(true) {
         boolean var14 = false;

         try {
            var14 = true;
            ListenerCallQueue.Callback var2;
            synchronized(this) {
               Preconditions.checkState(this.isThreadScheduled);
               var2 = (ListenerCallQueue.Callback)this.waitQueue.poll();
               if (var2 == null) {
                  this.isThreadScheduled = false;
                  var1 = false;
                  var14 = false;
                  break;
               }
            }

            try {
               var2.call(this.listener);
            } catch (RuntimeException var17) {
               logger.log(Level.SEVERE, "Exception while executing callback: " + this.listener + "." + var2.methodCall, var17);
            }
         } finally {
            if (var14) {
               if (var1) {
                  synchronized(this) {
                     this.isThreadScheduled = false;
                  }
               }

            }
         }
      }

      if (var1) {
         synchronized(this) {
            this.isThreadScheduled = false;
         }
      }

   }

   abstract static class Callback<L> {
      private final String methodCall;

      Callback(String var1) {
         super();
         this.methodCall = var1;
      }

      abstract void call(L var1);

      void enqueueOn(Iterable<ListenerCallQueue<L>> var1) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            ListenerCallQueue var3 = (ListenerCallQueue)var2.next();
            var3.add(this);
         }

      }
   }
}
