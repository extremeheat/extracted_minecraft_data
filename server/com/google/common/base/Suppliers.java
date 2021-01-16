package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

@GwtCompatible
public final class Suppliers {
   private Suppliers() {
      super();
   }

   public static <F, T> Supplier<T> compose(Function<? super F, T> var0, Supplier<F> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new Suppliers.SupplierComposition(var0, var1);
   }

   public static <T> Supplier<T> memoize(Supplier<T> var0) {
      if (!(var0 instanceof Suppliers.NonSerializableMemoizingSupplier) && !(var0 instanceof Suppliers.MemoizingSupplier)) {
         return (Supplier)(var0 instanceof Serializable ? new Suppliers.MemoizingSupplier(var0) : new Suppliers.NonSerializableMemoizingSupplier(var0));
      } else {
         return var0;
      }
   }

   public static <T> Supplier<T> memoizeWithExpiration(Supplier<T> var0, long var1, TimeUnit var3) {
      return new Suppliers.ExpiringMemoizingSupplier(var0, var1, var3);
   }

   public static <T> Supplier<T> ofInstance(@Nullable T var0) {
      return new Suppliers.SupplierOfInstance(var0);
   }

   public static <T> Supplier<T> synchronizedSupplier(Supplier<T> var0) {
      return new Suppliers.ThreadSafeSupplier((Supplier)Preconditions.checkNotNull(var0));
   }

   public static <T> Function<Supplier<T>, T> supplierFunction() {
      Suppliers.SupplierFunctionImpl var0 = Suppliers.SupplierFunctionImpl.INSTANCE;
      return var0;
   }

   private static enum SupplierFunctionImpl implements Suppliers.SupplierFunction<Object> {
      INSTANCE;

      private SupplierFunctionImpl() {
      }

      public Object apply(Supplier<Object> var1) {
         return var1.get();
      }

      public String toString() {
         return "Suppliers.supplierFunction()";
      }
   }

   private interface SupplierFunction<T> extends Function<Supplier<T>, T> {
   }

   private static class ThreadSafeSupplier<T> implements Supplier<T>, Serializable {
      final Supplier<T> delegate;
      private static final long serialVersionUID = 0L;

      ThreadSafeSupplier(Supplier<T> var1) {
         super();
         this.delegate = var1;
      }

      public T get() {
         synchronized(this.delegate) {
            return this.delegate.get();
         }
      }

      public String toString() {
         return "Suppliers.synchronizedSupplier(" + this.delegate + ")";
      }
   }

   private static class SupplierOfInstance<T> implements Supplier<T>, Serializable {
      final T instance;
      private static final long serialVersionUID = 0L;

      SupplierOfInstance(@Nullable T var1) {
         super();
         this.instance = var1;
      }

      public T get() {
         return this.instance;
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof Suppliers.SupplierOfInstance) {
            Suppliers.SupplierOfInstance var2 = (Suppliers.SupplierOfInstance)var1;
            return Objects.equal(this.instance, var2.instance);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hashCode(this.instance);
      }

      public String toString() {
         return "Suppliers.ofInstance(" + this.instance + ")";
      }
   }

   @VisibleForTesting
   static class ExpiringMemoizingSupplier<T> implements Supplier<T>, Serializable {
      final Supplier<T> delegate;
      final long durationNanos;
      transient volatile T value;
      transient volatile long expirationNanos;
      private static final long serialVersionUID = 0L;

      ExpiringMemoizingSupplier(Supplier<T> var1, long var2, TimeUnit var4) {
         super();
         this.delegate = (Supplier)Preconditions.checkNotNull(var1);
         this.durationNanos = var4.toNanos(var2);
         Preconditions.checkArgument(var2 > 0L);
      }

      public T get() {
         long var1 = this.expirationNanos;
         long var3 = Platform.systemNanoTime();
         if (var1 == 0L || var3 - var1 >= 0L) {
            synchronized(this) {
               if (var1 == this.expirationNanos) {
                  Object var6 = this.delegate.get();
                  this.value = var6;
                  var1 = var3 + this.durationNanos;
                  this.expirationNanos = var1 == 0L ? 1L : var1;
                  return var6;
               }
            }
         }

         return this.value;
      }

      public String toString() {
         return "Suppliers.memoizeWithExpiration(" + this.delegate + ", " + this.durationNanos + ", NANOS)";
      }
   }

   @VisibleForTesting
   static class NonSerializableMemoizingSupplier<T> implements Supplier<T> {
      volatile Supplier<T> delegate;
      volatile boolean initialized;
      T value;

      NonSerializableMemoizingSupplier(Supplier<T> var1) {
         super();
         this.delegate = (Supplier)Preconditions.checkNotNull(var1);
      }

      public T get() {
         if (!this.initialized) {
            synchronized(this) {
               if (!this.initialized) {
                  Object var2 = this.delegate.get();
                  this.value = var2;
                  this.initialized = true;
                  this.delegate = null;
                  return var2;
               }
            }
         }

         return this.value;
      }

      public String toString() {
         return "Suppliers.memoize(" + this.delegate + ")";
      }
   }

   @VisibleForTesting
   static class MemoizingSupplier<T> implements Supplier<T>, Serializable {
      final Supplier<T> delegate;
      transient volatile boolean initialized;
      transient T value;
      private static final long serialVersionUID = 0L;

      MemoizingSupplier(Supplier<T> var1) {
         super();
         this.delegate = (Supplier)Preconditions.checkNotNull(var1);
      }

      public T get() {
         if (!this.initialized) {
            synchronized(this) {
               if (!this.initialized) {
                  Object var2 = this.delegate.get();
                  this.value = var2;
                  this.initialized = true;
                  return var2;
               }
            }
         }

         return this.value;
      }

      public String toString() {
         return "Suppliers.memoize(" + this.delegate + ")";
      }
   }

   private static class SupplierComposition<F, T> implements Supplier<T>, Serializable {
      final Function<? super F, T> function;
      final Supplier<F> supplier;
      private static final long serialVersionUID = 0L;

      SupplierComposition(Function<? super F, T> var1, Supplier<F> var2) {
         super();
         this.function = var1;
         this.supplier = var2;
      }

      public T get() {
         return this.function.apply(this.supplier.get());
      }

      public boolean equals(@Nullable Object var1) {
         if (!(var1 instanceof Suppliers.SupplierComposition)) {
            return false;
         } else {
            Suppliers.SupplierComposition var2 = (Suppliers.SupplierComposition)var1;
            return this.function.equals(var2.function) && this.supplier.equals(var2.supplier);
         }
      }

      public int hashCode() {
         return Objects.hashCode(this.function, this.supplier);
      }

      public String toString() {
         return "Suppliers.compose(" + this.function + ", " + this.supplier + ")";
      }
   }
}
