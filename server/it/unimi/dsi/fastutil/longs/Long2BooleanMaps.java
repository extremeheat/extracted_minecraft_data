package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanSets;
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
import java.util.function.LongFunction;
import java.util.function.LongPredicate;

public final class Long2BooleanMaps {
   public static final Long2BooleanMaps.EmptyMap EMPTY_MAP = new Long2BooleanMaps.EmptyMap();

   private Long2BooleanMaps() {
      super();
   }

   public static ObjectIterator<Long2BooleanMap.Entry> fastIterator(Long2BooleanMap var0) {
      ObjectSet var1 = var0.long2BooleanEntrySet();
      return var1 instanceof Long2BooleanMap.FastEntrySet ? ((Long2BooleanMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Long2BooleanMap var0, Consumer<? super Long2BooleanMap.Entry> var1) {
      ObjectSet var2 = var0.long2BooleanEntrySet();
      if (var2 instanceof Long2BooleanMap.FastEntrySet) {
         ((Long2BooleanMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Long2BooleanMap.Entry> fastIterable(Long2BooleanMap var0) {
      final ObjectSet var1 = var0.long2BooleanEntrySet();
      return (ObjectIterable)(var1 instanceof Long2BooleanMap.FastEntrySet ? new ObjectIterable<Long2BooleanMap.Entry>() {
         public ObjectIterator<Long2BooleanMap.Entry> iterator() {
            return ((Long2BooleanMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Long2BooleanMap.Entry> var1x) {
            ((Long2BooleanMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Long2BooleanMap singleton(long var0, boolean var2) {
      return new Long2BooleanMaps.Singleton(var0, var2);
   }

   public static Long2BooleanMap singleton(Long var0, Boolean var1) {
      return new Long2BooleanMaps.Singleton(var0, var1);
   }

   public static Long2BooleanMap synchronize(Long2BooleanMap var0) {
      return new Long2BooleanMaps.SynchronizedMap(var0);
   }

   public static Long2BooleanMap synchronize(Long2BooleanMap var0, Object var1) {
      return new Long2BooleanMaps.SynchronizedMap(var0, var1);
   }

   public static Long2BooleanMap unmodifiable(Long2BooleanMap var0) {
      return new Long2BooleanMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Long2BooleanFunctions.UnmodifiableFunction implements Long2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2BooleanMap map;
      protected transient ObjectSet<Long2BooleanMap.Entry> entries;
      protected transient LongSet keys;
      protected transient BooleanCollection values;

      protected UnmodifiableMap(Long2BooleanMap var1) {
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

      public void putAll(Map<? extends Long, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.long2BooleanEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Boolean>> entrySet() {
         return this.long2BooleanEntrySet();
      }

      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.unmodifiable(this.map.keySet());
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

      public boolean getOrDefault(long var1, boolean var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Long, ? super Boolean> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Long, ? super Boolean, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean putIfAbsent(long var1, boolean var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(long var1, boolean var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(long var1, boolean var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(long var1, boolean var3, boolean var4) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsent(long var1, LongPredicate var3) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsentNullable(long var1, LongFunction<? extends Boolean> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsentPartial(long var1, Long2BooleanFunction var3) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfPresent(long var1, BiFunction<? super Long, ? super Boolean, ? extends Boolean> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean compute(long var1, BiFunction<? super Long, ? super Boolean, ? extends Boolean> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean merge(long var1, boolean var3, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var4) {
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
      public Boolean replace(Long var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Long var1, Boolean var2, Boolean var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean putIfAbsent(Long var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfAbsent(Long var1, Function<? super Long, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfPresent(Long var1, BiFunction<? super Long, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean compute(Long var1, BiFunction<? super Long, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean merge(Long var1, Boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Long2BooleanFunctions.SynchronizedFunction implements Long2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2BooleanMap map;
      protected transient ObjectSet<Long2BooleanMap.Entry> entries;
      protected transient LongSet keys;
      protected transient BooleanCollection values;

      protected SynchronizedMap(Long2BooleanMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Long2BooleanMap var1) {
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

      public void putAll(Map<? extends Long, ? extends Boolean> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.long2BooleanEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Boolean>> entrySet() {
         return this.long2BooleanEntrySet();
      }

      public LongSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = LongSets.synchronize(this.map.keySet(), this.sync);
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

      public boolean getOrDefault(long var1, boolean var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Long, ? super Boolean> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Long, ? super Boolean, ? extends Boolean> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public boolean putIfAbsent(long var1, boolean var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(long var1, boolean var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public boolean replace(long var1, boolean var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(long var1, boolean var3, boolean var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var4);
         }
      }

      public boolean computeIfAbsent(long var1, LongPredicate var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public boolean computeIfAbsentNullable(long var1, LongFunction<? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var3);
         }
      }

      public boolean computeIfAbsentPartial(long var1, Long2BooleanFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public boolean computeIfPresent(long var1, BiFunction<? super Long, ? super Boolean, ? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public boolean compute(long var1, BiFunction<? super Long, ? super Boolean, ? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public boolean merge(long var1, boolean var3, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var4) {
         synchronized(this.sync) {
            return this.map.merge(var1, var3, var4);
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
      public Boolean replace(Long var1, Boolean var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Long var1, Boolean var2, Boolean var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean putIfAbsent(Long var1, Boolean var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfAbsent(Long var1, Function<? super Long, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfPresent(Long var1, BiFunction<? super Long, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean compute(Long var1, BiFunction<? super Long, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean merge(Long var1, Boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Long2BooleanFunctions.Singleton implements Long2BooleanMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Long2BooleanMap.Entry> entries;
      protected transient LongSet keys;
      protected transient BooleanCollection values;

      protected Singleton(long var1, boolean var3) {
         super(var1, var3);
      }

      public boolean containsValue(boolean var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Boolean)var1 == this.value;
      }

      public void putAll(Map<? extends Long, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractLong2BooleanMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Boolean>> entrySet() {
         return this.long2BooleanEntrySet();
      }

      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.singleton(this.key);
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
         return HashCommon.long2int(this.key) ^ (this.value ? 1231 : 1237);
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

   public static class EmptyMap extends Long2BooleanFunctions.EmptyFunction implements Long2BooleanMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Long, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public LongSet keySet() {
         return LongSets.EMPTY_SET;
      }

      public BooleanCollection values() {
         return BooleanSets.EMPTY_SET;
      }

      public Object clone() {
         return Long2BooleanMaps.EMPTY_MAP;
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
