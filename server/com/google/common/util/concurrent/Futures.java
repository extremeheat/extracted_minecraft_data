package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;

@Beta
@GwtCompatible(
   emulated = true
)
public final class Futures extends GwtFuturesCatchingSpecialization {
   private static final AsyncFunction<ListenableFuture<Object>, Object> DEREFERENCER = new AsyncFunction<ListenableFuture<Object>, Object>() {
      public ListenableFuture<Object> apply(ListenableFuture<Object> var1) {
         return var1;
      }
   };

   private Futures() {
      super();
   }

   @GwtIncompatible
   public static <V, X extends Exception> CheckedFuture<V, X> makeChecked(ListenableFuture<V> var0, Function<? super Exception, X> var1) {
      return new Futures.MappingCheckedFuture((ListenableFuture)Preconditions.checkNotNull(var0), var1);
   }

   public static <V> ListenableFuture<V> immediateFuture(@Nullable V var0) {
      if (var0 == null) {
         ImmediateFuture.ImmediateSuccessfulFuture var1 = ImmediateFuture.ImmediateSuccessfulFuture.NULL;
         return var1;
      } else {
         return new ImmediateFuture.ImmediateSuccessfulFuture(var0);
      }
   }

   @GwtIncompatible
   public static <V, X extends Exception> CheckedFuture<V, X> immediateCheckedFuture(@Nullable V var0) {
      return new ImmediateFuture.ImmediateSuccessfulCheckedFuture(var0);
   }

   public static <V> ListenableFuture<V> immediateFailedFuture(Throwable var0) {
      Preconditions.checkNotNull(var0);
      return new ImmediateFuture.ImmediateFailedFuture(var0);
   }

   public static <V> ListenableFuture<V> immediateCancelledFuture() {
      return new ImmediateFuture.ImmediateCancelledFuture();
   }

   @GwtIncompatible
   public static <V, X extends Exception> CheckedFuture<V, X> immediateFailedCheckedFuture(X var0) {
      Preconditions.checkNotNull(var0);
      return new ImmediateFuture.ImmediateFailedCheckedFuture(var0);
   }

   @Partially.GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
   public static <V, X extends Throwable> ListenableFuture<V> catching(ListenableFuture<? extends V> var0, Class<X> var1, Function<? super X, ? extends V> var2) {
      return AbstractCatchingFuture.create(var0, var1, var2);
   }

   @Partially.GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
   public static <V, X extends Throwable> ListenableFuture<V> catching(ListenableFuture<? extends V> var0, Class<X> var1, Function<? super X, ? extends V> var2, Executor var3) {
      return AbstractCatchingFuture.create(var0, var1, var2, var3);
   }

   @CanIgnoreReturnValue
   @Partially.GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
   public static <V, X extends Throwable> ListenableFuture<V> catchingAsync(ListenableFuture<? extends V> var0, Class<X> var1, AsyncFunction<? super X, ? extends V> var2) {
      return AbstractCatchingFuture.create(var0, var1, var2);
   }

   @CanIgnoreReturnValue
   @Partially.GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
   public static <V, X extends Throwable> ListenableFuture<V> catchingAsync(ListenableFuture<? extends V> var0, Class<X> var1, AsyncFunction<? super X, ? extends V> var2, Executor var3) {
      return AbstractCatchingFuture.create(var0, var1, var2, var3);
   }

   @GwtIncompatible
   public static <V> ListenableFuture<V> withTimeout(ListenableFuture<V> var0, long var1, TimeUnit var3, ScheduledExecutorService var4) {
      return TimeoutFuture.create(var0, var1, var3, var4);
   }

   public static <I, O> ListenableFuture<O> transformAsync(ListenableFuture<I> var0, AsyncFunction<? super I, ? extends O> var1) {
      return AbstractTransformFuture.create(var0, var1);
   }

   public static <I, O> ListenableFuture<O> transformAsync(ListenableFuture<I> var0, AsyncFunction<? super I, ? extends O> var1, Executor var2) {
      return AbstractTransformFuture.create(var0, var1, var2);
   }

   public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> var0, Function<? super I, ? extends O> var1) {
      return AbstractTransformFuture.create(var0, var1);
   }

   public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> var0, Function<? super I, ? extends O> var1, Executor var2) {
      return AbstractTransformFuture.create(var0, var1, var2);
   }

   @GwtIncompatible
   public static <I, O> Future<O> lazyTransform(final Future<I> var0, final Function<? super I, ? extends O> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new Future<O>() {
         public boolean cancel(boolean var1x) {
            return var0.cancel(var1x);
         }

         public boolean isCancelled() {
            return var0.isCancelled();
         }

         public boolean isDone() {
            return var0.isDone();
         }

         public O get() throws InterruptedException, ExecutionException {
            return this.applyTransformation(var0.get());
         }

         public O get(long var1x, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException {
            return this.applyTransformation(var0.get(var1x, var3));
         }

         private O applyTransformation(I var1x) throws ExecutionException {
            try {
               return var1.apply(var1x);
            } catch (Throwable var3) {
               throw new ExecutionException(var3);
            }
         }
      };
   }

   public static <V> ListenableFuture<V> dereference(ListenableFuture<? extends ListenableFuture<? extends V>> var0) {
      return transformAsync(var0, DEREFERENCER);
   }

   @SafeVarargs
   @Beta
   public static <V> ListenableFuture<List<V>> allAsList(ListenableFuture<? extends V>... var0) {
      return new CollectionFuture.ListFuture(ImmutableList.copyOf((Object[])var0), true);
   }

   @Beta
   public static <V> ListenableFuture<List<V>> allAsList(Iterable<? extends ListenableFuture<? extends V>> var0) {
      return new CollectionFuture.ListFuture(ImmutableList.copyOf(var0), true);
   }

   @SafeVarargs
   public static <V> Futures.FutureCombiner<V> whenAllComplete(ListenableFuture<? extends V>... var0) {
      return new Futures.FutureCombiner(false, ImmutableList.copyOf((Object[])var0));
   }

   public static <V> Futures.FutureCombiner<V> whenAllComplete(Iterable<? extends ListenableFuture<? extends V>> var0) {
      return new Futures.FutureCombiner(false, ImmutableList.copyOf(var0));
   }

   @SafeVarargs
   public static <V> Futures.FutureCombiner<V> whenAllSucceed(ListenableFuture<? extends V>... var0) {
      return new Futures.FutureCombiner(true, ImmutableList.copyOf((Object[])var0));
   }

   public static <V> Futures.FutureCombiner<V> whenAllSucceed(Iterable<? extends ListenableFuture<? extends V>> var0) {
      return new Futures.FutureCombiner(true, ImmutableList.copyOf(var0));
   }

   public static <V> ListenableFuture<V> nonCancellationPropagating(ListenableFuture<V> var0) {
      return (ListenableFuture)(var0.isDone() ? var0 : new Futures.NonCancellationPropagatingFuture(var0));
   }

   @SafeVarargs
   @Beta
   public static <V> ListenableFuture<List<V>> successfulAsList(ListenableFuture<? extends V>... var0) {
      return new CollectionFuture.ListFuture(ImmutableList.copyOf((Object[])var0), false);
   }

   @Beta
   public static <V> ListenableFuture<List<V>> successfulAsList(Iterable<? extends ListenableFuture<? extends V>> var0) {
      return new CollectionFuture.ListFuture(ImmutableList.copyOf(var0), false);
   }

   @Beta
   @GwtIncompatible
   public static <T> ImmutableList<ListenableFuture<T>> inCompletionOrder(Iterable<? extends ListenableFuture<? extends T>> var0) {
      final ConcurrentLinkedQueue var1 = Queues.newConcurrentLinkedQueue();
      ImmutableList.Builder var2 = ImmutableList.builder();
      SerializingExecutor var3 = new SerializingExecutor(MoreExecutors.directExecutor());
      Iterator var4 = var0.iterator();

      while(var4.hasNext()) {
         final ListenableFuture var5 = (ListenableFuture)var4.next();
         SettableFuture var6 = SettableFuture.create();
         var1.add(var6);
         var5.addListener(new Runnable() {
            public void run() {
               ((SettableFuture)var1.remove()).setFuture(var5);
            }
         }, var3);
         var2.add((Object)var6);
      }

      return var2.build();
   }

   public static <V> void addCallback(ListenableFuture<V> var0, FutureCallback<? super V> var1) {
      addCallback(var0, var1, MoreExecutors.directExecutor());
   }

   public static <V> void addCallback(final ListenableFuture<V> var0, final FutureCallback<? super V> var1, Executor var2) {
      Preconditions.checkNotNull(var1);
      Runnable var3 = new Runnable() {
         public void run() {
            Object var1x;
            try {
               var1x = Futures.getDone(var0);
            } catch (ExecutionException var3) {
               var1.onFailure(var3.getCause());
               return;
            } catch (RuntimeException var4) {
               var1.onFailure(var4);
               return;
            } catch (Error var5) {
               var1.onFailure(var5);
               return;
            }

            var1.onSuccess(var1x);
         }
      };
      var0.addListener(var3, var2);
   }

   @CanIgnoreReturnValue
   public static <V> V getDone(Future<V> var0) throws ExecutionException {
      Preconditions.checkState(var0.isDone(), "Future was expected to be done: %s", (Object)var0);
      return Uninterruptibles.getUninterruptibly(var0);
   }

   @CanIgnoreReturnValue
   @GwtIncompatible
   public static <V, X extends Exception> V getChecked(Future<V> var0, Class<X> var1) throws X {
      return FuturesGetChecked.getChecked(var0, var1);
   }

   @CanIgnoreReturnValue
   @GwtIncompatible
   public static <V, X extends Exception> V getChecked(Future<V> var0, Class<X> var1, long var2, TimeUnit var4) throws X {
      return FuturesGetChecked.getChecked(var0, var1, var2, var4);
   }

   @CanIgnoreReturnValue
   @GwtIncompatible
   public static <V> V getUnchecked(Future<V> var0) {
      Preconditions.checkNotNull(var0);

      try {
         return Uninterruptibles.getUninterruptibly(var0);
      } catch (ExecutionException var2) {
         wrapAndThrowUnchecked(var2.getCause());
         throw new AssertionError();
      }
   }

   @GwtIncompatible
   private static void wrapAndThrowUnchecked(Throwable var0) {
      if (var0 instanceof Error) {
         throw new ExecutionError((Error)var0);
      } else {
         throw new UncheckedExecutionException(var0);
      }
   }

   @GwtIncompatible
   private static class MappingCheckedFuture<V, X extends Exception> extends AbstractCheckedFuture<V, X> {
      final Function<? super Exception, X> mapper;

      MappingCheckedFuture(ListenableFuture<V> var1, Function<? super Exception, X> var2) {
         super(var1);
         this.mapper = (Function)Preconditions.checkNotNull(var2);
      }

      protected X mapException(Exception var1) {
         return (Exception)this.mapper.apply(var1);
      }
   }

   private static final class NonCancellationPropagatingFuture<V> extends AbstractFuture.TrustedFuture<V> {
      NonCancellationPropagatingFuture(final ListenableFuture<V> var1) {
         super();
         var1.addListener(new Runnable() {
            public void run() {
               NonCancellationPropagatingFuture.this.setFuture(var1);
            }
         }, MoreExecutors.directExecutor());
      }
   }

   @Beta
   @CanIgnoreReturnValue
   @GwtCompatible
   public static final class FutureCombiner<V> {
      private final boolean allMustSucceed;
      private final ImmutableList<ListenableFuture<? extends V>> futures;

      private FutureCombiner(boolean var1, ImmutableList<ListenableFuture<? extends V>> var2) {
         super();
         this.allMustSucceed = var1;
         this.futures = var2;
      }

      public <C> ListenableFuture<C> callAsync(AsyncCallable<C> var1, Executor var2) {
         return new CombinedFuture(this.futures, this.allMustSucceed, var2, var1);
      }

      public <C> ListenableFuture<C> callAsync(AsyncCallable<C> var1) {
         return this.callAsync(var1, MoreExecutors.directExecutor());
      }

      @CanIgnoreReturnValue
      public <C> ListenableFuture<C> call(Callable<C> var1, Executor var2) {
         return new CombinedFuture(this.futures, this.allMustSucceed, var2, var1);
      }

      @CanIgnoreReturnValue
      public <C> ListenableFuture<C> call(Callable<C> var1) {
         return this.call(var1, MoreExecutors.directExecutor());
      }

      // $FF: synthetic method
      FutureCombiner(boolean var1, ImmutableList var2, Object var3) {
         this(var1, var2);
      }
   }
}
