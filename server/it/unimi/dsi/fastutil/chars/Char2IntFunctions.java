package it.unimi.dsi.fastutil.chars;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

public final class Char2IntFunctions {
   public static final Char2IntFunctions.EmptyFunction EMPTY_FUNCTION = new Char2IntFunctions.EmptyFunction();

   private Char2IntFunctions() {
      super();
   }

   public static Char2IntFunction singleton(char var0, int var1) {
      return new Char2IntFunctions.Singleton(var0, var1);
   }

   public static Char2IntFunction singleton(Character var0, Integer var1) {
      return new Char2IntFunctions.Singleton(var0, var1);
   }

   public static Char2IntFunction synchronize(Char2IntFunction var0) {
      return new Char2IntFunctions.SynchronizedFunction(var0);
   }

   public static Char2IntFunction synchronize(Char2IntFunction var0, Object var1) {
      return new Char2IntFunctions.SynchronizedFunction(var0, var1);
   }

   public static Char2IntFunction unmodifiable(Char2IntFunction var0) {
      return new Char2IntFunctions.UnmodifiableFunction(var0);
   }

   public static Char2IntFunction primitive(Function<? super Character, ? extends Integer> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Char2IntFunction) {
         return (Char2IntFunction)var0;
      } else if (var0 instanceof IntUnaryOperator) {
         IntUnaryOperator var10000 = (IntUnaryOperator)var0;
         Objects.requireNonNull((IntUnaryOperator)var0);
         return var10000::applyAsInt;
      } else {
         return new Char2IntFunctions.PrimitiveFunction(var0);
      }
   }

   public static class PrimitiveFunction implements Char2IntFunction {
      protected final Function<? super Character, ? extends Integer> function;

      protected PrimitiveFunction(Function<? super Character, ? extends Integer> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(char var1) {
         return this.function.apply(var1) != null;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsKey(Object var1) {
         if (var1 == null) {
            return false;
         } else {
            return this.function.apply((Character)var1) != null;
         }
      }

      public int get(char var1) {
         Integer var2 = (Integer)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Integer get(Object var1) {
         return var1 == null ? null : (Integer)this.function.apply((Character)var1);
      }

      /** @deprecated */
      @Deprecated
      public Integer put(Character var1, Integer var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractChar2IntFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2IntFunction function;

      protected UnmodifiableFunction(Char2IntFunction var1) {
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

      public boolean containsKey(char var1) {
         return this.function.containsKey(var1);
      }

      public int put(char var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public int get(char var1) {
         return this.function.get(var1);
      }

      public int remove(char var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer put(Character var1, Integer var2) {
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

   public static class SynchronizedFunction implements Char2IntFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2IntFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Char2IntFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Char2IntFunction var1) {
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
      public int applyAsInt(int var1) {
         synchronized(this.sync) {
            return this.function.applyAsInt(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer apply(Character var1) {
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

      public boolean containsKey(char var1) {
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

      public int put(char var1, int var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public int get(char var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public int remove(char var1) {
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
      public Integer put(Character var1, Integer var2) {
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

   public static class Singleton extends AbstractChar2IntFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final char key;
      protected final int value;

      protected Singleton(char var1, int var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(char var1) {
         return this.key == var1;
      }

      public int get(char var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractChar2IntFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public int get(char var1) {
         return 0;
      }

      public boolean containsKey(char var1) {
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
         return Char2IntFunctions.EMPTY_FUNCTION;
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
         return Char2IntFunctions.EMPTY_FUNCTION;
      }
   }
}
