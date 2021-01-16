package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongToIntFunction;

public final class Long2ShortFunctions {
   public static final Long2ShortFunctions.EmptyFunction EMPTY_FUNCTION = new Long2ShortFunctions.EmptyFunction();

   private Long2ShortFunctions() {
      super();
   }

   public static Long2ShortFunction singleton(long var0, short var2) {
      return new Long2ShortFunctions.Singleton(var0, var2);
   }

   public static Long2ShortFunction singleton(Long var0, Short var1) {
      return new Long2ShortFunctions.Singleton(var0, var1);
   }

   public static Long2ShortFunction synchronize(Long2ShortFunction var0) {
      return new Long2ShortFunctions.SynchronizedFunction(var0);
   }

   public static Long2ShortFunction synchronize(Long2ShortFunction var0, Object var1) {
      return new Long2ShortFunctions.SynchronizedFunction(var0, var1);
   }

   public static Long2ShortFunction unmodifiable(Long2ShortFunction var0) {
      return new Long2ShortFunctions.UnmodifiableFunction(var0);
   }

   public static Long2ShortFunction primitive(Function<? super Long, ? extends Short> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Long2ShortFunction) {
         return (Long2ShortFunction)var0;
      } else {
         return (Long2ShortFunction)(var0 instanceof LongToIntFunction ? (var1) -> {
            return SafeMath.safeIntToShort(((LongToIntFunction)var0).applyAsInt(var1));
         } : new Long2ShortFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction implements Long2ShortFunction {
      protected final Function<? super Long, ? extends Short> function;

      protected PrimitiveFunction(Function<? super Long, ? extends Short> var1) {
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

      public short get(long var1) {
         Short var3 = (Short)this.function.apply(var1);
         return var3 == null ? this.defaultReturnValue() : var3;
      }

      /** @deprecated */
      @Deprecated
      public Short get(Object var1) {
         return var1 == null ? null : (Short)this.function.apply((Long)var1);
      }

      /** @deprecated */
      @Deprecated
      public Short put(Long var1, Short var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractLong2ShortFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ShortFunction function;

      protected UnmodifiableFunction(Long2ShortFunction var1) {
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

      public short defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      public void defaultReturnValue(short var1) {
         throw new UnsupportedOperationException();
      }

      public boolean containsKey(long var1) {
         return this.function.containsKey(var1);
      }

      public short put(long var1, short var3) {
         throw new UnsupportedOperationException();
      }

      public short get(long var1) {
         return this.function.get(var1);
      }

      public short remove(long var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short put(Long var1, Short var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short get(Object var1) {
         return this.function.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public Short remove(Object var1) {
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

   public static class SynchronizedFunction implements Long2ShortFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ShortFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Long2ShortFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Long2ShortFunction var1) {
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
      public Short apply(Long var1) {
         synchronized(this.sync) {
            return (Short)this.function.apply(var1);
         }
      }

      public int size() {
         synchronized(this.sync) {
            return this.function.size();
         }
      }

      public short defaultReturnValue() {
         synchronized(this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      public void defaultReturnValue(short var1) {
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

      public short put(long var1, short var3) {
         synchronized(this.sync) {
            return this.function.put(var1, var3);
         }
      }

      public short get(long var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public short remove(long var1) {
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
      public Short put(Long var1, Short var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short get(Object var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short remove(Object var1) {
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

   public static class Singleton extends AbstractLong2ShortFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final long key;
      protected final short value;

      protected Singleton(long var1, short var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public boolean containsKey(long var1) {
         return this.key == var1;
      }

      public short get(long var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractLong2ShortFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public short get(long var1) {
         return 0;
      }

      public boolean containsKey(long var1) {
         return false;
      }

      public short defaultReturnValue() {
         return 0;
      }

      public void defaultReturnValue(short var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public Object clone() {
         return Long2ShortFunctions.EMPTY_FUNCTION;
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
         return Long2ShortFunctions.EMPTY_FUNCTION;
      }
   }
}
