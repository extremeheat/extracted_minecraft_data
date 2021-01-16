package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

@Beta
@GwtIncompatible
public abstract class AbstractScheduledService implements Service {
   private static final Logger logger = Logger.getLogger(AbstractScheduledService.class.getName());
   private final AbstractService delegate = new AbstractScheduledService.ServiceDelegate();

   protected AbstractScheduledService() {
      super();
   }

   protected abstract void runOneIteration() throws Exception;

   protected void startUp() throws Exception {
   }

   protected void shutDown() throws Exception {
   }

   protected abstract AbstractScheduledService.Scheduler scheduler();

   protected ScheduledExecutorService executor() {
      class 1ThreadFactoryImpl implements ThreadFactory {
         _ThreadFactoryImpl/* $FF was: 1ThreadFactoryImpl*/() {
            super();
         }

         public Thread newThread(Runnable var1) {
            return MoreExecutors.newThread(AbstractScheduledService.this.serviceName(), var1);
         }
      }

      final ScheduledExecutorService var1 = Executors.newSingleThreadScheduledExecutor(new 1ThreadFactoryImpl());
      this.addListener(new Service.Listener() {
         public void terminated(Service.State var1x) {
            var1.shutdown();
         }

         public void failed(Service.State var1x, Throwable var2) {
            var1.shutdown();
         }
      }, MoreExecutors.directExecutor());
      return var1;
   }

   protected String serviceName() {
      return this.getClass().getSimpleName();
   }

   public String toString() {
      return this.serviceName() + " [" + this.state() + "]";
   }

   public final boolean isRunning() {
      return this.delegate.isRunning();
   }

   public final Service.State state() {
      return this.delegate.state();
   }

   public final void addListener(Service.Listener var1, Executor var2) {
      this.delegate.addListener(var1, var2);
   }

   public final Throwable failureCause() {
      return this.delegate.failureCause();
   }

   @CanIgnoreReturnValue
   public final Service startAsync() {
      this.delegate.startAsync();
      return this;
   }

   @CanIgnoreReturnValue
   public final Service stopAsync() {
      this.delegate.stopAsync();
      return this;
   }

   public final void awaitRunning() {
      this.delegate.awaitRunning();
   }

   public final void awaitRunning(long var1, TimeUnit var3) throws TimeoutException {
      this.delegate.awaitRunning(var1, var3);
   }

   public final void awaitTerminated() {
      this.delegate.awaitTerminated();
   }

   public final void awaitTerminated(long var1, TimeUnit var3) throws TimeoutException {
      this.delegate.awaitTerminated(var1, var3);
   }

   @Beta
   public abstract static class CustomScheduler extends AbstractScheduledService.Scheduler {
      public CustomScheduler() {
         super(null);
      }

      final Future<?> schedule(AbstractService var1, ScheduledExecutorService var2, Runnable var3) {
         AbstractScheduledService.CustomScheduler.ReschedulableCallable var4 = new AbstractScheduledService.CustomScheduler.ReschedulableCallable(var1, var2, var3);
         var4.reschedule();
         return var4;
      }

      protected abstract AbstractScheduledService.CustomScheduler.Schedule getNextSchedule() throws Exception;

      @Beta
      protected static final class Schedule {
         private final long delay;
         private final TimeUnit unit;

         public Schedule(long var1, TimeUnit var3) {
            super();
            this.delay = var1;
            this.unit = (TimeUnit)Preconditions.checkNotNull(var3);
         }
      }

      private class ReschedulableCallable extends ForwardingFuture<Void> implements Callable<Void> {
         private final Runnable wrappedRunnable;
         private final ScheduledExecutorService executor;
         private final AbstractService service;
         private final ReentrantLock lock = new ReentrantLock();
         @GuardedBy("lock")
         private Future<Void> currentFuture;

         ReschedulableCallable(AbstractService var2, ScheduledExecutorService var3, Runnable var4) {
            super();
            this.wrappedRunnable = var4;
            this.executor = var3;
            this.service = var2;
         }

         public Void call() throws Exception {
            this.wrappedRunnable.run();
            this.reschedule();
            return null;
         }

         public void reschedule() {
            AbstractScheduledService.CustomScheduler.Schedule var1;
            try {
               var1 = CustomScheduler.this.getNextSchedule();
            } catch (Throwable var8) {
               this.service.notifyFailed(var8);
               return;
            }

            Throwable var2 = null;
            this.lock.lock();

            try {
               if (this.currentFuture == null || !this.currentFuture.isCancelled()) {
                  this.currentFuture = this.executor.schedule(this, var1.delay, var1.unit);
               }
            } catch (Throwable var9) {
               var2 = var9;
            } finally {
               this.lock.unlock();
            }

            if (var2 != null) {
               this.service.notifyFailed(var2);
            }

         }

         public boolean cancel(boolean var1) {
            this.lock.lock();

            boolean var2;
            try {
               var2 = this.currentFuture.cancel(var1);
            } finally {
               this.lock.unlock();
            }

            return var2;
         }

         public boolean isCancelled() {
            this.lock.lock();

            boolean var1;
            try {
               var1 = this.currentFuture.isCancelled();
            } finally {
               this.lock.unlock();
            }

            return var1;
         }

         protected Future<Void> delegate() {
            throw new UnsupportedOperationException("Only cancel and isCancelled is supported by this future");
         }
      }
   }

   private final class ServiceDelegate extends AbstractService {
      private volatile Future<?> runningTask;
      private volatile ScheduledExecutorService executorService;
      private final ReentrantLock lock;
      private final Runnable task;

      private ServiceDelegate() {
         super();
         this.lock = new ReentrantLock();
         this.task = new AbstractScheduledService.ServiceDelegate.Task();
      }

      protected final void doStart() {
         this.executorService = MoreExecutors.renamingDecorator(AbstractScheduledService.this.executor(), new Supplier<String>() {
            public String get() {
               return AbstractScheduledService.this.serviceName() + " " + ServiceDelegate.this.state();
            }
         });
         this.executorService.execute(new Runnable() {
            public void run() {
               ServiceDelegate.this.lock.lock();

               try {
                  AbstractScheduledService.this.startUp();
                  ServiceDelegate.this.runningTask = AbstractScheduledService.this.scheduler().schedule(AbstractScheduledService.this.delegate, ServiceDelegate.this.executorService, ServiceDelegate.this.task);
                  ServiceDelegate.this.notifyStarted();
               } catch (Throwable var5) {
                  ServiceDelegate.this.notifyFailed(var5);
                  if (ServiceDelegate.this.runningTask != null) {
                     ServiceDelegate.this.runningTask.cancel(false);
                  }
               } finally {
                  ServiceDelegate.this.lock.unlock();
               }

            }
         });
      }

      protected final void doStop() {
         this.runningTask.cancel(false);
         this.executorService.execute(new Runnable() {
            public void run() {
               try {
                  ServiceDelegate.this.lock.lock();

                  label49: {
                     try {
                        if (ServiceDelegate.this.state() == Service.State.STOPPING) {
                           AbstractScheduledService.this.shutDown();
                           break label49;
                        }
                     } finally {
                        ServiceDelegate.this.lock.unlock();
                     }

                     return;
                  }

                  ServiceDelegate.this.notifyStopped();
               } catch (Throwable var5) {
                  ServiceDelegate.this.notifyFailed(var5);
               }

            }
         });
      }

      public String toString() {
         return AbstractScheduledService.this.toString();
      }

      // $FF: synthetic method
      ServiceDelegate(Object var2) {
         this();
      }

      class Task implements Runnable {
         Task() {
            super();
         }

         public void run() {
            ServiceDelegate.this.lock.lock();

            try {
               if (ServiceDelegate.this.runningTask.isCancelled()) {
                  return;
               }

               AbstractScheduledService.this.runOneIteration();
            } catch (Throwable var8) {
               try {
                  AbstractScheduledService.this.shutDown();
               } catch (Exception var7) {
                  AbstractScheduledService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", var7);
               }

               ServiceDelegate.this.notifyFailed(var8);
               ServiceDelegate.this.runningTask.cancel(false);
            } finally {
               ServiceDelegate.this.lock.unlock();
            }

         }
      }
   }

   public abstract static class Scheduler {
      public static AbstractScheduledService.Scheduler newFixedDelaySchedule(final long var0, final long var2, final TimeUnit var4) {
         Preconditions.checkNotNull(var4);
         Preconditions.checkArgument(var2 > 0L, "delay must be > 0, found %s", var2);
         return new AbstractScheduledService.Scheduler() {
            public Future<?> schedule(AbstractService var1, ScheduledExecutorService var2x, Runnable var3) {
               return var2x.scheduleWithFixedDelay(var3, var0, var2, var4);
            }
         };
      }

      public static AbstractScheduledService.Scheduler newFixedRateSchedule(final long var0, final long var2, final TimeUnit var4) {
         Preconditions.checkNotNull(var4);
         Preconditions.checkArgument(var2 > 0L, "period must be > 0, found %s", var2);
         return new AbstractScheduledService.Scheduler() {
            public Future<?> schedule(AbstractService var1, ScheduledExecutorService var2x, Runnable var3) {
               return var2x.scheduleAtFixedRate(var3, var0, var2, var4);
            }
         };
      }

      abstract Future<?> schedule(AbstractService var1, ScheduledExecutorService var2, Runnable var3);

      private Scheduler() {
         super();
      }

      // $FF: synthetic method
      Scheduler(Object var1) {
         this();
      }
   }
}
