package it.unimi.dsi.fastutil.shorts;

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
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

public final class Short2BooleanMaps {
   public static final Short2BooleanMaps.EmptyMap EMPTY_MAP = new Short2BooleanMaps.EmptyMap();

   private Short2BooleanMaps() {
      super();
   }

   public static ObjectIterator<Short2BooleanMap.Entry> fastIterator(Short2BooleanMap var0) {
      ObjectSet var1 = var0.short2BooleanEntrySet();
      return var1 instanceof Short2BooleanMap.FastEntrySet ? ((Short2BooleanMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Short2BooleanMap var0, Consumer<? super Short2BooleanMap.Entry> var1) {
      ObjectSet var2 = var0.short2BooleanEntrySet();
      if (var2 instanceof Short2BooleanMap.FastEntrySet) {
         ((Short2BooleanMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Short2BooleanMap.Entry> fastIterable(Short2BooleanMap var0) {
      final ObjectSet var1 = var0.short2BooleanEntrySet();
      return (ObjectIterable)(var1 instanceof Short2BooleanMap.FastEntrySet ? new ObjectIterable<Short2BooleanMap.Entry>() {
         public ObjectIterator<Short2BooleanMap.Entry> iterator() {
            return ((Short2BooleanMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Short2BooleanMap.Entry> var1x) {
            ((Short2BooleanMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Short2BooleanMap singleton(short var0, boolean var1) {
      return new Short2BooleanMaps.Singleton(var0, var1);
   }

   public static Short2BooleanMap singleton(Short var0, Boolean var1) {
      return new Short2BooleanMaps.Singleton(var0, var1);
   }

   public static Short2BooleanMap synchronize(Short2BooleanMap var0) {
      return new Short2BooleanMaps.SynchronizedMap(var0);
   }

   public static Short2BooleanMap synchronize(Short2BooleanMap var0, Object var1) {
      return new Short2BooleanMaps.SynchronizedMap(var0, var1);
   }

   public static Short2BooleanMap unmodifiable(Short2BooleanMap var0) {
      return new Short2BooleanMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Short2BooleanFunctions.UnmodifiableFunction implements Short2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2BooleanMap map;
      protected transient ObjectSet<Short2BooleanMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient BooleanCollection values;

      protected UnmodifiableMap(Short2BooleanMap var1) {
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

      public void putAll(Map<? extends Short, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2BooleanMap.Entry> short2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.short2BooleanEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Boolean>> entrySet() {
         return this.short2BooleanEntrySet();
      }

      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.unmodifiable(this.map.keySet());
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

      public boolean getOrDefault(short var1, boolean var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Short, ? super Boolean> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Short, ? super Boolean, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean putIfAbsent(short var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(short var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(short var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(short var1, boolean var2, boolean var3) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsent(short var1, IntPredicate var2) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsentNullable(short var1, IntFunction<? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsentPartial(short var1, Short2BooleanFunction var2) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfPresent(short var1, BiFunction<? super Short, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean compute(short var1, BiFunction<? super Short, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean merge(short var1, boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
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
      public Boolean replace(Short var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Short var1, Boolean var2, Boolean var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean putIfAbsent(Short var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfAbsent(Short var1, Function<? super Short, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfPresent(Short var1, BiFunction<? super Short, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean compute(Short var1, BiFunction<? super Short, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean merge(Short var1, Boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Short2BooleanFunctions.SynchronizedFunction implements Short2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2BooleanMap map;
      protected transient ObjectSet<Short2BooleanMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient BooleanCollection values;

      protected SynchronizedMap(Short2BooleanMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Short2BooleanMap var1) {
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

      public void putAll(Map<? extends Short, ? extends Boolean> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Short2BooleanMap.Entry> short2BooleanEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.short2BooleanEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Boolean>> entrySet() {
         return this.short2BooleanEntrySet();
      }

      public ShortSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ShortSets.synchronize(this.map.keySet(), this.sync);
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

      public boolean getOrDefault(short var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Short, ? super Boolean> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Short, ? super Boolean, ? extends Boolean> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public boolean putIfAbsent(short var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(short var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public boolean replace(short var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(short var1, boolean var2, boolean var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public boolean computeIfAbsent(short var1, IntPredicate var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public boolean computeIfAbsentNullable(short var1, IntFunction<? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public boolean computeIfAbsentPartial(short var1, Short2BooleanFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public boolean computeIfPresent(short var1, BiFunction<? super Short, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public boolean compute(short var1, BiFunction<? super Short, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public boolean merge(short var1, boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
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
      public Boolean replace(Short var1, Boolean var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Short var1, Boolean var2, Boolean var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean putIfAbsent(Short var1, Boolean var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfAbsent(Short var1, Function<? super Short, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfPresent(Short var1, BiFunction<? super Short, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean compute(Short var1, BiFunction<? super Short, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean merge(Short var1, Boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Short2BooleanFunctions.Singleton implements Short2BooleanMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Short2BooleanMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient BooleanCollection values;

      protected Singleton(short var1, boolean var2) {
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

      public void putAll(Map<? extends Short, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2BooleanMap.Entry> short2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractShort2BooleanMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Boolean>> entrySet() {
         return this.short2BooleanEntrySet();
      }

      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.singleton(this.key);
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
         return this.key ^ (this.value ? 1231 : 1237);
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

   public static class EmptyMap extends Short2BooleanFunctions.EmptyFunction implements Short2BooleanMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Short, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2BooleanMap.Entry> short2BooleanEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ShortSet keySet() {
         return ShortSets.EMPTY_SET;
      }

      public BooleanCollection values() {
         return BooleanSets.EMPTY_SET;
      }

      public Object clone() {
         return Short2BooleanMaps.EMPTY_MAP;
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
