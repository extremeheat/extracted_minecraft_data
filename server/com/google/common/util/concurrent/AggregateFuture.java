package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AggregateFuture<InputT, OutputT> extends AbstractFuture.TrustedFuture<OutputT> {
   private static final Logger logger = Logger.getLogger(AggregateFuture.class.getName());
   private AggregateFuture<InputT, OutputT>.RunningState runningState;

   AggregateFuture() {
      super();
   }

   protected final void afterDone() {
      super.afterDone();
      AggregateFuture.RunningState var1 = this.runningState;
      if (var1 != null) {
         this.runningState = null;
         ImmutableCollection var2 = var1.futures;
         boolean var3 = this.wasInterrupted();
         if (this.wasInterrupted()) {
            var1.interruptTask();
         }

         if (this.isCancelled() & var2 != null) {
            UnmodifiableIterator var4 = var2.iterator();

            while(var4.hasNext()) {
               ListenableFuture var5 = (ListenableFuture)var4.next();
               var5.cancel(var3);
            }
         }
      }

   }

   final void init(AggregateFuture<InputT, OutputT>.RunningState var1) {
      this.runningState = var1;
      var1.init();
   }

   private static boolean addCausalChain(Set<Throwable> var0, Throwable var1) {
      while(var1 != null) {
         boolean var2 = var0.add(var1);
         if (!var2) {
            return false;
         }

         var1 = var1.getCause();
      }

      return true;
   }

   abstract class RunningState extends AggregateFutureState implements Runnable {
      private ImmutableCollection<? extends ListenableFuture<? extends InputT>> futures;
      private final boolean allMustSucceed;
      private final boolean collectsValues;

      RunningState(ImmutableCollection<? extends ListenableFuture<? extends InputT>> var2, boolean var3, boolean var4) {
         super(var2.size());
         this.futures = (ImmutableCollection)Preconditions.checkNotNull(var2);
         this.allMustSucceed = var3;
         this.collectsValues = var4;
      }

      public final void run() {
         this.decrementCountAndMaybeComplete();
      }

      private void init() {
         if (this.futures.isEmpty()) {
            this.handleAllCompleted();
         } else {
            if (this.allMustSucceed) {
               int var1 = 0;
               UnmodifiableIterator var2 = this.futures.iterator();

               while(var2.hasNext()) {
                  final ListenableFuture var3 = (ListenableFuture)var2.next();
                  final int var4 = var1++;
                  var3.addListener(new Runnable() {
                     public void run() {
                        try {
                           RunningState.this.handleOneInputDone(var4, var3);
                        } finally {
                           RunningState.this.decrementCountAndMaybeComplete();
                        }

                     }
                  }, MoreExecutors.directExecutor());
               }
            } else {
               UnmodifiableIterator var5 = this.futures.iterator();

               while(var5.hasNext()) {
                  ListenableFuture var6 = (ListenableFuture)var5.next();
                  var6.addListener(this, MoreExecutors.directExecutor());
               }
            }

         }
      }

      private void handleException(Throwable var1) {
         Preconditions.checkNotNull(var1);
         boolean var2 = false;
         boolean var3 = true;
         if (this.allMustSucceed) {
            var2 = AggregateFuture.this.setException(var1);
            if (var2) {
               this.releaseResourcesAfterFailure();
            } else {
               var3 = AggregateFuture.addCausalChain(this.getOrInitSeenExceptions(), var1);
            }
         }

         if (var1 instanceof Error | this.allMustSucceed & !var2 & var3) {
            String var4 = var1 instanceof Error ? "Input Future failed with Error" : "Got more than one input Future failure. Logging failures after the first";
            AggregateFuture.logger.log(Level.SEVERE, var4, var1);
         }

      }

      final void addInitialException(Set<Throwable> var1) {
         if (!AggregateFuture.this.isCancelled()) {
            AggregateFuture.addCausalChain(var1, AggregateFuture.this.trustedGetException());
         }

      }

      private void handleOneInputDone(int var1, Future<? extends InputT> var2) {
         Preconditions.checkState(this.allMustSucceed || !AggregateFuture.this.isDone() || AggregateFuture.this.isCancelled(), "Future was done before all dependencies completed");

         try {
            Preconditions.checkState(var2.isDone(), "Tried to set value from future which is not done");
            if (this.allMustSucceed) {
               if (var2.isCancelled()) {
                  AggregateFuture.this.runningState = null;
                  AggregateFuture.this.cancel(false);
               } else {
                  Object var3 = Futures.getDone(var2);
                  if (this.collectsValues) {
                     this.collectOneValue(this.allMustSucceed, var1, var3);
                  }
               }
            } else if (this.collectsValues && !var2.isCancelled()) {
               this.collectOneValue(this.allMustSucceed, var1, Futures.getDone(var2));
            }
         } catch (ExecutionException var4) {
            this.handleException(var4.getCause());
         } catch (Throwable var5) {
            this.handleException(var5);
         }

      }

      private void decrementCountAndMaybeComplete() {
         int var1 = this.decrementRemainingAndGet();
         Preconditions.checkState(var1 >= 0, "Less than 0 remaining futures");
         if (var1 == 0) {
            this.processCompleted();
         }

      }

      private void processCompleted() {
         if (this.collectsValues & !this.allMustSucceed) {
            int var1 = 0;
            UnmodifiableIterator var2 = this.futures.iterator();

            while(var2.hasNext()) {
               ListenableFuture var3 = (ListenableFuture)var2.next();
               this.handleOneInputDone(var1++, var3);
            }
         }

         this.handleAllCompleted();
      }

      void releaseResourcesAfterFailure() {
         this.futures = null;
      }

      abstract void collectOneValue(boolean var1, int var2, @Nullable InputT var3);

      abstract void handleAllCompleted();

      void interruptTask() {
      }
   }
}
