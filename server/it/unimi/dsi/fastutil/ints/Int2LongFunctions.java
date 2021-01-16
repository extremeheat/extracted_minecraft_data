package it.unimi.dsi.fastutil.ints;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntToLongFunction;

public final class Int2LongFunctions {
   public static final Int2LongFunctions.EmptyFunction EMPTY_FUNCTION = new Int2LongFunctions.EmptyFunction();

   private Int2LongFunctions() {
      super();
   }

   public static Int2LongFunction singleton(int var0, long var1) {
      return new Int2LongFunctions.Singleton(var0, var1);
   }

   public static Int2LongFunction singleton(Integer var0, Long var1) {
      return new Int2LongFunctions.Singleton(var0, var1);
   }

   public static Int2LongFunction synchronize(Int2LongFunction var0) {
      return new Int2LongFunctions.SynchronizedFunction(var0);
   }

   public static Int2LongFunction synchronize(Int2LongFunction var0, Object var1) {
      return new Int2LongFunctions.SynchronizedFunction(var0, var1);
   }

   public static Int2LongFunction unmodifiable(Int2LongFunction var0) {
      return new Int2LongFunctions.UnmodifiableFunction(var0);
   }

   public static Int2LongFunction primitive(Function<? super Integer, ? extends Long> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Int2LongFunction) {
         return (Int2LongFunction)var0;
      } else if (var0 instanceof IntToLongFunction) {
         IntToLongFunction var10000 = (IntToLongFunction)var0;
         Objects.requireNonNull((IntToLongFunction)var0);
         return var10000::applyAsLong;
      } else {
         return new Int2LongFunctions.PrimitiveFunction(var0);
      }
   }

   public static class PrimitiveFunction implements Int2LongFunction {
      protected final Function<? super Integer, ? extends Long> function;

      protected PrimitiveFunction(Function<? super Integer, ? extends Long> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(int var1) {
         return this.function.apply(var1) != null;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsKey(Object var1) {
         if (var1 == null) {
            return false;
         } else {
            return this.function.apply((Integer)var1) != null;
         }
      }

      public long get(int var1) {
         Long var2 = (Long)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Long get(Object var1) {
         return var1 == null ? null : (Long)this.function.apply((Integer)var1);
      }

      /** @deprecated */
      @Deprecated
      public Long put(Integer var1, Long var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractInt2LongFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2LongFunction function;

      protected UnmodifiableFunction(Int2LongFunction var1) {
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

      public boolean containsKey(int var1) {
         return this.function.containsKey(var1);
      }

      public long put(int var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public long get(int var1) {
         return this.function.get(var1);
      }

      public long remove(int var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long put(Integer var1, Long var2) {
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

   public static class SynchronizedFunction implements Int2LongFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2LongFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Int2LongFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Int2LongFunction var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      public long applyAsLong(int var1) {
         synchronized(this.sync) {
            return this.function.applyAsLong(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long apply(Integer var1) {
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

      public boolean containsKey(int var1) {
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

      public long put(int var1, long var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public long get(int var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public long remove(int var1) {
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
      public Long put(Integer var1, Long var2) {
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

   public static class Singleton extends AbstractInt2LongFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final int key;
      protected final long value;

      protected Singleton(int var1, long var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(int var1) {
         return this.key == var1;
      }

      public long get(int var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractInt2LongFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public long get(int var1) {
         return 0L;
      }

      public boolean containsKey(int var1) {
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
         return Int2LongFunctions.EMPTY_FUNCTION;
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
         return Int2LongFunctions.EMPTY_FUNCTION;
      }
   }
}
