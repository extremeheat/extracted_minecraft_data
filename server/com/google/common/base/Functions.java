package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public final class Functions {
   private Functions() {
      super();
   }

   public static Function<Object, String> toStringFunction() {
      return Functions.ToStringFunction.INSTANCE;
   }

   public static <E> Function<E, E> identity() {
      return Functions.IdentityFunction.INSTANCE;
   }

   public static <K, V> Function<K, V> forMap(Map<K, V> var0) {
      return new Functions.FunctionForMapNoDefault(var0);
   }

   public static <K, V> Function<K, V> forMap(Map<K, ? extends V> var0, @Nullable V var1) {
      return new Functions.ForMapWithDefault(var0, var1);
   }

   public static <A, B, C> Function<A, C> compose(Function<B, C> var0, Function<A, ? extends B> var1) {
      return new Functions.FunctionComposition(var0, var1);
   }

   public static <T> Function<T, Boolean> forPredicate(Predicate<T> var0) {
      return new Functions.PredicateFunction(var0);
   }

   public static <E> Function<Object, E> constant(@Nullable E var0) {
      return new Functions.ConstantFunction(var0);
   }

   public static <T> Function<Object, T> forSupplier(Supplier<T> var0) {
      return new Functions.SupplierFunction(var0);
   }

   private static class SupplierFunction<T> implements Function<Object, T>, Serializable {
      private final Supplier<T> supplier;
      private static final long serialVersionUID = 0L;

      private SupplierFunction(Supplier<T> var1) {
         super();
         this.supplier = (Supplier)Preconditions.checkNotNull(var1);
      }

      public T apply(@Nullable Object var1) {
         return this.supplier.get();
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof Functions.SupplierFunction) {
            Functions.SupplierFunction var2 = (Functions.SupplierFunction)var1;
            return this.supplier.equals(var2.supplier);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.supplier.hashCode();
      }

      public String toString() {
         return "Functions.forSupplier(" + this.supplier + ")";
      }

      // $FF: synthetic method
      SupplierFunction(Supplier var1, Object var2) {
         this(var1);
      }
   }

   private static class ConstantFunction<E> implements Function<Object, E>, Serializable {
      private final E value;
      private static final long serialVersionUID = 0L;

      public ConstantFunction(@Nullable E var1) {
         super();
         this.value = var1;
      }

      public E apply(@Nullable Object var1) {
         return this.value;
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof Functions.ConstantFunction) {
            Functions.ConstantFunction var2 = (Functions.ConstantFunction)var1;
            return Objects.equal(this.value, var2.value);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.value == null ? 0 : this.value.hashCode();
      }

      public String toString() {
         return "Functions.constant(" + this.value + ")";
      }
   }

   private static class PredicateFunction<T> implements Function<T, Boolean>, Serializable {
      private final Predicate<T> predicate;
      private static final long serialVersionUID = 0L;

      private PredicateFunction(Predicate<T> var1) {
         super();
         this.predicate = (Predicate)Preconditions.checkNotNull(var1);
      }

      public Boolean apply(@Nullable T var1) {
         return this.predicate.apply(var1);
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof Functions.PredicateFunction) {
            Functions.PredicateFunction var2 = (Functions.PredicateFunction)var1;
            return this.predicate.equals(var2.predicate);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.predicate.hashCode();
      }

      public String toString() {
         return "Functions.forPredicate(" + this.predicate + ")";
      }

      // $FF: synthetic method
      PredicateFunction(Predicate var1, Object var2) {
         this(var1);
      }
   }

   private static class FunctionComposition<A, B, C> implements Function<A, C>, Serializable {
      private final Function<B, C> g;
      private final Function<A, ? extends B> f;
      private static final long serialVersionUID = 0L;

      public FunctionComposition(Function<B, C> var1, Function<A, ? extends B> var2) {
         super();
         this.g = (Function)Preconditions.checkNotNull(var1);
         this.f = (Function)Preconditions.checkNotNull(var2);
      }

      public C apply(@Nullable A var1) {
         return this.g.apply(this.f.apply(var1));
      }

      public boolean equals(@Nullable Object var1) {
         if (!(var1 instanceof Functions.FunctionComposition)) {
            return false;
         } else {
            Functions.FunctionComposition var2 = (Functions.FunctionComposition)var1;
            return this.f.equals(var2.f) && this.g.equals(var2.g);
         }
      }

      public int hashCode() {
         return this.f.hashCode() ^ this.g.hashCode();
      }

      public String toString() {
         return this.g + "(" + this.f + ")";
      }
   }

   private static class ForMapWithDefault<K, V> implements Function<K, V>, Serializable {
      final Map<K, ? extends V> map;
      final V defaultValue;
      private static final long serialVersionUID = 0L;

      ForMapWithDefault(Map<K, ? extends V> var1, @Nullable V var2) {
         super();
         this.map = (Map)Preconditions.checkNotNull(var1);
         this.defaultValue = var2;
      }

      public V apply(@Nullable K var1) {
         Object var2 = this.map.get(var1);
         return var2 == null && !this.map.containsKey(var1) ? this.defaultValue : var2;
      }

      public boolean equals(@Nullable Object var1) {
         if (!(var1 instanceof Functions.ForMapWithDefault)) {
            return false;
         } else {
            Functions.ForMapWithDefault var2 = (Functions.ForMapWithDefault)var1;
            return this.map.equals(var2.map) && Objects.equal(this.defaultValue, var2.defaultValue);
         }
      }

      public int hashCode() {
         return Objects.hashCode(this.map, this.defaultValue);
      }

      public String toString() {
         return "Functions.forMap(" + this.map + ", defaultValue=" + this.defaultValue + ")";
      }
   }

   private static class FunctionForMapNoDefault<K, V> implements Function<K, V>, Serializable {
      final Map<K, V> map;
      private static final long serialVersionUID = 0L;

      FunctionForMapNoDefault(Map<K, V> var1) {
         super();
         this.map = (Map)Preconditions.checkNotNull(var1);
      }

      public V apply(@Nullable K var1) {
         Object var2 = this.map.get(var1);
         Preconditions.checkArgument(var2 != null || this.map.containsKey(var1), "Key '%s' not present in map", var1);
         return var2;
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof Functions.FunctionForMapNoDefault) {
            Functions.FunctionForMapNoDefault var2 = (Functions.FunctionForMapNoDefault)var1;
            return this.map.equals(var2.map);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.map.hashCode();
      }

      public String toString() {
         return "Functions.forMap(" + this.map + ")";
      }
   }

   private static enum IdentityFunction implements Function<Object, Object> {
      INSTANCE;

      private IdentityFunction() {
      }

      @Nullable
      public Object apply(@Nullable Object var1) {
         return var1;
      }

      public String toString() {
         return "Functions.identity()";
      }
   }

   private static enum ToStringFunction implements Function<Object, String> {
      INSTANCE;

      private ToStringFunction() {
      }

      public String apply(Object var1) {
         Preconditions.checkNotNull(var1);
         return var1.toString();
      }

      public String toString() {
         return "Functions.toStringFunction()";
      }
   }
}
