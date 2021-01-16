package io.netty.util.concurrent;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultPromise<V> extends AbstractFuture<V> implements Promise<V> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultPromise.class);
   private static final InternalLogger rejectedExecutionLogger = InternalLoggerFactory.getInstance(DefaultPromise.class.getName() + ".rejectedExecution");
   private static final int MAX_LISTENER_STACK_DEPTH = Math.min(8, SystemPropertyUtil.getInt("io.netty.defaultPromise.maxListenerStackDepth", 8));
   private static final AtomicReferenceFieldUpdater<DefaultPromise, Object> RESULT_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DefaultPromise.class, Object.class, "result");
   private static final Object SUCCESS = new Object();
   private static final Object UNCANCELLABLE = new Object();
   private static final DefaultPromise.CauseHolder CANCELLATION_CAUSE_HOLDER = new DefaultPromise.CauseHolder(ThrowableUtil.unknownStackTrace(new CancellationException(), DefaultPromise.class, "cancel(...)"));
   private volatile Object result;
   private final EventExecutor executor;
   private Object listeners;
   private short waiters;
   private boolean notifyingListeners;

   public DefaultPromise(EventExecutor var1) {
      super();
      this.executor = (EventExecutor)ObjectUtil.checkNotNull(var1, "executor");
   }

   protected DefaultPromise() {
      super();
      this.executor = null;
   }

   public Promise<V> setSuccess(V var1) {
      if (this.setSuccess0(var1)) {
         this.notifyListeners();
         return this;
      } else {
         throw new IllegalStateException("complete already: " + this);
      }
   }

   public boolean trySuccess(V var1) {
      if (this.setSuccess0(var1)) {
         this.notifyListeners();
         return true;
      } else {
         return false;
      }
   }

   public Promise<V> setFailure(Throwable var1) {
      if (this.setFailure0(var1)) {
         this.notifyListeners();
         return this;
      } else {
         throw new IllegalStateException("complete already: " + this, var1);
      }
   }

   public boolean tryFailure(Throwable var1) {
      if (this.setFailure0(var1)) {
         this.notifyListeners();
         return true;
      } else {
         return false;
      }
   }

   public boolean setUncancellable() {
      if (RESULT_UPDATER.compareAndSet(this, (Object)null, UNCANCELLABLE)) {
         return true;
      } else {
         Object var1 = this.result;
         return !isDone0(var1) || !isCancelled0(var1);
      }
   }

   public boolean isSuccess() {
      Object var1 = this.result;
      return var1 != null && var1 != UNCANCELLABLE && !(var1 instanceof DefaultPromise.CauseHolder);
   }

   public boolean isCancellable() {
      return this.result == null;
   }

   public Throwable cause() {
      Object var1 = this.result;
      return var1 instanceof DefaultPromise.CauseHolder ? ((DefaultPromise.CauseHolder)var1).cause : null;
   }

   public Promise<V> addListener(GenericFutureListener<? extends Future<? super V>> var1) {
      ObjectUtil.checkNotNull(var1, "listener");
      synchronized(this) {
         this.addListener0(var1);
      }

      if (this.isDone()) {
         this.notifyListeners();
      }

      return this;
   }

   public Promise<V> addListeners(GenericFutureListener<? extends Future<? super V>>... var1) {
      ObjectUtil.checkNotNull(var1, "listeners");
      synchronized(this) {
         GenericFutureListener[] var3 = var1;
         int var4 = var1.length;
         int var5 = 0;

         while(var5 < var4) {
            GenericFutureListener var6 = var3[var5];
            if (var6 != null) {
               this.addListener0(var6);
               ++var5;
               continue;
            }
         }
      }

      if (this.isDone()) {
         this.notifyListeners();
      }

      return this;
   }

   public Promise<V> removeListener(GenericFutureListener<? extends Future<? super V>> var1) {
      ObjectUtil.checkNotNull(var1, "listener");
      synchronized(this) {
         this.removeListener0(var1);
         return this;
      }
   }

   public Promise<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... var1) {
      ObjectUtil.checkNotNull(var1, "listeners");
      synchronized(this) {
         GenericFutureListener[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            GenericFutureListener var6 = var3[var5];
            if (var6 == null) {
               break;
            }

            this.removeListener0(var6);
         }

         return this;
      }
   }

   public Promise<V> await() throws InterruptedException {
      if (this.isDone()) {
         return this;
      } else if (Thread.interrupted()) {
         throw new InterruptedException(this.toString());
      } else {
         this.checkDeadLock();
         synchronized(this) {
            while(!this.isDone()) {
               this.incWaiters();

               try {
                  this.wait();
               } finally {
                  this.decWaiters();
               }
            }

            return this;
         }
      }
   }

   public Promise<V> awaitUninterruptibly() {
      if (this.isDone()) {
         return this;
      } else {
         this.checkDeadLock();
         boolean var1 = false;
         synchronized(this) {
            while(!this.isDone()) {
               this.incWaiters();

               try {
                  this.wait();
               } catch (InterruptedException var9) {
                  var1 = true;
               } finally {
                  this.decWaiters();
               }
            }
         }

         if (var1) {
            Thread.currentThread().interrupt();
         }

         return this;
      }
   }

   public boolean await(long var1, TimeUnit var3) throws InterruptedException {
      return this.await0(var3.toNanos(var1), true);
   }

   public boolean await(long var1) throws InterruptedException {
      return this.await0(TimeUnit.MILLISECONDS.toNanos(var1), true);
   }

   public boolean awaitUninterruptibly(long var1, TimeUnit var3) {
      try {
         return this.await0(var3.toNanos(var1), false);
      } catch (InterruptedException var5) {
         throw new InternalError();
      }
   }

   public boolean awaitUninterruptibly(long var1) {
      try {
         return this.await0(TimeUnit.MILLISECONDS.toNanos(var1), false);
      } catch (InterruptedException var4) {
         throw new InternalError();
      }
   }

   public V getNow() {
      Object var1 = this.result;
      return !(var1 instanceof DefaultPromise.CauseHolder) && var1 != SUCCESS ? var1 : null;
   }

   public boolean cancel(boolean var1) {
      if (RESULT_UPDATER.compareAndSet(this, (Object)null, CANCELLATION_CAUSE_HOLDER)) {
         this.checkNotifyWaiters();
         this.notifyListeners();
         return true;
      } else {
         return false;
      }
   }

   public boolean isCancelled() {
      return isCancelled0(this.result);
   }

   public boolean isDone() {
      return isDone0(this.result);
   }

   public Promise<V> sync() throws InterruptedException {
      this.await();
      this.rethrowIfFailed();
      return this;
   }

   public Promise<V> syncUninterruptibly() {
      this.awaitUninterruptibly();
      this.rethrowIfFailed();
      return this;
   }

   public String toString() {
      return this.toStringBuilder().toString();
   }

   protected StringBuilder toStringBuilder() {
      StringBuilder var1 = (new StringBuilder(64)).append(StringUtil.simpleClassName((Object)this)).append('@').append(Integer.toHexString(this.hashCode()));
      Object var2 = this.result;
      if (var2 == SUCCESS) {
         var1.append("(success)");
      } else if (var2 == UNCANCELLABLE) {
         var1.append("(uncancellable)");
      } else if (var2 instanceof DefaultPromise.CauseHolder) {
         var1.append("(failure: ").append(((DefaultPromise.CauseHolder)var2).cause).append(')');
      } else if (var2 != null) {
         var1.append("(success: ").append(var2).append(')');
      } else {
         var1.append("(incomplete)");
      }

      return var1;
   }

   protected EventExecutor executor() {
      return this.executor;
   }

   protected void checkDeadLock() {
      EventExecutor var1 = this.executor();
      if (var1 != null && var1.inEventLoop()) {
         throw new BlockingOperationException(this.toString());
      }
   }

   protected static void notifyListener(EventExecutor var0, Future<?> var1, GenericFutureListener<?> var2) {
      ObjectUtil.checkNotNull(var0, "eventExecutor");
      ObjectUtil.checkNotNull(var1, "future");
      ObjectUtil.checkNotNull(var2, "listener");
      notifyListenerWithStackOverFlowProtection(var0, var1, var2);
   }

   private void notifyListeners() {
      EventExecutor var1 = this.executor();
      if (var1.inEventLoop()) {
         InternalThreadLocalMap var2 = InternalThreadLocalMap.get();
         int var3 = var2.futureListenerStackDepth();
         if (var3 < MAX_LISTENER_STACK_DEPTH) {
            var2.setFutureListenerStackDepth(var3 + 1);

            try {
               this.notifyListenersNow();
            } finally {
               var2.setFutureListenerStackDepth(var3);
            }

            return;
         }
      }

      safeExecute(var1, new Runnable() {
         public void run() {
            DefaultPromise.this.notifyListenersNow();
         }
      });
   }

   private static void notifyListenerWithStackOverFlowProtection(EventExecutor var0, final Future<?> var1, final GenericFutureListener<?> var2) {
      if (var0.inEventLoop()) {
         InternalThreadLocalMap var3 = InternalThreadLocalMap.get();
         int var4 = var3.futureListenerStackDepth();
         if (var4 < MAX_LISTENER_STACK_DEPTH) {
            var3.setFutureListenerStackDepth(var4 + 1);

            try {
               notifyListener0(var1, var2);
            } finally {
               var3.setFutureListenerStackDepth(var4);
            }

            return;
         }
      }

      safeExecute(var0, new Runnable() {
         public void run() {
            DefaultPromise.notifyListener0(var1, var2);
         }
      });
   }

   private void notifyListenersNow() {
      Object var1;
      synchronized(this) {
         if (this.notifyingListeners || this.listeners == null) {
            return;
         }

         this.notifyingListeners = true;
         var1 = this.listeners;
         this.listeners = null;
      }

      while(true) {
         if (var1 instanceof DefaultFutureListeners) {
            this.notifyListeners0((DefaultFutureListeners)var1);
         } else {
            notifyListener0(this, (GenericFutureListener)var1);
         }

         synchronized(this) {
            if (this.listeners == null) {
               this.notifyingListeners = false;
               return;
            }

            var1 = this.listeners;
            this.listeners = null;
         }
      }
   }

   private void notifyListeners0(DefaultFutureListeners var1) {
      GenericFutureListener[] var2 = var1.listeners();
      int var3 = var1.size();

      for(int var4 = 0; var4 < var3; ++var4) {
         notifyListener0(this, var2[var4]);
      }

   }

   private static void notifyListener0(Future var0, GenericFutureListener var1) {
      try {
         var1.operationComplete(var0);
      } catch (Throwable var3) {
         logger.warn("An exception was thrown by " + var1.getClass().getName() + ".operationComplete()", var3);
      }

   }

   private void addListener0(GenericFutureListener<? extends Future<? super V>> var1) {
      if (this.listeners == null) {
         this.listeners = var1;
      } else if (this.listeners instanceof DefaultFutureListeners) {
         ((DefaultFutureListeners)this.listeners).add(var1);
      } else {
         this.listeners = new DefaultFutureListeners((GenericFutureListener)this.listeners, var1);
      }

   }

   private void removeListener0(GenericFutureListener<? extends Future<? super V>> var1) {
      if (this.listeners instanceof DefaultFutureListeners) {
         ((DefaultFutureListeners)this.listeners).remove(var1);
      } else if (this.listeners == var1) {
         this.listeners = null;
      }

   }

   private boolean setSuccess0(V var1) {
      return this.setValue0(var1 == null ? SUCCESS : var1);
   }

   private boolean setFailure0(Throwable var1) {
      return this.setValue0(new DefaultPromise.CauseHolder((Throwable)ObjectUtil.checkNotNull(var1, "cause")));
   }

   private boolean setValue0(Object var1) {
      if (!RESULT_UPDATER.compareAndSet(this, (Object)null, var1) && !RESULT_UPDATER.compareAndSet(this, UNCANCELLABLE, var1)) {
         return false;
      } else {
         this.checkNotifyWaiters();
         return true;
      }
   }

   private synchronized void checkNotifyWaiters() {
      if (this.waiters > 0) {
         this.notifyAll();
      }

   }

   private void incWaiters() {
      if (this.waiters == 32767) {
         throw new IllegalStateException("too many waiters: " + this);
      } else {
         ++this.waiters;
      }
   }

   private void decWaiters() {
      --this.waiters;
   }

   private void rethrowIfFailed() {
      Throwable var1 = this.cause();
      if (var1 != null) {
         PlatformDependent.throwException(var1);
      }
   }

   private boolean await0(long var1, boolean var3) throws InterruptedException {
      if (this.isDone()) {
         return true;
      } else if (var1 <= 0L) {
         return this.isDone();
      } else if (var3 && Thread.interrupted()) {
         throw new InterruptedException(this.toString());
      } else {
         this.checkDeadLock();
         long var4 = System.nanoTime();
         long var6 = var1;
         boolean var8 = false;

         try {
            boolean var9;
            do {
               synchronized(this) {
                  if (this.isDone()) {
                     boolean var10 = true;
                     return var10;
                  }

                  this.incWaiters();

                  try {
                     this.wait(var6 / 1000000L, (int)(var6 % 1000000L));
                  } catch (InterruptedException var22) {
                     if (var3) {
                        throw var22;
                     }

                     var8 = true;
                  } finally {
                     this.decWaiters();
                  }
               }

               if (this.isDone()) {
                  var9 = true;
                  return var9;
               }

               var6 = var1 - (System.nanoTime() - var4);
            } while(var6 > 0L);

            var9 = this.isDone();
            return var9;
         } finally {
            if (var8) {
               Thread.currentThread().interrupt();
            }

         }
      }
   }

   void notifyProgressiveListeners(final long var1, final long var3) {
      Object var5 = this.progressiveListeners();
      if (var5 != null) {
         final ProgressiveFuture var6 = (ProgressiveFuture)this;
         EventExecutor var7 = this.executor();
         if (var7.inEventLoop()) {
            if (var5 instanceof GenericProgressiveFutureListener[]) {
               notifyProgressiveListeners0(var6, (GenericProgressiveFutureListener[])((GenericProgressiveFutureListener[])var5), var1, var3);
            } else {
               notifyProgressiveListener0(var6, (GenericProgressiveFutureListener)var5, var1, var3);
            }
         } else if (var5 instanceof GenericProgressiveFutureListener[]) {
            final GenericProgressiveFutureListener[] var8 = (GenericProgressiveFutureListener[])((GenericProgressiveFutureListener[])var5);
            safeExecute(var7, new Runnable() {
               public void run() {
                  DefaultPromise.notifyProgressiveListeners0(var6, var8, var1, var3);
               }
            });
         } else {
            final GenericProgressiveFutureListener var9 = (GenericProgressiveFutureListener)var5;
            safeExecute(var7, new Runnable() {
               public void run() {
                  DefaultPromise.notifyProgressiveListener0(var6, var9, var1, var3);
               }
            });
         }

      }
   }

   private synchronized Object progressiveListeners() {
      Object var1 = this.listeners;
      if (var1 == null) {
         return null;
      } else if (var1 instanceof DefaultFutureListeners) {
         DefaultFutureListeners var2 = (DefaultFutureListeners)var1;
         int var3 = var2.progressiveSize();
         GenericFutureListener[] var4;
         int var6;
         switch(var3) {
         case 0:
            return null;
         case 1:
            var4 = var2.listeners();
            int var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               GenericFutureListener var7 = var4[var6];
               if (var7 instanceof GenericProgressiveFutureListener) {
                  return var7;
               }
            }

            return null;
         default:
            var4 = var2.listeners();
            GenericProgressiveFutureListener[] var9 = new GenericProgressiveFutureListener[var3];
            var6 = 0;

            for(int var10 = 0; var10 < var3; ++var6) {
               GenericFutureListener var8 = var4[var6];
               if (var8 instanceof GenericProgressiveFutureListener) {
                  var9[var10++] = (GenericProgressiveFutureListener)var8;
               }
            }

            return var9;
         }
      } else {
         return var1 instanceof GenericProgressiveFutureListener ? var1 : null;
      }
   }

   private static void notifyProgressiveListeners0(ProgressiveFuture<?> var0, GenericProgressiveFutureListener<?>[] var1, long var2, long var4) {
      GenericProgressiveFutureListener[] var6 = var1;
      int var7 = var1.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         GenericProgressiveFutureListener var9 = var6[var8];
         if (var9 == null) {
            break;
         }

         notifyProgressiveListener0(var0, var9, var2, var4);
      }

   }

   private static void notifyProgressiveListener0(ProgressiveFuture var0, GenericProgressiveFutureListener var1, long var2, long var4) {
      try {
         var1.operationProgressed(var0, var2, var4);
      } catch (Throwable var7) {
         logger.warn("An exception was thrown by " + var1.getClass().getName() + ".operationProgressed()", var7);
      }

   }

   private static boolean isCancelled0(Object var0) {
      return var0 instanceof DefaultPromise.CauseHolder && ((DefaultPromise.CauseHolder)var0).cause instanceof CancellationException;
   }

   private static boolean isDone0(Object var0) {
      return var0 != null && var0 != UNCANCELLABLE;
   }

   private static void safeExecute(EventExecutor var0, Runnable var1) {
      try {
         var0.execute(var1);
      } catch (Throwable var3) {
         rejectedExecutionLogger.error("Failed to submit a listener notification task. Event loop shut down?", var3);
      }

   }

   private static final class CauseHolder {
      final Throwable cause;

      CauseHolder(Throwable var1) {
         super();
         this.cause = var1;
      }
   }
}
