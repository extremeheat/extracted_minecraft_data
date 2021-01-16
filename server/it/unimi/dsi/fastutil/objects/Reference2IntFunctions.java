package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class Reference2IntFunctions {
   public static final Reference2IntFunctions.EmptyFunction EMPTY_FUNCTION = new Reference2IntFunctions.EmptyFunction();

   private Reference2IntFunctions() {
      super();
   }

   public static <K> Reference2IntFunction<K> singleton(K var0, int var1) {
      return new Reference2IntFunctions.Singleton(var0, var1);
   }

   public static <K> Reference2IntFunction<K> singleton(K var0, Integer var1) {
      return new Reference2IntFunctions.Singleton(var0, var1);
   }

   public static <K> Reference2IntFunction<K> synchronize(Reference2IntFunction<K> var0) {
      return new Reference2IntFunctions.SynchronizedFunction(var0);
   }

   public static <K> Reference2IntFunction<K> synchronize(Reference2IntFunction<K> var0, Object var1) {
      return new Reference2IntFunctions.SynchronizedFunction(var0, var1);
   }

   public static <K> Reference2IntFunction<K> unmodifiable(Reference2IntFunction<K> var0) {
      return new Reference2IntFunctions.UnmodifiableFunction(var0);
   }

   public static <K> Reference2IntFunction<K> primitive(Function<? super K, ? extends Integer> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Reference2IntFunction) {
         return (Reference2IntFunction)var0;
      } else {
         return (Reference2IntFunction)(var0 instanceof ToIntFunction ? (var1) -> {
            return ((ToIntFunction)var0).applyAsInt(var1);
         } : new Reference2IntFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction<K> implements Reference2IntFunction<K> {
      protected final Function<? super K, ? extends Integer> function;

      protected PrimitiveFunction(Function<? super K, ? extends Integer> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(Object var1) {
         return this.function.apply(var1) != null;
      }

      public int getInt(Object var1) {
         Integer var2 = (Integer)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Integer get(Object var1) {
         return (Integer)this.function.apply(var1);
      }

      /** @deprecated */
      @Deprecated
      public Integer put(K var1, Integer var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction<K> extends AbstractReference2IntFunction<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2IntFunction<K> function;

      protected UnmodifiableFunction(Reference2IntFunction<K> var1) {
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

      public boolean containsKey(Object var1) {
         return this.function.containsKey(var1);
      }

      public int put(K var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public int getInt(Object var1) {
         return this.function.getInt(var1);
      }

      public int removeInt(Object var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer put(K var1, Integer var2) {
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

   public static class SynchronizedFunction<K> implements Reference2IntFunction<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2IntFunction<K> function;
      protected final Object sync;

      protected SynchronizedFunction(Reference2IntFunction<K> var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Reference2IntFunction<K> var1) {
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
      public Integer apply(K var1) {
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

      public boolean containsKey(Object var1) {
         synchronized(this.sync) {
            return this.function.containsKey(var1);
         }
      }

      public int put(K var1, int var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public int getInt(Object var1) {
         synchronized(this.sync) {
            return this.function.getInt(var1);
         }
      }

      public int removeInt(Object var1) {
         synchronized(this.sync) {
            return this.function.removeInt(var1);
         }
      }

      public void clear() {
         synchronized(this.sync) {
            this.function.clear();
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer put(K var1, Integer var2) {
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

   public static class Singleton<K> extends AbstractReference2IntFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final int value;

      protected Singleton(K var1, int var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(Object var1) {
         return this.key == var1;
      }

      public int getInt(Object var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction<K> extends AbstractReference2IntFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public int getInt(Object var1) {
         return 0;
      }

      public boolean containsKey(Object var1) {
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
         return Reference2IntFunctions.EMPTY_FUNCTION;
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
         return Reference2IntFunctions.EMPTY_FUNCTION;
      }
   }
}
