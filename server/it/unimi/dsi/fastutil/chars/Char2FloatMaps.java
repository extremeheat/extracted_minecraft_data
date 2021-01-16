package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollections;
import it.unimi.dsi.fastutil.floats.FloatSets;
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
import java.util.function.IntToDoubleFunction;

public final class Char2FloatMaps {
   public static final Char2FloatMaps.EmptyMap EMPTY_MAP = new Char2FloatMaps.EmptyMap();

   private Char2FloatMaps() {
      super();
   }

   public static ObjectIterator<Char2FloatMap.Entry> fastIterator(Char2FloatMap var0) {
      ObjectSet var1 = var0.char2FloatEntrySet();
      return var1 instanceof Char2FloatMap.FastEntrySet ? ((Char2FloatMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Char2FloatMap var0, Consumer<? super Char2FloatMap.Entry> var1) {
      ObjectSet var2 = var0.char2FloatEntrySet();
      if (var2 instanceof Char2FloatMap.FastEntrySet) {
         ((Char2FloatMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Char2FloatMap.Entry> fastIterable(Char2FloatMap var0) {
      final ObjectSet var1 = var0.char2FloatEntrySet();
      return (ObjectIterable)(var1 instanceof Char2FloatMap.FastEntrySet ? new ObjectIterable<Char2FloatMap.Entry>() {
         public ObjectIterator<Char2FloatMap.Entry> iterator() {
            return ((Char2FloatMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Char2FloatMap.Entry> var1x) {
            ((Char2FloatMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Char2FloatMap singleton(char var0, float var1) {
      return new Char2FloatMaps.Singleton(var0, var1);
   }

   public static Char2FloatMap singleton(Character var0, Float var1) {
      return new Char2FloatMaps.Singleton(var0, var1);
   }

   public static Char2FloatMap synchronize(Char2FloatMap var0) {
      return new Char2FloatMaps.SynchronizedMap(var0);
   }

   public static Char2FloatMap synchronize(Char2FloatMap var0, Object var1) {
      return new Char2FloatMaps.SynchronizedMap(var0, var1);
   }

   public static Char2FloatMap unmodifiable(Char2FloatMap var0) {
      return new Char2FloatMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Char2FloatFunctions.UnmodifiableFunction implements Char2FloatMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2FloatMap map;
      protected transient ObjectSet<Char2FloatMap.Entry> entries;
      protected transient CharSet keys;
      protected transient FloatCollection values;

      protected UnmodifiableMap(Char2FloatMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(float var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Character, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Char2FloatMap.Entry> char2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.char2FloatEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Character, Float>> entrySet() {
         return this.char2FloatEntrySet();
      }

      public CharSet keySet() {
         if (this.keys == null) {
            this.keys = CharSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public FloatCollection values() {
         return this.values == null ? FloatCollections.unmodifiable(this.map.values()) : this.values;
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

      public float getOrDefault(char var1, float var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Character, ? super Float> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Character, ? super Float, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public float putIfAbsent(char var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(char var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public float replace(char var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(char var1, float var2, float var3) {
         throw new UnsupportedOperationException();
      }

      public float computeIfAbsent(char var1, IntToDoubleFunction var2) {
         throw new UnsupportedOperationException();
      }

      public float computeIfAbsentNullable(char var1, IntFunction<? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public float computeIfAbsentPartial(char var1, Char2FloatFunction var2) {
         throw new UnsupportedOperationException();
      }

      public float computeIfPresent(char var1, BiFunction<? super Character, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public float compute(char var1, BiFunction<? super Character, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public float merge(char var1, float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float getOrDefault(Object var1, Float var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float replace(Character var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Character var1, Float var2, Float var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float putIfAbsent(Character var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfAbsent(Character var1, Function<? super Character, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfPresent(Character var1, BiFunction<? super Character, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float compute(Character var1, BiFunction<? super Character, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float merge(Character var1, Float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Char2FloatFunctions.SynchronizedFunction implements Char2FloatMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2FloatMap map;
      protected transient ObjectSet<Char2FloatMap.Entry> entries;
      protected transient CharSet keys;
      protected transient FloatCollection values;

      protected SynchronizedMap(Char2FloatMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Char2FloatMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(float var1) {
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

      public void putAll(Map<? extends Character, ? extends Float> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Char2FloatMap.Entry> char2FloatEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.char2FloatEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Character, Float>> entrySet() {
         return this.char2FloatEntrySet();
      }

      public CharSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = CharSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public FloatCollection values() {
         synchronized(this.sync) {
            return this.values == null ? FloatCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public float getOrDefault(char var1, float var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Character, ? super Float> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Character, ? super Float, ? extends Float> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public float putIfAbsent(char var1, float var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(char var1, float var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public float replace(char var1, float var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(char var1, float var2, float var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public float computeIfAbsent(char var1, IntToDoubleFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public float computeIfAbsentNullable(char var1, IntFunction<? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public float computeIfAbsentPartial(char var1, Char2FloatFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public float computeIfPresent(char var1, BiFunction<? super Character, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public float compute(char var1, BiFunction<? super Character, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public float merge(char var1, float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float getOrDefault(Object var1, Float var2) {
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
      public Float replace(Character var1, Float var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Character var1, Float var2, Float var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float putIfAbsent(Character var1, Float var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfAbsent(Character var1, Function<? super Character, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfPresent(Character var1, BiFunction<? super Character, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float compute(Character var1, BiFunction<? super Character, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float merge(Character var1, Float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Char2FloatFunctions.Singleton implements Char2FloatMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Char2FloatMap.Entry> entries;
      protected transient CharSet keys;
      protected transient FloatCollection values;

      protected Singleton(char var1, float var2) {
         super(var1, var2);
      }

      public boolean containsValue(float var1) {
         return Float.floatToIntBits(this.value) == Float.floatToIntBits(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return Float.floatToIntBits((Float)var1) == Float.floatToIntBits(this.value);
      }

      public void putAll(Map<? extends Character, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Char2FloatMap.Entry> char2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractChar2FloatMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Character, Float>> entrySet() {
         return this.char2FloatEntrySet();
      }

      public CharSet keySet() {
         if (this.keys == null) {
            this.keys = CharSets.singleton(this.key);
         }

         return this.keys;
      }

      public FloatCollection values() {
         if (this.values == null) {
            this.values = FloatSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return this.key ^ HashCommon.float2int(this.value);
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

   public static class EmptyMap extends Char2FloatFunctions.EmptyFunction implements Char2FloatMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(float var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Character, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Char2FloatMap.Entry> char2FloatEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public CharSet keySet() {
         return CharSets.EMPTY_SET;
      }

      public FloatCollection values() {
         return FloatSets.EMPTY_SET;
      }

      public Object clone() {
         return Char2FloatMaps.EMPTY_MAP;
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
