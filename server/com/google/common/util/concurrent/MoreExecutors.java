package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.concurrent.GuardedBy;

@GwtCompatible(
   emulated = true
)
public final class MoreExecutors {
   private MoreExecutors() {
      super();
   }

   @Beta
   @GwtIncompatible
   public static ExecutorService getExitingExecutorService(ThreadPoolExecutor var0, long var1, TimeUnit var3) {
      return (new MoreExecutors.Application()).getExitingExecutorService(var0, var1, var3);
   }

   @Beta
   @GwtIncompatible
   public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor var0, long var1, TimeUnit var3) {
      return (new MoreExecutors.Application()).getExitingScheduledExecutorService(var0, var1, var3);
   }

   @Beta
   @GwtIncompatible
   public static void addDelayedShutdownHook(ExecutorService var0, long var1, TimeUnit var3) {
      (new MoreExecutors.Application()).addDelayedShutdownHook(var0, var1, var3);
   }

   @Beta
   @GwtIncompatible
   public static ExecutorService getExitingExecutorService(ThreadPoolExecutor var0) {
      return (new MoreExecutors.Application()).getExitingExecutorService(var0);
   }

   @Beta
   @GwtIncompatible
   public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor var0) {
      return (new MoreExecutors.Application()).getExitingScheduledExecutorService(var0);
   }

   @GwtIncompatible
   private static void useDaemonThreadFactory(ThreadPoolExecutor var0) {
      var0.setThreadFactory((new ThreadFactoryBuilder()).setDaemon(true).setThreadFactory(var0.getThreadFactory()).build());
   }

   @GwtIncompatible
   public static ListeningExecutorService newDirectExecutorService() {
      return new MoreExecutors.DirectExecutorService();
   }

   public static Executor directExecutor() {
      return MoreExecutors.DirectExecutor.INSTANCE;
   }

   @GwtIncompatible
   public static ListeningExecutorService listeningDecorator(ExecutorService var0) {
      return (ListeningExecutorService)(var0 instanceof ListeningExecutorService ? (ListeningExecutorService)var0 : (var0 instanceof ScheduledExecutorService ? new MoreExecutors.ScheduledListeningDecorator((ScheduledExecutorService)var0) : new MoreExecutors.ListeningDecorator(var0)));
   }

   @GwtIncompatible
   public static ListeningScheduledExecutorService listeningDecorator(ScheduledExecutorService var0) {
      return (ListeningScheduledExecutorService)(var0 instanceof ListeningScheduledExecutorService ? (ListeningScheduledExecutorService)var0 : new MoreExecutors.ScheduledListeningDecorator(var0));
   }

   @GwtIncompatible
   static <T> T invokeAnyImpl(ListeningExecutorService var0, Collection<? extends Callable<T>> var1, boolean var2, long var3, TimeUnit var5) throws InterruptedException, ExecutionException, TimeoutException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var5);
      int var6 = var1.size();
      Preconditions.checkArgument(var6 > 0);
      ArrayList var7 = Lists.newArrayListWithCapacity(var6);
      LinkedBlockingQueue var8 = Queues.newLinkedBlockingQueue();
      long var9 = var5.toNanos(var3);
      boolean var26 = false;

      Object var30;
      try {
         var26 = true;
         ExecutionException var11 = null;
         long var12 = var2 ? System.nanoTime() : 0L;
         Iterator var14 = var1.iterator();
         var7.add(submitAndAddQueueListener(var0, (Callable)var14.next(), var8));
         --var6;
         int var15 = 1;

         while(true) {
            Future var16 = (Future)var8.poll();
            if (var16 == null) {
               if (var6 > 0) {
                  --var6;
                  var7.add(submitAndAddQueueListener(var0, (Callable)var14.next(), var8));
                  ++var15;
               } else {
                  if (var15 == 0) {
                     if (var11 == null) {
                        var11 = new ExecutionException((Throwable)null);
                     }

                     throw var11;
                  }

                  if (var2) {
                     var16 = (Future)var8.poll(var9, TimeUnit.NANOSECONDS);
                     if (var16 == null) {
                        throw new TimeoutException();
                     }

                     long var17 = System.nanoTime();
                     var9 -= var17 - var12;
                     var12 = var17;
                  } else {
                     var16 = (Future)var8.take();
                  }
               }
            }

            if (var16 != null) {
               --var15;

               try {
                  var30 = var16.get();
                  var26 = false;
                  break;
               } catch (ExecutionException var27) {
                  var11 = var27;
               } catch (RuntimeException var28) {
                  var11 = new ExecutionException(var28);
               }
            }
         }
      } finally {
         if (var26) {
            Iterator var21 = var7.iterator();

            while(var21.hasNext()) {
               Future var22 = (Future)var21.next();
               var22.cancel(true);
            }

         }
      }

      Iterator var18 = var7.iterator();

      while(var18.hasNext()) {
         Future var19 = (Future)var18.next();
         var19.cancel(true);
      }

      return var30;
   }

   @GwtIncompatible
   private static <T> ListenableFuture<T> submitAndAddQueueListener(ListeningExecutorService var0, Callable<T> var1, final BlockingQueue<Future<T>> var2) {
      final ListenableFuture var3 = var0.submit(var1);
      var3.addListener(new Runnable() {
         public void run() {
            var2.add(var3);
         }
      }, directExecutor());
      return var3;
   }

   @Beta
   @GwtIncompatible
   public static ThreadFactory platformThreadFactory() {
      if (!isAppEngine()) {
         return Executors.defaultThreadFactory();
      } else {
         try {
            return (ThreadFactory)Class.forName("com.google.appengine.api.ThreadManager").getMethod("currentRequestThreadFactory").invoke((Object)null);
         } catch (IllegalAccessException var1) {
            throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", var1);
         } catch (ClassNotFoundException var2) {
            throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", var2);
         } catch (NoSuchMethodException var3) {
            throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", var3);
         } catch (InvocationTargetException var4) {
            throw Throwables.propagate(var4.getCause());
         }
      }
   }

   @GwtIncompatible
   private static boolean isAppEngine() {
      if (System.getProperty("com.google.appengine.runtime.environment") == null) {
         return false;
      } else {
         try {
            return Class.forName("com.google.apphosting.api.ApiProxy").getMethod("getCurrentEnvironment").invoke((Object)null) != null;
         } catch (ClassNotFoundException var1) {
            return false;
         } catch (InvocationTargetException var2) {
            return false;
         } catch (IllegalAccessException var3) {
            return false;
         } catch (NoSuchMethodException var4) {
            return false;
         }
      }
   }

   @GwtIncompatible
   static Thread newThread(String var0, Runnable var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Thread var2 = platformThreadFactory().newThread(var1);

      try {
         var2.setName(var0);
      } catch (SecurityException var4) {
      }

      return var2;
   }

   @GwtIncompatible
   static Executor renamingDecorator(final Executor var0, final Supplier<String> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return isAppEngine() ? var0 : new Executor() {
         public void execute(Runnable var1x) {
            var0.execute(Callables.threadRenaming(var1x, var1));
         }
      };
   }

   @GwtIncompatible
   static ExecutorService renamingDecorator(ExecutorService var0, final Supplier<String> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return (ExecutorService)(isAppEngine() ? var0 : new WrappingExecutorService(var0) {
         protected <T> Callable<T> wrapTask(Callable<T> var1x) {
            return Callables.threadRenaming(var1x, var1);
         }

         protected Runnable wrapTask(Runnable var1x) {
            return Callables.threadRenaming(var1x, var1);
         }
      });
   }

   @GwtIncompatible
   static ScheduledExecutorService renamingDecorator(ScheduledExecutorService var0, final Supplier<String> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return (ScheduledExecutorService)(isAppEngine() ? var0 : new WrappingScheduledExecutorService(var0) {
         protected <T> Callable<T> wrapTask(Callable<T> var1x) {
            return Callables.threadRenaming(var1x, var1);
         }

         protected Runnable wrapTask(Runnable var1x) {
            return Callables.threadRenaming(var1x, var1);
         }
      });
   }

   @Beta
   @CanIgnoreReturnValue
   @GwtIncompatible
   public static boolean shutdownAndAwaitTermination(ExecutorService var0, long var1, TimeUnit var3) {
      long var4 = var3.toNanos(var1) / 2L;
      var0.shutdown();

      try {
         if (!var0.awaitTermination(var4, TimeUnit.NANOSECONDS)) {
            var0.shutdownNow();
            var0.awaitTermination(var4, TimeUnit.NANOSECONDS);
         }
      } catch (InterruptedException var7) {
         Thread.currentThread().interrupt();
         var0.shutdownNow();
      }

      return var0.isTerminated();
   }

   static Executor rejectionPropagatingExecutor(final Executor var0, final AbstractFuture<?> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return var0 == directExecutor() ? var0 : new Executor() {
         volatile boolean thrownFromDelegate = true;

         public void execute(final Runnable var1x) {
            try {
               var0.execute(new Runnable() {
                  public void run() {
                     thrownFromDelegate = false;
                     var1x.run();
                  }
               });
            } catch (RejectedExecutionException var3) {
               if (this.thrownFromDelegate) {
                  var1.setException(var3);
               }
            }

         }
      };
   }

   @GwtIncompatible
   private static final class ScheduledListeningDecorator extends MoreExecutors.ListeningDecorator implements ListeningScheduledExecutorService {
      final ScheduledExecutorService delegate;

      ScheduledListeningDecorator(ScheduledExecutorService var1) {
         super(var1);
         this.delegate = (ScheduledExecutorService)Preconditions.checkNotNull(var1);
      }

      public ListenableScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
         TrustedListenableFutureTask var5 = TrustedListenableFutureTask.create(var1, (Object)null);
         ScheduledFuture var6 = this.delegate.schedule(var5, var2, var4);
         return new MoreExecutors.ScheduledListeningDecorator.ListenableScheduledTask(var5, var6);
      }

      public <V> ListenableScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
         TrustedListenableFutureTask var5 = TrustedListenableFutureTask.create(var1);
         ScheduledFuture var6 = this.delegate.schedule(var5, var2, var4);
         return new MoreExecutors.ScheduledListeningDecorator.ListenableScheduledTask(var5, var6);
      }

      public ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
         MoreExecutors.ScheduledListeningDecorator.NeverSuccessfulListenableFutureTask var7 = new MoreExecutors.ScheduledListeningDecorator.NeverSuccessfulListenableFutureTask(var1);
         ScheduledFuture var8 = this.delegate.scheduleAtFixedRate(var7, var2, var4, var6);
         return new MoreExecutors.ScheduledListeningDecorator.ListenableScheduledTask(var7, var8);
      }

      public ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
         MoreExecutors.ScheduledListeningDecorator.NeverSuccessfulListenableFutureTask var7 = new MoreExecutors.ScheduledListeningDecorator.NeverSuccessfulListenableFutureTask(var1);
         ScheduledFuture var8 = this.delegate.scheduleWithFixedDelay(var7, var2, var4, var6);
         return new MoreExecutors.ScheduledListeningDecorator.ListenableScheduledTask(var7, var8);
      }

      @GwtIncompatible
      private static final class NeverSuccessfulListenableFutureTask extends AbstractFuture<Void> implements Runnable {
         private final Runnable delegate;

         public NeverSuccessfulListenableFutureTask(Runnable var1) {
            super();
            this.delegate = (Runnable)Preconditions.checkNotNull(var1);
         }

         public void run() {
            try {
               this.delegate.run();
            } catch (Throwable var2) {
               this.setException(var2);
               throw Throwables.propagate(var2);
            }
         }
      }

      private static final class ListenableScheduledTask<V> extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V> implements ListenableScheduledFuture<V> {
         private final ScheduledFuture<?> scheduledDelegate;

         public ListenableScheduledTask(ListenableFuture<V> var1, ScheduledFuture<?> var2) {
            super(var1);
            this.scheduledDelegate = var2;
         }

         public boolean cancel(boolean var1) {
            boolean var2 = super.cancel(var1);
            if (var2) {
               this.scheduledDelegate.cancel(var1);
            }

            return var2;
         }

         public long getDelay(TimeUnit var1) {
            return this.scheduledDelegate.getDelay(var1);
         }

         public int compareTo(Delayed var1) {
            return this.scheduledDelegate.compareTo(var1);
         }
      }
   }

   @GwtIncompatible
   private static class ListeningDecorator extends AbstractListeningExecutorService {
      private final ExecutorService delegate;

      ListeningDecorator(ExecutorService var1) {
         super();
         this.delegate = (ExecutorService)Preconditions.checkNotNull(var1);
      }

      public final boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
         return this.delegate.awaitTermination(var1, var3);
      }

      public final boolean isShutdown() {
         return this.delegate.isShutdown();
      }

      public final boolean isTerminated() {
         return this.delegate.isTerminated();
      }

      public final void shutdown() {
         this.delegate.shutdown();
      }

      public final List<Runnable> shutdownNow() {
         return this.delegate.shutdownNow();
      }

      public final void execute(Runnable var1) {
         this.delegate.execute(var1);
      }
   }

   private static enum DirectExecutor implements Executor {
      INSTANCE;

      private DirectExecutor() {
      }

      public void execute(Runnable var1) {
         var1.run();
      }

      public String toString() {
         return "MoreExecutors.directExecutor()";
      }
   }

   @GwtIncompatible
   private static final class DirectExecutorService extends AbstractListeningExecutorService {
      private final Object lock;
      @GuardedBy("lock")
      private int runningTasks;
      @GuardedBy("lock")
      private boolean shutdown;

      private DirectExecutorService() {
         super();
         this.lock = new Object();
         this.runningTasks = 0;
         this.shutdown = false;
      }

      public void execute(Runnable var1) {
         this.startTask();

         try {
            var1.run();
         } finally {
            this.endTask();
         }

      }

      public boolean isShutdown() {
         synchronized(this.lock) {
            return this.shutdown;
         }
      }

      public void shutdown() {
         synchronized(this.lock) {
            this.shutdown = true;
            if (this.runningTasks == 0) {
               this.lock.notifyAll();
            }

         }
      }

      public List<Runnable> shutdownNow() {
         this.shutdown();
         return Collections.emptyList();
      }

      public boolean isTerminated() {
         synchronized(this.lock) {
            return this.shutdown && this.runningTasks == 0;
         }
      }

      public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
         long var4 = var3.toNanos(var1);
         synchronized(this.lock) {
            while(!this.shutdown || this.runningTasks != 0) {
               if (var4 <= 0L) {
                  return false;
               }

               long var7 = System.nanoTime();
               TimeUnit.NANOSECONDS.timedWait(this.lock, var4);
               var4 -= System.nanoTime() - var7;
            }

            return true;
         }
      }

      private void startTask() {
         synchronized(this.lock) {
            if (this.shutdown) {
               throw new RejectedExecutionException("Executor already shutdown");
            } else {
               ++this.runningTasks;
            }
         }
      }

      private void endTask() {
         synchronized(this.lock) {
            int var2 = --this.runningTasks;
            if (var2 == 0) {
               this.lock.notifyAll();
            }

         }
      }

      // $FF: synthetic method
      DirectExecutorService(Object var1) {
         this();
      }
   }

   @GwtIncompatible
   @VisibleForTesting
   static class Application {
      Application() {
         super();
      }

      final ExecutorService getExitingExecutorService(ThreadPoolExecutor var1, long var2, TimeUnit var4) {
         MoreExecutors.useDaemonThreadFactory(var1);
         ExecutorService var5 = Executors.unconfigurableExecutorService(var1);
         this.addDelayedShutdownHook(var5, var2, var4);
         return var5;
      }

      final ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor var1, long var2, TimeUnit var4) {
         MoreExecutors.useDaemonThreadFactory(var1);
         ScheduledExecutorService var5 = Executors.unconfigurableScheduledExecutorService(var1);
         this.addDelayedShutdownHook(var5, var2, var4);
         return var5;
      }

      final void addDelayedShutdownHook(final ExecutorService var1, final long var2, final TimeUnit var4) {
         Preconditions.checkNotNull(var1);
         Preconditions.checkNotNull(var4);
         this.addShutdownHook(MoreExecutors.newThread("DelayedShutdownHook-for-" + var1, new Runnable() {
            public void run() {
               try {
                  var1.shutdown();
                  var1.awaitTermination(var2, var4);
               } catch (InterruptedException var2x) {
               }

            }
         }));
      }

      final ExecutorService getExitingExecutorService(ThreadPoolExecutor var1) {
         return this.getExitingExecutorService(var1, 120L, TimeUnit.SECONDS);
      }

      final ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor var1) {
         return this.getExitingScheduledExecutorService(var1, 120L, TimeUnit.SECONDS);
      }

      @VisibleForTesting
      void addShutdownHook(Thread var1) {
         Runtime.getRuntime().addShutdownHook(var1);
      }
   }
}
