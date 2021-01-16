package it.unimi.dsi.fastutil.shorts;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

public final class Short2DoubleFunctions {
   public static final Short2DoubleFunctions.EmptyFunction EMPTY_FUNCTION = new Short2DoubleFunctions.EmptyFunction();

   private Short2DoubleFunctions() {
      super();
   }

   public static Short2DoubleFunction singleton(short var0, double var1) {
      return new Short2DoubleFunctions.Singleton(var0, var1);
   }

   public static Short2DoubleFunction singleton(Short var0, Double var1) {
      return new Short2DoubleFunctions.Singleton(var0, var1);
   }

   public static Short2DoubleFunction synchronize(Short2DoubleFunction var0) {
      return new Short2DoubleFunctions.SynchronizedFunction(var0);
   }

   public static Short2DoubleFunction synchronize(Short2DoubleFunction var0, Object var1) {
      return new Short2DoubleFunctions.SynchronizedFunction(var0, var1);
   }

   public static Short2DoubleFunction unmodifiable(Short2DoubleFunction var0) {
      return new Short2DoubleFunctions.UnmodifiableFunction(var0);
   }

   public static Short2DoubleFunction primitive(Function<? super Short, ? extends Double> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Short2DoubleFunction) {
         return (Short2DoubleFunction)var0;
      } else if (var0 instanceof IntToDoubleFunction) {
         IntToDoubleFunction var10000 = (IntToDoubleFunction)var0;
         Objects.requireNonNull((IntToDoubleFunction)var0);
         return var10000::applyAsDouble;
      } else {
         return new Short2DoubleFunctions.PrimitiveFunction(var0);
      }
   }

   public static class PrimitiveFunction implements Short2DoubleFunction {
      protected final Function<? super Short, ? extends Double> function;

      protected PrimitiveFunction(Function<? super Short, ? extends Double> var1) {
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

      public double get(short var1) {
         Double var2 = (Double)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Double get(Object var1) {
         return var1 == null ? null : (Double)this.function.apply((Short)var1);
      }

      /** @deprecated */
      @Deprecated
      public Double put(Short var1, Double var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractShort2DoubleFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2DoubleFunction function;

      protected UnmodifiableFunction(Short2DoubleFunction var1) {
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

      public boolean containsKey(short var1) {
         return this.function.containsKey(var1);
      }

      public double put(short var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public double get(short var1) {
         return this.function.get(var1);
      }

      public double remove(short var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double put(Short var1, Double var2) {
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

   public static class SynchronizedFunction implements Short2DoubleFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2DoubleFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Short2DoubleFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Short2DoubleFunction var1) {
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
      public Double apply(Short var1) {
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

      public double put(short var1, double var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public double get(short var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public double remove(short var1) {
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
      public Double put(Short var1, Double var2) {
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

   public static class Singleton extends AbstractShort2DoubleFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final short key;
      protected final double value;

      protected Singleton(short var1, double var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(short var1) {
         return this.key == var1;
      }

      public double get(short var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractShort2DoubleFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public double get(short var1) {
         return 0.0D;
      }

      public boolean containsKey(short var1) {
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
         return Short2DoubleFunctions.EMPTY_FUNCTION;
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
         return Short2DoubleFunctions.EMPTY_FUNCTION;
      }
   }
}
