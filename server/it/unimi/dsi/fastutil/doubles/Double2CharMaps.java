package it.unimi.dsi.fastutil.doubles;

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
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Double2CharMaps {
   public static final Double2CharMaps.EmptyMap EMPTY_MAP = new Double2CharMaps.EmptyMap();

   private Double2CharMaps() {
      super();
   }

   public static ObjectIterator<Double2CharMap.Entry> fastIterator(Double2CharMap var0) {
      ObjectSet var1 = var0.double2CharEntrySet();
      return var1 instanceof Double2CharMap.FastEntrySet ? ((Double2CharMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Double2CharMap var0, Consumer<? super Double2CharMap.Entry> var1) {
      ObjectSet var2 = var0.double2CharEntrySet();
      if (var2 instanceof Double2CharMap.FastEntrySet) {
         ((Double2CharMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Double2CharMap.Entry> fastIterable(Double2CharMap var0) {
      final ObjectSet var1 = var0.double2CharEntrySet();
      return (ObjectIterable)(var1 instanceof Double2CharMap.FastEntrySet ? new ObjectIterable<Double2CharMap.Entry>() {
         public ObjectIterator<Double2CharMap.Entry> iterator() {
            return ((Double2CharMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Double2CharMap.Entry> var1x) {
            ((Double2CharMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Double2CharMap singleton(double var0, char var2) {
      return new Double2CharMaps.Singleton(var0, var2);
   }

   public static Double2CharMap singleton(Double var0, Character var1) {
      return new Double2CharMaps.Singleton(var0, var1);
   }

   public static Double2CharMap synchronize(Double2CharMap var0) {
      return new Double2CharMaps.SynchronizedMap(var0);
   }

   public static Double2CharMap synchronize(Double2CharMap var0, Object var1) {
      return new Double2CharMaps.SynchronizedMap(var0, var1);
   }

   public static Double2CharMap unmodifiable(Double2CharMap var0) {
      return new Double2CharMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Double2CharFunctions.UnmodifiableFunction implements Double2CharMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2CharMap map;
      protected transient ObjectSet<Double2CharMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient CharCollection values;

      protected UnmodifiableMap(Double2CharMap var1) {
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

      public void putAll(Map<? extends Double, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2CharMap.Entry> double2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.double2CharEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Character>> entrySet() {
         return this.double2CharEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.unmodifiable(this.map.keySet());
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

      public char getOrDefault(double var1, char var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Double, ? super Character> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Double, ? super Character, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public char putIfAbsent(double var1, char var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(double var1, char var3) {
         throw new UnsupportedOperationException();
      }

      public char replace(double var1, char var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(double var1, char var3, char var4) {
         throw new UnsupportedOperationException();
      }

      public char computeIfAbsent(double var1, DoubleToIntFunction var3) {
         throw new UnsupportedOperationException();
      }

      public char computeIfAbsentNullable(double var1, DoubleFunction<? extends Character> var3) {
         throw new UnsupportedOperationException();
      }

      public char computeIfAbsentPartial(double var1, Double2CharFunction var3) {
         throw new UnsupportedOperationException();
      }

      public char computeIfPresent(double var1, BiFunction<? super Double, ? super Character, ? extends Character> var3) {
         throw new UnsupportedOperationException();
      }

      public char compute(double var1, BiFunction<? super Double, ? super Character, ? extends Character> var3) {
         throw new UnsupportedOperationException();
      }

      public char merge(double var1, char var3, BiFunction<? super Character, ? super Character, ? extends Character> var4) {
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
      public Character replace(Double var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, Character var2, Character var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character putIfAbsent(Double var1, Character var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfAbsent(Double var1, Function<? super Double, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfPresent(Double var1, BiFunction<? super Double, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character compute(Double var1, BiFunction<? super Double, ? super Character, ? extends Character> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Character merge(Double var1, Character var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Double2CharFunctions.SynchronizedFunction implements Double2CharMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2CharMap map;
      protected transient ObjectSet<Double2CharMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient CharCollection values;

      protected SynchronizedMap(Double2CharMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Double2CharMap var1) {
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

      public void putAll(Map<? extends Double, ? extends Character> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Double2CharMap.Entry> double2CharEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.double2CharEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Character>> entrySet() {
         return this.double2CharEntrySet();
      }

      public DoubleSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = DoubleSets.synchronize(this.map.keySet(), this.sync);
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

      public char getOrDefault(double var1, char var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Double, ? super Character> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Double, ? super Character, ? extends Character> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public char putIfAbsent(double var1, char var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(double var1, char var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public char replace(double var1, char var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(double var1, char var3, char var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var4);
         }
      }

      public char computeIfAbsent(double var1, DoubleToIntFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public char computeIfAbsentNullable(double var1, DoubleFunction<? extends Character> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var3);
         }
      }

      public char computeIfAbsentPartial(double var1, Double2CharFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public char computeIfPresent(double var1, BiFunction<? super Double, ? super Character, ? extends Character> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public char compute(double var1, BiFunction<? super Double, ? super Character, ? extends Character> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public char merge(double var1, char var3, BiFunction<? super Character, ? super Character, ? extends Character> var4) {
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
      public Character replace(Double var1, Character var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, Character var2, Character var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character putIfAbsent(Double var1, Character var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfAbsent(Double var1, Function<? super Double, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character computeIfPresent(Double var1, BiFunction<? super Double, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character compute(Double var1, BiFunction<? super Double, ? super Character, ? extends Character> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Character merge(Double var1, Character var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Double2CharFunctions.Singleton implements Double2CharMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Double2CharMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient CharCollection values;

      protected Singleton(double var1, char var3) {
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

      public void putAll(Map<? extends Double, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2CharMap.Entry> double2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractDouble2CharMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Character>> entrySet() {
         return this.double2CharEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.singleton(this.key);
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
         return HashCommon.double2int(this.key) ^ this.value;
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

   public static class EmptyMap extends Double2CharFunctions.EmptyFunction implements Double2CharMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Double, ? extends Character> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2CharMap.Entry> double2CharEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public DoubleSet keySet() {
         return DoubleSets.EMPTY_SET;
      }

      public CharCollection values() {
         return CharSets.EMPTY_SET;
      }

      public Object clone() {
         return Double2CharMaps.EMPTY_MAP;
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
