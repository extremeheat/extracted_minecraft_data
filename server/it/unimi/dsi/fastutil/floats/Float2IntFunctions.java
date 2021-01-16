package it.unimi.dsi.fastutil.floats;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Float2IntFunctions {
   public static final Float2IntFunctions.EmptyFunction EMPTY_FUNCTION = new Float2IntFunctions.EmptyFunction();

   private Float2IntFunctions() {
      super();
   }

   public static Float2IntFunction singleton(float var0, int var1) {
      return new Float2IntFunctions.Singleton(var0, var1);
   }

   public static Float2IntFunction singleton(Float var0, Integer var1) {
      return new Float2IntFunctions.Singleton(var0, var1);
   }

   public static Float2IntFunction synchronize(Float2IntFunction var0) {
      return new Float2IntFunctions.SynchronizedFunction(var0);
   }

   public static Float2IntFunction synchronize(Float2IntFunction var0, Object var1) {
      return new Float2IntFunctions.SynchronizedFunction(var0, var1);
   }

   public static Float2IntFunction unmodifiable(Float2IntFunction var0) {
      return new Float2IntFunctions.UnmodifiableFunction(var0);
   }

   public static Float2IntFunction primitive(Function<? super Float, ? extends Integer> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Float2IntFunction) {
         return (Float2IntFunction)var0;
      } else if (var0 instanceof DoubleToIntFunction) {
         DoubleToIntFunction var10000 = (DoubleToIntFunction)var0;
         Objects.requireNonNull((DoubleToIntFunction)var0);
         return var10000::applyAsInt;
      } else {
         return new Float2IntFunctions.PrimitiveFunction(var0);
      }
   }

   public static class PrimitiveFunction implements Float2IntFunction {
      protected final Function<? super Float, ? extends Integer> function;

      protected PrimitiveFunction(Function<? super Float, ? extends Integer> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(float var1) {
         return this.function.apply(var1) != null;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsKey(Object var1) {
         if (var1 == null) {
            return false;
         } else {
            return this.function.apply((Float)var1) != null;
         }
      }

      public int get(float var1) {
         Integer var2 = (Integer)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Integer get(Object var1) {
         return var1 == null ? null : (Integer)this.function.apply((Float)var1);
      }

      /** @deprecated */
      @Deprecated
      public Integer put(Float var1, Integer var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractFloat2IntFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2IntFunction function;

      protected UnmodifiableFunction(Float2IntFunction var1) {
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

      public int defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      public void defaultReturnValue(int var1) {
         throw new UnsupportedOperationException();
      }

      public boolean containsKey(float var1) {
         return this.function.containsKey(var1);
      }

      public int put(float var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public int get(float var1) {
         return this.function.get(var1);
      }

      public int remove(float var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer put(Float var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer get(Object var1) {
         return this.function.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public Integer remove(Object var1) {
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

   public static class SynchronizedFunction implements Float2IntFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2IntFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Float2IntFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Float2IntFunction var1) {
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
      public int applyAsInt(double var1) {
         synchronized(this.sync) {
            return this.function.applyAsInt(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer apply(Float var1) {
         synchronized(this.sync) {
            return (Integer)this.function.apply(var1);
         }
      }

      public int size() {
         synchronized(this.sync) {
            return this.function.size();
         }
      }

      public int defaultReturnValue() {
         synchronized(this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      public void defaultReturnValue(int var1) {
         synchronized(this.sync) {
            this.function.defaultReturnValue(var1);
         }
      }

      public boolean containsKey(float var1) {
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

      public int put(float var1, int var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public int get(float var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public int remove(float var1) {
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
      public Integer put(Float var1, Integer var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer get(Object var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer remove(Object var1) {
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

   public static class Singleton extends AbstractFloat2IntFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final float key;
      protected final int value;

      protected Singleton(float var1, int var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(float var1) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(var1);
      }

      public int get(float var1) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(var1) ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractFloat2IntFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public int get(float var1) {
         return 0;
      }

      public boolean containsKey(float var1) {
         return false;
      }

      public int defaultReturnValue() {
         return 0;
      }

      public void defaultReturnValue(int var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public Object clone() {
         return Float2IntFunctions.EMPTY_FUNCTION;
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
         return Float2IntFunctions.EMPTY_FUNCTION;
      }
   }
}
