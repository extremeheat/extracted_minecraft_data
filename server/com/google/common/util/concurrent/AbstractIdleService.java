package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Supplier;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
@GwtIncompatible
public abstract class AbstractIdleService implements Service {
   private final Supplier<String> threadNameSupplier = new AbstractIdleService.ThreadNameSupplier();
   private final Service delegate = new AbstractIdleService.DelegateService();

   protected AbstractIdleService() {
      super();
   }

   protected abstract void startUp() throws Exception;

   protected abstract void shutDown() throws Exception;

   protected Executor executor() {
      return new Executor() {
         public void execute(Runnable var1) {
            MoreExecutors.newThread((String)AbstractIdleService.this.threadNameSupplier.get(), var1).start();
         }
      };
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

   protected String serviceName() {
      return this.getClass().getSimpleName();
   }

   private final class DelegateService extends AbstractService {
      private DelegateService() {
         super();
      }

      protected final void doStart() {
         MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier).execute(new Runnable() {
            public void run() {
               try {
                  AbstractIdleService.this.startUp();
                  DelegateService.this.notifyStarted();
               } catch (Throwable var2) {
                  DelegateService.this.notifyFailed(var2);
               }

            }
         });
      }

      protected final void doStop() {
         MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier).execute(new Runnable() {
            public void run() {
               try {
                  AbstractIdleService.this.shutDown();
                  DelegateService.this.notifyStopped();
               } catch (Throwable var2) {
                  DelegateService.this.notifyFailed(var2);
               }

            }
         });
      }

      public String toString() {
         return AbstractIdleService.this.toString();
      }

      // $FF: synthetic method
      DelegateService(Object var2) {
         this();
      }
   }

   private final class ThreadNameSupplier implements Supplier<String> {
      private ThreadNameSupplier() {
         super();
      }

      public String get() {
         return AbstractIdleService.this.serviceName() + " " + AbstractIdleService.this.state();
      }

      // $FF: synthetic method
      ThreadNameSupplier(Object var2) {
         this();
      }
   }
}
