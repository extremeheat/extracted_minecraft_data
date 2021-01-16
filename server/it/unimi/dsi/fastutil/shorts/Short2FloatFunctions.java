package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

public final class Short2FloatFunctions {
   public static final Short2FloatFunctions.EmptyFunction EMPTY_FUNCTION = new Short2FloatFunctions.EmptyFunction();

   private Short2FloatFunctions() {
      super();
   }

   public static Short2FloatFunction singleton(short var0, float var1) {
      return new Short2FloatFunctions.Singleton(var0, var1);
   }

   public static Short2FloatFunction singleton(Short var0, Float var1) {
      return new Short2FloatFunctions.Singleton(var0, var1);
   }

   public static Short2FloatFunction synchronize(Short2FloatFunction var0) {
      return new Short2FloatFunctions.SynchronizedFunction(var0);
   }

   public static Short2FloatFunction synchronize(Short2FloatFunction var0, Object var1) {
      return new Short2FloatFunctions.SynchronizedFunction(var0, var1);
   }

   public static Short2FloatFunction unmodifiable(Short2FloatFunction var0) {
      return new Short2FloatFunctions.UnmodifiableFunction(var0);
   }

   public static Short2FloatFunction primitive(Function<? super Short, ? extends Float> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Short2FloatFunction) {
         return (Short2FloatFunction)var0;
      } else {
         return (Short2FloatFunction)(var0 instanceof IntToDoubleFunction ? (var1) -> {
            return SafeMath.safeDoubleToFloat(((IntToDoubleFunction)var0).applyAsDouble(var1));
         } : new Short2FloatFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction implements Short2FloatFunction {
      protected final Function<? super Short, ? extends Float> function;

      protected PrimitiveFunction(Function<? super Short, ? extends Float> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(short var1) {
         return this.function.apply(var1) != null;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsKey(Object var1) {
         if (var1 == null) {
            return false;
         } else {
            return this.function.apply((Short)var1) != null;
         }
      }

      public float get(short var1) {
         Float var2 = (Float)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Float get(Object var1) {
         return var1 == null ? null : (Float)this.function.apply((Short)var1);
      }

      /** @deprecated */
      @Deprecated
      public Float put(Short var1, Float var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractShort2FloatFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2FloatFunction function;

      protected UnmodifiableFunction(Short2FloatFunction var1) {
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

      public boolean containsKey(short var1) {
         return this.function.containsKey(var1);
      }

      public float put(short var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public float get(short var1) {
         return this.function.get(var1);
      }

      public float remove(short var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float put(Short var1, Float var2) {
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

   public static class SynchronizedFunction implements Short2FloatFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2FloatFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Short2FloatFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Short2FloatFunction var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      /** @deprecated */
      @Deprecated
      public double applyAsDouble(int var1) {
         synchronized(this.sync) {
            return this.function.applyAsDouble(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float apply(Short var1) {
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

      public boolean containsKey(short var1) {
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

      public float put(short var1, float var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public float get(short var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public float remove(short var1) {
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
      public Float put(Short var1, Float var2) {
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

   public static class Singleton extends AbstractShort2FloatFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final short key;
      protected final float value;

      protected Singleton(short var1, float var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(short var1) {
         return this.key == var1;
      }

      public float get(short var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractShort2FloatFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public float get(short var1) {
         return 0.0F;
      }

      public boolean containsKey(short var1) {
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
         return Short2FloatFunctions.EMPTY_FUNCTION;
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
         return Short2FloatFunctions.EMPTY_FUNCTION;
      }
   }
}
