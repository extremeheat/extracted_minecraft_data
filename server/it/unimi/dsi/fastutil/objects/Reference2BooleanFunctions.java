package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Reference2BooleanFunctions {
   public static final Reference2BooleanFunctions.EmptyFunction EMPTY_FUNCTION = new Reference2BooleanFunctions.EmptyFunction();

   private Reference2BooleanFunctions() {
      super();
   }

   public static <K> Reference2BooleanFunction<K> singleton(K var0, boolean var1) {
      return new Reference2BooleanFunctions.Singleton(var0, var1);
   }

   public static <K> Reference2BooleanFunction<K> singleton(K var0, Boolean var1) {
      return new Reference2BooleanFunctions.Singleton(var0, var1);
   }

   public static <K> Reference2BooleanFunction<K> synchronize(Reference2BooleanFunction<K> var0) {
      return new Reference2BooleanFunctions.SynchronizedFunction(var0);
   }

   public static <K> Reference2BooleanFunction<K> synchronize(Reference2BooleanFunction<K> var0, Object var1) {
      return new Reference2BooleanFunctions.SynchronizedFunction(var0, var1);
   }

   public static <K> Reference2BooleanFunction<K> unmodifiable(Reference2BooleanFunction<K> var0) {
      return new Reference2BooleanFunctions.UnmodifiableFunction(var0);
   }

   public static <K> Reference2BooleanFunction<K> primitive(Function<? super K, ? extends Boolean> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Reference2BooleanFunction) {
         return (Reference2BooleanFunction)var0;
      } else {
         return (Reference2BooleanFunction)(var0 instanceof Predicate ? (var1) -> {
            return ((Predicate)var0).test(var1);
         } : new Reference2BooleanFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction<K> implements Reference2BooleanFunction<K> {
      protected final Function<? super K, ? extends Boolean> function;

      protected PrimitiveFunction(Function<? super K, ? extends Boolean> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(Object var1) {
         return this.function.apply(var1) != null;
      }

      public boolean getBoolean(Object var1) {
         Boolean var2 = (Boolean)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Boolean get(Object var1) {
         return (Boolean)this.function.apply(var1);
      }

      /** @deprecated */
      @Deprecated
      public Boolean put(K var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction<K> extends AbstractReference2BooleanFunction<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2BooleanFunction<K> function;

      protected UnmodifiableFunction(Reference2BooleanFunction<K> var1) {
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

      public boolean containsKey(Object var1) {
         return this.function.containsKey(var1);
      }

      public boolean put(K var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean getBoolean(Object var1) {
         return this.function.getBoolean(var1);
      }

      public boolean removeBoolean(Object var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean put(K var1, Boolean var2) {
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

   public static class SynchronizedFunction<K> implements Reference2BooleanFunction<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2BooleanFunction<K> function;
      protected final Object sync;

      protected SynchronizedFunction(Reference2BooleanFunction<K> var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Reference2BooleanFunction<K> var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      public boolean test(K var1) {
         synchronized(this.sync) {
            return this.function.test(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean apply(K var1) {
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

      public boolean containsKey(Object var1) {
         synchronized(this.sync) {
            return this.function.containsKey(var1);
         }
      }

      public boolean put(K var1, boolean var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public boolean getBoolean(Object var1) {
         synchronized(this.sync) {
            return this.function.getBoolean(var1);
         }
      }

      public boolean removeBoolean(Object var1) {
         synchronized(this.sync) {
            return this.function.removeBoolean(var1);
         }
      }

      public void clear() {
         synchronized(this.sync) {
            this.function.clear();
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean put(K var1, Boolean var2) {
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

   public static class Singleton<K> extends AbstractReference2BooleanFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final boolean value;

      protected Singleton(K var1, boolean var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(Object var1) {
         return this.key == var1;
      }

      public boolean getBoolean(Object var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction<K> extends AbstractReference2BooleanFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public boolean getBoolean(Object var1) {
         return false;
      }

      public boolean containsKey(Object var1) {
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
         return Reference2BooleanFunctions.EMPTY_FUNCTION;
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
         return Reference2BooleanFunctions.EMPTY_FUNCTION;
      }
   }
}
