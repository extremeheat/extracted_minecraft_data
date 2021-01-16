package it.unimi.dsi.fastutil.chars;

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

public final class Char2CharMaps {
   public static final Char2CharMaps.EmptyMap EMPTY_MAP = new Char2CharMaps.EmptyMap();

   private Char2CharMaps() {
      super();
   }

   public static ObjectIterator<Char2CharMap.Entry> fastIterator(Char2CharMap var0) {
      ObjectSet var1 = var0.char2CharEntrySet();
      return var1 instanceof Char2CharMap.FastEntrySet ? ((Char2CharMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Char2CharMap var0, Consumer<? super Char2CharMap.Entry> var1) {
      ObjectSet var2 = var0.char2CharEntrySet();
      if (var2 instanceof Char2CharMap.FastEntrySet) {
         ((Char2CharMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Char2CharMap.Entry> fastIterable(Char2CharMap var0) {
      final ObjectSet var1 = var0.char2CharEntrySet();
      return (ObjectIterable)(var1 instanceof Char2CharMap.FastEntrySet ? new ObjectIterable<Char2CharMap.Entry>() {
         public ObjectIterator<Char2CharMap.Entry> iterator() {
            return ((Char2CharMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Char2CharMap.Entry> var1x) {
            ((Char2CharMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Char2CharMap singleton(char var0, char var1) {
      return new Char2CharMaps.Singleton(var0, var1);
   }

   public static Char2CharMap singleton(Character var0, Character var1) {
      return new Char2CharMaps.Singleton(var0, var1);
   }

   public static Char2CharMap synchronize(Char2CharMap var0) {
      return new Char2CharMaps.SynchronizedMap(var0);
   }

   public static Char2CharMap synchronize(Char2CharMap var0, Object var1) {
      return new Char2CharMaps.SynchronizedMap(var0, var1);
   }

   public static Char2CharMap unmodifiable(Char2CharMap var0) {
      return new Char2CharMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Char2CharFunctions.UnmodifiableFunction implements Char2CharMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2CharMap map;
      protected transient ObjectSet<Char2CharMap.Entry> entries;
      protected transient CharSet keys;
      protected transient CharCollection values;

      protected UnmodifiableMap(Char2CharMap var1) {
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

      public void putAll(Map<? extends Character, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Char2CharMap.Entry> char2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.char2CharEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Character, Character>> entrySet() {
         return this.char2CharEntrySet();
      }

      public CharSet keySet() {
         if (this.keys == null) {
            this.keys = CharSets.unmodifiable(this.map.keySet());
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

      public char getOrDefault(char var1, char var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Character, ? super Character> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Character, ? super Character, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public char putIfAbsent(char var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(char var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public char replace(char var1, char var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(char var1, char var2, char var3) {
         throw new UnsupportedOperationException();
      }

      public char computeIfAbsent(char var1, IntUnaryOperator var2) {
         throw new UnsupportedOperationException();
      }

      public char computeIfAbsentNullable(char var1, IntFunction<? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      public char computeIfAbsentPartial(char var1, Char2CharFunction var2) {
         throw new UnsupportedOperationException();
      }

      public char computeIfPresent(char var1, BiFunction<? super Character, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      public char compute(char var1, BiFunction<? super Character, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      public char merge(char var1, char var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
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
      public Character replace(Character var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Character var1, Character var2, Character var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character putIfAbsent(Character var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfAbsent(Character var1, Function<? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfPresent(Character var1, BiFunction<? super Character, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character compute(Character var1, BiFunction<? super Character, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character merge(Character var1, Character var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Char2CharFunctions.SynchronizedFunction implements Char2CharMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2CharMap map;
      protected transient ObjectSet<Char2CharMap.Entry> entries;
      protected transient CharSet keys;
      protected transient CharCollection values;

      protected SynchronizedMap(Char2CharMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Char2CharMap var1) {
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

      public void putAll(Map<? extends Character, ? extends Character> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Char2CharMap.Entry> char2CharEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.char2CharEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Character, Character>> entrySet() {
         return this.char2CharEntrySet();
      }

      public CharSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = CharSets.synchronize(this.map.keySet(), this.sync);
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

      public char getOrDefault(char var1, char var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Character, ? super Character> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Character, ? super Character, ? extends Character> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public char putIfAbsent(char var1, char var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(char var1, char var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public char replace(char var1, char var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(char var1, char var2, char var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public char computeIfAbsent(char var1, IntUnaryOperator var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public char computeIfAbsentNullable(char var1, IntFunction<? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public char computeIfAbsentPartial(char var1, Char2CharFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public char computeIfPresent(char var1, BiFunction<? super Character, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public char compute(char var1, BiFunction<? super Character, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public char merge(char var1, char var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
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
      public Character replace(Character var1, Character var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Character var1, Character var2, Character var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character putIfAbsent(Character var1, Character var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfAbsent(Character var1, Function<? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfPresent(Character var1, BiFunction<? super Character, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character compute(Character var1, BiFunction<? super Character, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character merge(Character var1, Character var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Char2CharFunctions.Singleton implements Char2CharMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Char2CharMap.Entry> entries;
      protected transient CharSet keys;
      protected transient CharCollection values;

      protected Singleton(char var1, char var2) {
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

      public void putAll(Map<? extends Character, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Char2CharMap.Entry> char2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractChar2CharMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Character, Character>> entrySet() {
         return this.char2CharEntrySet();
      }

      public CharSet keySet() {
         if (this.keys == null) {
            this.keys = CharSets.singleton(this.key);
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

   public static class EmptyMap extends Char2CharFunctions.EmptyFunction implements Char2CharMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Character, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Char2CharMap.Entry> char2CharEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public CharSet keySet() {
         return CharSets.EMPTY_SET;
      }

      public CharCollection values() {
         return CharSets.EMPTY_SET;
      }

      public Object clone() {
         return Char2CharMaps.EMPTY_MAP;
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
