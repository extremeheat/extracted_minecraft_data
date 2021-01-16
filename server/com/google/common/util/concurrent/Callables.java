package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class Callables {
   private Callables() {
      super();
   }

   public static <T> Callable<T> returning(@Nullable final T var0) {
      return new Callable<T>() {
         public T call() {
            return var0;
         }
      };
   }

   @Beta
   @GwtIncompatible
   public static <T> AsyncCallable<T> asAsyncCallable(final Callable<T> var0, final ListeningExecutorService var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new AsyncCallable<T>() {
         public ListenableFuture<T> call() throws Exception {
            return var1.submit(var0);
         }
      };
   }

   @GwtIncompatible
   static <T> Callable<T> threadRenaming(final Callable<T> var0, final Supplier<String> var1) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var0);
      return new Callable<T>() {
         public T call() throws Exception {
            Thread var1x = Thread.currentThread();
            String var2 = var1x.getName();
            boolean var3 = Callables.trySetName((String)var1.get(), var1x);
            boolean var9 = false;

            Object var4;
            try {
               var9 = true;
               var4 = var0.call();
               var9 = false;
            } finally {
               if (var9) {
                  if (var3) {
                     Callables.trySetName(var2, var1x);
                  }

               }
            }

            if (var3) {
               Callables.trySetName(var2, var1x);
            }

            return var4;
         }
      };
   }

   @GwtIncompatible
   static Runnable threadRenaming(final Runnable var0, final Supplier<String> var1) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var0);
      return new Runnable() {
         public void run() {
            Thread var1x = Thread.currentThread();
            String var2 = var1x.getName();
            boolean var3 = Callables.trySetName((String)var1.get(), var1x);
            boolean var8 = false;

            try {
               var8 = true;
               var0.run();
               var8 = false;
            } finally {
               if (var8) {
                  if (var3) {
                     Callables.trySetName(var2, var1x);
                  }

               }
            }

            if (var3) {
               Callables.trySetName(var2, var1x);
            }

         }
      };
   }

   @GwtIncompatible
   private static boolean trySetName(String var0, Thread var1) {
      try {
         var1.setName(var0);
         return true;
      } catch (SecurityException var3) {
         return false;
      }
   }
}
