package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharCollections;
import it.unimi.dsi.fastutil.chars.CharSets;
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
import java.util.function.LongToIntFunction;

public final class Long2CharMaps {
   public static final Long2CharMaps.EmptyMap EMPTY_MAP = new Long2CharMaps.EmptyMap();

   private Long2CharMaps() {
      super();
   }

   public static ObjectIterator<Long2CharMap.Entry> fastIterator(Long2CharMap var0) {
      ObjectSet var1 = var0.long2CharEntrySet();
      return var1 instanceof Long2CharMap.FastEntrySet ? ((Long2CharMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Long2CharMap var0, Consumer<? super Long2CharMap.Entry> var1) {
      ObjectSet var2 = var0.long2CharEntrySet();
      if (var2 instanceof Long2CharMap.FastEntrySet) {
         ((Long2CharMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Long2CharMap.Entry> fastIterable(Long2CharMap var0) {
      final ObjectSet var1 = var0.long2CharEntrySet();
      return (ObjectIterable)(var1 instanceof Long2CharMap.FastEntrySet ? new ObjectIterable<Long2CharMap.Entry>() {
         public ObjectIterator<Long2CharMap.Entry> iterator() {
            return ((Long2CharMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Long2CharMap.Entry> var1x) {
            ((Long2CharMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Long2CharMap singleton(long var0, char var2) {
      return new Long2CharMaps.Singleton(var0, var2);
   }

   public static Long2CharMap singleton(Long var0, Character var1) {
      return new Long2CharMaps.Singleton(var0, var1);
   }

   public static Long2CharMap synchronize(Long2CharMap var0) {
      return new Long2CharMaps.SynchronizedMap(var0);
   }

   public static Long2CharMap synchronize(Long2CharMap var0, Object var1) {
      return new Long2CharMaps.SynchronizedMap(var0, var1);
   }

   public static Long2CharMap unmodifiable(Long2CharMap var0) {
      return new Long2CharMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Long2CharFunctions.UnmodifiableFunction implements Long2CharMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2CharMap map;
      protected transient ObjectSet<Long2CharMap.Entry> entries;
      protected transient LongSet keys;
      protected transient CharCollection values;

      protected UnmodifiableMap(Long2CharMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(char var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Long, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2CharMap.Entry> long2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.long2CharEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Character>> entrySet() {
         return this.long2CharEntrySet();
      }

      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public CharCollection values() {
         return this.values == null ? CharCollections.unmodifiable(this.map.values()) : this.values;
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

      public char getOrDefault(long var1, char var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Long, ? super Character> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Long, ? super Character, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public char putIfAbsent(long var1, char var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(long var1, char var3) {
         throw new UnsupportedOperationException();
      }

      public char replace(long var1, char var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(long var1, char var3, char var4) {
         throw new UnsupportedOperationException();
      }

      public char computeIfAbsent(long var1, LongToIntFunction var3) {
         throw new UnsupportedOperationException();
      }

      public char computeIfAbsentNullable(long var1, LongFunction<? extends Character> var3) {
         throw new UnsupportedOperationException();
      }

      public char computeIfAbsentPartial(long var1, Long2CharFunction var3) {
         throw new UnsupportedOperationException();
      }

      public char computeIfPresent(long var1, BiFunction<? super Long, ? super Character, ? extends Character> var3) {
         throw new UnsupportedOperationException();
      }

      public char compute(long var1, BiFunction<? super Long, ? super Character, ? extends Character> var3) {
         throw new UnsupportedOperationException();
      }

      public char merge(long var1, char var3, BiFunction<? super Character, ? super Character, ? extends Character> var4) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character getOrDefault(Object var1, Character var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character replace(Long var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Long var1, Character var2, Character var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character putIfAbsent(Long var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfAbsent(Long var1, Function<? super Long, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfPresent(Long var1, BiFunction<? super Long, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character compute(Long var1, BiFunction<? super Long, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character merge(Long var1, Character var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Long2CharFunctions.SynchronizedFunction implements Long2CharMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2CharMap map;
      protected transient ObjectSet<Long2CharMap.Entry> entries;
      protected transient LongSet keys;
      protected transient CharCollection values;

      protected SynchronizedMap(Long2CharMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Long2CharMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(char var1) {
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

      public void putAll(Map<? extends Long, ? extends Character> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Long2CharMap.Entry> long2CharEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.long2CharEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Character>> entrySet() {
         return this.long2CharEntrySet();
      }

      public LongSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = LongSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public CharCollection values() {
         synchronized(this.sync) {
            return this.values == null ? CharCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public char getOrDefault(long var1, char var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Long, ? super Character> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Long, ? super Character, ? extends Character> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public char putIfAbsent(long var1, char var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(long var1, char var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public char replace(long var1, char var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(long var1, char var3, char var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var4);
         }
      }

      public char computeIfAbsent(long var1, LongToIntFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public char computeIfAbsentNullable(long var1, LongFunction<? extends Character> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var3);
         }
      }

      public char computeIfAbsentPartial(long var1, Long2CharFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public char computeIfPresent(long var1, BiFunction<? super Long, ? super Character, ? extends Character> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public char compute(long var1, BiFunction<? super Long, ? super Character, ? extends Character> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public char merge(long var1, char var3, BiFunction<? super Character, ? super Character, ? extends Character> var4) {
         synchronized(this.sync) {
            return this.map.merge(var1, var3, var4);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character getOrDefault(Object var1, Character var2) {
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
      public Character replace(Long var1, Character var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Long var1, Character var2, Character var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character putIfAbsent(Long var1, Character var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfAbsent(Long var1, Function<? super Long, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfPresent(Long var1, BiFunction<? super Long, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character compute(Long var1, BiFunction<? super Long, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character merge(Long var1, Character var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Long2CharFunctions.Singleton implements Long2CharMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Long2CharMap.Entry> entries;
      protected transient LongSet keys;
      protected transient CharCollection values;

      protected Singleton(long var1, char var3) {
         super(var1, var3);
      }

      public boolean containsValue(char var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Character)var1 == this.value;
      }

      public void putAll(Map<? extends Long, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2CharMap.Entry> long2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractLong2CharMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Character>> entrySet() {
         return this.long2CharEntrySet();
      }

      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.singleton(this.key);
         }

         return this.keys;
      }

      public CharCollection values() {
         if (this.values == null) {
            this.values = CharSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return HashCommon.long2int(this.key) ^ this.value;
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

   public static class EmptyMap extends Long2CharFunctions.EmptyFunction implements Long2CharMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(char var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Long, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2CharMap.Entry> long2CharEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public LongSet keySet() {
         return LongSets.EMPTY_SET;
      }

      public CharCollection values() {
         return CharSets.EMPTY_SET;
      }

      public Object clone() {
         return Long2CharMaps.EMPTY_MAP;
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
