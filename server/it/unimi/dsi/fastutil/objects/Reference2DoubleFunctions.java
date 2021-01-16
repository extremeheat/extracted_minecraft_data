package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public final class Reference2DoubleFunctions {
   public static final Reference2DoubleFunctions.EmptyFunction EMPTY_FUNCTION = new Reference2DoubleFunctions.EmptyFunction();

   private Reference2DoubleFunctions() {
      super();
   }

   public static <K> Reference2DoubleFunction<K> singleton(K var0, double var1) {
      return new Reference2DoubleFunctions.Singleton(var0, var1);
   }

   public static <K> Reference2DoubleFunction<K> singleton(K var0, Double var1) {
      return new Reference2DoubleFunctions.Singleton(var0, var1);
   }

   public static <K> Reference2DoubleFunction<K> synchronize(Reference2DoubleFunction<K> var0) {
      return new Reference2DoubleFunctions.SynchronizedFunction(var0);
   }

   public static <K> Reference2DoubleFunction<K> synchronize(Reference2DoubleFunction<K> var0, Object var1) {
      return new Reference2DoubleFunctions.SynchronizedFunction(var0, var1);
   }

   public static <K> Reference2DoubleFunction<K> unmodifiable(Reference2DoubleFunction<K> var0) {
      return new Reference2DoubleFunctions.UnmodifiableFunction(var0);
   }

   public static <K> Reference2DoubleFunction<K> primitive(Function<? super K, ? extends Double> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Reference2DoubleFunction) {
         return (Reference2DoubleFunction)var0;
      } else {
         return (Reference2DoubleFunction)(var0 instanceof ToDoubleFunction ? (var1) -> {
            return ((ToDoubleFunction)var0).applyAsDouble(var1);
         } : new Reference2DoubleFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction<K> implements Reference2DoubleFunction<K> {
      protected final Function<? super K, ? extends Double> function;

      protected PrimitiveFunction(Function<? super K, ? extends Double> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(Object var1) {
         return this.function.apply(var1) != null;
      }

      public double getDouble(Object var1) {
         Double var2 = (Double)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Double get(Object var1) {
         return (Double)this.function.apply(var1);
      }

      /** @deprecated */
      @Deprecated
      public Double put(K var1, Double var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction<K> extends AbstractReference2DoubleFunction<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2DoubleFunction<K> function;

      protected UnmodifiableFunction(Reference2DoubleFunction<K> var1) {
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

      public double defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      public void defaultReturnValue(double var1) {
         throw new UnsupportedOperationException();
      }

      public boolean containsKey(Object var1) {
         return this.function.containsKey(var1);
      }

      public double put(K var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public double getDouble(Object var1) {
         return this.function.getDouble(var1);
      }

      public double removeDouble(Object var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double put(K var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double get(Object var1) {
         return this.function.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public Double remove(Object var1) {
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

   public static class SynchronizedFunction<K> implements Reference2DoubleFunction<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2DoubleFunction<K> function;
      protected final Object sync;

      protected SynchronizedFunction(Reference2DoubleFunction<K> var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Reference2DoubleFunction<K> var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      public double applyAsDouble(K var1) {
         synchronized(this.sync) {
            return this.function.applyAsDouble(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double apply(K var1) {
         synchronized(this.sync) {
            return (Double)this.function.apply(var1);
         }
      }

      public int size() {
         synchronized(this.sync) {
            return this.function.size();
         }
      }

      public double defaultReturnValue() {
         synchronized(this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      public void defaultReturnValue(double var1) {
         synchronized(this.sync) {
            this.function.defaultReturnValue(var1);
         }
      }

      public boolean containsKey(Object var1) {
         synchronized(this.sync) {
            return this.function.containsKey(var1);
         }
      }

      public double put(K var1, double var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public double getDouble(Object var1) {
         synchronized(this.sync) {
            return this.function.getDouble(var1);
         }
      }

      public double removeDouble(Object var1) {
         synchronized(this.sync) {
            return this.function.removeDouble(var1);
         }
      }

      public void clear() {
         synchronized(this.sync) {
            this.function.clear();
         }
      }

      /** @deprecated */
      @Deprecated
      public Double put(K var1, Double var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double get(Object var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double remove(Object var1) {
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

   public static class Singleton<K> extends AbstractReference2DoubleFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final double value;

      protected Singleton(K var1, double var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(Object var1) {
         return this.key == var1;
      }

      public double getDouble(Object var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction<K> extends AbstractReference2DoubleFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public double getDouble(Object var1) {
         return 0.0D;
      }

      public boolean containsKey(Object var1) {
         return false;
      }

      public double defaultReturnValue() {
         return 0.0D;
      }

      public void defaultReturnValue(double var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public Object clone() {
         return Reference2DoubleFunctions.EMPTY_FUNCTION;
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
         return Reference2DoubleFunctions.EMPTY_FUNCTION;
      }
   }
}
