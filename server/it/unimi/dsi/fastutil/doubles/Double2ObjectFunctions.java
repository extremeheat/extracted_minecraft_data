package it.unimi.dsi.fastutil.doubles;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleFunction;
import java.util.function.Function;

public final class Double2ObjectFunctions {
   public static final Double2ObjectFunctions.EmptyFunction EMPTY_FUNCTION = new Double2ObjectFunctions.EmptyFunction();

   private Double2ObjectFunctions() {
      super();
   }

   public static <V> Double2ObjectFunction<V> singleton(double var0, V var2) {
      return new Double2ObjectFunctions.Singleton(var0, var2);
   }

   public static <V> Double2ObjectFunction<V> singleton(Double var0, V var1) {
      return new Double2ObjectFunctions.Singleton(var0, var1);
   }

   public static <V> Double2ObjectFunction<V> synchronize(Double2ObjectFunction<V> var0) {
      return new Double2ObjectFunctions.SynchronizedFunction(var0);
   }

   public static <V> Double2ObjectFunction<V> synchronize(Double2ObjectFunction<V> var0, Object var1) {
      return new Double2ObjectFunctions.SynchronizedFunction(var0, var1);
   }

   public static <V> Double2ObjectFunction<V> unmodifiable(Double2ObjectFunction<V> var0) {
      return new Double2ObjectFunctions.UnmodifiableFunction(var0);
   }

   public static <V> Double2ObjectFunction<V> primitive(Function<? super Double, ? extends V> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Double2ObjectFunction) {
         return (Double2ObjectFunction)var0;
      } else if (var0 instanceof DoubleFunction) {
         DoubleFunction var10000 = (DoubleFunction)var0;
         Objects.requireNonNull((DoubleFunction)var0);
         return var10000::apply;
      } else {
         return new Double2ObjectFunctions.PrimitiveFunction(var0);
      }
   }

   public static class PrimitiveFunction<V> implements Double2ObjectFunction<V> {
      protected final Function<? super Double, ? extends V> function;

      protected PrimitiveFunction(Function<? super Double, ? extends V> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(double var1) {
         return this.function.apply(var1) != null;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsKey(Object var1) {
         if (var1 == null) {
            return false;
         } else {
            return this.function.apply((Double)var1) != null;
         }
      }

      public V get(double var1) {
         Object var3 = this.function.apply(var1);
         return var3 == null ? null : var3;
      }

      /** @deprecated */
      @Deprecated
      public V get(Object var1) {
         return var1 == null ? null : this.function.apply((Double)var1);
      }

      /** @deprecated */
      @Deprecated
      public V put(Double var1, V var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction<V> extends AbstractDouble2ObjectFunction<V> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ObjectFunction<V> function;

      protected UnmodifiableFunction(Double2ObjectFunction<V> var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
         }
      }

      public int size() {
         return this.function.size();
      }

      public V defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      public void defaultReturnValue(V var1) {
         throw new UnsupportedOperationException();
      }

      public boolean containsKey(double var1) {
         return this.function.containsKey(var1);
      }

      public V put(double var1, V var3) {
         throw new UnsupportedOperationException();
      }

      public V get(double var1) {
         return this.function.get(var1);
      }

      public V remove(double var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V put(Double var1, V var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V get(Object var1) {
         return this.function.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public V remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public int hashCode() {
         return this.function.hashCode();
      }

      public boolean equals(Object var1) {
         return var1 == this || this.function.equals(var1);
      }

      public String toString() {
         return this.function.toString();
      }
   }

   public static class SynchronizedFunction<V> implements Double2ObjectFunction<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ObjectFunction<V> function;
      protected final Object sync;

      protected SynchronizedFunction(Double2ObjectFunction<V> var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Double2ObjectFunction<V> var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      public V apply(double var1) {
         synchronized(this.sync) {
            return this.function.apply(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public V apply(Double var1) {
         synchronized(this.sync) {
            return this.function.apply(var1);
         }
      }

      public int size() {
         synchronized(this.sync) {
            return this.function.size();
         }
      }

      public V defaultReturnValue() {
         synchronized(this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      public void defaultReturnValue(V var1) {
         synchronized(this.sync) {
            this.function.defaultReturnValue(var1);
         }
      }

      public boolean containsKey(double var1) {
         synchronized(this.sync) {
            return this.function.containsKey(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean containsKey(Object var1) {
         synchronized(this.sync) {
            return this.function.containsKey(var1);
         }
      }

      public V put(double var1, V var3) {
         synchronized(this.sync) {
            return this.function.put(var1, var3);
         }
      }

      public V get(double var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public V remove(double var1) {
         synchronized(this.sync) {
            return this.function.remove(var1);
         }
      }

      public void clear() {
         synchronized(this.sync) {
            this.function.clear();
         }
      }

      /** @deprecated */
      @Deprecated
      public V put(Double var1, V var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V get(Object var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public V remove(Object var1) {
         synchronized(this.sync) {
            return this.function.remove(var1);
         }
      }

      public int hashCode() {
         synchronized(this.sync) {
            return this.function.hashCode();
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.sync) {
               return this.function.equals(var1);
            }
         }
      }

      public String toString() {
         synchronized(this.sync) {
            return this.function.toString();
         }
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         synchronized(this.sync) {
            var1.defaultWriteObject();
         }
      }
   }

   public static class Singleton<V> extends AbstractDouble2ObjectFunction<V> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final double key;
      protected final V value;

      protected Singleton(double var1, V var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public boolean containsKey(double var1) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(var1);
      }

      public V get(double var1) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(var1) ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction<V> extends AbstractDouble2ObjectFunction<V> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public V get(double var1) {
         return null;
      }

      public boolean containsKey(double var1) {
         return false;
      }

      public V defaultReturnValue() {
         return null;
      }

      public void defaultReturnValue(V var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public Object clone() {
         return Double2ObjectFunctions.EMPTY_FUNCTION;
      }

      public int hashCode() {
         return 0;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof it.unimi.dsi.fastutil.Function)) {
            return false;
         } else {
            return ((it.unimi.dsi.fastutil.Function)var1).size() == 0;
         }
      }

      public String toString() {
         return "{}";
      }

      private Object readResolve() {
         return Double2ObjectFunctions.EMPTY_FUNCTION;
      }
   }
}
