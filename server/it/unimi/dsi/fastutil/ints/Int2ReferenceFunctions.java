package it.unimi.dsi.fastutil.ints;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class Int2ReferenceFunctions {
   public static final Int2ReferenceFunctions.EmptyFunction EMPTY_FUNCTION = new Int2ReferenceFunctions.EmptyFunction();

   private Int2ReferenceFunctions() {
      super();
   }

   public static <V> Int2ReferenceFunction<V> singleton(int var0, V var1) {
      return new Int2ReferenceFunctions.Singleton(var0, var1);
   }

   public static <V> Int2ReferenceFunction<V> singleton(Integer var0, V var1) {
      return new Int2ReferenceFunctions.Singleton(var0, var1);
   }

   public static <V> Int2ReferenceFunction<V> synchronize(Int2ReferenceFunction<V> var0) {
      return new Int2ReferenceFunctions.SynchronizedFunction(var0);
   }

   public static <V> Int2ReferenceFunction<V> synchronize(Int2ReferenceFunction<V> var0, Object var1) {
      return new Int2ReferenceFunctions.SynchronizedFunction(var0, var1);
   }

   public static <V> Int2ReferenceFunction<V> unmodifiable(Int2ReferenceFunction<V> var0) {
      return new Int2ReferenceFunctions.UnmodifiableFunction(var0);
   }

   public static <V> Int2ReferenceFunction<V> primitive(Function<? super Integer, ? extends V> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Int2ReferenceFunction) {
         return (Int2ReferenceFunction)var0;
      } else if (var0 instanceof IntFunction) {
         IntFunction var10000 = (IntFunction)var0;
         Objects.requireNonNull((IntFunction)var0);
         return var10000::apply;
      } else {
         return new Int2ReferenceFunctions.PrimitiveFunction(var0);
      }
   }

   public static class PrimitiveFunction<V> implements Int2ReferenceFunction<V> {
      protected final Function<? super Integer, ? extends V> function;

      protected PrimitiveFunction(Function<? super Integer, ? extends V> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(int var1) {
         return this.function.apply(var1) != null;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsKey(Object var1) {
         if (var1 == null) {
            return false;
         } else {
            return this.function.apply((Integer)var1) != null;
         }
      }

      public V get(int var1) {
         Object var2 = this.function.apply(var1);
         return var2 == null ? null : var2;
      }

      /** @deprecated */
      @Deprecated
      public V get(Object var1) {
         return var1 == null ? null : this.function.apply((Integer)var1);
      }

      /** @deprecated */
      @Deprecated
      public V put(Integer var1, V var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction<V> extends AbstractInt2ReferenceFunction<V> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2ReferenceFunction<V> function;

      protected UnmodifiableFunction(Int2ReferenceFunction<V> var1) {
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

      public boolean containsKey(int var1) {
         return this.function.containsKey(var1);
      }

      public V put(int var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public V get(int var1) {
         return this.function.get(var1);
      }

      public V remove(int var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V put(Integer var1, V var2) {
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

   public static class SynchronizedFunction<V> implements Int2ReferenceFunction<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2ReferenceFunction<V> function;
      protected final Object sync;

      protected SynchronizedFunction(Int2ReferenceFunction<V> var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Int2ReferenceFunction<V> var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      public V apply(int var1) {
         synchronized(this.sync) {
            return this.function.apply(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public V apply(Integer var1) {
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

      public boolean containsKey(int var1) {
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

      public V put(int var1, V var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public V get(int var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public V remove(int var1) {
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
      public V put(Integer var1, V var2) {
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

   public static class Singleton<V> extends AbstractInt2ReferenceFunction<V> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final int key;
      protected final V value;

      protected Singleton(int var1, V var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(int var1) {
         return this.key == var1;
      }

      public V get(int var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction<V> extends AbstractInt2ReferenceFunction<V> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public V get(int var1) {
         return null;
      }

      public boolean containsKey(int var1) {
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
         return Int2ReferenceFunctions.EMPTY_FUNCTION;
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
         return Int2ReferenceFunctions.EMPTY_FUNCTION;
      }
   }
}
