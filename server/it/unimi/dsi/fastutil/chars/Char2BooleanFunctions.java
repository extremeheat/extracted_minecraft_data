package it.unimi.dsi.fastutil.chars;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntPredicate;

public final class Char2BooleanFunctions {
   public static final Char2BooleanFunctions.EmptyFunction EMPTY_FUNCTION = new Char2BooleanFunctions.EmptyFunction();

   private Char2BooleanFunctions() {
      super();
   }

   public static Char2BooleanFunction singleton(char var0, boolean var1) {
      return new Char2BooleanFunctions.Singleton(var0, var1);
   }

   public static Char2BooleanFunction singleton(Character var0, Boolean var1) {
      return new Char2BooleanFunctions.Singleton(var0, var1);
   }

   public static Char2BooleanFunction synchronize(Char2BooleanFunction var0) {
      return new Char2BooleanFunctions.SynchronizedFunction(var0);
   }

   public static Char2BooleanFunction synchronize(Char2BooleanFunction var0, Object var1) {
      return new Char2BooleanFunctions.SynchronizedFunction(var0, var1);
   }

   public static Char2BooleanFunction unmodifiable(Char2BooleanFunction var0) {
      return new Char2BooleanFunctions.UnmodifiableFunction(var0);
   }

   public static Char2BooleanFunction primitive(Function<? super Character, ? extends Boolean> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Char2BooleanFunction) {
         return (Char2BooleanFunction)var0;
      } else if (var0 instanceof IntPredicate) {
         IntPredicate var10000 = (IntPredicate)var0;
         Objects.requireNonNull((IntPredicate)var0);
         return var10000::test;
      } else {
         return new Char2BooleanFunctions.PrimitiveFunction(var0);
      }
   }

   public static class PrimitiveFunction implements Char2BooleanFunction {
      protected final Function<? super Character, ? extends Boolean> function;

      protected PrimitiveFunction(Function<? super Character, ? extends Boolean> var1) {
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

      public boolean get(char var1) {
         Boolean var2 = (Boolean)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Boolean get(Object var1) {
         return var1 == null ? null : (Boolean)this.function.apply((Character)var1);
      }

      /** @deprecated */
      @Deprecated
      public Boolean put(Character var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractChar2BooleanFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2BooleanFunction function;

      protected UnmodifiableFunction(Char2BooleanFunction var1) {
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

      public boolean containsKey(char var1) {
         return this.function.containsKey(var1);
      }

      public boolean put(char var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean get(char var1) {
         return this.function.get(var1);
      }

      public boolean remove(char var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean put(Character var1, Boolean var2) {
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

   public static class SynchronizedFunction implements Char2BooleanFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2BooleanFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Char2BooleanFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Char2BooleanFunction var1) {
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
      public boolean test(int var1) {
         synchronized(this.sync) {
            return this.function.test(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean apply(Character var1) {
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

      public boolean put(char var1, boolean var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public boolean get(char var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public boolean remove(char var1) {
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
      public Boolean put(Character var1, Boolean var2) {
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

   public static class Singleton extends AbstractChar2BooleanFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final char key;
      protected final boolean value;

      protected Singleton(char var1, boolean var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(char var1) {
         return this.key == var1;
      }

      public boolean get(char var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractChar2BooleanFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public boolean get(char var1) {
         return false;
      }

      public boolean containsKey(char var1) {
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
         return Char2BooleanFunctions.EMPTY_FUNCTION;
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
         return Char2BooleanFunctions.EMPTY_FUNCTION;
      }
   }
}
