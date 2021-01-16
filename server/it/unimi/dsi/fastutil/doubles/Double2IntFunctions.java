package it.unimi.dsi.fastutil.doubles;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Double2IntFunctions {
   public static final Double2IntFunctions.EmptyFunction EMPTY_FUNCTION = new Double2IntFunctions.EmptyFunction();

   private Double2IntFunctions() {
      super();
   }

   public static Double2IntFunction singleton(double var0, int var2) {
      return new Double2IntFunctions.Singleton(var0, var2);
   }

   public static Double2IntFunction singleton(Double var0, Integer var1) {
      return new Double2IntFunctions.Singleton(var0, var1);
   }

   public static Double2IntFunction synchronize(Double2IntFunction var0) {
      return new Double2IntFunctions.SynchronizedFunction(var0);
   }

   public static Double2IntFunction synchronize(Double2IntFunction var0, Object var1) {
      return new Double2IntFunctions.SynchronizedFunction(var0, var1);
   }

   public static Double2IntFunction unmodifiable(Double2IntFunction var0) {
      return new Double2IntFunctions.UnmodifiableFunction(var0);
   }

   public static Double2IntFunction primitive(Function<? super Double, ? extends Integer> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Double2IntFunction) {
         return (Double2IntFunction)var0;
      } else if (var0 instanceof DoubleToIntFunction) {
         DoubleToIntFunction var10000 = (DoubleToIntFunction)var0;
         Objects.requireNonNull((DoubleToIntFunction)var0);
         return var10000::applyAsInt;
      } else {
         return new Double2IntFunctions.PrimitiveFunction(var0);
      }
   }

   public static class PrimitiveFunction implements Double2IntFunction {
      protected final Function<? super Double, ? extends Integer> function;

      protected PrimitiveFunction(Function<? super Double, ? extends Integer> var1) {
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

      public int get(double var1) {
         Integer var3 = (Integer)this.function.apply(var1);
         return var3 == null ? this.defaultReturnValue() : var3;
      }

      /** @deprecated */
      @Deprecated
      public Integer get(Object var1) {
         return var1 == null ? null : (Integer)this.function.apply((Double)var1);
      }

      /** @deprecated */
      @Deprecated
      public Integer put(Double var1, Integer var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractDouble2IntFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2IntFunction function;

      protected UnmodifiableFunction(Double2IntFunction var1) {
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

      public boolean containsKey(double var1) {
         return this.function.containsKey(var1);
      }

      public int put(double var1, int var3) {
         throw new UnsupportedOperationException();
      }

      public int get(double var1) {
         return this.function.get(var1);
      }

      public int remove(double var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer put(Double var1, Integer var2) {
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

   public static class SynchronizedFunction implements Double2IntFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2IntFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Double2IntFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Double2IntFunction var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      public int applyAsInt(double var1) {
         synchronized(this.sync) {
            return this.function.applyAsInt(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer apply(Double var1) {
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

      public int put(double var1, int var3) {
         synchronized(this.sync) {
            return this.function.put(var1, var3);
         }
      }

      public int get(double var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public int remove(double var1) {
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
      public Integer put(Double var1, Integer var2) {
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

   public static class Singleton extends AbstractDouble2IntFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final double key;
      protected final int value;

      protected Singleton(double var1, int var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public boolean containsKey(double var1) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(var1);
      }

      public int get(double var1) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(var1) ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractDouble2IntFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public int get(double var1) {
         return 0;
      }

      public boolean containsKey(double var1) {
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
         return Double2IntFunctions.EMPTY_FUNCTION;
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
         return Double2IntFunctions.EMPTY_FUNCTION;
      }
   }
}
