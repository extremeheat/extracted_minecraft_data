package it.unimi.dsi.fastutil.bytes;

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

public final class Byte2BooleanMaps {
   public static final Byte2BooleanMaps.EmptyMap EMPTY_MAP = new Byte2BooleanMaps.EmptyMap();

   private Byte2BooleanMaps() {
      super();
   }

   public static ObjectIterator<Byte2BooleanMap.Entry> fastIterator(Byte2BooleanMap var0) {
      ObjectSet var1 = var0.byte2BooleanEntrySet();
      return var1 instanceof Byte2BooleanMap.FastEntrySet ? ((Byte2BooleanMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Byte2BooleanMap var0, Consumer<? super Byte2BooleanMap.Entry> var1) {
      ObjectSet var2 = var0.byte2BooleanEntrySet();
      if (var2 instanceof Byte2BooleanMap.FastEntrySet) {
         ((Byte2BooleanMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Byte2BooleanMap.Entry> fastIterable(Byte2BooleanMap var0) {
      final ObjectSet var1 = var0.byte2BooleanEntrySet();
      return (ObjectIterable)(var1 instanceof Byte2BooleanMap.FastEntrySet ? new ObjectIterable<Byte2BooleanMap.Entry>() {
         public ObjectIterator<Byte2BooleanMap.Entry> iterator() {
            return ((Byte2BooleanMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Byte2BooleanMap.Entry> var1x) {
            ((Byte2BooleanMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Byte2BooleanMap singleton(byte var0, boolean var1) {
      return new Byte2BooleanMaps.Singleton(var0, var1);
   }

   public static Byte2BooleanMap singleton(Byte var0, Boolean var1) {
      return new Byte2BooleanMaps.Singleton(var0, var1);
   }

   public static Byte2BooleanMap synchronize(Byte2BooleanMap var0) {
      return new Byte2BooleanMaps.SynchronizedMap(var0);
   }

   public static Byte2BooleanMap synchronize(Byte2BooleanMap var0, Object var1) {
      return new Byte2BooleanMaps.SynchronizedMap(var0, var1);
   }

   public static Byte2BooleanMap unmodifiable(Byte2BooleanMap var0) {
      return new Byte2BooleanMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Byte2BooleanFunctions.UnmodifiableFunction implements Byte2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2BooleanMap map;
      protected transient ObjectSet<Byte2BooleanMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient BooleanCollection values;

      protected UnmodifiableMap(Byte2BooleanMap var1) {
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

      public void putAll(Map<? extends Byte, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.byte2BooleanEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Boolean>> entrySet() {
         return this.byte2BooleanEntrySet();
      }

      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.unmodifiable(this.map.keySet());
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

      public boolean getOrDefault(byte var1, boolean var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Byte, ? super Boolean> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Byte, ? super Boolean, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean putIfAbsent(byte var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(byte var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(byte var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(byte var1, boolean var2, boolean var3) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsent(byte var1, IntPredicate var2) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsentNullable(byte var1, IntFunction<? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfAbsentPartial(byte var1, Byte2BooleanFunction var2) {
         throw new UnsupportedOperationException();
      }

      public boolean computeIfPresent(byte var1, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean compute(byte var1, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean merge(byte var1, boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
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
      public Boolean replace(Byte var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Byte var1, Boolean var2, Boolean var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean putIfAbsent(Byte var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfAbsent(Byte var1, Function<? super Byte, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfPresent(Byte var1, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean compute(Byte var1, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean merge(Byte var1, Boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Byte2BooleanFunctions.SynchronizedFunction implements Byte2BooleanMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2BooleanMap map;
      protected transient ObjectSet<Byte2BooleanMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient BooleanCollection values;

      protected SynchronizedMap(Byte2BooleanMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Byte2BooleanMap var1) {
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

      public void putAll(Map<? extends Byte, ? extends Boolean> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.byte2BooleanEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Boolean>> entrySet() {
         return this.byte2BooleanEntrySet();
      }

      public ByteSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ByteSets.synchronize(this.map.keySet(), this.sync);
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

      public boolean getOrDefault(byte var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Byte, ? super Boolean> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Byte, ? super Boolean, ? extends Boolean> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public boolean putIfAbsent(byte var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(byte var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public boolean replace(byte var1, boolean var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(byte var1, boolean var2, boolean var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public boolean computeIfAbsent(byte var1, IntPredicate var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public boolean computeIfAbsentNullable(byte var1, IntFunction<? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public boolean computeIfAbsentPartial(byte var1, Byte2BooleanFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public boolean computeIfPresent(byte var1, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public boolean compute(byte var1, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public boolean merge(byte var1, boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
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
      public Boolean replace(Byte var1, Boolean var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Byte var1, Boolean var2, Boolean var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean putIfAbsent(Byte var1, Boolean var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfAbsent(Byte var1, Function<? super Byte, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean computeIfPresent(Byte var1, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean compute(Byte var1, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean merge(Byte var1, Boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Byte2BooleanFunctions.Singleton implements Byte2BooleanMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Byte2BooleanMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient BooleanCollection values;

      protected Singleton(byte var1, boolean var2) {
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

      public void putAll(Map<? extends Byte, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractByte2BooleanMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Boolean>> entrySet() {
         return this.byte2BooleanEntrySet();
      }

      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.singleton(this.key);
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

   public static class EmptyMap extends Byte2BooleanFunctions.EmptyFunction implements Byte2BooleanMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Byte, ? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ByteSet keySet() {
         return ByteSets.EMPTY_SET;
      }

      public BooleanCollection values() {
         return BooleanSets.EMPTY_SET;
      }

      public Object clone() {
         return Byte2BooleanMaps.EMPTY_MAP;
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
