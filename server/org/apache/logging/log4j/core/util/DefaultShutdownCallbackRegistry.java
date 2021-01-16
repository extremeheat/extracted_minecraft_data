package org.apache.logging.log4j.core.util;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.status.StatusLogger;

public class DefaultShutdownCallbackRegistry implements ShutdownCallbackRegistry, LifeCycle2, Runnable {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   private final AtomicReference<LifeCycle.State> state;
   private final ThreadFactory threadFactory;
   private final Collection<Cancellable> hooks;
   private Reference<Thread> shutdownHookRef;

   public DefaultShutdownCallbackRegistry() {
      this(Executors.defaultThreadFactory());
   }

   protected DefaultShutdownCallbackRegistry(ThreadFactory var1) {
      super();
      this.state = new AtomicReference(LifeCycle.State.INITIALIZED);
      this.hooks = new CopyOnWriteArrayList();
      this.threadFactory = var1;
   }

   public void run() {
      if (this.state.compareAndSet(LifeCycle.State.STARTED, LifeCycle.State.STOPPING)) {
         Iterator var1 = this.hooks.iterator();

         while(var1.hasNext()) {
            Runnable var2 = (Runnable)var1.next();

            try {
               var2.run();
            } catch (Throwable var6) {
               Throwable var3 = var6;

               try {
                  LOGGER.error((Marker)SHUTDOWN_HOOK_MARKER, (String)"Caught exception executing shutdown hook {}", var2, var3);
               } catch (Throwable var5) {
                  System.err.println("Caught exception " + var5.getClass() + " logging exception " + var6.getClass());
                  var6.printStackTrace();
               }
            }
         }

         this.state.set(LifeCycle.State.STOPPED);
      }

   }

   public Cancellable addShutdownCallback(Runnable var1) {
      if (this.isStarted()) {
         DefaultShutdownCallbackRegistry.RegisteredCancellable var2 = new DefaultShutdownCallbackRegistry.RegisteredCancellable(var1, this.hooks);
         this.hooks.add(var2);
         return var2;
      } else {
         throw new IllegalStateException("Cannot add new shutdown hook as this is not started. Current state: " + ((LifeCycle.State)this.state.get()).name());
      }
   }

   public void initialize() {
   }

   public void start() {
      if (this.state.compareAndSet(LifeCycle.State.INITIALIZED, LifeCycle.State.STARTING)) {
         try {
            this.addShutdownHook(this.threadFactory.newThread(this));
            this.state.set(LifeCycle.State.STARTED);
         } catch (IllegalStateException var2) {
            this.state.set(LifeCycle.State.STOPPED);
            throw var2;
         } catch (Exception var3) {
            LOGGER.catching(var3);
            this.state.set(LifeCycle.State.STOPPED);
         }
      }

   }

   private void addShutdownHook(Thread var1) {
      this.shutdownHookRef = new WeakReference(var1);
      Runtime.getRuntime().addShutdownHook(var1);
   }

   public void stop() {
      this.stop(0L, AbstractLifeCycle.DEFAULT_STOP_TIMEUNIT);
   }

   public boolean stop(long var1, TimeUnit var3) {
      if (this.state.compareAndSet(LifeCycle.State.STARTED, LifeCycle.State.STOPPING)) {
         try {
            this.removeShutdownHook();
         } finally {
            this.state.set(LifeCycle.State.STOPPED);
         }
      }

      return true;
   }

   private void removeShutdownHook() {
      Thread var1 = (Thread)this.shutdownHookRef.get();
      if (var1 != null) {
         Runtime.getRuntime().removeShutdownHook(var1);
         this.shutdownHookRef.enqueue();
      }

   }

   public LifeCycle.State getState() {
      return (LifeCycle.State)this.state.get();
   }

   public boolean isStarted() {
      return this.state.get() == LifeCycle.State.STARTED;
   }

   public boolean isStopped() {
      return this.state.get() == LifeCycle.State.STOPPED;
   }

   private static class RegisteredCancellable implements Cancellable {
      private final Reference<Runnable> hook;
      private Collection<Cancellable> registered;

      RegisteredCancellable(Runnable var1, Collection<Cancellable> var2) {
         super();
         this.registered = var2;
         this.hook = new SoftReference(var1);
      }

      public void cancel() {
         this.hook.clear();
         this.registered.remove(this);
         this.registered = null;
      }

      public void run() {
         Runnable var1 = (Runnable)this.hook.get();
         if (var1 != null) {
            var1.run();
            this.hook.clear();
         }

      }

      public String toString() {
         return String.valueOf(this.hook.get());
      }
   }
}
