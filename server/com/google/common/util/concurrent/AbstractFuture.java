package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import sun.misc.Unsafe;

@GwtCompatible(
   emulated = true
)
public abstract class AbstractFuture<V> implements ListenableFuture<V> {
   private static final boolean GENERATE_CANCELLATION_CAUSES = Boolean.parseBoolean(System.getProperty("guava.concurrent.generate_cancellation_cause", "false"));
   private static final Logger log = Logger.getLogger(AbstractFuture.class.getName());
   private static final long SPIN_THRESHOLD_NANOS = 1000L;
   private static final AbstractFuture.AtomicHelper ATOMIC_HELPER;
   private static final Object NULL;
   private volatile Object value;
   private volatile AbstractFuture.Listener listeners;
   private volatile AbstractFuture.Waiter waiters;

   private void removeWaiter(AbstractFuture.Waiter var1) {
      var1.thread = null;

      label28:
      while(true) {
         AbstractFuture.Waiter var2 = null;
         AbstractFuture.Waiter var3 = this.waiters;
         if (var3 == AbstractFuture.Waiter.TOMBSTONE) {
            return;
         }

         AbstractFuture.Waiter var4;
         for(; var3 != null; var3 = var4) {
            var4 = var3.next;
            if (var3.thread != null) {
               var2 = var3;
            } else if (var2 != null) {
               var2.next = var4;
               if (var2.thread == null) {
                  continue label28;
               }
            } else if (!ATOMIC_HELPER.casWaiters(this, var3, var4)) {
               continue label28;
            }
         }

         return;
      }
   }

   protected AbstractFuture() {
      super();
   }

   @CanIgnoreReturnValue
   public V get(long var1, TimeUnit var3) throws InterruptedException, TimeoutException, ExecutionException {
      long var4 = var3.toNanos(var1);
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         Object var6 = this.value;
         if (var6 != null & !(var6 instanceof AbstractFuture.SetFuture)) {
            return this.getDoneValue(var6);
         } else {
            long var7 = var4 > 0L ? System.nanoTime() + var4 : 0L;
            if (var4 >= 1000L) {
               label117: {
                  AbstractFuture.Waiter var9 = this.waiters;
                  if (var9 != AbstractFuture.Waiter.TOMBSTONE) {
                     AbstractFuture.Waiter var10 = new AbstractFuture.Waiter();

                     do {
                        var10.setNext(var9);
                        if (ATOMIC_HELPER.casWaiters(this, var9, var10)) {
                           do {
                              LockSupport.parkNanos(this, var4);
                              if (Thread.interrupted()) {
                                 this.removeWaiter(var10);
                                 throw new InterruptedException();
                              }

                              var6 = this.value;
                              if (var6 != null & !(var6 instanceof AbstractFuture.SetFuture)) {
                                 return this.getDoneValue(var6);
                              }

                              var4 = var7 - System.nanoTime();
                           } while(var4 >= 1000L);

                           this.removeWaiter(var10);
                           break label117;
                        }

                        var9 = this.waiters;
                     } while(var9 != AbstractFuture.Waiter.TOMBSTONE);
                  }

                  return this.getDoneValue(this.value);
               }
            }

            while(var4 > 0L) {
               var6 = this.value;
               if (var6 != null & !(var6 instanceof AbstractFuture.SetFuture)) {
                  return this.getDoneValue(var6);
               }

               if (Thread.interrupted()) {
                  throw new InterruptedException();
               }

               var4 = var7 - System.nanoTime();
            }

            throw new TimeoutException();
         }
      }
   }

   @CanIgnoreReturnValue
   public V get() throws InterruptedException, ExecutionException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         Object var1 = this.value;
         if (var1 != null & !(var1 instanceof AbstractFuture.SetFuture)) {
            return this.getDoneValue(var1);
         } else {
            AbstractFuture.Waiter var2 = this.waiters;
            if (var2 != AbstractFuture.Waiter.TOMBSTONE) {
               AbstractFuture.Waiter var3 = new AbstractFuture.Waiter();

               do {
                  var3.setNext(var2);
                  if (ATOMIC_HELPER.casWaiters(this, var2, var3)) {
                     do {
                        LockSupport.park(this);
                        if (Thread.interrupted()) {
                           this.removeWaiter(var3);
                           throw new InterruptedException();
                        }

                        var1 = this.value;
                     } while(!(var1 != null & !(var1 instanceof AbstractFuture.SetFuture)));

                     return this.getDoneValue(var1);
                  }

                  var2 = this.waiters;
               } while(var2 != AbstractFuture.Waiter.TOMBSTONE);
            }

            return this.getDoneValue(this.value);
         }
      }
   }

   private V getDoneValue(Object var1) throws ExecutionException {
      if (var1 instanceof AbstractFuture.Cancellation) {
         throw cancellationExceptionWithCause("Task was cancelled.", ((AbstractFuture.Cancellation)var1).cause);
      } else if (var1 instanceof AbstractFuture.Failure) {
         throw new ExecutionException(((AbstractFuture.Failure)var1).exception);
      } else {
         return var1 == NULL ? null : var1;
      }
   }

   public boolean isDone() {
      Object var1 = this.value;
      return var1 != null & !(var1 instanceof AbstractFuture.SetFuture);
   }

   public boolean isCancelled() {
      Object var1 = this.value;
      return var1 instanceof AbstractFuture.Cancellation;
   }

   @CanIgnoreReturnValue
   public boolean cancel(boolean var1) {
      Object var2 = this.value;
      boolean var3 = false;
      if (var2 == null | var2 instanceof AbstractFuture.SetFuture) {
         CancellationException var4 = GENERATE_CANCELLATION_CAUSES ? new CancellationException("Future.cancel() was called.") : null;
         AbstractFuture.Cancellation var5 = new AbstractFuture.Cancellation(var1, var4);
         AbstractFuture var6 = this;

         while(true) {
            while(!ATOMIC_HELPER.casValue(var6, var2, var5)) {
               var2 = var6.value;
               if (!(var2 instanceof AbstractFuture.SetFuture)) {
                  return var3;
               }
            }

            var3 = true;
            if (var1) {
               var6.interruptTask();
            }

            complete(var6);
            if (!(var2 instanceof AbstractFuture.SetFuture)) {
               break;
            }

            ListenableFuture var7 = ((AbstractFuture.SetFuture)var2).future;
            if (!(var7 instanceof AbstractFuture.TrustedFuture)) {
               var7.cancel(var1);
               break;
            }

            AbstractFuture var8 = (AbstractFuture)var7;
            var2 = var8.value;
            if (!(var2 == null | var2 instanceof AbstractFuture.SetFuture)) {
               break;
            }

            var6 = var8;
         }
      }

      return var3;
   }

   protected void interruptTask() {
   }

   protected final boolean wasInterrupted() {
      Object var1 = this.value;
      return var1 instanceof AbstractFuture.Cancellation && ((AbstractFuture.Cancellation)var1).wasInterrupted;
   }

   public void addListener(Runnable var1, Executor var2) {
      Preconditions.checkNotNull(var1, "Runnable was null.");
      Preconditions.checkNotNull(var2, "Executor was null.");
      AbstractFuture.Listener var3 = this.listeners;
      if (var3 != AbstractFuture.Listener.TOMBSTONE) {
         AbstractFuture.Listener var4 = new AbstractFuture.Listener(var1, var2);

         do {
            var4.next = var3;
            if (ATOMIC_HELPER.casListeners(this, var3, var4)) {
               return;
            }

            var3 = this.listeners;
         } while(var3 != AbstractFuture.Listener.TOMBSTONE);
      }

      executeListener(var1, var2);
   }

   @CanIgnoreReturnValue
   protected boolean set(@Nullable V var1) {
      Object var2 = var1 == null ? NULL : var1;
      if (ATOMIC_HELPER.casValue(this, (Object)null, var2)) {
         complete(this);
         return true;
      } else {
         return false;
      }
   }

   @CanIgnoreReturnValue
   protected boolean setException(Throwable var1) {
      AbstractFuture.Failure var2 = new AbstractFuture.Failure((Throwable)Preconditions.checkNotNull(var1));
      if (ATOMIC_HELPER.casValue(this, (Object)null, var2)) {
         complete(this);
         return true;
      } else {
         return false;
      }
   }

   @Beta
   @CanIgnoreReturnValue
   protected boolean setFuture(ListenableFuture<? extends V> var1) {
      Preconditions.checkNotNull(var1);
      Object var2 = this.value;
      if (var2 == null) {
         if (var1.isDone()) {
            Object var9 = getFutureValue(var1);
            if (ATOMIC_HELPER.casValue(this, (Object)null, var9)) {
               complete(this);
               return true;
            }

            return false;
         }

         AbstractFuture.SetFuture var3 = new AbstractFuture.SetFuture(this, var1);
         if (ATOMIC_HELPER.casValue(this, (Object)null, var3)) {
            try {
               var1.addListener(var3, MoreExecutors.directExecutor());
            } catch (Throwable var8) {
               Throwable var4 = var8;

               AbstractFuture.Failure var5;
               try {
                  var5 = new AbstractFuture.Failure(var4);
               } catch (Throwable var7) {
                  var5 = AbstractFuture.Failure.FALLBACK_INSTANCE;
               }

               ATOMIC_HELPER.casValue(this, var3, var5);
            }

            return true;
         }

         var2 = this.value;
      }

      if (var2 instanceof AbstractFuture.Cancellation) {
         var1.cancel(((AbstractFuture.Cancellation)var2).wasInterrupted);
      }

      return false;
   }

   private static Object getFutureValue(ListenableFuture<?> var0) {
      if (var0 instanceof AbstractFuture.TrustedFuture) {
         return ((AbstractFuture)var0).value;
      } else {
         Object var1;
         try {
            Object var2 = Futures.getDone(var0);
            var1 = var2 == null ? NULL : var2;
         } catch (ExecutionException var3) {
            var1 = new AbstractFuture.Failure(var3.getCause());
         } catch (CancellationException var4) {
            var1 = new AbstractFuture.Cancellation(false, var4);
         } catch (Throwable var5) {
            var1 = new AbstractFuture.Failure(var5);
         }

         return var1;
      }
   }

   private static void complete(AbstractFuture<?> var0) {
      AbstractFuture.Listener var1 = null;

      label23:
      while(true) {
         var0.releaseWaiters();
         var0.afterDone();
         var1 = var0.clearListeners(var1);
         var0 = null;

         while(var1 != null) {
            AbstractFuture.Listener var2 = var1;
            var1 = var1.next;
            Runnable var3 = var2.task;
            if (var3 instanceof AbstractFuture.SetFuture) {
               AbstractFuture.SetFuture var4 = (AbstractFuture.SetFuture)var3;
               var0 = var4.owner;
               if (var0.value == var4) {
                  Object var5 = getFutureValue(var4.future);
                  if (ATOMIC_HELPER.casValue(var0, var4, var5)) {
                     continue label23;
                  }
               }
            } else {
               executeListener(var3, var2.executor);
            }
         }

         return;
      }
   }

   @Beta
   protected void afterDone() {
   }

   final Throwable trustedGetException() {
      return ((AbstractFuture.Failure)this.value).exception;
   }

   final void maybePropagateCancellation(@Nullable Future<?> var1) {
      if (var1 != null & this.isCancelled()) {
         var1.cancel(this.wasInterrupted());
      }

   }

   private void releaseWaiters() {
      AbstractFuture.Waiter var1;
      do {
         var1 = this.waiters;
      } while(!ATOMIC_HELPER.casWaiters(this, var1, AbstractFuture.Waiter.TOMBSTONE));

      for(AbstractFuture.Waiter var2 = var1; var2 != null; var2 = var2.next) {
         var2.unpark();
      }

   }

   private AbstractFuture.Listener clearListeners(AbstractFuture.Listener var1) {
      AbstractFuture.Listener var2;
      do {
         var2 = this.listeners;
      } while(!ATOMIC_HELPER.casListeners(this, var2, AbstractFuture.Listener.TOMBSTONE));

      AbstractFuture.Listener var3;
      AbstractFuture.Listener var4;
      for(var3 = var1; var2 != null; var3 = var4) {
         var4 = var2;
         var2 = var2.next;
         var4.next = var3;
      }

      return var3;
   }

   private static void executeListener(Runnable var0, Executor var1) {
      try {
         var1.execute(var0);
      } catch (RuntimeException var3) {
         log.log(Level.SEVERE, "RuntimeException while executing runnable " + var0 + " with executor " + var1, var3);
      }

   }

   private static CancellationException cancellationExceptionWithCause(@Nullable String var0, @Nullable Throwable var1) {
      CancellationException var2 = new CancellationException(var0);
      var2.initCause(var1);
      return var2;
   }

   static {
      Object var0;
      try {
         var0 = new AbstractFuture.UnsafeAtomicHelper();
      } catch (Throwable var4) {
         try {
            var0 = new AbstractFuture.SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.Waiter.class, Thread.class, "thread"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.Waiter.class, AbstractFuture.Waiter.class, "next"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, AbstractFuture.Waiter.class, "waiters"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, AbstractFuture.Listener.class, "listeners"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Object.class, "value"));
         } catch (Throwable var3) {
            log.log(Level.SEVERE, "UnsafeAtomicHelper is broken!", var4);
            log.log(Level.SEVERE, "SafeAtomicHelper is broken!", var3);
            var0 = new AbstractFuture.SynchronizedHelper();
         }
      }

      ATOMIC_HELPER = (AbstractFuture.AtomicHelper)var0;
      Class var1 = LockSupport.class;
      NULL = new Object();
   }

   private static final class SynchronizedHelper extends AbstractFuture.AtomicHelper {
      private SynchronizedHelper() {
         super(null);
      }

      void putThread(AbstractFuture.Waiter var1, Thread var2) {
         var1.thread = var2;
      }

      void putNext(AbstractFuture.Waiter var1, AbstractFuture.Waiter var2) {
         var1.next = var2;
      }

      boolean casWaiters(AbstractFuture<?> var1, AbstractFuture.Waiter var2, AbstractFuture.Waiter var3) {
         synchronized(var1) {
            if (var1.waiters == var2) {
               var1.waiters = var3;
               return true;
            } else {
               return false;
            }
         }
      }

      boolean casListeners(AbstractFuture<?> var1, AbstractFuture.Listener var2, AbstractFuture.Listener var3) {
         synchronized(var1) {
            if (var1.listeners == var2) {
               var1.listeners = var3;
               return true;
            } else {
               return false;
            }
         }
      }

      boolean casValue(AbstractFuture<?> var1, Object var2, Object var3) {
         synchronized(var1) {
            if (var1.value == var2) {
               var1.value = var3;
               return true;
            } else {
               return false;
            }
         }
      }

      // $FF: synthetic method
      SynchronizedHelper(Object var1) {
         this();
      }
   }

   private static final class SafeAtomicHelper extends AbstractFuture.AtomicHelper {
      final AtomicReferenceFieldUpdater<AbstractFuture.Waiter, Thread> waiterThreadUpdater;
      final AtomicReferenceFieldUpdater<AbstractFuture.Waiter, AbstractFuture.Waiter> waiterNextUpdater;
      final AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Waiter> waitersUpdater;
      final AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Listener> listenersUpdater;
      final AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater;

      SafeAtomicHelper(AtomicReferenceFieldUpdater<AbstractFuture.Waiter, Thread> var1, AtomicReferenceFieldUpdater<AbstractFuture.Waiter, AbstractFuture.Waiter> var2, AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Waiter> var3, AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Listener> var4, AtomicReferenceFieldUpdater<AbstractFuture, Object> var5) {
         super(null);
         this.waiterThreadUpdater = var1;
         this.waiterNextUpdater = var2;
         this.waitersUpdater = var3;
         this.listenersUpdater = var4;
         this.valueUpdater = var5;
      }

      void putThread(AbstractFuture.Waiter var1, Thread var2) {
         this.waiterThreadUpdater.lazySet(var1, var2);
      }

      void putNext(AbstractFuture.Waiter var1, AbstractFuture.Waiter var2) {
         this.waiterNextUpdater.lazySet(var1, var2);
      }

      boolean casWaiters(AbstractFuture<?> var1, AbstractFuture.Waiter var2, AbstractFuture.Waiter var3) {
         return this.waitersUpdater.compareAndSet(var1, var2, var3);
      }

      boolean casListeners(AbstractFuture<?> var1, AbstractFuture.Listener var2, AbstractFuture.Listener var3) {
         return this.listenersUpdater.compareAndSet(var1, var2, var3);
      }

      boolean casValue(AbstractFuture<?> var1, Object var2, Object var3) {
         return this.valueUpdater.compareAndSet(var1, var2, var3);
      }
   }

   private static final class UnsafeAtomicHelper extends AbstractFuture.AtomicHelper {
      static final Unsafe UNSAFE;
      static final long LISTENERS_OFFSET;
      static final long WAITERS_OFFSET;
      static final long VALUE_OFFSET;
      static final long WAITER_THREAD_OFFSET;
      static final long WAITER_NEXT_OFFSET;

      private UnsafeAtomicHelper() {
         super(null);
      }

      void putThread(AbstractFuture.Waiter var1, Thread var2) {
         UNSAFE.putObject(var1, WAITER_THREAD_OFFSET, var2);
      }

      void putNext(AbstractFuture.Waiter var1, AbstractFuture.Waiter var2) {
         UNSAFE.putObject(var1, WAITER_NEXT_OFFSET, var2);
      }

      boolean casWaiters(AbstractFuture<?> var1, AbstractFuture.Waiter var2, AbstractFuture.Waiter var3) {
         return UNSAFE.compareAndSwapObject(var1, WAITERS_OFFSET, var2, var3);
      }

      boolean casListeners(AbstractFuture<?> var1, AbstractFuture.Listener var2, AbstractFuture.Listener var3) {
         return UNSAFE.compareAndSwapObject(var1, LISTENERS_OFFSET, var2, var3);
      }

      boolean casValue(AbstractFuture<?> var1, Object var2, Object var3) {
         return UNSAFE.compareAndSwapObject(var1, VALUE_OFFSET, var2, var3);
      }

      // $FF: synthetic method
      UnsafeAtomicHelper(Object var1) {
         this();
      }

      static {
         Unsafe var0 = null;

         try {
            var0 = Unsafe.getUnsafe();
         } catch (SecurityException var5) {
            try {
               var0 = (Unsafe)AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
                  public Unsafe run() throws Exception {
                     Class var1 = Unsafe.class;
                     Field[] var2 = var1.getDeclaredFields();
                     int var3 = var2.length;

                     for(int var4 = 0; var4 < var3; ++var4) {
                        Field var5 = var2[var4];
                        var5.setAccessible(true);
                        Object var6 = var5.get((Object)null);
                        if (var1.isInstance(var6)) {
                           return (Unsafe)var1.cast(var6);
                        }
                     }

                     throw new NoSuchFieldError("the Unsafe");
                  }
               });
            } catch (PrivilegedActionException var4) {
               throw new RuntimeException("Could not initialize intrinsics", var4.getCause());
            }
         }

         try {
            Class var1 = AbstractFuture.class;
            WAITERS_OFFSET = var0.objectFieldOffset(var1.getDeclaredField("waiters"));
            LISTENERS_OFFSET = var0.objectFieldOffset(var1.getDeclaredField("listeners"));
            VALUE_OFFSET = var0.objectFieldOffset(var1.getDeclaredField("value"));
            WAITER_THREAD_OFFSET = var0.objectFieldOffset(AbstractFuture.Waiter.class.getDeclaredField("thread"));
            WAITER_NEXT_OFFSET = var0.objectFieldOffset(AbstractFuture.Waiter.class.getDeclaredField("next"));
            UNSAFE = var0;
         } catch (Exception var3) {
            Throwables.throwIfUnchecked(var3);
            throw new RuntimeException(var3);
         }
      }
   }

   private abstract static class AtomicHelper {
      private AtomicHelper() {
         super();
      }

      abstract void putThread(AbstractFuture.Waiter var1, Thread var2);

      abstract void putNext(AbstractFuture.Waiter var1, AbstractFuture.Waiter var2);

      abstract boolean casWaiters(AbstractFuture<?> var1, AbstractFuture.Waiter var2, AbstractFuture.Waiter var3);

      abstract boolean casListeners(AbstractFuture<?> var1, AbstractFuture.Listener var2, AbstractFuture.Listener var3);

      abstract boolean casValue(AbstractFuture<?> var1, Object var2, Object var3);

      // $FF: synthetic method
      AtomicHelper(Object var1) {
         this();
      }
   }

   private static final class SetFuture<V> implements Runnable {
      final AbstractFuture<V> owner;
      final ListenableFuture<? extends V> future;

      SetFuture(AbstractFuture<V> var1, ListenableFuture<? extends V> var2) {
         super();
         this.owner = var1;
         this.future = var2;
      }

      public void run() {
         if (this.owner.value == this) {
            Object var1 = AbstractFuture.getFutureValue(this.future);
            if (AbstractFuture.ATOMIC_HELPER.casValue(this.owner, this, var1)) {
               AbstractFuture.complete(this.owner);
            }

         }
      }
   }

   private static final class Cancellation {
      final boolean wasInterrupted;
      @Nullable
      final Throwable cause;

      Cancellation(boolean var1, @Nullable Throwable var2) {
         super();
         this.wasInterrupted = var1;
         this.cause = var2;
      }
   }

   private static final class Failure {
      static final AbstractFuture.Failure FALLBACK_INSTANCE = new AbstractFuture.Failure(new Throwable("Failure occurred while trying to finish a future.") {
         public synchronized Throwable fillInStackTrace() {
            return this;
         }
      });
      final Throwable exception;

      Failure(Throwable var1) {
         super();
         this.exception = (Throwable)Preconditions.checkNotNull(var1);
      }
   }

   private static final class Listener {
      static final AbstractFuture.Listener TOMBSTONE = new AbstractFuture.Listener((Runnable)null, (Executor)null);
      final Runnable task;
      final Executor executor;
      @Nullable
      AbstractFuture.Listener next;

      Listener(Runnable var1, Executor var2) {
         super();
         this.task = var1;
         this.executor = var2;
      }
   }

   private static final class Waiter {
      static final AbstractFuture.Waiter TOMBSTONE = new AbstractFuture.Waiter(false);
      @Nullable
      volatile Thread thread;
      @Nullable
      volatile AbstractFuture.Waiter next;

      Waiter(boolean var1) {
         super();
      }

      Waiter() {
         super();
         AbstractFuture.ATOMIC_HELPER.putThread(this, Thread.currentThread());
      }

      void setNext(AbstractFuture.Waiter var1) {
         AbstractFuture.ATOMIC_HELPER.putNext(this, var1);
      }

      void unpark() {
         Thread var1 = this.thread;
         if (var1 != null) {
            this.thread = null;
            LockSupport.unpark(var1);
         }

      }
   }

   abstract static class TrustedFuture<V> extends AbstractFuture<V> {
      TrustedFuture() {
         super();
      }

      @CanIgnoreReturnValue
      public final V get() throws InterruptedException, ExecutionException {
         return super.get();
      }

      @CanIgnoreReturnValue
      public final V get(long var1, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException {
         return super.get(var1, var3);
      }

      public final boolean isDone() {
         return super.isDone();
      }

      public final boolean isCancelled() {
         return super.isCancelled();
      }

      public final void addListener(Runnable var1, Executor var2) {
         super.addListener(var1, var2);
      }

      @CanIgnoreReturnValue
      public final boolean cancel(boolean var1) {
         return super.cancel(var1);
      }
   }
}
