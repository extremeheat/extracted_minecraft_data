package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

public final class Object2ReferenceFunctions {
   public static final Object2ReferenceFunctions.EmptyFunction EMPTY_FUNCTION = new Object2ReferenceFunctions.EmptyFunction();

   private Object2ReferenceFunctions() {
      super();
   }

   public static <K, V> Object2ReferenceFunction<K, V> singleton(K var0, V var1) {
      return new Object2ReferenceFunctions.Singleton(var0, var1);
   }

   public static <K, V> Object2ReferenceFunction<K, V> synchronize(Object2ReferenceFunction<K, V> var0) {
      return new Object2ReferenceFunctions.SynchronizedFunction(var0);
   }

   public static <K, V> Object2ReferenceFunction<K, V> synchronize(Object2ReferenceFunction<K, V> var0, Object var1) {
      return new Object2ReferenceFunctions.SynchronizedFunction(var0, var1);
   }

   public static <K, V> Object2ReferenceFunction<K, V> unmodifiable(Object2ReferenceFunction<K, V> var0) {
      return new Object2ReferenceFunctions.UnmodifiableFunction(var0);
   }

   public static class UnmodifiableFunction<K, V> extends AbstractObject2ReferenceFunction<K, V> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ReferenceFunction<K, V> function;

      protected UnmodifiableFunction(Object2ReferenceFunction<K, V> var1) {
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

      public boolean containsKey(Object var1) {
         return this.function.containsKey(var1);
      }

      public V put(K var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public V get(Object var1) {
         return this.function.get(var1);
      }

      public V remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
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

   public static class SynchronizedFunction<K, V> implements Object2ReferenceFunction<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ReferenceFunction<K, V> function;
      protected final Object sync;

      protected SynchronizedFunction(Object2ReferenceFunction<K, V> var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Object2ReferenceFunction<K, V> var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      public V apply(K var1) {
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

      public boolean containsKey(Object var1) {
         synchronized(this.sync) {
            return this.function.containsKey(var1);
         }
      }

      public V put(K var1, V var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public V get(Object var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public V remove(Object var1) {
         synchronized(this.sync) {
            return this.function.remove(var1);
         }
      }

      public void clear() {
         synchronized(this.sync) {
            this.function.clear();
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

   public static class Singleton<K, V> extends AbstractObject2ReferenceFunction<K, V> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final V value;

      protected Singleton(K var1, V var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(Object var1) {
         return Objects.equals(this.key, var1);
      }

      public V get(Object var1) {
         return Objects.equals(this.key, var1) ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction<K, V> extends AbstractObject2ReferenceFunction<K, V> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public V get(Object var1) {
         return null;
      }

      public boolean containsKey(Object var1) {
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
         return Object2ReferenceFunctions.EMPTY_FUNCTION;
      }

      public int hashCode() {
         return 0;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Function)) {
            return false;
         } else {
            return ((Function)var1).size() == 0;
         }
      }

      public String toString() {
         return "{}";
      }

      private Object readResolve() {
         return Object2ReferenceFunctions.EMPTY_FUNCTION;
      }
   }
}
