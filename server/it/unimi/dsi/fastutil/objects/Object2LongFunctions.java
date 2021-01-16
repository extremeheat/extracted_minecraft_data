package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToLongFunction;

public final class Object2LongFunctions {
   public static final Object2LongFunctions.EmptyFunction EMPTY_FUNCTION = new Object2LongFunctions.EmptyFunction();

   private Object2LongFunctions() {
      super();
   }

   public static <K> Object2LongFunction<K> singleton(K var0, long var1) {
      return new Object2LongFunctions.Singleton(var0, var1);
   }

   public static <K> Object2LongFunction<K> singleton(K var0, Long var1) {
      return new Object2LongFunctions.Singleton(var0, var1);
   }

   public static <K> Object2LongFunction<K> synchronize(Object2LongFunction<K> var0) {
      return new Object2LongFunctions.SynchronizedFunction(var0);
   }

   public static <K> Object2LongFunction<K> synchronize(Object2LongFunction<K> var0, Object var1) {
      return new Object2LongFunctions.SynchronizedFunction(var0, var1);
   }

   public static <K> Object2LongFunction<K> unmodifiable(Object2LongFunction<K> var0) {
      return new Object2LongFunctions.UnmodifiableFunction(var0);
   }

   public static <K> Object2LongFunction<K> primitive(Function<? super K, ? extends Long> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Object2LongFunction) {
         return (Object2LongFunction)var0;
      } else {
         return (Object2LongFunction)(var0 instanceof ToLongFunction ? (var1) -> {
            return ((ToLongFunction)var0).applyAsLong(var1);
         } : new Object2LongFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction<K> implements Object2LongFunction<K> {
      protected final Function<? super K, ? extends Long> function;

      protected PrimitiveFunction(Function<? super K, ? extends Long> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(Object var1) {
         return this.function.apply(var1) != null;
      }

      public long getLong(Object var1) {
         Long var2 = (Long)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Long get(Object var1) {
         return (Long)this.function.apply(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long put(K var1, Long var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction<K> extends AbstractObject2LongFunction<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2LongFunction<K> function;

      protected UnmodifiableFunction(Object2LongFunction<K> var1) {
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

      public long defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      public void defaultReturnValue(long var1) {
         throw new UnsupportedOperationException();
      }

      public boolean containsKey(Object var1) {
         return this.function.containsKey(var1);
      }

      public long put(K var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public long getLong(Object var1) {
         return this.function.getLong(var1);
      }

      public long removeLong(Object var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long put(K var1, Long var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long get(Object var1) {
         return this.function.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long remove(Object var1) {
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

   public static class SynchronizedFunction<K> implements Object2LongFunction<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2LongFunction<K> function;
      protected final Object sync;

      protected SynchronizedFunction(Object2LongFunction<K> var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Object2LongFunction<K> var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      public long applyAsLong(K var1) {
         synchronized(this.sync) {
            return this.function.applyAsLong(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long apply(K var1) {
         synchronized(this.sync) {
            return (Long)this.function.apply(var1);
         }
      }

      public int size() {
         synchronized(this.sync) {
            return this.function.size();
         }
      }

      public long defaultReturnValue() {
         synchronized(this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      public void defaultReturnValue(long var1) {
         synchronized(this.sync) {
            this.function.defaultReturnValue(var1);
         }
      }

      public boolean containsKey(Object var1) {
         synchronized(this.sync) {
            return this.function.containsKey(var1);
         }
      }

      public long put(K var1, long var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public long getLong(Object var1) {
         synchronized(this.sync) {
            return this.function.getLong(var1);
         }
      }

      public long removeLong(Object var1) {
         synchronized(this.sync) {
            return this.function.removeLong(var1);
         }
      }

      public void clear() {
         synchronized(this.sync) {
            this.function.clear();
         }
      }

      /** @deprecated */
      @Deprecated
      public Long put(K var1, Long var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long get(Object var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long remove(Object var1) {
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

   public static class Singleton<K> extends AbstractObject2LongFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final long value;

      protected Singleton(K var1, long var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(Object var1) {
         return Objects.equals(this.key, var1);
      }

      public long getLong(Object var1) {
         return Objects.equals(this.key, var1) ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction<K> extends AbstractObject2LongFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public long getLong(Object var1) {
         return 0L;
      }

      public boolean containsKey(Object var1) {
         return false;
      }

      public long defaultReturnValue() {
         return 0L;
      }

      public void defaultReturnValue(long var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public Object clone() {
         return Object2LongFunctions.EMPTY_FUNCTION;
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
         return Object2LongFunctions.EMPTY_FUNCTION;
      }
   }
}
