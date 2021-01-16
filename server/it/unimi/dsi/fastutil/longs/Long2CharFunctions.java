package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongToIntFunction;

public final class Long2CharFunctions {
   public static final Long2CharFunctions.EmptyFunction EMPTY_FUNCTION = new Long2CharFunctions.EmptyFunction();

   private Long2CharFunctions() {
      super();
   }

   public static Long2CharFunction singleton(long var0, char var2) {
      return new Long2CharFunctions.Singleton(var0, var2);
   }

   public static Long2CharFunction singleton(Long var0, Character var1) {
      return new Long2CharFunctions.Singleton(var0, var1);
   }

   public static Long2CharFunction synchronize(Long2CharFunction var0) {
      return new Long2CharFunctions.SynchronizedFunction(var0);
   }

   public static Long2CharFunction synchronize(Long2CharFunction var0, Object var1) {
      return new Long2CharFunctions.SynchronizedFunction(var0, var1);
   }

   public static Long2CharFunction unmodifiable(Long2CharFunction var0) {
      return new Long2CharFunctions.UnmodifiableFunction(var0);
   }

   public static Long2CharFunction primitive(Function<? super Long, ? extends Character> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Long2CharFunction) {
         return (Long2CharFunction)var0;
      } else {
         return (Long2CharFunction)(var0 instanceof LongToIntFunction ? (var1) -> {
            return SafeMath.safeIntToChar(((LongToIntFunction)var0).applyAsInt(var1));
         } : new Long2CharFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction implements Long2CharFunction {
      protected final Function<? super Long, ? extends Character> function;

      protected PrimitiveFunction(Function<? super Long, ? extends Character> var1) {
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

      public char get(long var1) {
         Character var3 = (Character)this.function.apply(var1);
         return var3 == null ? this.defaultReturnValue() : var3;
      }

      /** @deprecated */
      @Deprecated
      public Character get(Object var1) {
         return var1 == null ? null : (Character)this.function.apply((Long)var1);
      }

      /** @deprecated */
      @Deprecated
      public Character put(Long var1, Character var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractLong2CharFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2CharFunction function;

      protected UnmodifiableFunction(Long2CharFunction var1) {
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

      public boolean containsKey(long var1) {
         return this.function.containsKey(var1);
      }

      public char put(long var1, char var3) {
         throw new UnsupportedOperationException();
      }

      public char get(long var1) {
         return this.function.get(var1);
      }

      public char remove(long var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character put(Long var1, Character var2) {
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

   public static class SynchronizedFunction implements Long2CharFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2CharFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Long2CharFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Long2CharFunction var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      public int applyAsInt(long var1) {
         synchronized(this.sync) {
            return this.function.applyAsInt(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character apply(Long var1) {
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

      public char put(long var1, char var3) {
         synchronized(this.sync) {
            return this.function.put(var1, var3);
         }
      }

      public char get(long var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public char remove(long var1) {
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
      public Character put(Long var1, Character var2) {
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

   public static class Singleton extends AbstractLong2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final long key;
      protected final char value;

      protected Singleton(long var1, char var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public boolean containsKey(long var1) {
         return this.key == var1;
      }

      public char get(long var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractLong2CharFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public char get(long var1) {
         return '\u0000';
      }

      public boolean containsKey(long var1) {
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
         return Long2CharFunctions.EMPTY_FUNCTION;
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
         return Long2CharFunctions.EMPTY_FUNCTION;
      }
   }
}
