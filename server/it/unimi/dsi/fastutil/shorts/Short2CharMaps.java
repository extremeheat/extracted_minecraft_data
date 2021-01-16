package it.unimi.dsi.fastutil.shorts;

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
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public final class Short2CharMaps {
   public static final Short2CharMaps.EmptyMap EMPTY_MAP = new Short2CharMaps.EmptyMap();

   private Short2CharMaps() {
      super();
   }

   public static ObjectIterator<Short2CharMap.Entry> fastIterator(Short2CharMap var0) {
      ObjectSet var1 = var0.short2CharEntrySet();
      return var1 instanceof Short2CharMap.FastEntrySet ? ((Short2CharMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Short2CharMap var0, Consumer<? super Short2CharMap.Entry> var1) {
      ObjectSet var2 = var0.short2CharEntrySet();
      if (var2 instanceof Short2CharMap.FastEntrySet) {
         ((Short2CharMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Short2CharMap.Entry> fastIterable(Short2CharMap var0) {
      final ObjectSet var1 = var0.short2CharEntrySet();
      return (ObjectIterable)(var1 instanceof Short2CharMap.FastEntrySet ? new ObjectIterable<Short2CharMap.Entry>() {
         public ObjectIterator<Short2CharMap.Entry> iterator() {
            return ((Short2CharMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Short2CharMap.Entry> var1x) {
            ((Short2CharMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Short2CharMap singleton(short var0, char var1) {
      return new Short2CharMaps.Singleton(var0, var1);
   }

   public static Short2CharMap singleton(Short var0, Character var1) {
      return new Short2CharMaps.Singleton(var0, var1);
   }

   public static Short2CharMap synchronize(Short2CharMap var0) {
      return new Short2CharMaps.SynchronizedMap(var0);
   }

   public static Short2CharMap synchronize(Short2CharMap var0, Object var1) {
      return new Short2CharMaps.SynchronizedMap(var0, var1);
   }

   public static Short2CharMap unmodifiable(Short2CharMap var0) {
      return new Short2CharMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Short2CharFunctions.UnmodifiableFunction implements Short2CharMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2CharMap map;
      protected transient ObjectSet<Short2CharMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient CharCollection values;

      protected UnmodifiableMap(Short2CharMap var1) {
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

      public void putAll(Map<? extends Short, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2CharMap.Entry> short2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.short2CharEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Character>> entrySet() {
         return this.short2CharEntrySet();
      }

      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.unmodifiable(this.map.keySet());
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

      public char getOrDefault(short var1, char var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Short, ? super Character> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Short, ? super Character, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public char putIfAbsent(short var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(short var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public char replace(short var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(short var1, char var2, char var3) {
         throw new UnsupportedOperationException();
      }

      public char computeIfAbsent(short var1, IntUnaryOperator var2) {
         throw new UnsupportedOperationException();
      }

      public char computeIfAbsentNullable(short var1, IntFunction<? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      public char computeIfAbsentPartial(short var1, Short2CharFunction var2) {
         throw new UnsupportedOperationException();
      }

      public char computeIfPresent(short var1, BiFunction<? super Short, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      public char compute(short var1, BiFunction<? super Short, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      public char merge(short var1, char var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
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
      public Character replace(Short var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Short var1, Character var2, Character var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character putIfAbsent(Short var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfAbsent(Short var1, Function<? super Short, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfPresent(Short var1, BiFunction<? super Short, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character compute(Short var1, BiFunction<? super Short, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character merge(Short var1, Character var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Short2CharFunctions.SynchronizedFunction implements Short2CharMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2CharMap map;
      protected transient ObjectSet<Short2CharMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient CharCollection values;

      protected SynchronizedMap(Short2CharMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Short2CharMap var1) {
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

      public void putAll(Map<? extends Short, ? extends Character> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Short2CharMap.Entry> short2CharEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.short2CharEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Character>> entrySet() {
         return this.short2CharEntrySet();
      }

      public ShortSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ShortSets.synchronize(this.map.keySet(), this.sync);
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

      public char getOrDefault(short var1, char var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Short, ? super Character> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Short, ? super Character, ? extends Character> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public char putIfAbsent(short var1, char var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(short var1, char var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public char replace(short var1, char var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(short var1, char var2, char var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public char computeIfAbsent(short var1, IntUnaryOperator var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public char computeIfAbsentNullable(short var1, IntFunction<? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public char computeIfAbsentPartial(short var1, Short2CharFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public char computeIfPresent(short var1, BiFunction<? super Short, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public char compute(short var1, BiFunction<? super Short, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public char merge(short var1, char var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
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
      public Character replace(Short var1, Character var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Short var1, Character var2, Character var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character putIfAbsent(Short var1, Character var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfAbsent(Short var1, Function<? super Short, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfPresent(Short var1, BiFunction<? super Short, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character compute(Short var1, BiFunction<? super Short, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character merge(Short var1, Character var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Short2CharFunctions.Singleton implements Short2CharMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Short2CharMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient CharCollection values;

      protected Singleton(short var1, char var2) {
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

      public void putAll(Map<? extends Short, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2CharMap.Entry> short2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractShort2CharMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Character>> entrySet() {
         return this.short2CharEntrySet();
      }

      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.singleton(this.key);
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

   public static class EmptyMap extends Short2CharFunctions.EmptyFunction implements Short2CharMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Short, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2CharMap.Entry> short2CharEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ShortSet keySet() {
         return ShortSets.EMPTY_SET;
      }

      public CharCollection values() {
         return CharSets.EMPTY_SET;
      }

      public Object clone() {
         return Short2CharMaps.EMPTY_MAP;
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
