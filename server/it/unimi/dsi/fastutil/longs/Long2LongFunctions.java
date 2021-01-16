package it.unimi.dsi.fastutil.longs;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;

public final class Long2LongFunctions {
   public static final Long2LongFunctions.EmptyFunction EMPTY_FUNCTION = new Long2LongFunctions.EmptyFunction();

   private Long2LongFunctions() {
      super();
   }

   public static Long2LongFunction singleton(long var0, long var2) {
      return new Long2LongFunctions.Singleton(var0, var2);
   }

   public static Long2LongFunction singleton(Long var0, Long var1) {
      return new Long2LongFunctions.Singleton(var0, var1);
   }

   public static Long2LongFunction synchronize(Long2LongFunction var0) {
      return new Long2LongFunctions.SynchronizedFunction(var0);
   }

   public static Long2LongFunction synchronize(Long2LongFunction var0, Object var1) {
      return new Long2LongFunctions.SynchronizedFunction(var0, var1);
   }

   public static Long2LongFunction unmodifiable(Long2LongFunction var0) {
      return new Long2LongFunctions.UnmodifiableFunction(var0);
   }

   public static Long2LongFunction primitive(Function<? super Long, ? extends Long> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Long2LongFunction) {
         return (Long2LongFunction)var0;
      } else if (var0 instanceof LongUnaryOperator) {
         LongUnaryOperator var10000 = (LongUnaryOperator)var0;
         Objects.requireNonNull((LongUnaryOperator)var0);
         return var10000::applyAsLong;
      } else {
         return new Long2LongFunctions.PrimitiveFunction(var0);
      }
   }

   public static class PrimitiveFunction implements Long2LongFunction {
      protected final Function<? super Long, ? extends Long> function;

      protected PrimitiveFunction(Function<? super Long, ? extends Long> var1) {
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

      public long get(long var1) {
         Long var3 = (Long)this.function.apply(var1);
         return var3 == null ? this.defaultReturnValue() : var3;
      }

      /** @deprecated */
      @Deprecated
      public Long get(Object var1) {
         return var1 == null ? null : (Long)this.function.apply((Long)var1);
      }

      /** @deprecated */
      @Deprecated
      public Long put(Long var1, Long var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractLong2LongFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2LongFunction function;

      protected UnmodifiableFunction(Long2LongFunction var1) {
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

      public long defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      public void defaultReturnValue(long var1) {
         throw new UnsupportedOperationException();
      }

      public boolean containsKey(long var1) {
         return this.function.containsKey(var1);
      }

      public long put(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public long get(long var1) {
         return this.function.get(var1);
      }

      public long remove(long var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long put(Long var1, Long var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long get(Object var1) {
         return this.function.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long remove(Object var1) {
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

   public static class SynchronizedFunction implements Long2LongFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2LongFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Long2LongFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Long2LongFunction var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      public long applyAsLong(long var1) {
         synchronized(this.sync) {
            return this.function.applyAsLong(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long apply(Long var1) {
         synchronized(this.sync) {
            return (Long)this.function.apply(var1);
         }
      }

      public int size() {
         synchronized(this.sync) {
            return this.function.size();
         }
      }

      public long defaultReturnValue() {
         synchronized(this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      public void defaultReturnValue(long var1) {
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

      public long put(long var1, long var3) {
         synchronized(this.sync) {
            return this.function.put(var1, var3);
         }
      }

      public long get(long var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public long remove(long var1) {
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
      public Long put(Long var1, Long var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long get(Object var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long remove(Object var1) {
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

   public static class Singleton extends AbstractLong2LongFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final long key;
      protected final long value;

      protected Singleton(long var1, long var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public boolean containsKey(long var1) {
         return this.key == var1;
      }

      public long get(long var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractLong2LongFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public long get(long var1) {
         return 0L;
      }

      public boolean containsKey(long var1) {
         return false;
      }

      public long defaultReturnValue() {
         return 0L;
      }

      public void defaultReturnValue(long var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public Object clone() {
         return Long2LongFunctions.EMPTY_FUNCTION;
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
         return Long2LongFunctions.EMPTY_FUNCTION;
      }
   }
}
