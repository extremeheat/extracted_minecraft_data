package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongToIntFunction;

public final class Long2ByteFunctions {
   public static final Long2ByteFunctions.EmptyFunction EMPTY_FUNCTION = new Long2ByteFunctions.EmptyFunction();

   private Long2ByteFunctions() {
      super();
   }

   public static Long2ByteFunction singleton(long var0, byte var2) {
      return new Long2ByteFunctions.Singleton(var0, var2);
   }

   public static Long2ByteFunction singleton(Long var0, Byte var1) {
      return new Long2ByteFunctions.Singleton(var0, var1);
   }

   public static Long2ByteFunction synchronize(Long2ByteFunction var0) {
      return new Long2ByteFunctions.SynchronizedFunction(var0);
   }

   public static Long2ByteFunction synchronize(Long2ByteFunction var0, Object var1) {
      return new Long2ByteFunctions.SynchronizedFunction(var0, var1);
   }

   public static Long2ByteFunction unmodifiable(Long2ByteFunction var0) {
      return new Long2ByteFunctions.UnmodifiableFunction(var0);
   }

   public static Long2ByteFunction primitive(Function<? super Long, ? extends Byte> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Long2ByteFunction) {
         return (Long2ByteFunction)var0;
      } else {
         return (Long2ByteFunction)(var0 instanceof LongToIntFunction ? (var1) -> {
            return SafeMath.safeIntToByte(((LongToIntFunction)var0).applyAsInt(var1));
         } : new Long2ByteFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction implements Long2ByteFunction {
      protected final Function<? super Long, ? extends Byte> function;

      protected PrimitiveFunction(Function<? super Long, ? extends Byte> var1) {
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

      public byte get(long var1) {
         Byte var3 = (Byte)this.function.apply(var1);
         return var3 == null ? this.defaultReturnValue() : var3;
      }

      /** @deprecated */
      @Deprecated
      public Byte get(Object var1) {
         return var1 == null ? null : (Byte)this.function.apply((Long)var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte put(Long var1, Byte var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractLong2ByteFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ByteFunction function;

      protected UnmodifiableFunction(Long2ByteFunction var1) {
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

      public byte defaultReturnValue() {
         return this.function.defaultReturnValue();
      }

      public void defaultReturnValue(byte var1) {
         throw new UnsupportedOperationException();
      }

      public boolean containsKey(long var1) {
         return this.function.containsKey(var1);
      }

      public byte put(long var1, byte var3) {
         throw new UnsupportedOperationException();
      }

      public byte get(long var1) {
         return this.function.get(var1);
      }

      public byte remove(long var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte put(Long var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte get(Object var1) {
         return this.function.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte remove(Object var1) {
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

   public static class SynchronizedFunction implements Long2ByteFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ByteFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Long2ByteFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Long2ByteFunction var1) {
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
      public Byte apply(Long var1) {
         synchronized(this.sync) {
            return (Byte)this.function.apply(var1);
         }
      }

      public int size() {
         synchronized(this.sync) {
            return this.function.size();
         }
      }

      public byte defaultReturnValue() {
         synchronized(this.sync) {
            return this.function.defaultReturnValue();
         }
      }

      public void defaultReturnValue(byte var1) {
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

      public byte put(long var1, byte var3) {
         synchronized(this.sync) {
            return this.function.put(var1, var3);
         }
      }

      public byte get(long var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public byte remove(long var1) {
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
      public Byte put(Long var1, Byte var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte get(Object var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte remove(Object var1) {
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

   public static class Singleton extends AbstractLong2ByteFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final long key;
      protected final byte value;

      protected Singleton(long var1, byte var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public boolean containsKey(long var1) {
         return this.key == var1;
      }

      public byte get(long var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractLong2ByteFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public byte get(long var1) {
         return 0;
      }

      public boolean containsKey(long var1) {
         return false;
      }

      public byte defaultReturnValue() {
         return 0;
      }

      public void defaultReturnValue(byte var1) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public Object clone() {
         return Long2ByteFunctions.EMPTY_FUNCTION;
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
         return Long2ByteFunctions.EMPTY_FUNCTION;
      }
   }
}
