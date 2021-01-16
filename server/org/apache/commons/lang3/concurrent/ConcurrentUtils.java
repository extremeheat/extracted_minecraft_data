package org.apache.commons.lang3.concurrent;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.Validate;

public class ConcurrentUtils {
   private ConcurrentUtils() {
      super();
   }

   public static ConcurrentException extractCause(ExecutionException var0) {
      if (var0 != null && var0.getCause() != null) {
         throwCause(var0);
         return new ConcurrentException(var0.getMessage(), var0.getCause());
      } else {
         return null;
      }
   }

   public static ConcurrentRuntimeException extractCauseUnchecked(ExecutionException var0) {
      if (var0 != null && var0.getCause() != null) {
         throwCause(var0);
         return new ConcurrentRuntimeException(var0.getMessage(), var0.getCause());
      } else {
         return null;
      }
   }

   public static void handleCause(ExecutionException var0) throws ConcurrentException {
      ConcurrentException var1 = extractCause(var0);
      if (var1 != null) {
         throw var1;
      }
   }

   public static void handleCauseUnchecked(ExecutionException var0) {
      ConcurrentRuntimeException var1 = extractCauseUnchecked(var0);
      if (var1 != null) {
         throw var1;
      }
   }

   static Throwable checkedException(Throwable var0) {
      Validate.isTrue(var0 != null && !(var0 instanceof RuntimeException) && !(var0 instanceof Error), "Not a checked exception: " + var0);
      return var0;
   }

   private static void throwCause(ExecutionException var0) {
      if (var0.getCause() instanceof RuntimeException) {
         throw (RuntimeException)var0.getCause();
      } else if (var0.getCause() instanceof Error) {
         throw (Error)var0.getCause();
      }
   }

   public static <T> T initialize(ConcurrentInitializer<T> var0) throws ConcurrentException {
      return var0 != null ? var0.get() : null;
   }

   public static <T> T initializeUnchecked(ConcurrentInitializer<T> var0) {
      try {
         return initialize(var0);
      } catch (ConcurrentException var2) {
         throw new ConcurrentRuntimeException(var2.getCause());
      }
   }

   public static <K, V> V putIfAbsent(ConcurrentMap<K, V> var0, K var1, V var2) {
      if (var0 == null) {
         return null;
      } else {
         Object var3 = var0.putIfAbsent(var1, var2);
         return var3 != null ? var3 : var2;
      }
   }

   public static <K, V> V createIfAbsent(ConcurrentMap<K, V> var0, K var1, ConcurrentInitializer<V> var2) throws ConcurrentException {
      if (var0 != null && var2 != null) {
         Object var3 = var0.get(var1);
         return var3 == null ? putIfAbsent(var0, var1, var2.get()) : var3;
      } else {
         return null;
      }
   }

   public static <K, V> V createIfAbsentUnchecked(ConcurrentMap<K, V> var0, K var1, ConcurrentInitializer<V> var2) {
      try {
         return createIfAbsent(var0, var1, var2);
      } catch (ConcurrentException var4) {
         throw new ConcurrentRuntimeException(var4.getCause());
      }
   }

   public static <T> Future<T> constantFuture(T var0) {
      return new ConcurrentUtils.ConstantFuture(var0);
   }

   static final class ConstantFuture<T> implements Future<T> {
      private final T value;

      ConstantFuture(T var1) {
         super();
         this.value = var1;
      }

      public boolean isDone() {
         return true;
      }

      public T get() {
         return this.value;
      }

      public T get(long var1, TimeUnit var3) {
         return this.value;
      }

      public boolean isCancelled() {
         return false;
      }

      public boolean cancel(boolean var1) {
         return false;
      }
   }
}
