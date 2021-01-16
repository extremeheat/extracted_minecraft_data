package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class Object2ByteFunctions {
   public static final Object2ByteFunctions.EmptyFunction EMPTY_FUNCTION = new Object2ByteFunctions.EmptyFunction();

   private Object2ByteFunctions() {
      super();
   }

   public static <K> Object2ByteFunction<K> singleton(K var0, byte var1) {
      return new Object2ByteFunctions.Singleton(var0, var1);
   }

   public static <K> Object2ByteFunction<K> singleton(K var0, Byte var1) {
      return new Object2ByteFunctions.Singleton(var0, var1);
   }

   public static <K> Object2ByteFunction<K> synchronize(Object2ByteFunction<K> var0) {
      return new Object2ByteFunctions.SynchronizedFunction(var0);
   }

   public static <K> Object2ByteFunction<K> synchronize(Object2ByteFunction<K> var0, Object var1) {
      return new Object2ByteFunctions.SynchronizedFunction(var0, var1);
   }

   public static <K> Object2ByteFunction<K> unmodifiable(Object2ByteFunction<K> var0) {
      return new Object2ByteFunctions.UnmodifiableFunction(var0);
   }

   public static <K> Object2ByteFunction<K> primitive(Function<? super K, ? extends Byte> var0) {
      Objects.requireNonNull(var0);
      if (var0 instanceof Object2ByteFunction) {
         return (Object2ByteFunction)var0;
      } else {
         return (Object2ByteFunction)(var0 instanceof ToIntFunction ? (var1) -> {
            return SafeMath.safeIntToByte(((ToIntFunction)var0).applyAsInt(var1));
         } : new Object2ByteFunctions.PrimitiveFunction(var0));
      }
   }

   public static class PrimitiveFunction<K> implements Object2ByteFunction<K> {
      protected final Function<? super K, ? extends Byte> function;

      protected PrimitiveFunction(Function<? super K, ? extends Byte> var1) {
         super();
         this.function = var1;
      }

      public boolean containsKey(Object var1) {
         return this.function.apply(var1) != null;
      }

      public byte getByte(Object var1) {
         Byte var2 = (Byte)this.function.apply(var1);
         return var2 == null ? this.defaultReturnValue() : var2;
      }

      /** @deprecated */
      @Deprecated
      public Byte get(Object var1) {
         return (Byte)this.function.apply(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte put(K var1, Byte var2) {
         throw new UnsupportedOperationException();
      }
   }

   public static class UnmodifiableFunction<K> extends AbstractObject2ByteFunction<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ByteFunction<K> function;

      protected UnmodifiableFunction(Object2ByteFunction<K> var1) {
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

      public boolean containsKey(Object var1) {
         return this.function.containsKey(var1);
      }

      public byte put(K var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public byte getByte(Object var1) {
         return this.function.getByte(var1);
      }

      public byte removeByte(Object var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte put(K var1, Byte var2) {
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

   public static class SynchronizedFunction<K> implements Object2ByteFunction<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ByteFunction<K> function;
      protected final Object sync;

      protected SynchronizedFunction(Object2ByteFunction<K> var1, Object var2) {
         super();
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.function = var1;
            this.sync = var2;
         }
      }

      protected SynchronizedFunction(Object2ByteFunction<K> var1) {
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
      public Byte apply(K var1) {
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

      public boolean containsKey(Object var1) {
         synchronized(this.sync) {
            return this.function.containsKey(var1);
         }
      }

      public byte put(K var1, byte var2) {
         synchronized(this.sync) {
            return this.function.put(var1, var2);
         }
      }

      public byte getByte(Object var1) {
         synchronized(this.sync) {
            return this.function.getByte(var1);
         }
      }

      public byte removeByte(Object var1) {
         synchronized(this.sync) {
            return this.function.removeByte(var1);
         }
      }

      public void clear() {
         synchronized(this.sync) {
            this.function.clear();
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte put(K var1, Byte var2) {
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

   public static class Singleton<K> extends AbstractObject2ByteFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final byte value;

      protected Singleton(K var1, byte var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean containsKey(Object var1) {
         return Objects.equals(this.key, var1);
      }

      public byte getByte(Object var1) {
         return Objects.equals(this.key, var1) ? this.value : this.defRetValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction<K> extends AbstractObject2ByteFunction<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
         super();
      }

      public byte getByte(Object var1) {
         return 0;
      }

      public boolean containsKey(Object var1) {
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
         return Object2ByteFunctions.EMPTY_FUNCTION;
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
         return Object2ByteFunctions.EMPTY_FUNCTION;
      }
   }
}
