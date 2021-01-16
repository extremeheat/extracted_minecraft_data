package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharCollections;
import it.unimi.dsi.fastutil.chars.CharSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class Reference2CharMaps {
   public static final Reference2CharMaps.EmptyMap EMPTY_MAP = new Reference2CharMaps.EmptyMap();

   private Reference2CharMaps() {
      super();
   }

   public static <K> ObjectIterator<Reference2CharMap.Entry<K>> fastIterator(Reference2CharMap<K> var0) {
      ObjectSet var1 = var0.reference2CharEntrySet();
      return var1 instanceof Reference2CharMap.FastEntrySet ? ((Reference2CharMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K> void fastForEach(Reference2CharMap<K> var0, Consumer<? super Reference2CharMap.Entry<K>> var1) {
      ObjectSet var2 = var0.reference2CharEntrySet();
      if (var2 instanceof Reference2CharMap.FastEntrySet) {
         ((Reference2CharMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <K> ObjectIterable<Reference2CharMap.Entry<K>> fastIterable(Reference2CharMap<K> var0) {
      final ObjectSet var1 = var0.reference2CharEntrySet();
      return (ObjectIterable)(var1 instanceof Reference2CharMap.FastEntrySet ? new ObjectIterable<Reference2CharMap.Entry<K>>() {
         public ObjectIterator<Reference2CharMap.Entry<K>> iterator() {
            return ((Reference2CharMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Reference2CharMap.Entry<K>> var1x) {
            ((Reference2CharMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <K> Reference2CharMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Reference2CharMap<K> singleton(K var0, char var1) {
      return new Reference2CharMaps.Singleton(var0, var1);
   }

   public static <K> Reference2CharMap<K> singleton(K var0, Character var1) {
      return new Reference2CharMaps.Singleton(var0, var1);
   }

   public static <K> Reference2CharMap<K> synchronize(Reference2CharMap<K> var0) {
      return new Reference2CharMaps.SynchronizedMap(var0);
   }

   public static <K> Reference2CharMap<K> synchronize(Reference2CharMap<K> var0, Object var1) {
      return new Reference2CharMaps.SynchronizedMap(var0, var1);
   }

   public static <K> Reference2CharMap<K> unmodifiable(Reference2CharMap<K> var0) {
      return new Reference2CharMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<K> extends Reference2CharFunctions.UnmodifiableFunction<K> implements Reference2CharMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2CharMap<K> map;
      protected transient ObjectSet<Reference2CharMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient CharCollection values;

      protected UnmodifiableMap(Reference2CharMap<K> var1) {
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

      public void putAll(Map<? extends K, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Reference2CharMap.Entry<K>> reference2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.reference2CharEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Character>> entrySet() {
         return this.reference2CharEntrySet();
      }

      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.unmodifiable(this.map.keySet());
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

      public char getOrDefault(Object var1, char var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super K, ? super Character> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super K, ? super Character, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public char putIfAbsent(K var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public char replace(K var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(K var1, char var2, char var3) {
         throw new UnsupportedOperationException();
      }

      public char computeCharIfAbsent(K var1, ToIntFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public char computeCharIfAbsentPartial(K var1, Reference2CharFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public char computeCharIfPresent(K var1, BiFunction<? super K, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      public char computeChar(K var1, BiFunction<? super K, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      public char mergeChar(K var1, char var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
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
      public Character replace(K var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Character var2, Character var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character putIfAbsent(K var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      public Character computeIfAbsent(K var1, Function<? super K, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      public Character computeIfPresent(K var1, BiFunction<? super K, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      public Character compute(K var1, BiFunction<? super K, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character merge(K var1, Character var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap<K> extends Reference2CharFunctions.SynchronizedFunction<K> implements Reference2CharMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2CharMap<K> map;
      protected transient ObjectSet<Reference2CharMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient CharCollection values;

      protected SynchronizedMap(Reference2CharMap<K> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Reference2CharMap<K> var1) {
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

      public void putAll(Map<? extends K, ? extends Character> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Reference2CharMap.Entry<K>> reference2CharEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.reference2CharEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Character>> entrySet() {
         return this.reference2CharEntrySet();
      }

      public ReferenceSet<K> keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ReferenceSets.synchronize(this.map.keySet(), this.sync);
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

      public char getOrDefault(Object var1, char var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super K, ? super Character> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super K, ? super Character, ? extends Character> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public char putIfAbsent(K var1, char var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(Object var1, char var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public char replace(K var1, char var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(K var1, char var2, char var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public char computeCharIfAbsent(K var1, ToIntFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeCharIfAbsent(var1, var2);
         }
      }

      public char computeCharIfAbsentPartial(K var1, Reference2CharFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeCharIfAbsentPartial(var1, var2);
         }
      }

      public char computeCharIfPresent(K var1, BiFunction<? super K, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.computeCharIfPresent(var1, var2);
         }
      }

      public char computeChar(K var1, BiFunction<? super K, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.computeChar(var1, var2);
         }
      }

      public char mergeChar(K var1, char var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
         synchronized(this.sync) {
            return this.map.mergeChar(var1, var2, var3);
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
      public Character replace(K var1, Character var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Character var2, Character var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character putIfAbsent(K var1, Character var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public Character computeIfAbsent(K var1, Function<? super K, ? extends Character> var2) {
         synchronized(this.sync) {
            return (Character)this.map.computeIfAbsent(var1, var2);
         }
      }

      public Character computeIfPresent(K var1, BiFunction<? super K, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return (Character)this.map.computeIfPresent(var1, var2);
         }
      }

      public Character compute(K var1, BiFunction<? super K, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return (Character)this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character merge(K var1, Character var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton<K> extends Reference2CharFunctions.Singleton<K> implements Reference2CharMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Reference2CharMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient CharCollection values;

      protected Singleton(K var1, char var2) {
         super(var1, var2);
      }

      public boolean containsValue(char var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Character)var1 == this.value;
      }

      public void putAll(Map<? extends K, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Reference2CharMap.Entry<K>> reference2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractReference2CharMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Character>> entrySet() {
         return this.reference2CharEntrySet();
      }

      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.singleton(this.key);
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
         return System.identityHashCode(this.key) ^ this.value;
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

   public static class EmptyMap<K> extends Reference2CharFunctions.EmptyFunction<K> implements Reference2CharMap<K>, Serializable, Cloneable {
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

      public void putAll(Map<? extends K, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Reference2CharMap.Entry<K>> reference2CharEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ReferenceSet<K> keySet() {
         return ReferenceSets.EMPTY_SET;
      }

      public CharCollection values() {
         return CharSets.EMPTY_SET;
      }

      public Object clone() {
         return Reference2CharMaps.EMPTY_MAP;
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
