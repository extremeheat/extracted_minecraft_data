package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

public final class Int2ByteFunctions {
   public static final Int2ByteFunctions.EmptyFunction EMPTY_FUNCTION = new Int2ByteFunctions.EmptyFunction();

   private Int2ByteFunctions() {
      super();
   }

   public static Int2ByteFunction singleton(int var0, byte var1) {
      return new Int2ByteFunctions.Singleton(var0, var1);
   }

   public static Int2ByteFunction singleton(Integer var0, Byte var1) {
      return new Int2ByteFunctions.Singleton(var0, var1);
   }

   public static Int2ByteFunction synchronize(Int2ByteFunction var0) {
      return new Int2ByteFunctions.SynchronizedFunction(var0);
   }

   public static Int2ByteFunction synchronize(Int2ByteFunction var0, Object var1) {
      return new Int2ByteFunctions.SynchronizedFunction(var0, var1);
   }

   public static Int2ByteFunction unmodifiable(Int2ByteFunction var0) {
      return new Int2ByteFunctions.UnmodifiableFunction(var0);
   }

   public static Int2ByteFunction primitive(Function<? super Integer, ? extends Byte> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Int2ByteFunction) {
         return (Int2ByteFunction)var0;
      } else {
         return (Int2ByteFunction)(var0 instanceof IntUnaryOperator ? (var1) -> {
            return SafeMath.safeIntToByte(((IntUnaryOperator)var0).applyAsInt(var1));
         } : new Int2ByteFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction implements Int2ByteFunction {
      protected final Function<? super Integer, ? extends Byte> function;

      protected PrimitiveFunction(Function<? super Integer, ? extends Byte> var1) {
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

      public byte get(int var1) {
         Byte var2 = (Byte)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Byte get(Object var1) {
         return var1 == null ? null : (Byte)this.function.apply((Integer)var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte put(Integer var1, Byte var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction extends AbstractInt2ByteFunction implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2ByteFunction function;

      protected UnmodifiableFunction(Int2ByteFunction var1) {
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

      public boolean containsKey(int var1) {
         return this.function.containsKey(var1);
      }

      public byte put(int var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public byte get(int var1) {
         return this.function.get(var1);
      }

      public byte remove(int var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte put(Integer var1, Byte var2) {
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

   public static class SynchronizedFunction implements Int2ByteFunction, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2ByteFunction function;
      protected final Object sync;

      protected SynchronizedFunction(Int2ByteFunction var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Int2ByteFunction var1) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = this;
         }
      }

      public int applyAsInt(int var1) {
         synchronized(this.sync) {
            return this.function.applyAsInt(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte apply(Integer var1) {
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

      public byte put(int var1, byte var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public byte get(int var1) {
         synchronized(this.sync) {
            return this.function.get(var1);
         }
      }

      public byte remove(int var1) {
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
      public Byte put(Integer var1, Byte var2) {
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

   public static class Singleton extends AbstractInt2ByteFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final int key;
      protected final byte value;

      protected Singleton(int var1, byte var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(int var1) {
         return this.key == var1;
      }

      public byte get(int var1) {
         return this.key == var1 ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction extends AbstractInt2ByteFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public byte get(int var1) {
         return 0;
      }

      public boolean containsKey(int var1) {
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
         return Int2ByteFunctions.EMPTY_FUNCTION;
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
         return Int2ByteFunctions.EMPTY_FUNCTION;
      }
   }
}
