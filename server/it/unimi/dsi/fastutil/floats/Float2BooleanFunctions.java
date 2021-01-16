package it.unimi.dsi.fastutil.floats;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Function;

public final class Float2BooleanFunctions {
   public static final Float2BooleanFunctions.EmptyFunction EMPTY_FUNCTION = new Float2BooleanFunctions.EmptyFunction();

   private Float2BooleanFunctions() {
      super();
   }

   public static Float2BooleanFunction singleton(float var0, boolean var1) {
      return new Float2BooleanFunctions.Singleton(var0, var1);
   }

   public static Float2BooleanFunction singleton(Float var0, Boolean var1) {
      return new Float2BooleanFunctions.Singleton(var0, var1);
   }

   public static Float2BooleanFunction synchronize(Float2BooleanFunction var0) {
      return new Float2BooleanFunctions.SynchronizedFunction(var0);
   }

   public static Float2BooleanFunction synchronize(Float2BooleanFunction var0, Object var1) {
      return new Float2BooleanFunctions.SynchronizedFunction(var0, var1);
   }

   public static Float2BooleanFunction unmodifiable(Float2BooleanFunction var0) {
      return new Float2BooleanFunctions.UnmodifiableFunction(var0);
   }

   public static Float2BooleanFunction primitive(Function<? super Float, ? extends Boolean> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Float2BooleanFunction) {
         return (Float2BooleanFunction)var0;
      } else if (var0 instanceof DoublePredicate) {
         DoublePredicate var10000 = (DoublePredicate)var0;
         Objects.requireNonNull((DoublePredicate)var0);
         return var10000::test;
      } else {
         return new Float2BooleanFunctions.PrimitiveFunction(var0);
      }
   }

   public static class PrimitiveFunction implements Float2BooleanFunction {
      protected final Function<? super Float, ? extends Boolean> function;

      protected PrimitiveFunction(Function<? super Float, ? extends Boolean> var1) {
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

      public boolean get(float var1) {
         Boolean var2 = (Boolean)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Boolean get(Object var1) {
         return var1 == null ? null : (Boolean)this.function.apply((Float)var1);
      }

      /** @deprecated */
      @Deprecated
      public Boolean put(Float var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractFloat2BooleanFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2BooleanFunction function;

      protected UnmodifiableFunction(Float2BooleanFunction var1) {
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

      public boolean defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      public void defaultReturnValue(boolean var1) {
         throw new UnsupportedOperationException();
      }

      public boolean containsKey(float var1) {
         return this.function.containsKey(var1);
      }

      public boolean put(float var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean get(float var1) {
         return this.function.get(var1);
      }

      public boolean remove(float var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean put(Float var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean get(Object var1) {
         return this.function.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public Boolean remove(Object var1) {
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

   public static class SynchronizedFunction implements Float2BooleanFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2BooleanFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Float2BooleanFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Float2BooleanFunction var1) {
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
      public boolean test(double var1) {
         synchronized(this.sync) {
            return this.function.test(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean apply(Float var1) {
         synchronized(this.sync) {
            return (Boolean)this.function.apply(var1);
         }
      }

      public int size() {
         synchronized(this.sync) {
            return this.function.size();
         }
      }

      public boolean defaultReturnValue() {
         synchronized(this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      public void defaultReturnValue(boolean var1) {
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

      public boolean put(float var1, boolean var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public boolean get(float var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public boolean remove(float var1) {
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
      public Boolean put(Float var1, Boolean var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean get(Object var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean remove(Object var1) {
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

   public static class Singleton extends AbstractFloat2BooleanFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final float key;
      protected final boolean value;

      protected Singleton(float var1, boolean var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(float var1) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(var1);
      }

      public boolean get(float var1) {
         return Float.floatToIntBits(this.key) == Float.floatToIntBits(var1) ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractFloat2BooleanFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public boolean get(float var1) {
         return false;
      }

      public boolean containsKey(float var1) {
         return false;
      }

      public boolean defaultReturnValue() {
         return false;
      }

      public void defaultReturnValue(boolean var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public Object clone() {
         return Float2BooleanFunctions.EMPTY_FUNCTION;
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
         return Float2BooleanFunctions.EMPTY_FUNCTION;
      }
   }
}
