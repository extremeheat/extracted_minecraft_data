package org.apache.commons.lang3.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class BackgroundInitializer<T> implements ConcurrentInitializer<T> {
   private ExecutorService externalExecutor;
   private ExecutorService executor;
   private Future<T> future;

   protected BackgroundInitializer() {
      this((ExecutorService)null);
   }

   protected BackgroundInitializer(ExecutorService var1) {
      super();
      this.setExternalExecutor(var1);
   }

   public final synchronized ExecutorService getExternalExecutor() {
      return this.externalExecutor;
   }

   public synchronized boolean isStarted() {
      return this.future != null;
   }

   public final synchronized void setExternalExecutor(ExecutorService var1) {
      if (this.isStarted()) {
         throw new IllegalStateException("Cannot set ExecutorService after start()!");
      } else {
         this.externalExecutor = var1;
      }
   }

   public synchronized boolean start() {
      if (!this.isStarted()) {
         this.executor = this.getExternalExecutor();
         ExecutorService var1;
         if (this.executor == null) {
            this.executor = var1 = this.createExecutor();
         } else {
            var1 = null;
         }

         this.future = this.executor.submit(this.createTask(var1));
         return true;
      } else {
         return false;
      }
   }

   public T get() throws ConcurrentException {
      try {
         return this.getFuture().get();
      } catch (ExecutionException var2) {
         ConcurrentUtils.handleCause(var2);
         return null;
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
         throw new ConcurrentException(var3);
      }
   }

   public synchronized Future<T> getFuture() {
      if (this.future == null) {
         throw new IllegalStateException("start() must be called first!");
      } else {
         return this.future;
      }
   }

   protected final synchronized ExecutorService getActiveExecutor() {
      return this.executor;
   }

   protected int getTaskCount() {
      return 1;
   }

   protected abstract T initialize() throws Exception;

   private Callable<T> createTask(ExecutorService var1) {
      return new BackgroundInitializer.InitializationTask(var1);
   }

   private ExecutorService createExecutor() {
      return Executors.newFixedThreadPool(this.getTaskCount());
   }

   private class InitializationTask implements Callable<T> {
      private final ExecutorService execFinally;

      public InitializationTask(ExecutorService var2) {
         super();
         this.execFinally = var2;
      }

      public T call() throws Exception {
         Object var1;
         try {
            var1 = BackgroundInitializer.this.initialize();
         } finally {
            if (this.execFinally != null) {
               this.execFinally.shutdown();
            }

         }

         return var1;
      }
   }
}
