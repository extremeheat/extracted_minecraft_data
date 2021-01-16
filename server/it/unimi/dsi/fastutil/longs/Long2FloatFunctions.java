package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongToDoubleFunction;

public final class Long2FloatFunctions {
   public static final Long2FloatFunctions.EmptyFunction EMPTY_FUNCTION = new Long2FloatFunctions.EmptyFunction();

   private Long2FloatFunctions() {
      super();
   }

   public static Long2FloatFunction singleton(long var0, float var2) {
      return new Long2FloatFunctions.Singleton(var0, var2);
   }

   public static Long2FloatFunction singleton(Long var0, Float var1) {
      return new Long2FloatFunctions.Singleton(var0, var1);
   }

   public static Long2FloatFunction synchronize(Long2FloatFunction var0) {
      return new Long2FloatFunctions.SynchronizedFunction(var0);
   }

   public static Long2FloatFunction synchronize(Long2FloatFunction var0, Object var1) {
      return new Long2FloatFunctions.SynchronizedFunction(var0, var1);
   }

   public static Long2FloatFunction unmodifiable(Long2FloatFunction var0) {
      return new Long2FloatFunctions.UnmodifiableFunction(var0);
   }

   public static Long2FloatFunction primitive(Function<? super Long, ? extends Float> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Long2FloatFunction) {
         return (Long2FloatFunction)var0;
      } else {
         return (Long2FloatFunction)(var0 instanceof LongToDoubleFunction ? (var1) -> {
            return SafeMath.safeDoubleToFloat(((LongToDoubleFunction)var0).applyAsDouble(var1));
         } : new Long2FloatFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction implements Long2FloatFunction {
      protected final Function<? super Long, ? extends Float> function;

      protected PrimitiveFunction(Function<? super Long, ? extends Float> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(long var1) {
         return this.function.apply(var1) != null;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsKey(Object var1) {
         if (var1 == null) {
            return false;
         } else {
            return this.function.apply((Long)var1) != null;
         }
      }

      public float get(long var1) {
         Float var3 = (Float)this.function.apply(var1);
         return var3 == null ? this.defaultReturnValue() : var3;
      }

      /** @deprecated */
      @Deprecated
      public Float get(Object var1) {
         return var1 == null ? null : (Float)this.function.apply((Long)var1);
      }

      /** @deprecated */
      @Deprecated
      public Float put(Long var1, Float var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractLong2FloatFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2FloatFunction function;

      protected UnmodifiableFunction(Long2FloatFunction var1) {
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

      public boolean containsKey(long var1) {
         return this.function.containsKey(var1);
      }

      public float put(long var1, float var3) {
         throw new UnsupportedOperationException();
      }

      public float get(long var1) {
         return this.function.get(var1);
      }

      public float remove(long var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float put(Long var1, Float var2) {
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

   public static class SynchronizedFunction implements Long2FloatFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2FloatFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Long2FloatFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Long2FloatFunction var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      public double applyAsDouble(long var1) {
         synchronized(this.sync) {
            return this.function.applyAsDouble(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float apply(Long var1) {
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

      public boolean containsKey(long var1) {
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

      public float put(long var1, float var3) {
         synchronized(this.sync) {
            return this.function.put(var1, var3);
         }
      }

      public float get(long var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public float remove(long var1) {
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
      public Float put(Long var1, Float var2) {
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

   public static class Singleton extends AbstractLong2FloatFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final long key;
      protected final float value;

      protected Singleton(long var1, float var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public boolean containsKey(long var1) {
         return this.key == var1;
      }

      public float get(long var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractLong2FloatFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public float get(long var1) {
         return 0.0F;
      }

      public boolean containsKey(long var1) {
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
         return Long2FloatFunctions.EMPTY_FUNCTION;
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
         return Long2FloatFunctions.EMPTY_FUNCTION;
      }
   }
}
