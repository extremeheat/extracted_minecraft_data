package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.j2objc.annotations.Weak;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;
import javax.annotation.concurrent.GuardedBy;

@Beta
@GwtIncompatible
public final class Monitor {
   private final boolean fair;
   private final ReentrantLock lock;
   @GuardedBy("lock")
   private Monitor.Guard activeGuards;

   public Monitor() {
      this(false);
   }

   public Monitor(boolean var1) {
      super();
      this.activeGuards = null;
      this.fair = var1;
      this.lock = new ReentrantLock(var1);
   }

   public Monitor.Guard newGuard(final BooleanSupplier var1) {
      Preconditions.checkNotNull(var1, "isSatisfied");
      return new Monitor.Guard(this) {
         public boolean isSatisfied() {
            return var1.getAsBoolean();
         }
      };
   }

   public void enter() {
      this.lock.lock();
   }

   public void enterInterruptibly() throws InterruptedException {
      this.lock.lockInterruptibly();
   }

   public boolean enter(long var1, TimeUnit var3) {
      long var4 = toSafeNanos(var1, var3);
      ReentrantLock var6 = this.lock;
      if (!this.fair && var6.tryLock()) {
         return true;
      } else {
         boolean var7 = Thread.interrupted();

         try {
            long var8 = System.nanoTime();
            long var10 = var4;

            while(true) {
               try {
                  boolean var12 = var6.tryLock(var10, TimeUnit.NANOSECONDS);
                  return var12;
               } catch (InterruptedException var16) {
                  var7 = true;
                  var10 = remainingNanos(var8, var4);
               }
            }
         } finally {
            if (var7) {
               Thread.currentThread().interrupt();
            }

         }
      }
   }

   public boolean enterInterruptibly(long var1, TimeUnit var3) throws InterruptedException {
      return this.lock.tryLock(var1, var3);
   }

   public boolean tryEnter() {
      return this.lock.tryLock();
   }

   public void enterWhen(Monitor.Guard var1) throws InterruptedException {
      if (var1.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock var2 = this.lock;
         boolean var3 = var2.isHeldByCurrentThread();
         var2.lockInterruptibly();
         boolean var4 = false;

         try {
            if (!var1.isSatisfied()) {
               this.await(var1, var3);
            }

            var4 = true;
         } finally {
            if (!var4) {
               this.leave();
            }

         }

      }
   }

   public void enterWhenUninterruptibly(Monitor.Guard var1) {
      if (var1.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock var2 = this.lock;
         boolean var3 = var2.isHeldByCurrentThread();
         var2.lock();
         boolean var4 = false;

         try {
            if (!var1.isSatisfied()) {
               this.awaitUninterruptibly(var1, var3);
            }

            var4 = true;
         } finally {
            if (!var4) {
               this.leave();
            }

         }

      }
   }

   public boolean enterWhen(Monitor.Guard var1, long var2, TimeUnit var4) throws InterruptedException {
      long var5 = toSafeNanos(var2, var4);
      if (var1.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock var7;
         boolean var8;
         long var9;
         label269: {
            var7 = this.lock;
            var8 = var7.isHeldByCurrentThread();
            var9 = 0L;
            if (!this.fair) {
               if (Thread.interrupted()) {
                  throw new InterruptedException();
               }

               if (var7.tryLock()) {
                  break label269;
               }
            }

            var9 = initNanoTime(var5);
            if (!var7.tryLock(var2, var4)) {
               return false;
            }
         }

         boolean var11 = false;
         boolean var12 = true;

         boolean var13;
         try {
            var11 = var1.isSatisfied() || this.awaitNanos(var1, var9 == 0L ? var5 : remainingNanos(var9, var5), var8);
            var12 = false;
            var13 = var11;
         } finally {
            if (!var11) {
               try {
                  if (var12 && !var8) {
                     this.signalNextWaiter();
                  }
               } finally {
                  var7.unlock();
               }
            }

         }

         return var13;
      }
   }

   public boolean enterWhenUninterruptibly(Monitor.Guard var1, long var2, TimeUnit var4) {
      long var5 = toSafeNanos(var2, var4);
      if (var1.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock var7 = this.lock;
         long var8 = 0L;
         boolean var10 = var7.isHeldByCurrentThread();
         boolean var11 = Thread.interrupted();

         try {
            if (this.fair || !var7.tryLock()) {
               var8 = initNanoTime(var5);
               long var12 = var5;

               while(true) {
                  try {
                     if (!var7.tryLock(var12, TimeUnit.NANOSECONDS)) {
                        boolean var31 = false;
                        return var31;
                     }
                     break;
                  } catch (InterruptedException var27) {
                     InterruptedException var14 = var27;
                     var11 = true;
                     var12 = remainingNanos(var8, var5);
                  }
               }
            }

            boolean var29 = false;

            try {
               while(true) {
                  try {
                     if (var1.isSatisfied()) {
                        var29 = true;
                     } else {
                        long var13;
                        if (var8 == 0L) {
                           var8 = initNanoTime(var5);
                           var13 = var5;
                        } else {
                           var13 = remainingNanos(var8, var5);
                        }

                        var29 = this.awaitNanos(var1, var13, var10);
                     }

                     boolean var30 = var29;
                     return var30;
                  } catch (InterruptedException var25) {
                     var11 = true;
                     var10 = false;
                  }
               }
            } finally {
               if (!var29) {
                  var7.unlock();
               }

            }
         } finally {
            if (var11) {
               Thread.currentThread().interrupt();
            }

         }
      }
   }

   public boolean enterIf(Monitor.Guard var1) {
      if (var1.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock var2 = this.lock;
         var2.lock();
         boolean var3 = false;

         boolean var4;
         try {
            var4 = var3 = var1.isSatisfied();
         } finally {
            if (!var3) {
               var2.unlock();
            }

         }

         return var4;
      }
   }

   public boolean enterIfInterruptibly(Monitor.Guard var1) throws InterruptedException {
      if (var1.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock var2 = this.lock;
         var2.lockInterruptibly();
         boolean var3 = false;

         boolean var4;
         try {
            var4 = var3 = var1.isSatisfied();
         } finally {
            if (!var3) {
               var2.unlock();
            }

         }

         return var4;
      }
   }

   public boolean enterIf(Monitor.Guard var1, long var2, TimeUnit var4) {
      if (var1.monitor != this) {
         throw new IllegalMonitorStateException();
      } else if (!this.enter(var2, var4)) {
         return false;
      } else {
         boolean var5 = false;

         boolean var6;
         try {
            var6 = var5 = var1.isSatisfied();
         } finally {
            if (!var5) {
               this.lock.unlock();
            }

         }

         return var6;
      }
   }

   public boolean enterIfInterruptibly(Monitor.Guard var1, long var2, TimeUnit var4) throws InterruptedException {
      if (var1.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock var5 = this.lock;
         if (!var5.tryLock(var2, var4)) {
            return false;
         } else {
            boolean var6 = false;

            boolean var7;
            try {
               var7 = var6 = var1.isSatisfied();
            } finally {
               if (!var6) {
                  var5.unlock();
               }

            }

            return var7;
         }
      }
   }

   public boolean tryEnterIf(Monitor.Guard var1) {
      if (var1.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock var2 = this.lock;
         if (!var2.tryLock()) {
            return false;
         } else {
            boolean var3 = false;

            boolean var4;
            try {
               var4 = var3 = var1.isSatisfied();
            } finally {
               if (!var3) {
                  var2.unlock();
               }

            }

            return var4;
         }
      }
   }

   public void waitFor(Monitor.Guard var1) throws InterruptedException {
      if (!(var1.monitor == this & this.lock.isHeldByCurrentThread())) {
         throw new IllegalMonitorStateException();
      } else {
         if (!var1.isSatisfied()) {
            this.await(var1, true);
         }

      }
   }

   public void waitForUninterruptibly(Monitor.Guard var1) {
      if (!(var1.monitor == this & this.lock.isHeldByCurrentThread())) {
         throw new IllegalMonitorStateException();
      } else {
         if (!var1.isSatisfied()) {
            this.awaitUninterruptibly(var1, true);
         }

      }
   }

   public boolean waitFor(Monitor.Guard var1, long var2, TimeUnit var4) throws InterruptedException {
      long var5 = toSafeNanos(var2, var4);
      if (!(var1.monitor == this & this.lock.isHeldByCurrentThread())) {
         throw new IllegalMonitorStateException();
      } else if (var1.isSatisfied()) {
         return true;
      } else if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         return this.awaitNanos(var1, var5, true);
      }
   }

   public boolean waitForUninterruptibly(Monitor.Guard var1, long var2, TimeUnit var4) {
      long var5 = toSafeNanos(var2, var4);
      if (!(var1.monitor == this & this.lock.isHeldByCurrentThread())) {
         throw new IllegalMonitorStateException();
      } else if (var1.isSatisfied()) {
         return true;
      } else {
         boolean var7 = true;
         long var8 = initNanoTime(var5);
         boolean var10 = Thread.interrupted();

         try {
            long var11 = var5;

            while(true) {
               try {
                  boolean var13 = this.awaitNanos(var1, var11, var7);
                  return var13;
               } catch (InterruptedException var18) {
                  var10 = true;
                  if (var1.isSatisfied()) {
                     boolean var14 = true;
                     return var14;
                  }

                  var7 = false;
                  var11 = remainingNanos(var8, var5);
               }
            }
         } finally {
            if (var10) {
               Thread.currentThread().interrupt();
            }

         }
      }
   }

   public void leave() {
      ReentrantLock var1 = this.lock;

      try {
         if (var1.getHoldCount() == 1) {
            this.signalNextWaiter();
         }
      } finally {
         var1.unlock();
      }

   }

   public boolean isFair() {
      return this.fair;
   }

   public boolean isOccupied() {
      return this.lock.isLocked();
   }

   public boolean isOccupiedByCurrentThread() {
      return this.lock.isHeldByCurrentThread();
   }

   public int getOccupiedDepth() {
      return this.lock.getHoldCount();
   }

   public int getQueueLength() {
      return this.lock.getQueueLength();
   }

   public boolean hasQueuedThreads() {
      return this.lock.hasQueuedThreads();
   }

   public boolean hasQueuedThread(Thread var1) {
      return this.lock.hasQueuedThread(var1);
   }

   public boolean hasWaiters(Monitor.Guard var1) {
      return this.getWaitQueueLength(var1) > 0;
   }

   public int getWaitQueueLength(Monitor.Guard var1) {
      if (var1.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         this.lock.lock();

         int var2;
         try {
            var2 = var1.waiterCount;
         } finally {
            this.lock.unlock();
         }

         return var2;
      }
   }

   private static long toSafeNanos(long var0, TimeUnit var2) {
      long var3 = var2.toNanos(var0);
      return var3 <= 0L ? 0L : (var3 > 6917529027641081853L ? 6917529027641081853L : var3);
   }

   private static long initNanoTime(long var0) {
      if (var0 <= 0L) {
         return 0L;
      } else {
         long var2 = System.nanoTime();
         return var2 == 0L ? 1L : var2;
      }
   }

   private static long remainingNanos(long var0, long var2) {
      return var2 <= 0L ? 0L : var2 - (System.nanoTime() - var0);
   }

   @GuardedBy("lock")
   private void signalNextWaiter() {
      for(Monitor.Guard var1 = this.activeGuards; var1 != null; var1 = var1.next) {
         if (this.isSatisfied(var1)) {
            var1.condition.signal();
            break;
         }
      }

   }

   @GuardedBy("lock")
   private boolean isSatisfied(Monitor.Guard var1) {
      try {
         return var1.isSatisfied();
      } catch (Throwable var3) {
         this.signalAllWaiters();
         throw Throwables.propagate(var3);
      }
   }

   @GuardedBy("lock")
   private void signalAllWaiters() {
      for(Monitor.Guard var1 = this.activeGuards; var1 != null; var1 = var1.next) {
         var1.condition.signalAll();
      }

   }

   @GuardedBy("lock")
   private void beginWaitingFor(Monitor.Guard var1) {
      int var2 = var1.waiterCount++;
      if (var2 == 0) {
         var1.next = this.activeGuards;
         this.activeGuards = var1;
      }

   }

   @GuardedBy("lock")
   private void endWaitingFor(Monitor.Guard var1) {
      int var2 = --var1.waiterCount;
      if (var2 == 0) {
         Monitor.Guard var3 = this.activeGuards;

         Monitor.Guard var4;
         for(var4 = null; var3 != var1; var3 = var3.next) {
            var4 = var3;
         }

         if (var4 == null) {
            this.activeGuards = var3.next;
         } else {
            var4.next = var3.next;
         }

         var3.next = null;
      }

   }

   @GuardedBy("lock")
   private void await(Monitor.Guard var1, boolean var2) throws InterruptedException {
      if (var2) {
         this.signalNextWaiter();
      }

      this.beginWaitingFor(var1);

      try {
         do {
            var1.condition.await();
         } while(!var1.isSatisfied());
      } finally {
         this.endWaitingFor(var1);
      }

   }

   @GuardedBy("lock")
   private void awaitUninterruptibly(Monitor.Guard var1, boolean var2) {
      if (var2) {
         this.signalNextWaiter();
      }

      this.beginWaitingFor(var1);

      try {
         do {
            var1.condition.awaitUninterruptibly();
         } while(!var1.isSatisfied());
      } finally {
         this.endWaitingFor(var1);
      }

   }

   @GuardedBy("lock")
   private boolean awaitNanos(Monitor.Guard var1, long var2, boolean var4) throws InterruptedException {
      boolean var5 = true;

      boolean var6;
      try {
         while(var2 > 0L) {
            if (var5) {
               if (var4) {
                  this.signalNextWaiter();
               }

               this.beginWaitingFor(var1);
               var5 = false;
            }

            var2 = var1.condition.awaitNanos(var2);
            if (var1.isSatisfied()) {
               var6 = true;
               return var6;
            }
         }

         var6 = false;
      } finally {
         if (!var5) {
            this.endWaitingFor(var1);
         }

      }

      return var6;
   }

   @Beta
   public abstract static class Guard {
      @Weak
      final Monitor monitor;
      final Condition condition;
      @GuardedBy("monitor.lock")
      int waiterCount = 0;
      @GuardedBy("monitor.lock")
      Monitor.Guard next;

      protected Guard(Monitor var1) {
         super();
         this.monitor = (Monitor)Preconditions.checkNotNull(var1, "monitor");
         this.condition = var1.lock.newCondition();
      }

      public abstract boolean isSatisfied();
   }
}
