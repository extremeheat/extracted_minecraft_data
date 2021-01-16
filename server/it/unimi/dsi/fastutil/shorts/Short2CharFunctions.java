package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

public final class Short2CharFunctions {
   public static final Short2CharFunctions.EmptyFunction EMPTY_FUNCTION = new Short2CharFunctions.EmptyFunction();

   private Short2CharFunctions() {
      super();
   }

   public static Short2CharFunction singleton(short var0, char var1) {
      return new Short2CharFunctions.Singleton(var0, var1);
   }

   public static Short2CharFunction singleton(Short var0, Character var1) {
      return new Short2CharFunctions.Singleton(var0, var1);
   }

   public static Short2CharFunction synchronize(Short2CharFunction var0) {
      return new Short2CharFunctions.SynchronizedFunction(var0);
   }

   public static Short2CharFunction synchronize(Short2CharFunction var0, Object var1) {
      return new Short2CharFunctions.SynchronizedFunction(var0, var1);
   }

   public static Short2CharFunction unmodifiable(Short2CharFunction var0) {
      return new Short2CharFunctions.UnmodifiableFunction(var0);
   }

   public static Short2CharFunction primitive(Function<? super Short, ? extends Character> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Short2CharFunction) {
         return (Short2CharFunction)var0;
      } else {
         return (Short2CharFunction)(var0 instanceof IntUnaryOperator ? (var1) -> {
            return SafeMath.safeIntToChar(((IntUnaryOperator)var0).applyAsInt(var1));
         } : new Short2CharFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction implements Short2CharFunction {
      protected final Function<? super Short, ? extends Character> function;

      protected PrimitiveFunction(Function<? super Short, ? extends Character> var1) {
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

      public char get(short var1) {
         Character var2 = (Character)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Character get(Object var1) {
         return var1 == null ? null : (Character)this.function.apply((Short)var1);
      }

      /** @deprecated */
      @Deprecated
      public Character put(Short var1, Character var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractShort2CharFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2CharFunction function;

      protected UnmodifiableFunction(Short2CharFunction var1) {
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

      public boolean containsKey(short var1) {
         return this.function.containsKey(var1);
      }

      public char put(short var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public char get(short var1) {
         return this.function.get(var1);
      }

      public char remove(short var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character put(Short var1, Character var2) {
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

   public static class SynchronizedFunction implements Short2CharFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2CharFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Short2CharFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Short2CharFunction var1) {
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
      public Character apply(Short var1) {
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

      public char put(short var1, char var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public char get(short var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public char remove(short var1) {
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
      public Character put(Short var1, Character var2) {
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

   public static class Singleton extends AbstractShort2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final short key;
      protected final char value;

      protected Singleton(short var1, char var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(short var1) {
         return this.key == var1;
      }

      public char get(short var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractShort2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public char get(short var1) {
         return '\u0000';
      }

      public boolean containsKey(short var1) {
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
         return Short2CharFunctions.EMPTY_FUNCTION;
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
         return Short2CharFunctions.EMPTY_FUNCTION;
      }
   }
}
