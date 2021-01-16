package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

public final class Char2CharFunctions {
   public static final Char2CharFunctions.EmptyFunction EMPTY_FUNCTION = new Char2CharFunctions.EmptyFunction();

   private Char2CharFunctions() {
      super();
   }

   public static Char2CharFunction singleton(char var0, char var1) {
      return new Char2CharFunctions.Singleton(var0, var1);
   }

   public static Char2CharFunction singleton(Character var0, Character var1) {
      return new Char2CharFunctions.Singleton(var0, var1);
   }

   public static Char2CharFunction synchronize(Char2CharFunction var0) {
      return new Char2CharFunctions.SynchronizedFunction(var0);
   }

   public static Char2CharFunction synchronize(Char2CharFunction var0, Object var1) {
      return new Char2CharFunctions.SynchronizedFunction(var0, var1);
   }

   public static Char2CharFunction unmodifiable(Char2CharFunction var0) {
      return new Char2CharFunctions.UnmodifiableFunction(var0);
   }

   public static Char2CharFunction primitive(Function<? super Character, ? extends Character> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Char2CharFunction) {
         return (Char2CharFunction)var0;
      } else {
         return (Char2CharFunction)(var0 instanceof IntUnaryOperator ? (var1) -> {
            return SafeMath.safeIntToChar(((IntUnaryOperator)var0).applyAsInt(var1));
         } : new Char2CharFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction implements Char2CharFunction {
      protected final Function<? super Character, ? extends Character> function;

      protected PrimitiveFunction(Function<? super Character, ? extends Character> var1) {
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

      public char get(char var1) {
         Character var2 = (Character)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Character get(Object var1) {
         return var1 == null ? null : (Character)this.function.apply((Character)var1);
      }

      /** @deprecated */
      @Deprecated
      public Character put(Character var1, Character var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractChar2CharFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2CharFunction function;

      protected UnmodifiableFunction(Char2CharFunction var1) {
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

      public boolean containsKey(char var1) {
         return this.function.containsKey(var1);
      }

      public char put(char var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public char get(char var1) {
         return this.function.get(var1);
      }

      public char remove(char var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character put(Character var1, Character var2) {
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

   public static class SynchronizedFunction implements Char2CharFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2CharFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Char2CharFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Char2CharFunction var1) {
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
      public Character apply(Character var1) {
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

      public char put(char var1, char var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public char get(char var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public char remove(char var1) {
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
      public Character put(Character var1, Character var2) {
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

   public static class Singleton extends AbstractChar2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final char key;
      protected final char value;

      protected Singleton(char var1, char var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(char var1) {
         return this.key == var1;
      }

      public char get(char var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractChar2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public char get(char var1) {
         return '\u0000';
      }

      public boolean containsKey(char var1) {
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
         return Char2CharFunctions.EMPTY_FUNCTION;
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
         return Char2CharFunctions.EMPTY_FUNCTION;
      }
   }
}
