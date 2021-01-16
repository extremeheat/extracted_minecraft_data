package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

public final class Byte2ShortFunctions {
   public static final Byte2ShortFunctions.EmptyFunction EMPTY_FUNCTION = new Byte2ShortFunctions.EmptyFunction();

   private Byte2ShortFunctions() {
      super();
   }

   public static Byte2ShortFunction singleton(byte var0, short var1) {
      return new Byte2ShortFunctions.Singleton(var0, var1);
   }

   public static Byte2ShortFunction singleton(Byte var0, Short var1) {
      return new Byte2ShortFunctions.Singleton(var0, var1);
   }

   public static Byte2ShortFunction synchronize(Byte2ShortFunction var0) {
      return new Byte2ShortFunctions.SynchronizedFunction(var0);
   }

   public static Byte2ShortFunction synchronize(Byte2ShortFunction var0, Object var1) {
      return new Byte2ShortFunctions.SynchronizedFunction(var0, var1);
   }

   public static Byte2ShortFunction unmodifiable(Byte2ShortFunction var0) {
      return new Byte2ShortFunctions.UnmodifiableFunction(var0);
   }

   public static Byte2ShortFunction primitive(Function<? super Byte, ? extends Short> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Byte2ShortFunction) {
         return (Byte2ShortFunction)var0;
      } else {
         return (Byte2ShortFunction)(var0 instanceof IntUnaryOperator ? (var1) -> {
            return SafeMath.safeIntToShort(((IntUnaryOperator)var0).applyAsInt(var1));
         } : new Byte2ShortFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction implements Byte2ShortFunction {
      protected final Function<? super Byte, ? extends Short> function;

      protected PrimitiveFunction(Function<? super Byte, ? extends Short> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(byte var1) {
         return this.function.apply(var1) != null;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsKey(Object var1) {
         if (var1 == null) {
            return false;
         } else {
            return this.function.apply((Byte)var1) != null;
         }
      }

      public short get(byte var1) {
         Short var2 = (Short)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Short get(Object var1) {
         return var1 == null ? null : (Short)this.function.apply((Byte)var1);
      }

      /** @deprecated */
      @Deprecated
      public Short put(Byte var1, Short var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractByte2ShortFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ShortFunction function;

      protected UnmodifiableFunction(Byte2ShortFunction var1) {
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

      public boolean containsKey(byte var1) {
         return this.function.containsKey(var1);
      }

      public short put(byte var1, short var2) {
         throw new UnsupportedOperationException();
      }

      public short get(byte var1) {
         return this.function.get(var1);
      }

      public short remove(byte var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short put(Byte var1, Short var2) {
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

   public static class SynchronizedFunction implements Byte2ShortFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ShortFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Byte2ShortFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Byte2ShortFunction var1) {
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
      public int applyAsInt(int var1) {
         synchronized(this.sync) {
            return this.function.applyAsInt(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short apply(Byte var1) {
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

      public boolean containsKey(byte var1) {
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

      public short put(byte var1, short var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public short get(byte var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public short remove(byte var1) {
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
      public Short put(Byte var1, Short var2) {
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

   public static class Singleton extends AbstractByte2ShortFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final byte key;
      protected final short value;

      protected Singleton(byte var1, short var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(byte var1) {
         return this.key == var1;
      }

      public short get(byte var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractByte2ShortFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public short get(byte var1) {
         return 0;
      }

      public boolean containsKey(byte var1) {
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
         return Byte2ShortFunctions.EMPTY_FUNCTION;
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
         return Byte2ShortFunctions.EMPTY_FUNCTION;
      }
   }
}
