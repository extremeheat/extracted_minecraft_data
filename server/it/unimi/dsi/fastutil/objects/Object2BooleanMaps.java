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

public final class Object2BooleanMaps {
   public static final Object2BooleanMaps.EmptyMap EMPTY_MAP = new Object2BooleanMaps.EmptyMap();

   private Object2BooleanMaps() {
      super();
   }

   public static <K> ObjectIterator<Object2BooleanMap.Entry<K>> fastIterator(Object2BooleanMap<K> var0) {
      ObjectSet var1 = var0.object2BooleanEntrySet();
      return var1 instanceof Object2BooleanMap.FastEntrySet ? ((Object2BooleanMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K> void fastForEach(Object2BooleanMap<K> var0, Consumer<? super Object2BooleanMap.Entry<K>> var1) {
      ObjectSet var2 = var0.object2BooleanEntrySet();
      if (var2 instanceof Object2BooleanMap.FastEntrySet) {
         ((Object2BooleanMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <K> ObjectIterable<Object2BooleanMap.Entry<K>> fastIterable(Object2BooleanMap<K> var0) {
      final ObjectSet var1 = var0.object2BooleanEntrySet();
      return (ObjectIterable)(var1 instanceof Object2BooleanMap.FastEntrySet ? new ObjectIterable<Object2BooleanMap.Entry<K>>() {
         public ObjectIterator<Object2BooleanMap.Entry<K>> iterator() {
            return ((Object2BooleanMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Object2BooleanMap.Entry<K>> var1x) {
            ((Object2BooleanMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <K> Object2BooleanMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Object2BooleanMap<K> singleton(K var0, boolean var1) {
      return new Object2BooleanMaps.Singleton(var0, var1);
   }

   public static <K> Object2BooleanMap<K> singleton(K var0, Boolean var1) {
      return new Object2BooleanMaps.Singleton(var0, var1);
   }

   public static <K> Object2BooleanMap<K> synchronize(Object2BooleanMap<K> var0) {
      return new Object2BooleanMaps.SynchronizedMap(var0);
   }

   public static <K> Object2BooleanMap<K> synchronize(Object2BooleanMap<K> var0, Object var1) {
      return new Object2BooleanMaps.SynchronizedMap(var0, var1);
   }

   public static <K> Object2BooleanMap<K> unmodifiable(Object2BooleanMap<K> var0) {
      return new Object2BooleanMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<K> extends Object2BooleanFunctions.UnmodifiableFunction<K> implements Object2BooleanMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2BooleanMap<K> map;
      protected transient ObjectSet<Object2BooleanMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient BooleanCollection values;

      protected UnmodifiableMap(Object2BooleanMap<K> var1) {
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

      public ObjectSet<Object2BooleanMap.Entry<K>> object2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.object2BooleanEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Boolean>> entrySet() {
         return this.object2BooleanEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.unmodifiable(this.map.keySet());
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

      public boolean computeBooleanIfAbsentPartial(K var1, Object2BooleanFunction<? super K> var2) {
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

   public static class SynchronizedMap<K> extends Object2BooleanFunctions.SynchronizedFunction<K> implements Object2BooleanMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2BooleanMap<K> map;
      protected transient ObjectSet<Object2BooleanMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient BooleanCollection values;

      protected SynchronizedMap(Object2BooleanMap<K> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Object2BooleanMap<K> var1) {
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

      public ObjectSet<Object2BooleanMap.Entry<K>> object2BooleanEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.object2BooleanEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Boolean>> entrySet() {
         return this.object2BooleanEntrySet();
      }

      public ObjectSet<K> keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ObjectSets.synchronize(this.map.keySet(), this.sync);
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

      public boolean computeBooleanIfAbsentPartial(K var1, Object2BooleanFunction<? super K> var2) {
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

   public static class Singleton<K> extends Object2BooleanFunctions.Singleton<K> implements Object2BooleanMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Object2BooleanMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
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

      public ObjectSet<Object2BooleanMap.Entry<K>> object2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractObject2BooleanMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Boolean>> entrySet() {
         return this.object2BooleanEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.singleton(this.key);
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
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value ? 1231 : 1237);
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

   public static class EmptyMap<K> extends Object2BooleanFunctions.EmptyFunction<K> implements Object2BooleanMap<K>, Serializable, Cloneable {
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

      public ObjectSet<Object2BooleanMap.Entry<K>> object2BooleanEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ObjectSet<K> keySet() {
         return ObjectSets.EMPTY_SET;
      }

      public BooleanCollection values() {
         return BooleanSets.EMPTY_SET;
      }

      public Object clone() {
         return Object2BooleanMaps.EMPTY_MAP;
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
