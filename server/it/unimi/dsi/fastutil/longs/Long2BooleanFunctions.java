package it.unimi.dsi.fastutil.longs;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongPredicate;

public final class Long2BooleanFunctions {
   public static final Long2BooleanFunctions.EmptyFunction EMPTY_FUNCTION = new Long2BooleanFunctions.EmptyFunction();

   private Long2BooleanFunctions() {
      super();
   }

   public static Long2BooleanFunction singleton(long var0, boolean var2) {
      return new Long2BooleanFunctions.Singleton(var0, var2);
   }

   public static Long2BooleanFunction singleton(Long var0, Boolean var1) {
      return new Long2BooleanFunctions.Singleton(var0, var1);
   }

   public static Long2BooleanFunction synchronize(Long2BooleanFunction var0) {
      return new Long2BooleanFunctions.SynchronizedFunction(var0);
   }

   public static Long2BooleanFunction synchronize(Long2BooleanFunction var0, Object var1) {
      return new Long2BooleanFunctions.SynchronizedFunction(var0, var1);
   }

   public static Long2BooleanFunction unmodifiable(Long2BooleanFunction var0) {
      return new Long2BooleanFunctions.UnmodifiableFunction(var0);
   }

   public static Long2BooleanFunction primitive(Function<? super Long, ? extends Boolean> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Long2BooleanFunction) {
         return (Long2BooleanFunction)var0;
      } else if (var0 instanceof LongPredicate) {
         LongPredicate var10000 = (LongPredicate)var0;
         Objects.requireNonNull((LongPredicate)var0);
         return var10000::test;
      } else {
         return new Long2BooleanFunctions.PrimitiveFunction(var0);
      }
   }

   public static class PrimitiveFunction implements Long2BooleanFunction {
      protected final Function<? super Long, ? extends Boolean> function;

      protected PrimitiveFunction(Function<? super Long, ? extends Boolean> var1) {
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

      public boolean get(long var1) {
         Boolean var3 = (Boolean)this.function.apply(var1);
         return var3 == null ? this.defaultReturnValue() : var3;
      }

      /** @deprecated */
      @Deprecated
      public Boolean get(Object var1) {
         return var1 == null ? null : (Boolean)this.function.apply((Long)var1);
      }

      /** @deprecated */
      @Deprecated
      public Boolean put(Long var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractLong2BooleanFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2BooleanFunction function;

      protected UnmodifiableFunction(Long2BooleanFunction var1) {
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

      public boolean containsKey(long var1) {
         return this.function.containsKey(var1);
      }

      public boolean put(long var1, boolean var3) {
         throw new UnsupportedOperationException();
      }

      public boolean get(long var1) {
         return this.function.get(var1);
      }

      public boolean remove(long var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean put(Long var1, Boolean var2) {
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

   public static class SynchronizedFunction implements Long2BooleanFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2BooleanFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Long2BooleanFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Long2BooleanFunction var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      public boolean test(long var1) {
         synchronized(this.sync) {
            return this.function.test(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean apply(Long var1) {
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

      public boolean put(long var1, boolean var3) {
         synchronized(this.sync) {
            return this.function.put(var1, var3);
         }
      }

      public boolean get(long var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public boolean remove(long var1) {
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
      public Boolean put(Long var1, Boolean var2) {
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

   public static class Singleton extends AbstractLong2BooleanFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final long key;
      protected final boolean value;

      protected Singleton(long var1, boolean var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public boolean containsKey(long var1) {
         return this.key == var1;
      }

      public boolean get(long var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractLong2BooleanFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public boolean get(long var1) {
         return false;
      }

      public boolean containsKey(long var1) {
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
         return Long2BooleanFunctions.EMPTY_FUNCTION;
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
         return Long2BooleanFunctions.EMPTY_FUNCTION;
      }
   }
}
