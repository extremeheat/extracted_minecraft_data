package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class Reference2CharFunctions {
   public static final Reference2CharFunctions.EmptyFunction EMPTY_FUNCTION = new Reference2CharFunctions.EmptyFunction();

   private Reference2CharFunctions() {
      super();
   }

   public static <K> Reference2CharFunction<K> singleton(K var0, char var1) {
      return new Reference2CharFunctions.Singleton(var0, var1);
   }

   public static <K> Reference2CharFunction<K> singleton(K var0, Character var1) {
      return new Reference2CharFunctions.Singleton(var0, var1);
   }

   public static <K> Reference2CharFunction<K> synchronize(Reference2CharFunction<K> var0) {
      return new Reference2CharFunctions.SynchronizedFunction(var0);
   }

   public static <K> Reference2CharFunction<K> synchronize(Reference2CharFunction<K> var0, Object var1) {
      return new Reference2CharFunctions.SynchronizedFunction(var0, var1);
   }

   public static <K> Reference2CharFunction<K> unmodifiable(Reference2CharFunction<K> var0) {
      return new Reference2CharFunctions.UnmodifiableFunction(var0);
   }

   public static <K> Reference2CharFunction<K> primitive(Function<? super K, ? extends Character> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Reference2CharFunction) {
         return (Reference2CharFunction)var0;
      } else {
         return (Reference2CharFunction)(var0 instanceof ToIntFunction ? (var1) -> {
            return SafeMath.safeIntToChar(((ToIntFunction)var0).applyAsInt(var1));
         } : new Reference2CharFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction<K> implements Reference2CharFunction<K> {
      protected final Function<? super K, ? extends Character> function;

      protected PrimitiveFunction(Function<? super K, ? extends Character> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(Object var1) {
         return this.function.apply(var1) != null;
      }

      public char getChar(Object var1) {
         Character var2 = (Character)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Character get(Object var1) {
         return (Character)this.function.apply(var1);
      }

      /** @deprecated */
      @Deprecated
      public Character put(K var1, Character var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction<K> extends AbstractReference2CharFunction<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2CharFunction<K> function;

      protected UnmodifiableFunction(Reference2CharFunction<K> var1) {
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

      public boolean containsKey(Object var1) {
         return this.function.containsKey(var1);
      }

      public char put(K var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public char getChar(Object var1) {
         return this.function.getChar(var1);
      }

      public char removeChar(Object var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character put(K var1, Character var2) {
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

   public static class SynchronizedFunction<K> implements Reference2CharFunction<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2CharFunction<K> function;
      protected final Object sync;

      protected SynchronizedFunction(Reference2CharFunction<K> var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Reference2CharFunction<K> var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      public int applyAsInt(K var1) {
         synchronized(this.sync) {
            return this.function.applyAsInt(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character apply(K var1) {
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

      public boolean containsKey(Object var1) {
         synchronized(this.sync) {
            return this.function.containsKey(var1);
         }
      }

      public char put(K var1, char var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public char getChar(Object var1) {
         synchronized(this.sync) {
            return this.function.getChar(var1);
         }
      }

      public char removeChar(Object var1) {
         synchronized(this.sync) {
            return this.function.removeChar(var1);
         }
      }

      public void clear() {
         synchronized(this.sync) {
            this.function.clear();
         }
      }

      /** @deprecated */
      @Deprecated
      public Character put(K var1, Character var2) {
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

   public static class Singleton<K> extends AbstractReference2CharFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final char value;

      protected Singleton(K var1, char var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(Object var1) {
         return this.key == var1;
      }

      public char getChar(Object var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction<K> extends AbstractReference2CharFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public char getChar(Object var1) {
         return '\u0000';
      }

      public boolean containsKey(Object var1) {
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
         return Reference2CharFunctions.EMPTY_FUNCTION;
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
         return Reference2CharFunctions.EMPTY_FUNCTION;
      }
   }
}
