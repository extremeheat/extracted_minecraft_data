package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
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

public final class Short2DoubleMaps {
   public static final Short2DoubleMaps.EmptyMap EMPTY_MAP = new Short2DoubleMaps.EmptyMap();

   private Short2DoubleMaps() {
      super();
   }

   public static ObjectIterator<Short2DoubleMap.Entry> fastIterator(Short2DoubleMap var0) {
      ObjectSet var1 = var0.short2DoubleEntrySet();
      return var1 instanceof Short2DoubleMap.FastEntrySet ? ((Short2DoubleMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Short2DoubleMap var0, Consumer<? super Short2DoubleMap.Entry> var1) {
      ObjectSet var2 = var0.short2DoubleEntrySet();
      if (var2 instanceof Short2DoubleMap.FastEntrySet) {
         ((Short2DoubleMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Short2DoubleMap.Entry> fastIterable(Short2DoubleMap var0) {
      final ObjectSet var1 = var0.short2DoubleEntrySet();
      return (ObjectIterable)(var1 instanceof Short2DoubleMap.FastEntrySet ? new ObjectIterable<Short2DoubleMap.Entry>() {
         public ObjectIterator<Short2DoubleMap.Entry> iterator() {
            return ((Short2DoubleMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Short2DoubleMap.Entry> var1x) {
            ((Short2DoubleMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Short2DoubleMap singleton(short var0, double var1) {
      return new Short2DoubleMaps.Singleton(var0, var1);
   }

   public static Short2DoubleMap singleton(Short var0, Double var1) {
      return new Short2DoubleMaps.Singleton(var0, var1);
   }

   public static Short2DoubleMap synchronize(Short2DoubleMap var0) {
      return new Short2DoubleMaps.SynchronizedMap(var0);
   }

   public static Short2DoubleMap synchronize(Short2DoubleMap var0, Object var1) {
      return new Short2DoubleMaps.SynchronizedMap(var0, var1);
   }

   public static Short2DoubleMap unmodifiable(Short2DoubleMap var0) {
      return new Short2DoubleMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Short2DoubleFunctions.UnmodifiableFunction implements Short2DoubleMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2DoubleMap map;
      protected transient ObjectSet<Short2DoubleMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient DoubleCollection values;

      protected UnmodifiableMap(Short2DoubleMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(double var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Short, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2DoubleMap.Entry> short2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.short2DoubleEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Double>> entrySet() {
         return this.short2DoubleEntrySet();
      }

      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public DoubleCollection values() {
         return this.values == null ? DoubleCollections.unmodifiable(this.map.values()) : this.values;
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

      public double getOrDefault(short var1, double var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Short, ? super Double> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Short, ? super Double, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public double putIfAbsent(short var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(short var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public double replace(short var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(short var1, double var2, double var4) {
         throw new UnsupportedOperationException();
      }

      public double computeIfAbsent(short var1, IntToDoubleFunction var2) {
         throw new UnsupportedOperationException();
      }

      public double computeIfAbsentNullable(short var1, IntFunction<? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      public double computeIfAbsentPartial(short var1, Short2DoubleFunction var2) {
         throw new UnsupportedOperationException();
      }

      public double computeIfPresent(short var1, BiFunction<? super Short, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      public double compute(short var1, BiFunction<? super Short, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      public double merge(short var1, double var2, BiFunction<? super Double, ? super Double, ? extends Double> var4) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double getOrDefault(Object var1, Double var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double replace(Short var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Short var1, Double var2, Double var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double putIfAbsent(Short var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfAbsent(Short var1, Function<? super Short, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfPresent(Short var1, BiFunction<? super Short, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double compute(Short var1, BiFunction<? super Short, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double merge(Short var1, Double var2, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Short2DoubleFunctions.SynchronizedFunction implements Short2DoubleMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2DoubleMap map;
      protected transient ObjectSet<Short2DoubleMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient DoubleCollection values;

      protected SynchronizedMap(Short2DoubleMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Short2DoubleMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(double var1) {
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

      public void putAll(Map<? extends Short, ? extends Double> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Short2DoubleMap.Entry> short2DoubleEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.short2DoubleEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Double>> entrySet() {
         return this.short2DoubleEntrySet();
      }

      public ShortSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ShortSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public DoubleCollection values() {
         synchronized(this.sync) {
            return this.values == null ? DoubleCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public double getOrDefault(short var1, double var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Short, ? super Double> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Short, ? super Double, ? extends Double> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public double putIfAbsent(short var1, double var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(short var1, double var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public double replace(short var1, double var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(short var1, double var2, double var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var4);
         }
      }

      public double computeIfAbsent(short var1, IntToDoubleFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public double computeIfAbsentNullable(short var1, IntFunction<? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public double computeIfAbsentPartial(short var1, Short2DoubleFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public double computeIfPresent(short var1, BiFunction<? super Short, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public double compute(short var1, BiFunction<? super Short, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public double merge(short var1, double var2, BiFunction<? super Double, ? super Double, ? extends Double> var4) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var4);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double getOrDefault(Object var1, Double var2) {
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
      public Double replace(Short var1, Double var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Short var1, Double var2, Double var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double putIfAbsent(Short var1, Double var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfAbsent(Short var1, Function<? super Short, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfPresent(Short var1, BiFunction<? super Short, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double compute(Short var1, BiFunction<? super Short, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double merge(Short var1, Double var2, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Short2DoubleFunctions.Singleton implements Short2DoubleMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Short2DoubleMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient DoubleCollection values;

      protected Singleton(short var1, double var2) {
         super(var1, var2);
      }

      public boolean containsValue(double var1) {
         return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return Double.doubleToLongBits((Double)var1) == Double.doubleToLongBits(this.value);
      }

      public void putAll(Map<? extends Short, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2DoubleMap.Entry> short2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractShort2DoubleMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Double>> entrySet() {
         return this.short2DoubleEntrySet();
      }

      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.singleton(this.key);
         }

         return this.keys;
      }

      public DoubleCollection values() {
         if (this.values == null) {
            this.values = DoubleSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return this.key ^ HashCommon.double2int(this.value);
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

   public static class EmptyMap extends Short2DoubleFunctions.EmptyFunction implements Short2DoubleMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(double var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Short, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2DoubleMap.Entry> short2DoubleEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ShortSet keySet() {
         return ShortSets.EMPTY_SET;
      }

      public DoubleCollection values() {
         return DoubleSets.EMPTY_SET;
      }

      public Object clone() {
         return Short2DoubleMaps.EMPTY_MAP;
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
