package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

@Beta
@GwtIncompatible
public final class JdkFutureAdapters {
   public static <V> ListenableFuture<V> listenInPoolThread(Future<V> var0) {
      return (ListenableFuture)(var0 instanceof ListenableFuture ? (ListenableFuture)var0 : new JdkFutureAdapters.ListenableFutureAdapter(var0));
   }

   public static <V> ListenableFuture<V> listenInPoolThread(Future<V> var0, Executor var1) {
      Preconditions.checkNotNull(var1);
      return (ListenableFuture)(var0 instanceof ListenableFuture ? (ListenableFuture)var0 : new JdkFutureAdapters.ListenableFutureAdapter(var0, var1));
   }

   private JdkFutureAdapters() {
      super();
   }

   private static class ListenableFutureAdapter<V> extends ForwardingFuture<V> implements ListenableFuture<V> {
      private static final ThreadFactory threadFactory = (new ThreadFactoryBuilder()).setDaemon(true).setNameFormat("ListenableFutureAdapter-thread-%d").build();
      private static final Executor defaultAdapterExecutor;
      private final Executor adapterExecutor;
      private final ExecutionList executionList;
      private final AtomicBoolean hasListeners;
      private final Future<V> delegate;

      ListenableFutureAdapter(Future<V> var1) {
         this(var1, defaultAdapterExecutor);
      }

      ListenableFutureAdapter(Future<V> var1, Executor var2) {
         super();
         this.executionList = new ExecutionList();
         this.hasListeners = new AtomicBoolean(false);
         this.delegate = (Future)Preconditions.checkNotNull(var1);
         this.adapterExecutor = (Executor)Preconditions.checkNotNull(var2);
      }

      protected Future<V> delegate() {
         return this.delegate;
      }

      public void addListener(Runnable var1, Executor var2) {
         this.executionList.add(var1, var2);
         if (this.hasListeners.compareAndSet(false, true)) {
            if (this.delegate.isDone()) {
               this.executionList.execute();
               return;
            }

            this.adapterExecutor.execute(new Runnable() {
               public void run() {
                  try {
                     Uninterruptibles.getUninterruptibly(ListenableFutureAdapter.this.delegate);
                  } catch (Throwable var2) {
                  }

                  ListenableFutureAdapter.this.executionList.execute();
               }
            });
         }

      }

      static {
         defaultAdapterExecutor = Executors.newCachedThreadPool(threadFactory);
      }
   }
}
