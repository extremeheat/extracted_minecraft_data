package it.unimi.dsi.fastutil.longs;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongToDoubleFunction;

public final class Long2DoubleFunctions {
   public static final Long2DoubleFunctions.EmptyFunction EMPTY_FUNCTION = new Long2DoubleFunctions.EmptyFunction();

   private Long2DoubleFunctions() {
      super();
   }

   public static Long2DoubleFunction singleton(long var0, double var2) {
      return new Long2DoubleFunctions.Singleton(var0, var2);
   }

   public static Long2DoubleFunction singleton(Long var0, Double var1) {
      return new Long2DoubleFunctions.Singleton(var0, var1);
   }

   public static Long2DoubleFunction synchronize(Long2DoubleFunction var0) {
      return new Long2DoubleFunctions.SynchronizedFunction(var0);
   }

   public static Long2DoubleFunction synchronize(Long2DoubleFunction var0, Object var1) {
      return new Long2DoubleFunctions.SynchronizedFunction(var0, var1);
   }

   public static Long2DoubleFunction unmodifiable(Long2DoubleFunction var0) {
      return new Long2DoubleFunctions.UnmodifiableFunction(var0);
   }

   public static Long2DoubleFunction primitive(Function<? super Long, ? extends Double> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Long2DoubleFunction) {
         return (Long2DoubleFunction)var0;
      } else if (var0 instanceof LongToDoubleFunction) {
         LongToDoubleFunction var10000 = (LongToDoubleFunction)var0;
         Objects.requireNonNull((LongToDoubleFunction)var0);
         return var10000::applyAsDouble;
      } else {
         return new Long2DoubleFunctions.PrimitiveFunction(var0);
      }
   }

   public static class PrimitiveFunction implements Long2DoubleFunction {
      protected final Function<? super Long, ? extends Double> function;

      protected PrimitiveFunction(Function<? super Long, ? extends Double> var1) {
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

      public double get(long var1) {
         Double var3 = (Double)this.function.apply(var1);
         return var3 == null ? this.defaultReturnValue() : var3;
      }

      /** @deprecated */
      @Deprecated
      public Double get(Object var1) {
         return var1 == null ? null : (Double)this.function.apply((Long)var1);
      }

      /** @deprecated */
      @Deprecated
      public Double put(Long var1, Double var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractLong2DoubleFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2DoubleFunction function;

      protected UnmodifiableFunction(Long2DoubleFunction var1) {
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

      public boolean containsKey(long var1) {
         return this.function.containsKey(var1);
      }

      public double put(long var1, double var3) {
         throw new UnsupportedOperationException();
      }

      public double get(long var1) {
         return this.function.get(var1);
      }

      public double remove(long var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double put(Long var1, Double var2) {
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

   public static class SynchronizedFunction implements Long2DoubleFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2DoubleFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Long2DoubleFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Long2DoubleFunction var1) {
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
      public Double apply(Long var1) {
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

      public double put(long var1, double var3) {
         synchronized(this.sync) {
            return this.function.put(var1, var3);
         }
      }

      public double get(long var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public double remove(long var1) {
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
      public Double put(Long var1, Double var2) {
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

   public static class Singleton extends AbstractLong2DoubleFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final long key;
      protected final double value;

      protected Singleton(long var1, double var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public boolean containsKey(long var1) {
         return this.key == var1;
      }

      public double get(long var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractLong2DoubleFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public double get(long var1) {
         return 0.0D;
      }

      public boolean containsKey(long var1) {
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
         return Long2DoubleFunctions.EMPTY_FUNCTION;
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
         return Long2DoubleFunctions.EMPTY_FUNCTION;
      }
   }
}
