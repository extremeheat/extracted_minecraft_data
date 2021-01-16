package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.logging.Level;
import java.util.logging.Logger;

@GwtCompatible(
   emulated = true
)
abstract class InterruptibleTask implements Runnable {
   private volatile Thread runner;
   private volatile boolean doneInterrupting;
   private static final InterruptibleTask.AtomicHelper ATOMIC_HELPER;
   private static final Logger log = Logger.getLogger(InterruptibleTask.class.getName());

   InterruptibleTask() {
      super();
   }

   public final void run() {
      if (ATOMIC_HELPER.compareAndSetRunner(this, (Thread)null, Thread.currentThread())) {
         try {
            this.runInterruptibly();
         } finally {
            if (this.wasInterrupted()) {
               while(!this.doneInterrupting) {
                  Thread.yield();
               }
            }

         }

      }
   }

   abstract void runInterruptibly();

   abstract boolean wasInterrupted();

   final void interruptTask() {
      Thread var1 = this.runner;
      if (var1 != null) {
         var1.interrupt();
      }

      this.doneInterrupting = true;
   }

   static {
      Object var0;
      try {
         var0 = new InterruptibleTask.SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(InterruptibleTask.class, Thread.class, "runner"));
      } catch (Throwable var2) {
         log.log(Level.SEVERE, "SafeAtomicHelper is broken!", var2);
         var0 = new InterruptibleTask.SynchronizedAtomicHelper();
      }

      ATOMIC_HELPER = (InterruptibleTask.AtomicHelper)var0;
   }

   private static final class SynchronizedAtomicHelper extends InterruptibleTask.AtomicHelper {
      private SynchronizedAtomicHelper() {
         super(null);
      }

      boolean compareAndSetRunner(InterruptibleTask var1, Thread var2, Thread var3) {
         synchronized(var1) {
            if (var1.runner == var2) {
               var1.runner = var3;
            }

            return true;
         }
      }

      // $FF: synthetic method
      SynchronizedAtomicHelper(Object var1) {
         this();
      }
   }

   private static final class SafeAtomicHelper extends InterruptibleTask.AtomicHelper {
      final AtomicReferenceFieldUpdater<InterruptibleTask, Thread> runnerUpdater;

      SafeAtomicHelper(AtomicReferenceFieldUpdater var1) {
         super(null);
         this.runnerUpdater = var1;
      }

      boolean compareAndSetRunner(InterruptibleTask var1, Thread var2, Thread var3) {
         return this.runnerUpdater.compareAndSet(var1, var2, var3);
      }
   }

   private abstract static class AtomicHelper {
      private AtomicHelper() {
         super();
      }

      abstract boolean compareAndSetRunner(InterruptibleTask var1, Thread var2, Thread var3);

      // $FF: synthetic method
      AtomicHelper(Object var1) {
         this();
      }
   }
}
