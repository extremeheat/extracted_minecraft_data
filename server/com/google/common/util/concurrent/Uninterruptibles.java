package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
@GwtCompatible(
   emulated = true
)
public final class Uninterruptibles {
   @GwtIncompatible
   public static void awaitUninterruptibly(CountDownLatch var0) {
      boolean var1 = false;

      try {
         while(true) {
            try {
               var0.await();
               return;
            } catch (InterruptedException var6) {
               var1 = true;
            }
         }
      } finally {
         if (var1) {
            Thread.currentThread().interrupt();
         }

      }
   }

   @CanIgnoreReturnValue
   @GwtIncompatible
   public static boolean awaitUninterruptibly(CountDownLatch var0, long var1, TimeUnit var3) {
      boolean var4 = false;

      try {
         long var5 = var3.toNanos(var1);
         long var7 = System.nanoTime() + var5;

         while(true) {
            try {
               boolean var9 = var0.await(var5, TimeUnit.NANOSECONDS);
               return var9;
            } catch (InterruptedException var13) {
               var4 = true;
               var5 = var7 - System.nanoTime();
            }
         }
      } finally {
         if (var4) {
            Thread.currentThread().interrupt();
         }

      }
   }

   @GwtIncompatible
   public static void joinUninterruptibly(Thread var0) {
      boolean var1 = false;

      try {
         while(true) {
            try {
               var0.join();
               return;
            } catch (InterruptedException var6) {
               var1 = true;
            }
         }
      } finally {
         if (var1) {
            Thread.currentThread().interrupt();
         }

      }
   }

   @CanIgnoreReturnValue
   public static <V> V getUninterruptibly(Future<V> var0) throws ExecutionException {
      boolean var1 = false;

      try {
         while(true) {
            try {
               Object var2 = var0.get();
               return var2;
            } catch (InterruptedException var6) {
               var1 = true;
            }
         }
      } finally {
         if (var1) {
            Thread.currentThread().interrupt();
         }

      }
   }

   @CanIgnoreReturnValue
   @GwtIncompatible
   public static <V> V getUninterruptibly(Future<V> var0, long var1, TimeUnit var3) throws ExecutionException, TimeoutException {
      boolean var4 = false;

      try {
         long var5 = var3.toNanos(var1);
         long var7 = System.nanoTime() + var5;

         while(true) {
            try {
               Object var9 = var0.get(var5, TimeUnit.NANOSECONDS);
               return var9;
            } catch (InterruptedException var13) {
               var4 = true;
               var5 = var7 - System.nanoTime();
            }
         }
      } finally {
         if (var4) {
            Thread.currentThread().interrupt();
         }

      }
   }

   @GwtIncompatible
   public static void joinUninterruptibly(Thread var0, long var1, TimeUnit var3) {
      Preconditions.checkNotNull(var0);
      boolean var4 = false;

      try {
         long var5 = var3.toNanos(var1);
         long var7 = System.nanoTime() + var5;

         while(true) {
            try {
               TimeUnit.NANOSECONDS.timedJoin(var0, var5);
               return;
            } catch (InterruptedException var13) {
               var4 = true;
               var5 = var7 - System.nanoTime();
            }
         }
      } finally {
         if (var4) {
            Thread.currentThread().interrupt();
         }

      }
   }

   @GwtIncompatible
   public static <E> E takeUninterruptibly(BlockingQueue<E> var0) {
      boolean var1 = false;

      try {
         while(true) {
            try {
               Object var2 = var0.take();
               return var2;
            } catch (InterruptedException var6) {
               var1 = true;
            }
         }
      } finally {
         if (var1) {
            Thread.currentThread().interrupt();
         }

      }
   }

   @GwtIncompatible
   public static <E> void putUninterruptibly(BlockingQueue<E> var0, E var1) {
      boolean var2 = false;

      try {
         while(true) {
            try {
               var0.put(var1);
               return;
            } catch (InterruptedException var7) {
               var2 = true;
            }
         }
      } finally {
         if (var2) {
            Thread.currentThread().interrupt();
         }

      }
   }

   @GwtIncompatible
   public static void sleepUninterruptibly(long var0, TimeUnit var2) {
      boolean var3 = false;

      try {
         long var4 = var2.toNanos(var0);
         long var6 = System.nanoTime() + var4;

         while(true) {
            try {
               TimeUnit.NANOSECONDS.sleep(var4);
               return;
            } catch (InterruptedException var12) {
               var3 = true;
               var4 = var6 - System.nanoTime();
            }
         }
      } finally {
         if (var3) {
            Thread.currentThread().interrupt();
         }

      }
   }

   @GwtIncompatible
   public static boolean tryAcquireUninterruptibly(Semaphore var0, long var1, TimeUnit var3) {
      return tryAcquireUninterruptibly(var0, 1, var1, var3);
   }

   @GwtIncompatible
   public static boolean tryAcquireUninterruptibly(Semaphore var0, int var1, long var2, TimeUnit var4) {
      boolean var5 = false;

      try {
         long var6 = var4.toNanos(var2);
         long var8 = System.nanoTime() + var6;

         while(true) {
            try {
               boolean var10 = var0.tryAcquire(var1, var6, TimeUnit.NANOSECONDS);
               return var10;
            } catch (InterruptedException var14) {
               var5 = true;
               var6 = var8 - System.nanoTime();
            }
         }
      } finally {
         if (var5) {
            Thread.currentThread().interrupt();
         }

      }
   }

   private Uninterruptibles() {
      super();
   }
}
