package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Reference2BooleanMaps {
   public static final Reference2BooleanMaps.EmptyMap EMPTY_MAP = new Reference2BooleanMaps.EmptyMap();

   private Reference2BooleanMaps() {
      super();
   }

   public static <K> ObjectIterator<Reference2BooleanMap.Entry<K>> fastIterator(Reference2BooleanMap<K> var0) {
      ObjectSet var1 = var0.reference2BooleanEntrySet();
      return var1 instanceof Reference2BooleanMap.FastEntrySet ? ((Reference2BooleanMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K> void fastForEach(Reference2BooleanMap<K> var0, Consumer<? super Reference2BooleanMap.Entry<K>> var1) {
      ObjectSet var2 = var0.reference2BooleanEntrySet();
      if (var2 instanceof Reference2BooleanMap.FastEntrySet) {
         ((Reference2BooleanMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <K> ObjectIterable<Reference2BooleanMap.Entry<K>> fastIterable(Reference2BooleanMap<K> var0) {
      final ObjectSet var1 = var0.reference2BooleanEntrySet();
      return (ObjectIterable)(var1 instanceof Reference2BooleanMap.FastEntrySet ? new ObjectIterable<Reference2BooleanMap.Entry<K>>() {
         public ObjectIterator<Reference2BooleanMap.Entry<K>> iterator() {
            return ((Reference2BooleanMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Reference2BooleanMap.Entry<K>> var1x) {
            ((Reference2BooleanMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <K> Reference2BooleanMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Reference2BooleanMap<K> singleton(K var0, boolean var1) {
      return new Reference2BooleanMaps.Singleton(var0, var1);
   }

   public static <K> Reference2BooleanMap<K> singleton(K var0, Boolean var1) {
      return new Reference2BooleanMaps.Singleton(var0, var1);
   }

   public static <K> Reference2BooleanMap<K> synchronize(Reference2BooleanMap<K> var0) {
      return new Reference2BooleanMaps.SynchronizedMap(var0);
   }

   public static <K> Reference2BooleanMap<K> synchronize(Reference2BooleanMap<K> var0, Object var1) {
      return new Reference2BooleanMaps.SynchronizedMap(var0, var1);
   }

   public static <K> Reference2BooleanMap<K> unmodifiable(Reference2BooleanMap<K> var0) {
      return new Reference2BooleanMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<K> extends Reference2BooleanFunctions.UnmodifiableFunction<K> implements Reference2BooleanMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2BooleanMap<K> map;
      protected transient ObjectSet<Reference2BooleanMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient BooleanCollection values;

      protected UnmodifiableMap(Reference2BooleanMap<K> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(boolean var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends K, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Reference2BooleanMap.Entry<K>> reference2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.reference2BooleanEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Boolean>> entrySet() {
         return this.reference2BooleanEntrySet();
      }

      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public BooleanCollection values() {
         return this.values == null ? BooleanCollections.unmodifiable(this.map.values()) : this.values;
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

      public boolean getOrDefault(Object var1, boolean var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super K, ? super Boolean> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super K, ? super Boolean, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean putIfAbsent(K var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(K var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(K var1, boolean var2, boolean var3) {
         throw new UnsupportedOperationException();
      }

      public boolean computeBooleanIfAbsent(K var1, Predicate<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean computeBooleanIfAbsentPartial(K var1, Reference2BooleanFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean computeBooleanIfPresent(K var1, BiFunction<? super K, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean computeBoolean(K var1, BiFunction<? super K, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean mergeBoolean(K var1, boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean getOrDefault(Object var1, Boolean var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean replace(K var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Boolean var2, Boolean var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean putIfAbsent(K var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      public Boolean computeIfAbsent(K var1, Function<? super K, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public Boolean computeIfPresent(K var1, BiFunction<? super K, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public Boolean compute(K var1, BiFunction<? super K, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean merge(K var1, Boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap<K> extends Reference2BooleanFunctions.SynchronizedFunction<K> implements Reference2BooleanMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2BooleanMap<K> map;
      protected transient ObjectSet<Reference2BooleanMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient BooleanCollection values;

      protected SynchronizedMap(Reference2BooleanMap<K> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Reference2BooleanMap<K> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(boolean var1) {
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

      public void putAll(Map<? extends K, ? extends Boolean> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Reference2BooleanMap.Entry<K>> reference2BooleanEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.reference2BooleanEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Boolean>> entrySet() {
         return this.reference2BooleanEntrySet();
      }

      public ReferenceSet<K> keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ReferenceSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public BooleanCollection values() {
         synchronized(this.sync) {
            return this.values == null ? BooleanCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public boolean getOrDefault(Object var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super K, ? super Boolean> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super K, ? super Boolean, ? extends Boolean> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public boolean putIfAbsent(K var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(Object var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public boolean replace(K var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(K var1, boolean var2, boolean var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public boolean computeBooleanIfAbsent(K var1, Predicate<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeBooleanIfAbsent(var1, var2);
         }
      }

      public boolean computeBooleanIfAbsentPartial(K var1, Reference2BooleanFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeBooleanIfAbsentPartial(var1, var2);
         }
      }

      public boolean computeBooleanIfPresent(K var1, BiFunction<? super K, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeBooleanIfPresent(var1, var2);
         }
      }

      public boolean computeBoolean(K var1, BiFunction<? super K, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeBoolean(var1, var2);
         }
      }

      public boolean mergeBoolean(K var1, boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.mergeBoolean(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean getOrDefault(Object var1, Boolean var2) {
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
      public Boolean replace(K var1, Boolean var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Boolean var2, Boolean var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean putIfAbsent(K var1, Boolean var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public Boolean computeIfAbsent(K var1, Function<? super K, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return (Boolean)this.map.computeIfAbsent(var1, var2);
         }
      }

      public Boolean computeIfPresent(K var1, BiFunction<? super K, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return (Boolean)this.map.computeIfPresent(var1, var2);
         }
      }

      public Boolean compute(K var1, BiFunction<? super K, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return (Boolean)this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean merge(K var1, Boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton<K> extends Reference2BooleanFunctions.Singleton<K> implements Reference2BooleanMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Reference2BooleanMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient BooleanCollection values;

      protected Singleton(K var1, boolean var2) {
         super(var1, var2);
      }

      public boolean containsValue(boolean var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Boolean)var1 == this.value;
      }

      public void putAll(Map<? extends K, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Reference2BooleanMap.Entry<K>> reference2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractReference2BooleanMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Boolean>> entrySet() {
         return this.reference2BooleanEntrySet();
      }

      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.singleton(this.key);
         }

         return this.keys;
      }

      public BooleanCollection values() {
         if (this.values == null) {
            this.values = BooleanSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return System.identityHashCode(this.key) ^ (this.value ? 1231 : 1237);
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

   public static class EmptyMap<K> extends Reference2BooleanFunctions.EmptyFunction<K> implements Reference2BooleanMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(boolean var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends K, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Reference2BooleanMap.Entry<K>> reference2BooleanEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ReferenceSet<K> keySet() {
         return ReferenceSets.EMPTY_SET;
      }

      public BooleanCollection values() {
         return BooleanSets.EMPTY_SET;
      }

      public Object clone() {
         return Reference2BooleanMaps.EMPTY_MAP;
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
