package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public final class Char2ByteMaps {
   public static final Char2ByteMaps.EmptyMap EMPTY_MAP = new Char2ByteMaps.EmptyMap();

   private Char2ByteMaps() {
      super();
   }

   public static ObjectIterator<Char2ByteMap.Entry> fastIterator(Char2ByteMap var0) {
      ObjectSet var1 = var0.char2ByteEntrySet();
      return var1 instanceof Char2ByteMap.FastEntrySet ? ((Char2ByteMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Char2ByteMap var0, Consumer<? super Char2ByteMap.Entry> var1) {
      ObjectSet var2 = var0.char2ByteEntrySet();
      if (var2 instanceof Char2ByteMap.FastEntrySet) {
         ((Char2ByteMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Char2ByteMap.Entry> fastIterable(Char2ByteMap var0) {
      final ObjectSet var1 = var0.char2ByteEntrySet();
      return (ObjectIterable)(var1 instanceof Char2ByteMap.FastEntrySet ? new ObjectIterable<Char2ByteMap.Entry>() {
         public ObjectIterator<Char2ByteMap.Entry> iterator() {
            return ((Char2ByteMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Char2ByteMap.Entry> var1x) {
            ((Char2ByteMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Char2ByteMap singleton(char var0, byte var1) {
      return new Char2ByteMaps.Singleton(var0, var1);
   }

   public static Char2ByteMap singleton(Character var0, Byte var1) {
      return new Char2ByteMaps.Singleton(var0, var1);
   }

   public static Char2ByteMap synchronize(Char2ByteMap var0) {
      return new Char2ByteMaps.SynchronizedMap(var0);
   }

   public static Char2ByteMap synchronize(Char2ByteMap var0, Object var1) {
      return new Char2ByteMaps.SynchronizedMap(var0, var1);
   }

   public static Char2ByteMap unmodifiable(Char2ByteMap var0) {
      return new Char2ByteMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Char2ByteFunctions.UnmodifiableFunction implements Char2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2ByteMap map;
      protected transient ObjectSet<Char2ByteMap.Entry> entries;
      protected transient CharSet keys;
      protected transient ByteCollection values;

      protected UnmodifiableMap(Char2ByteMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(byte var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Character, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.char2ByteEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Character, Byte>> entrySet() {
         return this.char2ByteEntrySet();
      }

      public CharSet keySet() {
         if (this.keys == null) {
            this.keys = CharSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public ByteCollection values() {
         return this.values == null ? ByteCollections.unmodifiable(this.map.values()) : this.values;
      }

      public boolean isEmpty() {
         return this.map.isEmpty();
      }

      public int hashCode() {
         return this.map.hashCode();
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.map.equals(var1);
      }

      public byte getOrDefault(char var1, byte var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Character, ? super Byte> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Character, ? super Byte, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public byte putIfAbsent(char var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(char var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public byte replace(char var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(char var1, byte var2, byte var3) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsent(char var1, IntUnaryOperator var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsentNullable(char var1, IntFunction<? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsentPartial(char var1, Char2ByteFunction var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfPresent(char var1, BiFunction<? super Character, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public byte compute(char var1, BiFunction<? super Character, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public byte merge(char var1, byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte getOrDefault(Object var1, Byte var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte replace(Character var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Character var1, Byte var2, Byte var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte putIfAbsent(Character var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfAbsent(Character var1, Function<? super Character, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfPresent(Character var1, BiFunction<? super Character, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte compute(Character var1, BiFunction<? super Character, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte merge(Character var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Char2ByteFunctions.SynchronizedFunction implements Char2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2ByteMap map;
      protected transient ObjectSet<Char2ByteMap.Entry> entries;
      protected transient CharSet keys;
      protected transient ByteCollection values;

      protected SynchronizedMap(Char2ByteMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Char2ByteMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(byte var1) {
         synchronized(this.sync) {
            return this.map.containsValue(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         synchronized(this.sync) {
            return this.map.containsValue(var1);
         }
      }

      public void putAll(Map<? extends Character, ? extends Byte> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.char2ByteEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Character, Byte>> entrySet() {
         return this.char2ByteEntrySet();
      }

      public CharSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = CharSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public ByteCollection values() {
         synchronized(this.sync) {
            return this.values == null ? ByteCollections.synchronize(this.map.values(), this.sync) : this.values;
         }
      }

      public boolean isEmpty() {
         synchronized(this.sync) {
            return this.map.isEmpty();
         }
      }

      public int hashCode() {
         synchronized(this.sync) {
            return this.map.hashCode();
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.sync) {
               return this.map.equals(var1);
            }
         }
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         synchronized(this.sync) {
            var1.defaultWriteObject();
         }
      }

      public byte getOrDefault(char var1, byte var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Character, ? super Byte> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Character, ? super Byte, ? extends Byte> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public byte putIfAbsent(char var1, byte var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(char var1, byte var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public byte replace(char var1, byte var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(char var1, byte var2, byte var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public byte computeIfAbsent(char var1, IntUnaryOperator var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public byte computeIfAbsentNullable(char var1, IntFunction<? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public byte computeIfAbsentPartial(char var1, Char2ByteFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public byte computeIfPresent(char var1, BiFunction<? super Character, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public byte compute(char var1, BiFunction<? super Character, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public byte merge(char var1, byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte getOrDefault(Object var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte replace(Character var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Character var1, Byte var2, Byte var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte putIfAbsent(Character var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfAbsent(Character var1, Function<? super Character, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfPresent(Character var1, BiFunction<? super Character, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte compute(Character var1, BiFunction<? super Character, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte merge(Character var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Char2ByteFunctions.Singleton implements Char2ByteMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Char2ByteMap.Entry> entries;
      protected transient CharSet keys;
      protected transient ByteCollection values;

      protected Singleton(char var1, byte var2) {
         super(var1, var2);
      }

      public boolean containsValue(byte var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Byte)var1 == this.value;
      }

      public void putAll(Map<? extends Character, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractChar2ByteMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Character, Byte>> entrySet() {
         return this.char2ByteEntrySet();
      }

      public CharSet keySet() {
         if (this.keys == null) {
            this.keys = CharSets.singleton(this.key);
         }

         return this.keys;
      }

      public ByteCollection values() {
         if (this.values == null) {
            this.values = ByteSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return this.key ^ this.value;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Map)) {
            return false;
         } else {
            Map var2 = (Map)var1;
            return var2.size() != 1 ? false : ((Entry)var2.entrySet().iterator().next()).equals(this.entrySet().iterator().next());
         }
      }

      public String toString() {
         return "{" + this.key + "=>" + this.value + "}";
      }
   }

   public static class EmptyMap extends Char2ByteFunctions.EmptyFunction implements Char2ByteMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(byte var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Character, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public CharSet keySet() {
         return CharSets.EMPTY_SET;
      }

      public ByteCollection values() {
         return ByteSets.EMPTY_SET;
      }

      public Object clone() {
         return Char2ByteMaps.EMPTY_MAP;
      }

      public boolean isEmpty() {
         return true;
      }

      public int hashCode() {
         return 0;
      }

      public boolean equals(Object var1) {
         return !(var1 instanceof Map) ? false : ((Map)var1).isEmpty();
      }

      public String toString() {
         return "{}";
      }
   }
}
