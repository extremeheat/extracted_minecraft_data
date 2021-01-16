package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public final class Reference2FloatFunctions {
   public static final Reference2FloatFunctions.EmptyFunction EMPTY_FUNCTION = new Reference2FloatFunctions.EmptyFunction();

   private Reference2FloatFunctions() {
      super();
   }

   public static <K> Reference2FloatFunction<K> singleton(K var0, float var1) {
      return new Reference2FloatFunctions.Singleton(var0, var1);
   }

   public static <K> Reference2FloatFunction<K> singleton(K var0, Float var1) {
      return new Reference2FloatFunctions.Singleton(var0, var1);
   }

   public static <K> Reference2FloatFunction<K> synchronize(Reference2FloatFunction<K> var0) {
      return new Reference2FloatFunctions.SynchronizedFunction(var0);
   }

   public static <K> Reference2FloatFunction<K> synchronize(Reference2FloatFunction<K> var0, Object var1) {
      return new Reference2FloatFunctions.SynchronizedFunction(var0, var1);
   }

   public static <K> Reference2FloatFunction<K> unmodifiable(Reference2FloatFunction<K> var0) {
      return new Reference2FloatFunctions.UnmodifiableFunction(var0);
   }

   public static <K> Reference2FloatFunction<K> primitive(Function<? super K, ? extends Float> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Reference2FloatFunction) {
         return (Reference2FloatFunction)var0;
      } else {
         return (Reference2FloatFunction)(var0 instanceof ToDoubleFunction ? (var1) -> {
            return SafeMath.safeDoubleToFloat(((ToDoubleFunction)var0).applyAsDouble(var1));
         } : new Reference2FloatFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction<K> implements Reference2FloatFunction<K> {
      protected final Function<? super K, ? extends Float> function;

      protected PrimitiveFunction(Function<? super K, ? extends Float> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(Object var1) {
         return this.function.apply(var1) != null;
      }

      public float getFloat(Object var1) {
         Float var2 = (Float)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Float get(Object var1) {
         return (Float)this.function.apply(var1);
      }

      /** @deprecated */
      @Deprecated
      public Float put(K var1, Float var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction<K> extends AbstractReference2FloatFunction<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2FloatFunction<K> function;

      protected UnmodifiableFunction(Reference2FloatFunction<K> var1) {
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

      public float defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      public void defaultReturnValue(float var1) {
         throw new UnsupportedOperationException();
      }

      public boolean containsKey(Object var1) {
         return this.function.containsKey(var1);
      }

      public float put(K var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public float getFloat(Object var1) {
         return this.function.getFloat(var1);
      }

      public float removeFloat(Object var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float put(K var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float get(Object var1) {
         return this.function.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public Float remove(Object var1) {
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

   public static class SynchronizedFunction<K> implements Reference2FloatFunction<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2FloatFunction<K> function;
      protected final Object sync;

      protected SynchronizedFunction(Reference2FloatFunction<K> var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Reference2FloatFunction<K> var1) {
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
      public Float apply(K var1) {
         synchronized(this.sync) {
            return (Float)this.function.apply(var1);
         }
      }

      public int size() {
         synchronized(this.sync) {
            return this.function.size();
         }
      }

      public float defaultReturnValue() {
         synchronized(this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      public void defaultReturnValue(float var1) {
         synchronized(this.sync) {
            this.function.defaultReturnValue(var1);
         }
      }

      public boolean containsKey(Object var1) {
         synchronized(this.sync) {
            return this.function.containsKey(var1);
         }
      }

      public float put(K var1, float var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public float getFloat(Object var1) {
         synchronized(this.sync) {
            return this.function.getFloat(var1);
         }
      }

      public float removeFloat(Object var1) {
         synchronized(this.sync) {
            return this.function.removeFloat(var1);
         }
      }

      public void clear() {
         synchronized(this.sync) {
            this.function.clear();
         }
      }

      /** @deprecated */
      @Deprecated
      public Float put(K var1, Float var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float get(Object var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float remove(Object var1) {
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

   public static class Singleton<K> extends AbstractReference2FloatFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final float value;

      protected Singleton(K var1, float var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(Object var1) {
         return this.key == var1;
      }

      public float getFloat(Object var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction<K> extends AbstractReference2FloatFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public float getFloat(Object var1) {
         return 0.0F;
      }

      public boolean containsKey(Object var1) {
         return false;
      }

      public float defaultReturnValue() {
         return 0.0F;
      }

      public void defaultReturnValue(float var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public Object clone() {
         return Reference2FloatFunctions.EMPTY_FUNCTION;
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
         return Reference2FloatFunctions.EMPTY_FUNCTION;
      }
   }
}
