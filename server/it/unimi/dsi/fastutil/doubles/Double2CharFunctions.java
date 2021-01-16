package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Double2CharFunctions {
   public static final Double2CharFunctions.EmptyFunction EMPTY_FUNCTION = new Double2CharFunctions.EmptyFunction();

   private Double2CharFunctions() {
      super();
   }

   public static Double2CharFunction singleton(double var0, char var2) {
      return new Double2CharFunctions.Singleton(var0, var2);
   }

   public static Double2CharFunction singleton(Double var0, Character var1) {
      return new Double2CharFunctions.Singleton(var0, var1);
   }

   public static Double2CharFunction synchronize(Double2CharFunction var0) {
      return new Double2CharFunctions.SynchronizedFunction(var0);
   }

   public static Double2CharFunction synchronize(Double2CharFunction var0, Object var1) {
      return new Double2CharFunctions.SynchronizedFunction(var0, var1);
   }

   public static Double2CharFunction unmodifiable(Double2CharFunction var0) {
      return new Double2CharFunctions.UnmodifiableFunction(var0);
   }

   public static Double2CharFunction primitive(Function<? super Double, ? extends Character> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Double2CharFunction) {
         return (Double2CharFunction)var0;
      } else {
         return (Double2CharFunction)(var0 instanceof DoubleToIntFunction ? (var1) -> {
            return SafeMath.safeIntToChar(((DoubleToIntFunction)var0).applyAsInt(var1));
         } : new Double2CharFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction implements Double2CharFunction {
      protected final Function<? super Double, ? extends Character> function;

      protected PrimitiveFunction(Function<? super Double, ? extends Character> var1) {
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

      public char get(double var1) {
         Character var3 = (Character)this.function.apply(var1);
         return var3 == null ? this.defaultReturnValue() : var3;
      }

      /** @deprecated */
      @Deprecated
      public Character get(Object var1) {
         return var1 == null ? null : (Character)this.function.apply((Double)var1);
      }

      /** @deprecated */
      @Deprecated
      public Character put(Double var1, Character var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractDouble2CharFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2CharFunction function;

      protected UnmodifiableFunction(Double2CharFunction var1) {
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

      public char defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      public void defaultReturnValue(char var1) {
         throw new UnsupportedOperationException();
      }

      public boolean containsKey(double var1) {
         return this.function.containsKey(var1);
      }

      public char put(double var1, char var3) {
         throw new UnsupportedOperationException();
      }

      public char get(double var1) {
         return this.function.get(var1);
      }

      public char remove(double var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character put(Double var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character get(Object var1) {
         return this.function.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public Character remove(Object var1) {
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

   public static class SynchronizedFunction implements Double2CharFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2CharFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Double2CharFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Double2CharFunction var1) {
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
      public Character apply(Double var1) {
         synchronized(this.sync) {
            return (Character)this.function.apply(var1);
         }
      }

      public int size() {
         synchronized(this.sync) {
            return this.function.size();
         }
      }

      public char defaultReturnValue() {
         synchronized(this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      public void defaultReturnValue(char var1) {
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

      public char put(double var1, char var3) {
         synchronized(this.sync) {
            return this.function.put(var1, var3);
         }
      }

      public char get(double var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public char remove(double var1) {
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
      public Character put(Double var1, Character var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character get(Object var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character remove(Object var1) {
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

   public static class Singleton extends AbstractDouble2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final double key;
      protected final char value;

      protected Singleton(double var1, char var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public boolean containsKey(double var1) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(var1);
      }

      public char get(double var1) {
         return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(var1) ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractDouble2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public char get(double var1) {
         return '\u0000';
      }

      public boolean containsKey(double var1) {
         return false;
      }

      public char defaultReturnValue() {
         return '\u0000';
      }

      public void defaultReturnValue(char var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public Object clone() {
         return Double2CharFunctions.EMPTY_FUNCTION;
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
         return Double2CharFunctions.EMPTY_FUNCTION;
      }
   }
}
