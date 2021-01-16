package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollections;
import it.unimi.dsi.fastutil.floats.FloatSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public final class Reference2FloatMaps {
   public static final Reference2FloatMaps.EmptyMap EMPTY_MAP = new Reference2FloatMaps.EmptyMap();

   private Reference2FloatMaps() {
      super();
   }

   public static <K> ObjectIterator<Reference2FloatMap.Entry<K>> fastIterator(Reference2FloatMap<K> var0) {
      ObjectSet var1 = var0.reference2FloatEntrySet();
      return var1 instanceof Reference2FloatMap.FastEntrySet ? ((Reference2FloatMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K> void fastForEach(Reference2FloatMap<K> var0, Consumer<? super Reference2FloatMap.Entry<K>> var1) {
      ObjectSet var2 = var0.reference2FloatEntrySet();
      if (var2 instanceof Reference2FloatMap.FastEntrySet) {
         ((Reference2FloatMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <K> ObjectIterable<Reference2FloatMap.Entry<K>> fastIterable(Reference2FloatMap<K> var0) {
      final ObjectSet var1 = var0.reference2FloatEntrySet();
      return (ObjectIterable)(var1 instanceof Reference2FloatMap.FastEntrySet ? new ObjectIterable<Reference2FloatMap.Entry<K>>() {
         public ObjectIterator<Reference2FloatMap.Entry<K>> iterator() {
            return ((Reference2FloatMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Reference2FloatMap.Entry<K>> var1x) {
            ((Reference2FloatMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <K> Reference2FloatMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Reference2FloatMap<K> singleton(K var0, float var1) {
      return new Reference2FloatMaps.Singleton(var0, var1);
   }

   public static <K> Reference2FloatMap<K> singleton(K var0, Float var1) {
      return new Reference2FloatMaps.Singleton(var0, var1);
   }

   public static <K> Reference2FloatMap<K> synchronize(Reference2FloatMap<K> var0) {
      return new Reference2FloatMaps.SynchronizedMap(var0);
   }

   public static <K> Reference2FloatMap<K> synchronize(Reference2FloatMap<K> var0, Object var1) {
      return new Reference2FloatMaps.SynchronizedMap(var0, var1);
   }

   public static <K> Reference2FloatMap<K> unmodifiable(Reference2FloatMap<K> var0) {
      return new Reference2FloatMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<K> extends Reference2FloatFunctions.UnmodifiableFunction<K> implements Reference2FloatMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2FloatMap<K> map;
      protected transient ObjectSet<Reference2FloatMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient FloatCollection values;

      protected UnmodifiableMap(Reference2FloatMap<K> var1) {
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

      public void putAll(Map<? extends K, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Reference2FloatMap.Entry<K>> reference2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.reference2FloatEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Float>> entrySet() {
         return this.reference2FloatEntrySet();
      }

      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.unmodifiable(this.map.keySet());
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

      public float getOrDefault(Object var1, float var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super K, ? super Float> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super K, ? super Float, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public float putIfAbsent(K var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public float replace(K var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(K var1, float var2, float var3) {
         throw new UnsupportedOperationException();
      }

      public float computeFloatIfAbsent(K var1, ToDoubleFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public float computeFloatIfAbsentPartial(K var1, Reference2FloatFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public float computeFloatIfPresent(K var1, BiFunction<? super K, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public float computeFloat(K var1, BiFunction<? super K, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public float mergeFloat(K var1, float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
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
      public Float replace(K var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Float var2, Float var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float putIfAbsent(K var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      public Float computeIfAbsent(K var1, Function<? super K, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public Float computeIfPresent(K var1, BiFunction<? super K, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public Float compute(K var1, BiFunction<? super K, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float merge(K var1, Float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap<K> extends Reference2FloatFunctions.SynchronizedFunction<K> implements Reference2FloatMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2FloatMap<K> map;
      protected transient ObjectSet<Reference2FloatMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient FloatCollection values;

      protected SynchronizedMap(Reference2FloatMap<K> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Reference2FloatMap<K> var1) {
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

      public void putAll(Map<? extends K, ? extends Float> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Reference2FloatMap.Entry<K>> reference2FloatEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.reference2FloatEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Float>> entrySet() {
         return this.reference2FloatEntrySet();
      }

      public ReferenceSet<K> keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ReferenceSets.synchronize(this.map.keySet(), this.sync);
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

      public float getOrDefault(Object var1, float var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super K, ? super Float> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super K, ? super Float, ? extends Float> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public float putIfAbsent(K var1, float var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(Object var1, float var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public float replace(K var1, float var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(K var1, float var2, float var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public float computeFloatIfAbsent(K var1, ToDoubleFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeFloatIfAbsent(var1, var2);
         }
      }

      public float computeFloatIfAbsentPartial(K var1, Reference2FloatFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeFloatIfAbsentPartial(var1, var2);
         }
      }

      public float computeFloatIfPresent(K var1, BiFunction<? super K, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeFloatIfPresent(var1, var2);
         }
      }

      public float computeFloat(K var1, BiFunction<? super K, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeFloat(var1, var2);
         }
      }

      public float mergeFloat(K var1, float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         synchronized(this.sync) {
            return this.map.mergeFloat(var1, var2, var3);
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
      public Float replace(K var1, Float var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Float var2, Float var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float putIfAbsent(K var1, Float var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public Float computeIfAbsent(K var1, Function<? super K, ? extends Float> var2) {
         synchronized(this.sync) {
            return (Float)this.map.computeIfAbsent(var1, var2);
         }
      }

      public Float computeIfPresent(K var1, BiFunction<? super K, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return (Float)this.map.computeIfPresent(var1, var2);
         }
      }

      public Float compute(K var1, BiFunction<? super K, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return (Float)this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float merge(K var1, Float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton<K> extends Reference2FloatFunctions.Singleton<K> implements Reference2FloatMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Reference2FloatMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient FloatCollection values;

      protected Singleton(K var1, float var2) {
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

      public void putAll(Map<? extends K, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Reference2FloatMap.Entry<K>> reference2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractReference2FloatMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Float>> entrySet() {
         return this.reference2FloatEntrySet();
      }

      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.singleton(this.key);
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
         return System.identityHashCode(this.key) ^ HashCommon.float2int(this.value);
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

   public static class EmptyMap<K> extends Reference2FloatFunctions.EmptyFunction<K> implements Reference2FloatMap<K>, Serializable, Cloneable {
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

      public void putAll(Map<? extends K, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Reference2FloatMap.Entry<K>> reference2FloatEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ReferenceSet<K> keySet() {
         return ReferenceSets.EMPTY_SET;
      }

      public FloatCollection values() {
         return FloatSets.EMPTY_SET;
      }

      public Object clone() {
         return Reference2FloatMaps.EMPTY_MAP;
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
