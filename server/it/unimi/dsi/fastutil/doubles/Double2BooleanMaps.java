package it.unimi.dsi.fastutil.doubles;

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
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.Function;

public final class Double2BooleanMaps {
   public static final Double2BooleanMaps.EmptyMap EMPTY_MAP = new Double2BooleanMaps.EmptyMap();

   private Double2BooleanMaps() {
      super();
   }

   public static ObjectIterator<Double2BooleanMap.Entry> fastIterator(Double2BooleanMap var0) {
      ObjectSet var1 = var0.double2BooleanEntrySet();
      return var1 instanceof Double2BooleanMap.FastEntrySet ? ((Double2BooleanMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Double2BooleanMap var0, Consumer<? super Double2BooleanMap.Entry> var1) {
      ObjectSet var2 = var0.double2BooleanEntrySet();
      if (var2 instanceof Double2BooleanMap.FastEntrySet) {
         ((Double2BooleanMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Double2BooleanMap.Entry> fastIterable(Double2BooleanMap var0) {
      final ObjectSet var1 = var0.double2BooleanEntrySet();
      return (ObjectIterable)(var1 instanceof Double2BooleanMap.FastEntrySet ? new ObjectIterable<Double2BooleanMap.Entry>() {
         public ObjectIterator<Double2BooleanMap.Entry> iterator() {
            return ((Double2BooleanMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Double2BooleanMap.Entry> var1x) {
            ((Double2BooleanMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Double2BooleanMap singleton(double var0, boolean var2) {
      return new Double2BooleanMaps.Singleton(var0, var2);
   }

   public static Double2BooleanMap singleton(Double var0, Boolean var1) {
      return new Double2BooleanMaps.Singleton(var0, var1);
   }

   public static Double2BooleanMap synchronize(Double2BooleanMap var0) {
      return new Double2BooleanMaps.SynchronizedMap(var0);
   }

   public static Double2BooleanMap synchronize(Double2BooleanMap var0, Object var1) {
      return new Double2BooleanMaps.SynchronizedMap(var0, var1);
   }

   public static Double2BooleanMap unmodifiable(Double2BooleanMap var0) {
      return new Double2BooleanMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Double2BooleanFunctions.UnmodifiableFunction implements Double2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2BooleanMap map;
      protected transient ObjectSet<Double2BooleanMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient BooleanCollection values;

      protected UnmodifiableMap(Double2BooleanMap var1) {
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

      public void putAll(Map<? extends Double, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.double2BooleanEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Boolean>> entrySet() {
         return this.double2BooleanEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.unmodifiable(this.map.keySet());
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

      public boolean getOrDefault(double var1, boolean var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Double, ? super Boolean> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Double, ? super Boolean, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean putIfAbsent(double var1, boolean var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(double var1, boolean var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(double var1, boolean var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(double var1, boolean var3, boolean var4) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsent(double var1, DoublePredicate var3) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsentNullable(double var1, DoubleFunction<? extends Boolean> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsentPartial(double var1, Double2BooleanFunction var3) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfPresent(double var1, BiFunction<? super Double, ? super Boolean, ? extends Boolean> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean compute(double var1, BiFunction<? super Double, ? super Boolean, ? extends Boolean> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean merge(double var1, boolean var3, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var4) {
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
      public Boolean replace(Double var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, Boolean var2, Boolean var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean putIfAbsent(Double var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfAbsent(Double var1, Function<? super Double, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfPresent(Double var1, BiFunction<? super Double, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean compute(Double var1, BiFunction<? super Double, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean merge(Double var1, Boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Double2BooleanFunctions.SynchronizedFunction implements Double2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2BooleanMap map;
      protected transient ObjectSet<Double2BooleanMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient BooleanCollection values;

      protected SynchronizedMap(Double2BooleanMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Double2BooleanMap var1) {
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

      public void putAll(Map<? extends Double, ? extends Boolean> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.double2BooleanEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Boolean>> entrySet() {
         return this.double2BooleanEntrySet();
      }

      public DoubleSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = DoubleSets.synchronize(this.map.keySet(), this.sync);
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

      public boolean getOrDefault(double var1, boolean var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Double, ? super Boolean> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Double, ? super Boolean, ? extends Boolean> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public boolean putIfAbsent(double var1, boolean var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(double var1, boolean var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public boolean replace(double var1, boolean var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(double var1, boolean var3, boolean var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var4);
         }
      }

      public boolean computeIfAbsent(double var1, DoublePredicate var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public boolean computeIfAbsentNullable(double var1, DoubleFunction<? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var3);
         }
      }

      public boolean computeIfAbsentPartial(double var1, Double2BooleanFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public boolean computeIfPresent(double var1, BiFunction<? super Double, ? super Boolean, ? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public boolean compute(double var1, BiFunction<? super Double, ? super Boolean, ? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public boolean merge(double var1, boolean var3, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var4) {
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
      public Boolean replace(Double var1, Boolean var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, Boolean var2, Boolean var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean putIfAbsent(Double var1, Boolean var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfAbsent(Double var1, Function<? super Double, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfPresent(Double var1, BiFunction<? super Double, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean compute(Double var1, BiFunction<? super Double, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean merge(Double var1, Boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Double2BooleanFunctions.Singleton implements Double2BooleanMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Double2BooleanMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient BooleanCollection values;

      protected Singleton(double var1, boolean var3) {
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

      public void putAll(Map<? extends Double, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractDouble2BooleanMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Boolean>> entrySet() {
         return this.double2BooleanEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.singleton(this.key);
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
         return HashCommon.double2int(this.key) ^ (this.value ? 1231 : 1237);
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

   public static class EmptyMap extends Double2BooleanFunctions.EmptyFunction implements Double2BooleanMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Double, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public DoubleSet keySet() {
         return DoubleSets.EMPTY_SET;
      }

      public BooleanCollection values() {
         return BooleanSets.EMPTY_SET;
      }

      public Object clone() {
         return Double2BooleanMaps.EMPTY_MAP;
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
