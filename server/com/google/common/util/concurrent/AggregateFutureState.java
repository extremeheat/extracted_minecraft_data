package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.logging.Level;
import java.util.logging.Logger;

@GwtCompatible(
   emulated = true
)
abstract class AggregateFutureState {
   private volatile Set<Throwable> seenExceptions = null;
   private volatile int remaining;
   private static final AggregateFutureState.AtomicHelper ATOMIC_HELPER;
   private static final Logger log = Logger.getLogger(AggregateFutureState.class.getName());

   AggregateFutureState(int var1) {
      super();
      this.remaining = var1;
   }

   final Set<Throwable> getOrInitSeenExceptions() {
      Set var1 = this.seenExceptions;
      if (var1 == null) {
         var1 = Sets.newConcurrentHashSet();
         this.addInitialException(var1);
         ATOMIC_HELPER.compareAndSetSeenExceptions(this, (Set)null, var1);
         var1 = this.seenExceptions;
      }

      return var1;
   }

   abstract void addInitialException(Set<Throwable> var1);

   final int decrementRemainingAndGet() {
      return ATOMIC_HELPER.decrementAndGetRemainingCount(this);
   }

   static {
      Object var0;
      try {
         var0 = new AggregateFutureState.SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(AggregateFutureState.class, Set.class, "seenExceptions"), AtomicIntegerFieldUpdater.newUpdater(AggregateFutureState.class, "remaining"));
      } catch (Throwable var2) {
         log.log(Level.SEVERE, "SafeAtomicHelper is broken!", var2);
         var0 = new AggregateFutureState.SynchronizedAtomicHelper();
      }

      ATOMIC_HELPER = (AggregateFutureState.AtomicHelper)var0;
   }

   private static final class SynchronizedAtomicHelper extends AggregateFutureState.AtomicHelper {
      private SynchronizedAtomicHelper() {
         super(null);
      }

      void compareAndSetSeenExceptions(AggregateFutureState var1, Set<Throwable> var2, Set<Throwable> var3) {
         synchronized(var1) {
            if (var1.seenExceptions == var2) {
               var1.seenExceptions = var3;
            }

         }
      }

      int decrementAndGetRemainingCount(AggregateFutureState var1) {
         synchronized(var1) {
            var1.remaining--;
            return var1.remaining;
         }
      }

      // $FF: synthetic method
      SynchronizedAtomicHelper(Object var1) {
         this();
      }
   }

   private static final class SafeAtomicHelper extends AggregateFutureState.AtomicHelper {
      final AtomicReferenceFieldUpdater<AggregateFutureState, Set<Throwable>> seenExceptionsUpdater;
      final AtomicIntegerFieldUpdater<AggregateFutureState> remainingCountUpdater;

      SafeAtomicHelper(AtomicReferenceFieldUpdater var1, AtomicIntegerFieldUpdater var2) {
         super(null);
         this.seenExceptionsUpdater = var1;
         this.remainingCountUpdater = var2;
      }

      void compareAndSetSeenExceptions(AggregateFutureState var1, Set<Throwable> var2, Set<Throwable> var3) {
         this.seenExceptionsUpdater.compareAndSet(var1, var2, var3);
      }

      int decrementAndGetRemainingCount(AggregateFutureState var1) {
         return this.remainingCountUpdater.decrementAndGet(var1);
      }
   }

   private abstract static class AtomicHelper {
      private AtomicHelper() {
         super();
      }

      abstract void compareAndSetSeenExceptions(AggregateFutureState var1, Set<Throwable> var2, Set<Throwable> var3);

      abstract int decrementAndGetRemainingCount(AggregateFutureState var1);

      // $FF: synthetic method
      AtomicHelper(Object var1) {
         this();
      }
   }
}
